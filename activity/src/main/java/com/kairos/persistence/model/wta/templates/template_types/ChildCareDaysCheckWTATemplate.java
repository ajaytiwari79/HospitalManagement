package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getCareDays;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getIntervalByActivity;

/**
 * Created by pavan on 23/4/18.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ChildCareDaysCheckWTATemplate extends WTABaseRuleTemplate {
    private List<BigInteger> activityIds = new ArrayList<>();
    private boolean borrowLeave;
    private float recommendedValue;
    private CutOffIntervalUnit cutOffIntervalUnit;


    public ChildCareDaysCheckWTATemplate() {
       this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled()) {
            //CareDaysDTO careDays = getCareDays(infoWrapper.getChildCareDays(), infoWrapper.getStaffAge());
            if (isCollectionNotEmpty(infoWrapper.getChildCareDays())) {
                long leaveCount = calculateChildCareDaysLeaveCount(infoWrapper.getChildCareDays(), infoWrapper.getStaffChildAges());
                DateTimeInterval dateTimeInterval = getIntervalByActivity(infoWrapper.getActivityWrapperMap(), infoWrapper.getShift().getStartDate(), activityIds);
                if (isNotNull(dateTimeInterval)) {
                    List<ShiftWithActivityDTO> shifts = infoWrapper.getShifts().stream().filter(shift -> CollectionUtils.containsAny(shift.getActivityIds(), activityIds) && dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList());
                    if (leaveCount < (shifts.size() + 1)) {
                        WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation =
                                new WorkTimeAgreementRuleViolation(this.id, this.name, null, true, false, null,
                                        DurationType.DAYS, String.valueOf(leaveCount));
                        infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                    }
                }
            }
        }

    }

    public long calculateChildCareDaysLeaveCount(List<CareDaysDTO> careDaysDTOS, List<Integer> staffChildAges){
        long leaveCount = 0L;
        if(isCollectionNotEmpty(staffChildAges)) {
            for (Integer staffChildAge : staffChildAges) {
                for (CareDaysDTO careDaysDTO : careDaysDTOS) {
                    if(staffChildAge >= careDaysDTO.getFrom() && isNull(careDaysDTO.getTo()) || staffChildAge <= careDaysDTO.getTo()) {
                        leaveCount += careDaysDTO.getLeavesAllowed();
                        break;
                    }
                }
            }
        }
        return  leaveCount;
    }

    public ChildCareDaysCheckWTATemplate(String name, boolean disabled, String description) {
        super(name, description);
        this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
        this.disabled=disabled;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate)wtaBaseRuleTemplate;
        return (this != childCareDaysCheckWTATemplate) && !(borrowLeave == childCareDaysCheckWTATemplate.borrowLeave &&
                Float.compare(childCareDaysCheckWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(activityIds, childCareDaysCheckWTATemplate.activityIds) &&
                cutOffIntervalUnit == childCareDaysCheckWTATemplate.cutOffIntervalUnit && Objects.equals(this.phaseTemplateValues,childCareDaysCheckWTATemplate.phaseTemplateValues));
    }

}
