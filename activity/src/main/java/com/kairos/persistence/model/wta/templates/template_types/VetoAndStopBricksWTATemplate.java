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
import java.util.Objects;

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
    private BigInteger vetoActivityId;
    private BigInteger stopBrickActivityId;
    private float totalBlockingPoints; // It's for a duration from @validationStartDate  till the @numberOfWeeks

    public VetoAndStopBricksWTATemplate() {
        //Default Constructor
    }


    public VetoAndStopBricksWTATemplate(String name, String description, int numberOfWeeks, LocalDate validationStartDate, BigInteger vetoActivityId, BigInteger stopBrickActivityId) {
        super(name, description);
        this.numberOfWeeks = numberOfWeeks;
        this.validationStartDate = validationStartDate;
        this.vetoActivityId = vetoActivityId;
        this.stopBrickActivityId = stopBrickActivityId;
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

    public BigInteger getVetoActivityId() {
        return vetoActivityId;
    }

    public void setVetoActivityId(BigInteger vetoActivityId) {
        this.vetoActivityId = vetoActivityId;
    }

    public BigInteger getStopBrickActivityId() {
        return stopBrickActivityId;
    }

    public void setStopBrickActivityId(BigInteger stopBrickActivityId) {
        this.stopBrickActivityId = stopBrickActivityId;
    }

    public float getTotalBlockingPoints() {
        return totalBlockingPoints;
    }

    public void setTotalBlockingPoints(float totalBlockingPoints) {
        this.totalBlockingPoints = totalBlockingPoints;
    }

    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if (!isDisabled() && CollectionUtils.containsAny(infoWrapper.getShift().getActivityIds(),getActivityIds()) && validationStartDate.minusDays(1).isBefore(DateUtils.asLocalDate(infoWrapper.getShift().getStartDate()))) {
            DateTimeInterval interval = getIntervalByNumberOfWeeks(infoWrapper.getShift(), numberOfWeeks, validationStartDate);
            int totalVeto = 0;
            int totalStopBricks = 0;
            List<ShiftWithActivityDTO> shifts = new ArrayList<>(infoWrapper.getShifts());
            shifts.add(infoWrapper.getShift());
            for (ShiftWithActivityDTO shift : shifts) {
                if(interval.contains(shift.getStartDate())){
                    if (shift.getActivityIds().contains(vetoActivityId)) {
                        totalVeto++;
                    } else if (shift.getActivityIds().contains(stopBrickActivityId)) {
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
        CollectionUtils.addIgnoreNull(activityIds, vetoActivityId);
        CollectionUtils.addIgnoreNull(activityIds, stopBrickActivityId);
        return activityIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!super.equals(o)) return false;
        VetoAndStopBricksWTATemplate that = (VetoAndStopBricksWTATemplate) o;
        return numberOfWeeks == that.numberOfWeeks &&
                Float.compare(that.totalBlockingPoints, totalBlockingPoints) == 0 &&
                Objects.equals(validationStartDate, that.validationStartDate) &&
                Objects.equals(vetoActivityId, that.vetoActivityId) &&
                Objects.equals(stopBrickActivityId, that.stopBrickActivityId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), numberOfWeeks, validationStartDate, vetoActivityId, stopBrickActivityId, totalBlockingPoints);
    }
}
