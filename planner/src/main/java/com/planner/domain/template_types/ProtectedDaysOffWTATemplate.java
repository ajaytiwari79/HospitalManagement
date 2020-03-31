package com.planner.domain.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.config.ApplicationContextProviderNonManageBean;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.activity.wta.IntervalBalance;
import com.kairos.enums.DurationType;
import com.kairos.enums.shift.ShiftOperationType;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.domain.wta.WTABaseRuleTemplate;
import com.kairos.service.wta.WorkTimeAgreementService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_MISMATCHED_IDS;
import static com.kairos.enums.wta.WTATemplateType.PROTECTED_DAYS_OFF;

/**
 * Created by pradeep
 * Created at 29/7/19
 **/

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ProtectedDaysOffWTATemplate extends WTABaseRuleTemplate {
    @NotNull(message = MESSAGE_MISMATCHED_IDS)
    private BigInteger activityId;

    public ProtectedDaysOffWTATemplate() {
        this.wtaTemplateType = PROTECTED_DAYS_OFF;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled() && !ShiftOperationType.DELETE.equals(infoWrapper.getShiftOperationType()) && isNotNull(this.getActivityId())){
            WorkTimeAgreementService workTimeAgreementService = ApplicationContextProviderNonManageBean.getApplicationContext().getBean(WorkTimeAgreementService.class);
            IntervalBalance intervalBalance = workTimeAgreementService.getProtectedDaysOffCount(infoWrapper.getShift().getUnitId(), asLocalDate(infoWrapper.getShift().getStartDate()), infoWrapper.getShift().getStaffId(), this.activityId);
            if (this.getActivityId().equals(infoWrapper.getShift().getActivities().get(0).getActivityId()) && intervalBalance.getAvailable() < 1) {
                WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation =
                        new WorkTimeAgreementRuleViolation(this.id, this.name, null, true, false, (int) intervalBalance.getTotal(),
                                DurationType.DAYS.toValue(), String.valueOf(0));
                infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
            }
        }
    }

    public ProtectedDaysOffWTATemplate(BigInteger activityId , WTATemplateType wtaTemplateType) {
        this.wtaTemplateType=wtaTemplateType;
        this.activityId=activityId;
    }


}
