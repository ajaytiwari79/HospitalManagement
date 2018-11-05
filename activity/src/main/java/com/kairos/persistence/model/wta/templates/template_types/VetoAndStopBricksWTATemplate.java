package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.utils.ShiftValidatorService.*;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE12
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VetoAndStopBricksWTATemplate extends WTABaseRuleTemplate {

    private int numberOfWeeks;
    private LocalDate validationStartDate;
    private BigInteger vetoTypeActivity;
    private BigInteger stopBricksActivity;
   // private List<BigInteger> activities = new ArrayList<>();
    private float totalBlockingPoints; // It's for a duration from @validationStartDate  till the @numberOfWeeks

    public VetoAndStopBricksWTATemplate() {
        //Default Constructor
    }


    public VetoAndStopBricksWTATemplate(String name, String description, int numberOfWeeks, LocalDate validationStartDate, BigInteger vetoTypeActivity, BigInteger stopBricksActivity) {
        super(name, description);
        this.numberOfWeeks = numberOfWeeks;
        this.validationStartDate = validationStartDate;
        this.vetoTypeActivity = vetoTypeActivity;
        this.stopBricksActivity = stopBricksActivity;
    }

    public int getNumberOfWeeks() {
        return numberOfWeeks;
    }

    public void setNumberOfWeeks(int numberOfWeeks) {
        this.numberOfWeeks = numberOfWeeks;
    }

    public LocalDate getValidationStartDate() {
        return validationStartDate;
    }

    public void setValidationStartDate(LocalDate validationStartDate) {
        this.validationStartDate = validationStartDate;
    }

    public BigInteger getVetoTypeActivity() {
        return vetoTypeActivity;
    }

    public void setVetoTypeActivity(BigInteger vetoTypeActivity) {
        this.vetoTypeActivity = vetoTypeActivity;
    }

    public BigInteger getStopBricksActivity() {
        return stopBricksActivity;
    }

    public void setStopBricksActivity(BigInteger stopBricksActivity) {
        this.stopBricksActivity = stopBricksActivity;
    }

    public float getTotalBlockingPoints() {
        return totalBlockingPoints;
    }

    public void setTotalBlockingPoints(float totalBlockingPoints) {
        this.totalBlockingPoints = totalBlockingPoints;
    }

    //This will throw nullPointerException
    /*public List<BigInteger> getActivities() {
        activities.add(this.stopBricksActivity);
        activities.add(this.vetoTypeActivity);
        return activities;
    }*/

   /* @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled() && DateUtils.asLocalDate(infoWrapper.getShift().getStartDate()).plusDays(1).isAfter(validationStartDate)) {

            List<ShiftWithActivityDTO> shifts = filterShiftsByIntervalAndVetoAndStopBricksActivity(getIntervalByNumberOfWeeks(infoWrapper.getShift(), numberOfWeeks, validationStartDate), infoWrapper.getShifts(), getActivities());
            shifts.add(infoWrapper.getShift());
            int totalVeto = 0;
            int totalStopBricks = 0;
            for (ShiftWithActivityDTO shift : shifts) {
                if (shift.getActivitIds().contains(vetoTypeActivity)) {
                    totalVeto++;
                } else if (shift.getActivitIds().contains(stopBricksActivity)) {
                    totalStopBricks++;
                }
            }
            boolean isValid = validateVetoAndStopBrickRules(totalBlockingPoints, totalVeto, totalStopBricks);
            if (!isValid) {
                WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id, this.name, 0, true, false);
                infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
            }

        }
    }*/

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled() && CollectionUtils.containsAny(infoWrapper.getShift().getActivitIds(),getActivityIds())) {
            DateTimeInterval interval = getIntervalByNumberOfWeeks(infoWrapper.getShift(), numberOfWeeks, validationStartDate);
            int totalVeto = 0;
            int totalStopBricks = 0;
            List<ShiftWithActivityDTO> shifts = new ArrayList<>(infoWrapper.getShifts());
            shifts.add(infoWrapper.getShift());
            for (ShiftWithActivityDTO shift : shifts) {
                if(interval.contains(shift.getStartDate())){
                    if (shift.getActivitIds().contains(vetoTypeActivity)) {
                        totalVeto++;
                    } else if (shift.getActivitIds().contains(stopBricksActivity)) {
                        totalStopBricks++;
                    }
                }
            }
            boolean isValid = validateVetoAndStopBrickRules(totalBlockingPoints, totalVeto, totalStopBricks);
            if (!isValid) {
                WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(this.id, this.name, 0, true, false);
                infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
            }

        }
    }

    List<BigInteger> getActivityIds(){
        List<BigInteger> activityIds = new ArrayList<>();
        CollectionUtils.addIgnoreNull(activityIds,vetoTypeActivity);
        CollectionUtils.addIgnoreNull(activityIds,stopBricksActivity);
        return activityIds;
    }

}
