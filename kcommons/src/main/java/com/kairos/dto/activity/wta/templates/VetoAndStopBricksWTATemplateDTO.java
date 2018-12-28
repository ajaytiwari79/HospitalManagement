package com.kairos.dto.activity.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.LocalDate;


/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE12
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VetoAndStopBricksWTATemplateDTO extends WTABaseRuleTemplateDTO {

    @Positive(message = "message.ruleTemplate.weeks.notNull")
    private int numberOfWeeks;
    @NotNull(message = "message.ruleTemplate.weeks.notNull")
    private LocalDate validationStartDate;
    private BigInteger vetoActivityId;
    private BigInteger stopBrickActivityId;
    @Positive(message = "message.ruleTemplate.blocking.point")
    private float totalBlockingPoints; // It's for a duration from @validationStartDate  till the @numberOfWeeks

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

    public VetoAndStopBricksWTATemplateDTO(String name, boolean disabled,
                                           String description) {
        this.name = name;
        this.disabled = disabled;
        this.description = description;

    }
    public VetoAndStopBricksWTATemplateDTO() {
        this.wtaTemplateType = WTATemplateType.VETO_AND_STOP_BRICKS;;
    }

}
