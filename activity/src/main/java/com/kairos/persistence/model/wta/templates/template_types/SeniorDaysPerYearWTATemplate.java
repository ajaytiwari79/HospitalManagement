package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.activity.wta.AgeRange;
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
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.*;


/**
 * Created by pavan on 24/4/18.
 */
public class SeniorDaysPerYearWTATemplate extends WTABaseRuleTemplate {
    private List<AgeRange> ageRange;
    private List<BigInteger> activityIds = new ArrayList<>();
    private boolean borrowLeave;
    private boolean carryForwardLeave;
    private CutOffIntervalUnit cutOffIntervalUnit;


    public CutOffIntervalUnit getCutOffIntervalUnit() {
        return cutOffIntervalUnit;
    }

    public void setCutOffIntervalUnit(CutOffIntervalUnit cutOffIntervalUnit) {
        this.cutOffIntervalUnit = cutOffIntervalUnit;
    }

    public float getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(float recommendedValue) {
        this.recommendedValue = recommendedValue;
    }

    private float recommendedValue;

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

    public SeniorDaysPerYearWTATemplate() {
        this.wtaTemplateType = WTATemplateType.SENIOR_DAYS_PER_YEAR;
        //Default Constructor
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled()) {
            CareDaysDTO careDays = getCareDays(infoWrapper.getSeniorCareDays(), infoWrapper.getStaffAge());
            if (isNotNull(careDays)) {
                int leaveCount = careDays.getLeavesAllowed();

                DateTimeInterval dateTimeInterval = getIntervalByActivity(infoWrapper.getActivityWrapperMap(),infoWrapper.getShift().getStartDate(),activityIds);
                List<ShiftWithActivityDTO> shifts = infoWrapper.getShifts().stream().filter(shift -> CollectionUtils.containsAny(shift.getActivityIds(), activityIds) && dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList());
                if (leaveCount < (shifts.size()+1)) {
                    WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id, this.name, 0, true, false);
                    infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                }
            }
        }
    }


    public SeniorDaysPerYearWTATemplate(String name, boolean disabled, String description, List<AgeRange> ageRange) {
        super(name, description);
        this.disabled = disabled;
        this.ageRange = ageRange;
        this.wtaTemplateType = WTATemplateType.SENIOR_DAYS_PER_YEAR;
    }

    public List<AgeRange> getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(List<AgeRange> ageRange) {
        this.ageRange = ageRange;
    }

    public List<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }


    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) wtaBaseRuleTemplate;
        return (this != seniorDaysPerYearWTATemplate) && !(borrowLeave == seniorDaysPerYearWTATemplate.borrowLeave &&
                carryForwardLeave == seniorDaysPerYearWTATemplate.carryForwardLeave &&
                Float.compare(seniorDaysPerYearWTATemplate.recommendedValue, recommendedValue) == 0 &&
                Objects.equals(ageRange, seniorDaysPerYearWTATemplate.ageRange) &&
                Objects.equals(activityIds, seniorDaysPerYearWTATemplate.activityIds) &&
                cutOffIntervalUnit == seniorDaysPerYearWTATemplate.cutOffIntervalUnit && Objects.equals(this.phaseTemplateValues,seniorDaysPerYearWTATemplate.phaseTemplateValues));
    }


}
