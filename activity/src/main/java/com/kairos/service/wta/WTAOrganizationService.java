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
        boolean isWTAAlreadyExists = workingTimeAgreementMongoRepository.checkUniqueWTANameInOrganization(updateDTO.getName(), unitId, wtaId);
        if (isWTAAlreadyExists) {
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
        WTAResponseDTO wtaResponseDTO = prepareWtaWhileUpdate(oldWta,updateDTO);
        if (Optional.ofNullable(oldWta.getParentId()).isPresent()) {
            WorkingTimeAgreement workingTimeAgreement = workingTimeAgreementMongoRepository.findOne(oldWta.getParentId());
            workingTimeAgreement.setDeleted(true);
            save(workingTimeAgreement);
        }

        return wtaResponseDTO;
    }
    private WTAResponseDTO prepareWtaWhileUpdate(WorkingTimeAgreement oldWta, WTADTO updateDTO) {
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (DateUtils.getLocalDateFromDate(oldWta.getStartDate()).isEqual(updateDTO.getStartDate()) && (updateDTO.getStartDate().isAfter(DateUtils.getCurrentLocalDate()) || updateDTO.getStartDate().isEqual(DateUtils.getCurrentLocalDate()))) {
            logger.info("Its a future date and date is same as previous so we don't need to create wta we need to update in same");
            if (!updateDTO.getRuleTemplates().isEmpty()) {
                ruleTemplates = wtaService.updateRuleTemplatesAndSave(updateDTO.getRuleTemplates(), oldWta.getRuleTemplateIds());
            }
        } else{
            WorkingTimeAgreement versionWTA = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WorkingTimeAgreement.class);
            versionWTA.setId(null);
            versionWTA.setDeleted(false);
            versionWTA.setStartDate(oldWta.getStartDate());
            versionWTA.setEndDate(new Date(updateDTO.getStartDateMillis()));
            versionWTA.setCountryParentWTA(null);
            ruleTemplates = wtaBuilderService.copyRuleTemplates(updateDTO.getRuleTemplates(), true);
            if (!ruleTemplates.isEmpty()) {
                save(ruleTemplates);
            }
            save(versionWTA);
            oldWta.setParentId(versionWTA.getId());
        }
        oldWta.setDescription(updateDTO.getDescription());
        oldWta.setName(updateDTO.getName());
        oldWta.setStartDate(new Date(updateDTO.getStartDateMillis()));
        if (updateDTO.getEndDate() != null) {
            oldWta.setEndDate(new Date(updateDTO.getEndDateMillis()));
        }else {
            oldWta.setEndDate(null);
        }
        oldWta.setRuleTemplateIds(ruleTemplates.stream().map(ruleTemplate -> ruleTemplate.getId()).collect(Collectors.toList()));
        save(oldWta);
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        return wtaResponseDTO;
    }


    public CTAWTAWrapper getAllWtaOfOrganizationByExpertise(Long unitId, Long expertiseId){
        List<WTAQueryResultDTO> wtaQueryResultDTOS = workingTimeAgreementMongoRepository.getAllWtaOfOrganizationByExpertise(unitId,expertiseId);
        List<WTAResponseDTO> wtaResponseDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(wtaQueryResultDTOS,WTAResponseDTO.class);
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getDefaultCTA(unitId,expertiseId);
        return new CTAWTAWrapper(ctaResponseDTOS,wtaResponseDTOS);
    }



}