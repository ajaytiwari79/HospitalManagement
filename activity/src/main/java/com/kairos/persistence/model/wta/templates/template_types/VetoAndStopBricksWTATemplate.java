package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getIntervalByNumberOfWeeks;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.validateVetoAndStopBrickRules;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE12
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VetoAndStopBricksWTATemplate extends WTABaseRuleTemplate {

    @Positive(message = "message.ruleTemplate.weeks.notNull")
    private int numberOfWeeks;
    @NotNull(message = "message.ruleTemplate.weeks.notNull")
    private LocalDate validationStartDate;
    private BigInteger vetoActivityId;
    private BigInteger stopBrickActivityId;
    @Positive(message = "message.ruleTemplate.blocking.point")
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
            DateTimeInterval interval = getIntervalByNumberOfWeeks(infoWrapper.getShift().getStartDate(), numberOfWeeks, validationStartDate,infoWrapper.getLastPlanningPeriodEndDate());
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
                WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation =
                        new WorkTimeAgreementRuleViolation(this.id, this.name, null, true, false,null,
                                DurationType.DAYS,String.valueOf(totalBlockingPoints));
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
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplate) wtaBaseRuleTemplate;
        return (this != vetoAndStopBricksWTATemplate) && !(numberOfWeeks == vetoAndStopBricksWTATemplate.numberOfWeeks &&
                Float.compare(vetoAndStopBricksWTATemplate.totalBlockingPoints, totalBlockingPoints) == 0 &&
                Objects.equals(validationStartDate, vetoAndStopBricksWTATemplate.validationStartDate) &&
                Objects.equals(vetoActivityId, vetoAndStopBricksWTATemplate.vetoActivityId) &&
                Objects.equals(stopBrickActivityId, vetoAndStopBricksWTATemplate.stopBrickActivityId));
    }

}
