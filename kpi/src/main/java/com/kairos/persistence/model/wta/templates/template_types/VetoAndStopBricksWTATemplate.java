package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.LocalDate;

import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE12
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class VetoAndStopBricksWTATemplate extends WTABaseRuleTemplate {

    @Positive(message = "message.ruleTemplate.weeks.notNull")
    private int numberOfWeeks;
    @NotNull(message = "message.ruleTemplate.weeks.notNull")
    private LocalDate validationStartDate;
    private BigInteger vetoActivityId;
    private BigInteger stopBrickActivityId;
    @Positive(message = "message.ruleTemplate.blocking.point")
    private float totalBlockingPoints; // It's for a duration from @validationStartDate  till the @numberOfWeeks
    private transient DateTimeInterval interval;

    public VetoAndStopBricksWTATemplate() {
        this.wtaTemplateType = WTATemplateType.VETO_AND_STOP_BRICKS;
    }



}
