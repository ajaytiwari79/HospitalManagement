package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kairos.utils.ShiftValidatorService.getIntervalByActivity;

/**
 * Created by pavan on 23/4/18.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildCareDaysCheckWTATemplate extends WTABaseRuleTemplate {
    private List<BigInteger> activityIds = new ArrayList<>();
    private boolean borrowLeave;
    private boolean carryForwardLeave;
    private float recommendedValue;
    private CutOffIntervalUnit cutOffIntervalUnit;

    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
    }


    public boolean isCarryForwardLeave() {
        return carryForwardLeave;
    }

    public void setCarryForwardLeave(boolean carryForwardLeave) {
        this.carryForwardLeave = carryForwardLeave;
    }

    public boolean isBorrowLeave() {
        return borrowLeave;
    }

    public void setBorrowLeave(boolean borrowLeave) {
        this.borrowLeave = borrowLeave;
    }

    public ChildCareDaysCheckWTATemplate() {
       this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled()) {
            Optional<CareDaysDTO> careDaysOptional = infoWrapper.getChildCareDays().stream().filter(careDaysDTO -> (careDaysDTO.getFrom() <= infoWrapper.getStaffAge() && careDaysDTO.getTo() >= infoWrapper.getStaffAge())).findFirst();
            if (careDaysOptional.isPresent()) {
                int leaveCount = careDaysOptional.get().getLeavesAllowed();

                DateTimeInterval dateTimeInterval = getIntervalByActivity(infoWrapper.getActivityWrapperMap(),infoWrapper.getShift().getStartDate(),activityIds);
                List<ShiftWithActivityDTO> shifts = infoWrapper.getShifts().stream().filter(shift -> CollectionUtils.containsAny(shift.getActivityIds(), activityIds) && dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList());
                if (leaveCount < (shifts.size()+1)) {
                    WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id, this.name, 0, true, false);
                    infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                }
            }
        }

    }


    public CutOffIntervalUnit getCutOffIntervalUnit() {
        return cutOffIntervalUnit;
    }

    public void setCutOffIntervalUnit(CutOffIntervalUnit cutOffIntervalUnit) {
        this.cutOffIntervalUnit = cutOffIntervalUnit;
    }

    public ChildCareDaysCheckWTATemplate(String name, boolean disabled, String description) {
        super(name, description);
        this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
        this.disabled=disabled;
    }


    public List<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate)wtaBaseRuleTemplate;
        return (this != childCareDaysCheckWTATemplate) && !(borrowLeave == childCareDaysCheckWTATemplate.borrowLeave &&
                carryForwardLeave == childCareDaysCheckWTATemplate.carryForwardLeave &&
                Float.compare(childCareDaysCheckWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(activityIds, childCareDaysCheckWTATemplate.activityIds) &&
                cutOffIntervalUnit == childCareDaysCheckWTATemplate.cutOffIntervalUnit && Objects.equals(this.phaseTemplateValues,childCareDaysCheckWTATemplate.phaseTemplateValues));
    }

}
