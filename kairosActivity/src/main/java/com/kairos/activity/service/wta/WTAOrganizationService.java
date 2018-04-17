package com.kairos.activity.service.wta;


import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.organization.OrganizationDTO;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.activity.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.activity.persistence.model.wta.templates.PhaseTemplateValue;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.templates.WTABuilderService;
import com.kairos.activity.persistence.repository.wta.RuleTemplateCategoryMongoRepository;
import com.kairos.activity.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.response.dto.web.wta.PhaseTemplateValueDTO;
import com.kairos.response.dto.web.wta.WTARuleTemplateDTO;
import com.kairos.response.dto.web.wta.WTADTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
        return workingTimeAgreements;
    }


    public WorkingTimeAgreement updateWtaOfOrganization(Long unitId, BigInteger wtaId, WTADTO updateDTO) {
        OrganizationDTO organization = organizationRestClient.getOrganization(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit  " + unitId);
        }

        WorkingTimeAgreement oldWta = workingTimeAgreementMongoRepository.findOne(wtaId);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtaId);
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }


        WorkingTimeAgreement newWta = new WorkingTimeAgreement();

        boolean isWTAAlreadyExists = workingTimeAgreementMongoRepository.checkUniqueWTANameInOrganization("(?i)" + updateDTO.getName(), unitId, wtaId);
        if (isWTAAlreadyExists) {
            logger.info("Duplicate WTA name in organization :", wtaId);
            throw new DuplicateDataException("Duplicate WTA name in organization " + updateDTO.getName());
        }
        if (oldWta.getExpertise().getId() != updateDTO.getExpertiseId()) {
            logger.info("Expertise cant be changed at unit level :", wtaId);
            throw new ActionNotPermittedException("Expertise can't be changed");
        }

        //Copying Properties
        BeanUtils.copyProperties(oldWta, newWta);
        newWta.setId(null);
        newWta.setDeleted(true);
        newWta.setStartDateMillis(oldWta.getStartDateMillis());
        newWta.setEndDateMillis(updateDTO.getStartDateMillis());
        newWta.setCountryParentWTA(null);
        /*newWta.getRuleTemplateIds().forEach(ruleTemplate -> {
            if (Optional.ofNullable(ruleTemplate.getPhaseTemplateValues()).isPresent()) {
                ruleTemplate.getPhaseTemplateValues().forEach(phaseTemplateValue -> {
                });
            }
        });*/

        ruleTemplateCategoryMongoRepository.detachPreviousRuleTemplates(oldWta.getId());
        save(newWta);
        if (Optional.ofNullable(oldWta.getParentWTA()).isPresent()) {
            workingTimeAgreementMongoRepository.removeOldParentWTAMapping(oldWta.getParentWTA());
        }

        oldWta.setName(updateDTO.getName());
        oldWta.setDescription(updateDTO.getDescription());
        if (updateDTO.getStartDateMillis() < System.currentTimeMillis()) {
            throw new ActionNotPermittedException("Start date cant be less than current Date " + oldWta.getId());
        }
        oldWta.setStartDateMillis(updateDTO.getStartDateMillis());
        oldWta.setEndDateMillis(updateDTO.getEndDateMillis());
        oldWta.setExpertise(oldWta.getExpertise());
        oldWta.setParentWTA(newWta.getId());
        oldWta.setDisabled(false);

        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (updateDTO.getRuleTemplates().size() > 0) {
            WTAQueryResultDTO wtaQueryResultDTO = new WTAQueryResultDTO();
            wtaBuilderService.copyRuleTemplates(wtaQueryResultDTO,updateDTO.getRuleTemplates());
            WTABuilderService.copyWTARuleTemplateToWTA(oldWta,wtaQueryResultDTO);
            /*oldWta.setRuleTemplateIds(ruleTemplates);*/
        }
        //oldWta.setOrganization(organization);
        //organization.addWorkingTimeAgreements(newWta);


        save(oldWta);
        //Preparing Response for frontend
        //workingTimeAgreementMongoRepository.removeOldWorkingTimeAgreement(oldWta.getId(), organization.getId(), updateDTO.getStartDateMillis());
        oldWta.setParentWTA(newWta.getParentWTA());

        /*oldWta.getExpertise().setCountryId(null);*/
        return oldWta;
    }




    public List<PhaseTemplateValue> copyPhaseTemplateValue(List<PhaseTemplateValueDTO> phaseTemplateValues) {
        if(phaseTemplateValues==null){
            return null;
        }

        List<PhaseTemplateValue> phases = new ArrayList<>(4);
        for (PhaseTemplateValueDTO phaseTemplateValue : phaseTemplateValues) {
            PhaseTemplateValue newPhaseTemplateValue = new PhaseTemplateValue();
            newPhaseTemplateValue.setDisabled(phaseTemplateValue.isDisabled());
            newPhaseTemplateValue.setManagementValue(phaseTemplateValue.getManagementValue());
            newPhaseTemplateValue.setOptional(phaseTemplateValue.isOptional());
            newPhaseTemplateValue.setStaffValue(phaseTemplateValue.getStaffValue());
            newPhaseTemplateValue.setOptionalFrequency(phaseTemplateValue.getOptionalFrequency());
            newPhaseTemplateValue.setPhaseId(phaseTemplateValue.getPhaseId());
            newPhaseTemplateValue.setSequence(phaseTemplateValue.getSequence());
            newPhaseTemplateValue.setPhaseName(phaseTemplateValue.getPhaseName());
            phases.add(newPhaseTemplateValue);
        }
        return phases;
    }

    public void copyRuleTemplates(List<WTABaseRuleTemplate> ruleTemplates, List<WTARuleTemplateDTO> ruleTemplatesNewObjects) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>(20);

    }


}