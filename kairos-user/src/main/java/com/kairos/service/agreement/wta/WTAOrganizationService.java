package com.kairos.service.agreement.wta;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.WTAOrganizationMappingDTO;
import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.service.UserBaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vipul on 19/12/17.
 */

//@Transactional
@Service
public class WTAOrganizationService extends UserBaseService {

    @Inject
    private WorkingTimeAgreementGraphRepository workingTimeAgreementGraphRepository;
    @Inject
    private WTAService wtaService;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    public List<WTAWithCountryAndOrganizationTypeDTO> getAllWTAByOrganization(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit  " + unitId);
        }
        List<WTAWithCountryAndOrganizationTypeDTO> workingTimeAgreements = workingTimeAgreementGraphRepository.getWtaByOrganization(unitId);
        return workingTimeAgreements;
    }

    public HashMap<String, Object> getWTAForOrganizationMapping(Long unitId) {
        HashMap<String, Object> hs = new HashMap<>(2);

        Organization organization = organizationGraphRepository.findOne(unitId, 1);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit  " + unitId);
        }
        if (Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent()) {
            List<Long> subTypes = organization.getOrganizationSubTypes().stream().map(type -> type.getId()).collect(Collectors.toList());
            List<WTAOrganizationMappingDTO> countryWTA = workingTimeAgreementGraphRepository.getAllWTAByOrganizationSubType(subTypes);
            hs.put("countryWTA", countryWTA);
        }
        List<WTAOrganizationMappingDTO> organizationWTA=workingTimeAgreementGraphRepository.getAllWTAByOrganizationId(unitId);
        hs.put("organizationWTA", organizationWTA);
        return hs;
    }

    public WorkingTimeAgreement linkWTAWithOrganization(Long workingTimeAgreementId, Long unitId, boolean checked) {
        WorkingTimeAgreement workingTimeAgreement;
        if (checked) {
            workingTimeAgreement = workingTimeAgreementGraphRepository.findOne(workingTimeAgreementId, 2);
            WorkingTimeAgreement newWtaObject = new WorkingTimeAgreement();
            if (!Optional.ofNullable(workingTimeAgreement).isPresent()) {
                throw new DataNotFoundByIdException("Invalid wtaId  " + workingTimeAgreementId);
            }
            WorkingTimeAgreement.copyProperties(workingTimeAgreement, newWtaObject, "id");
            List<RuleTemplate> ruleTemplates = new ArrayList<>();
            if (workingTimeAgreement.getRuleTemplates().size() > 0) {
                ruleTemplates = copyRuleTemplates(workingTimeAgreement.getRuleTemplates());
            }
            setExpertiseAndUnlinkBasicProperties(workingTimeAgreement, newWtaObject, unitId);
            newWtaObject.setRuleTemplates(ruleTemplates);
            save(newWtaObject);
            newWtaObject.setOrganization(null);
            newWtaObject.setParentWTA(workingTimeAgreement.basicDetails());
            return newWtaObject;
        } else {
            workingTimeAgreement = workingTimeAgreementGraphRepository.getChildWTAbyParentWTA(workingTimeAgreementId, unitId);
            workingTimeAgreement.setDeleted(true);
            save(workingTimeAgreement);
            return workingTimeAgreement;
        }
    }

    private List<RuleTemplate> copyRuleTemplates(List<RuleTemplate> ruleTemplates) {
        List<RuleTemplate> wtaBaseRuleTemplates = new ArrayList<RuleTemplate>(20);
        for (RuleTemplate ruleTemplate : ruleTemplates) {
            WTABaseRuleTemplate ruleTemplateCopy = new WTABaseRuleTemplate();
            BeanUtils.copyProperties(ruleTemplate, ruleTemplateCopy, "id");
            ruleTemplateCopy.setRuleTemplateCategory(null);
            RuleTemplateCategory rtcCopy = ruleTemplate.getRuleTemplateCategory();
            rtcCopy.setId(null);
            ruleTemplateCopy.setRuleTemplateCategory(rtcCopy);
            wtaBaseRuleTemplates.add(ruleTemplateCopy);
        }
        return wtaBaseRuleTemplates;
    }

    private void setExpertiseAndUnlinkBasicProperties(WorkingTimeAgreement oldWTA, WorkingTimeAgreement newWta, Long unitId) {
        Expertise e = new Expertise();
        e.setName(oldWTA.getExpertise().getName());
        e.setDescription(oldWTA.getExpertise().getDescription());
        e.setId(oldWTA.getExpertise().getId());
        newWta.setExpertise(e);
        newWta.setId(null);
        newWta.setCountry(null);
        newWta.setOrganizationType(null);
        newWta.setOrganizationSubType(null);
        newWta.setOrganization(organizationGraphRepository.findOne(unitId, 0));
        newWta.setParentWTA(oldWTA);
    }

}
