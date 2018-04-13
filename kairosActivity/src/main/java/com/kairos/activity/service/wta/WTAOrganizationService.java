package com.kairos.activity.service.wta;


import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.organization.OrganizationDTO;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.WorkingTimeAgreement;
import com.kairos.activity.persistence.model.wta.templates.PhaseTemplateValue;
import com.kairos.activity.persistence.model.wta.templates.RuleTemplateCategory;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.response.dto.web.wta.PhaseTemplateValueDTO;
import com.kairos.response.dto.web.wta.RuleTemplateCategoryDTO;
import com.kairos.response.dto.web.wta.WTADTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static com.kairos.activity.persistence.enums.WTATemplateType.*;
import static com.kairos.persistence.model.enums.MasterDataTypeEnum.WTA;


/**
 * Created by vipul on 19/12/17.
 */

//@Transactional
@Service
public class WTAOrganizationService extends MongoBaseService {

    @Inject
    private WorkingTimeAgreementGraphRepository workingTimeAgreementGraphRepository;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryGraphRepository;
    @Inject private OrganizationRestClient organizationRestClient;
    @Inject private RuleTemplateService ruleTemplateService;

    private final Logger logger = LoggerFactory.getLogger(WTAOrganizationService.class);

    public List<WTAResponseDTO> getAllWTAByOrganization(Long unitId) {
        OrganizationDTO organization = organizationRestClient.getOrganization(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit  " + unitId);
        }
        List<WTAResponseDTO> workingTimeAgreements = workingTimeAgreementGraphRepository.getWtaByOrganization(unitId);
        return workingTimeAgreements;
    }


    public WorkingTimeAgreement updateWtaOfOrganization(Long unitId, Long wtaId, WTADTO updateDTO) {
        OrganizationDTO organization = organizationRestClient.getOrganization(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit  " + unitId);
        }

        WorkingTimeAgreement oldWta = workingTimeAgreementGraphRepository.findOne(wtaId, 2);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtaId);
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }


        WorkingTimeAgreement newWta = new WorkingTimeAgreement();

        boolean isWTAAlreadyExists = workingTimeAgreementGraphRepository.checkUniqueWTANameInOrganization("(?i)" + updateDTO.getName(), unitId, wtaId);
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
        /*newWta.getRuleTemplates().forEach(ruleTemplate -> {
            if (Optional.ofNullable(ruleTemplate.getPhaseTemplateValues()).isPresent()) {
                ruleTemplate.getPhaseTemplateValues().forEach(phaseTemplateValue -> {
                });
            }
        });*/

        ruleTemplateCategoryGraphRepository.detachPreviousRuleTemplates(oldWta.getId());
        save(newWta);
        if (Optional.ofNullable(oldWta.getParentWTA()).isPresent()) {
            workingTimeAgreementGraphRepository.removeOldParentWTAMapping(oldWta.getParentWTA());
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

            ruleTemplates = copyRuleTemplates(oldWta.getRuleTemplates(), updateDTO.getRuleTemplates());
            /*oldWta.setRuleTemplates(ruleTemplates);*/
        }
        //oldWta.setOrganization(organization);
        //organization.addWorkingTimeAgreements(newWta);


        save(oldWta);
        //Preparing Response for frontend
        //workingTimeAgreementGraphRepository.removeOldWorkingTimeAgreement(oldWta.getId(), organization.getId(), updateDTO.getStartDateMillis());
        oldWta.setParentWTA(newWta.getParentWTA());

        /*oldWta.getExpertise().setCountry(null);*/
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

    public void copyRuleTemplates(List<WTABaseRuleTemplate> ruleTemplates, List<RuleTemplateCategoryDTO> ruleTemplatesNewObjects) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>(20);

    }


}