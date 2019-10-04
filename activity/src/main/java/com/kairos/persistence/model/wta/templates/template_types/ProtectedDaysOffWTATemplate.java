package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.config.ApplicationContextProviderNonManageBean;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.activity.wta.IntervalBalance;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.service.wta.WorkTimeAgreementService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

import static com.kairos.commons.utils.DateUtils.asLocalDate;

/**
 * Created by pradeep
 * Created at 29/7/19
 **/

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ProtectedDaysOffWTATemplate extends WTABaseRuleTemplate {
    private BigInteger activityId;

    public ProtectedDaysOffWTATemplate() {
        this.wtaTemplateType = WTATemplateType.PROTECTED_DAYS_OFF;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        WorkTimeAgreementService workTimeAgreementService= ApplicationContextProviderNonManageBean.getApplicationContext().getBean(WorkTimeAgreementService.class);
        IntervalBalance intervalBalance =workTimeAgreementService.getProtectedDaysOffCount(infoWrapper.getShift().getUnitId(),asLocalDate(infoWrapper.getShift().getStartDate()),infoWrapper.getShift().getStaffId(),infoWrapper.getShift().getActivityIds().get(0));
        if(intervalBalance.getAvailable()<1) {
            WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation =
                    new WorkTimeAgreementRuleViolation(this.id, this.name, null, true, false, (int) intervalBalance.getTotal(),
                            DurationType.DAYS, String.valueOf(0));
            infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
        }
    }

    public ProtectedDaysOffWTATemplate(BigInteger activityId , WTATemplateType wtaTemplateType) {
        this.wtaTemplateType=wtaTemplateType;
        this.activityId=activityId;
    }


}
