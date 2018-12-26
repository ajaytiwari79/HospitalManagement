package com.kairos.service.wta;


import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTAWTAWrapper;
import com.kairos.dto.activity.wta.basic_details.WTABasicDetailsDTO;
import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.persistence.model.wta.Expertise;
import com.kairos.persistence.model.wta.OrganizationType;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.WTABuilderService;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.wta.rule_template.RuleTemplateCategoryRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Created by vipul on 19/12/17.
 */

@Transactional
@Service
public class WTAOrganizationService extends MongoBaseService {

    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private RuleTemplateCategoryRepository ruleTemplateCategoryMongoRepository;
    @Inject private GenericIntegrationService genericIntegrationService;
    @Inject private RuleTemplateService ruleTemplateService;
    @Inject private WTABuilderService wtaBuilderService;
    @Inject private ExceptionService exceptionService;
    @Inject private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject private  WTAService wtaService;

    private final Logger logger = LoggerFactory.getLogger(WTAOrganizationService.class);

    public List<WTAResponseDTO> getAllWTAByOrganization(Long unitId) {
        OrganizationDTO organization = genericIntegrationService.getOrganization();
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id",unitId);
        }
        List<WTAQueryResultDTO> workingTimeAgreements = workingTimeAgreementMongoRepository.getWtaByOrganization(unitId);
        List<WTAResponseDTO> wtaResponseDTOs = new ArrayList<>();
        workingTimeAgreements.forEach(wta->{
            wtaResponseDTOs.add(ObjectMapperUtils.copyPropertiesByMapper(wta,WTAResponseDTO.class));
        });
        wtaResponseDTOs.forEach(wtaResponseDTO -> {
            wtaResponseDTO.setStartDateMillis(wtaResponseDTO.getStartDate().getTime());
            if(wtaResponseDTO.getEndDate()!=null){
                wtaResponseDTO.setEndDateMillis(wtaResponseDTO.getStartDate().getTime());
            }
        });
        return wtaResponseDTOs;
    }


    public WTAResponseDTO updateWtaOfOrganization(Long unitId, BigInteger wtaId, WTADTO updateDTO) {
        if (DateUtils.getLocalDate(updateDTO.getStartDateMillis()).isBefore(LocalDate.now())) {
            exceptionService.actionNotPermittedException("message.wta.start-end-date");
        }
        WorkingTimeAgreement WTADuplicate = workingTimeAgreementMongoRepository.checkUniqueWTANameInOrganization(updateDTO.getName(), unitId, wtaId);
        if (Optional.ofNullable(WTADuplicate).isPresent()) {
            logger.info("Duplicate WTA name in organization :", wtaId);
            exceptionService.duplicateDataException("message.wta.name.alreadyExists",updateDTO.getName());
        }
        WorkingTimeAgreement oldWta = workingTimeAgreementMongoRepository.findOne(wtaId);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtaId);
            exceptionService.dataNotFoundByIdException("message.wta.id",wtaId);
        }
        if (oldWta.getExpertise().getId() != updateDTO.getExpertiseId()) {
            logger.info("Expertise cant be changed at unit level :", wtaId);
            exceptionService.actionNotPermittedException("message.expertise.unitlevel.update",wtaId);
        }
        OrganizationDTO organization = genericIntegrationService.getOrganization();
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.id",unitId);
        }
        WTAResponseDTO wtaResponseDTO = wtaBuilderService.prepareWtaWhileUpdate(oldWta,updateDTO);
        if (Optional.ofNullable(oldWta.getParentId()).isPresent()) {
            WorkingTimeAgreement workingTimeAgreement = workingTimeAgreementMongoRepository.findOne(oldWta.getParentId());
            workingTimeAgreement.setDeleted(true);
            save(workingTimeAgreement);
        }

        return wtaResponseDTO;
    }


    public CTAWTAWrapper getAllWtaOfOrganizationByExpertise(Long unitId, Long expertiseId){
        List<WTAQueryResultDTO> wtaQueryResultDTOS = workingTimeAgreementMongoRepository.getAllWtaOfOrganizationByExpertise(unitId,expertiseId);
        List<WTAResponseDTO> wtaResponseDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(wtaQueryResultDTOS,WTAResponseDTO.class);
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getDefaultCTA(unitId,expertiseId);
        return new CTAWTAWrapper(ctaResponseDTOS,wtaResponseDTOS);
    }



}