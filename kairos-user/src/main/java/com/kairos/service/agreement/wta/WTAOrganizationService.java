package com.kairos.service.agreement.wta;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.templates.WTAWithCategoryDTO;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.response.dto.web.WtaDTO;
import com.kairos.service.UserBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by vipul on 19/12/17.
 */
@Service
@Transactional
public class WTAOrganizationService extends UserBaseService{
    @Inject
    private WorkingTimeAgreementGraphRepository workingTimeAgreementGraphRepository;
    @Inject private  WTAService wtaService;
    @Inject private
    public List<WorkingTimeAgreement> getAllWTAByOrganization(Long unitId) {
        return workingTimeAgreementGraphRepository.getWtaByOrganization(unitId);
    }

    public List<WTAWithCountryAndOrganizationTypeDTO> getWTAForOrganizationMapping(long organizationSubTypeId) {
        return workingTimeAgreementGraphRepository.getAllWTAByOrganizationSubType(organizationSubTypeId);
    }
    public WorkingTimeAgreement copyWTAToOrganization(Long workingTimeAgreementId,Long unitId,boolean checked){
        Map<String, Object> map = new HashMap<>();
        List<WTAWithCategoryDTO> wtaRuleTemplateQueryResponseArrayList = new ArrayList<WTAWithCategoryDTO>();

        WorkingTimeAgreement workingTimeAgreement = workingTimeAgreementGraphRepository.findOne(workingTimeAgreementId);
            if (!Optional.ofNullable(workingTimeAgreement).isPresent()) {
                throw new DataNotFoundByIdException("Invalid wtaId  " + workingTimeAgreementId);
        }
        if (checked) {
            WorkingTimeAgreement newWtaObject = new WorkingTimeAgreement();
            WorkingTimeAgreement.copyProperties(workingTimeAgreement, newWtaObject);
            if (Optional.ofNullable(workingTimeAgreement.getRuleTemplates()).isPresent() && workingTimeAgreement.getRuleTemplates().size() > 0) {
                WtaDTO wtaDTO = workingTimeAgreement.buildwtaDTO();
                List<Long> ruleTemplateIds = new ArrayList<>();
                for (RuleTemplate wtaBRT : workingTimeAgreement.getRuleTemplates()) {
                    ruleTemplateIds.add(wtaBRT.getId());
                }
                wtaDTO.setRuleTemplates(ruleTemplateIds);
                wtaService.copyRuleTemplates(wtaDTO, workingTimeAgreement.getRuleTemplates(), wtaRuleTemplateQueryResponseArrayList);
            }
            newWtaObject.setId(null);
            newWtaObject.setOrganization();
            save(newWtaObject);
            map.put("wta", newWtaObject);
            map.put("ruleTemplate", wtaRuleTemplateQueryResponseArrayList);
        } else {
            workingTimeAgreement.setDeleted(true);
            save(workingTimeAgreement);
        }
        return  workingTimeAgreement;

    }

}
