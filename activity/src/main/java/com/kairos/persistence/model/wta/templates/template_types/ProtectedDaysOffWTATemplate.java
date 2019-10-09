package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.config.ApplicationContextProviderNonManageBean;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.activity.wta.IntervalBalance;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.service.wta.WorkTimeAgreementService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.enums.wta.WTATemplateType.PROTECTED_DAYS_OFF;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.brakeRuleTemplateAndUpdateViolationDetails;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getHoursByMinutes;

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
        this.wtaTemplateType = PROTECTED_DAYS_OFF;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        WorkTimeAgreementService workTimeAgreementService= ApplicationContextProviderNonManageBean.getApplicationContext().getBean(WorkTimeAgreementService.class);
        List<WTAQueryResultDTO> wtaQueryResultDTOS = workTimeAgreementService.getWTAByEmploymentIdAndDates(infoWrapper.getShift().getEmploymentId(), (infoWrapper.getShift().getStartDate()), infoWrapper.getShift().getEndDate());
        ProtectedDaysOffWTATemplate protectedDaysOffWTATemplate = (ProtectedDaysOffWTATemplate)wtaQueryResultDTOS.stream().flatMap(wtaQueryResultDTO -> wtaQueryResultDTO.getRuleTemplates().stream().filter(wtaBaseRuleTemplate -> PROTECTED_DAYS_OFF.equals(wtaBaseRuleTemplate.getWtaTemplateType()))).findFirst().get();
        IntervalBalance intervalBalance =workTimeAgreementService.getProtectedDaysOffCount(infoWrapper.getShift().getUnitId(),asLocalDate(infoWrapper.getShift().getStartDate()),infoWrapper.getShift().getStaffId(),protectedDaysOffWTATemplate.activityId);
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
