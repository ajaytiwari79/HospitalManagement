package com.kairos.activity.service.wta;


import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.organization.OrganizationDTO;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.templates.WTABuilderService;
import com.kairos.activity.persistence.repository.wta.RuleTemplateCategoryMongoRepository;
import com.kairos.activity.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.activity.response.dto.WTADTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
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
    private RuleTemplateCategoryMongoRepository ruleTemplateCategoryMongoRepository;
    @Inject private OrganizationRestClient organizationRestClient;
    @Inject private RuleTemplateService ruleTemplateService;
    @Inject private WTABuilderService wtaBuilderService;

    private final Logger logger = LoggerFactory.getLogger(WTAOrganizationService.class);

    public List<WTAResponseDTO> getAllWTAByOrganization(Long unitId) {
        OrganizationDTO organization = organizationRestClient.getOrganization(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit  " + unitId);
        }
        List<WTAResponseDTO> workingTimeAgreements = workingTimeAgreementMongoRepository.getWtaByOrganization(unitId);
        workingTimeAgreements.forEach(wtaResponseDTO -> {
            wtaResponseDTO.setStartDateMillis(wtaResponseDTO.getStartDate().getTime());
            if(wtaResponseDTO.getEndDate()!=null){
                wtaResponseDTO.setEndDateMillis(wtaResponseDTO.getStartDate().getTime());
            }
        });
        return workingTimeAgreements;
    }


    public WTAResponseDTO updateWtaOfOrganization(Long unitId, BigInteger wtaId, WTADTO updateDTO) {
        if (updateDTO.getStartDateMillis() < System.currentTimeMillis()) {
            throw new ActionNotPermittedException("Start date cant be less than current Date " + wtaId);
        }
        boolean isWTAAlreadyExists = workingTimeAgreementMongoRepository.checkUniqueWTANameInOrganization(updateDTO.getName(), unitId, wtaId);
        if (isWTAAlreadyExists) {
            logger.info("Duplicate WTA name in organization :", wtaId);
            throw new DuplicateDataException("Duplicate WTA name in organization " + updateDTO.getName());
        }
        WorkingTimeAgreement oldWta = workingTimeAgreementMongoRepository.findOne(wtaId);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtaId);
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }
        if (oldWta.getExpertise().getId() != updateDTO.getExpertiseId()) {
            logger.info("Expertise cant be changed at unit level :", wtaId);
            throw new ActionNotPermittedException("Expertise can't be changed");
        }
        OrganizationDTO organization = organizationRestClient.getOrganization(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit  " + unitId);
        }
        WorkingTimeAgreement newWta = new WorkingTimeAgreement();
        BeanUtils.copyProperties(oldWta, newWta);
        newWta.setId(null);
        newWta.setDeleted(true);
        newWta.setStartDate(oldWta.getStartDate());
        newWta.setEndDate(new Date(updateDTO.getStartDateMillis()));
        newWta.setCountryParentWTA(null);
        //ruleTemplateCategoryMongoRepository.detachPreviousRuleTemplates(oldWta.getId());
        save(newWta);
        if (Optional.ofNullable(oldWta.getParentWTA()).isPresent()) {
            WorkingTimeAgreement workingTimeAgreement = workingTimeAgreementMongoRepository.findOne(oldWta.getParentWTA());
            workingTimeAgreement.setDeleted(true);
            save(workingTimeAgreement);
        }
        oldWta.setName(updateDTO.getName());
        oldWta.setDescription(updateDTO.getDescription());

        oldWta.setStartDate(new Date(updateDTO.getStartDateMillis()));
        if(updateDTO.getEndDateMillis()!=null){
            oldWta.setEndDate(new Date(updateDTO.getEndDateMillis()));
        }
        oldWta.setExpertise(oldWta.getExpertise());
        oldWta.setParentWTA(newWta.getId());
        oldWta.setDisabled(false);
        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (updateDTO.getRuleTemplates().size() > 0) {
            ruleTemplates = wtaBuilderService.copyRuleTemplates(updateDTO.getRuleTemplates(),true);
            save(ruleTemplates);
            List<BigInteger> ruleTemplatesIds = ruleTemplates.stream().map(ruleTemplate->ruleTemplate.getId()).collect(Collectors.toList());
            oldWta.setRuleTemplateIds(ruleTemplatesIds);
        }
        //oldWta.setOrganization(organization);
        //organization.addWorkingTimeAgreements(newWta);


        save(oldWta);
        //Preparing Response for frontend
        //workingTimeAgreementMongoRepository.removeOldWorkingTimeAgreement(oldWta.getId(), organization.getId(), updateDTO.getStartDate());
        oldWta.setParentWTA(newWta.getParentWTA());
        WTAResponseDTO wtaResponseDTO = ObjectMapperUtils.copyPropertiesByMapper(oldWta, WTAResponseDTO.class);
        wtaResponseDTO.setStartDateMillis(oldWta.getStartDate().getTime());
        if (oldWta.getEndDate() != null) {
            wtaResponseDTO.setEndDateMillis(oldWta.getEndDate().getTime());
        }
        wtaResponseDTO.setRuleTemplates(WTABuilderService.copyRuleTemplatesToDTO(ruleTemplates));
        /*oldWta.getExpertise().setCountryId(null);*/
        return wtaResponseDTO;
    }


    public List<WTAResponseDTO> getAllWtaOfOrganizationByExpertise(Long unitId,Long expertiseId){
        return workingTimeAgreementMongoRepository.getAllWtaOfOrganizationByExpertise(unitId,expertiseId);
    }



}