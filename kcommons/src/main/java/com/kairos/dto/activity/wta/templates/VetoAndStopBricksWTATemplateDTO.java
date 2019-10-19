package com.kairos.dto.activity.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public class VetoAndStopBricksWTATemplateDTO extends WTABaseRuleTemplateDTO {

    @Positive(message = "message.ruleTemplate.weeks.notNull")
    private int numberOfWeeks;
    @NotNull(message = "message.ruleTemplate.weeks.notNull")
    private LocalDate validationStartDate;
    private BigInteger vetoActivityId;
    private BigInteger stopBrickActivityId;
    @Positive(message = "message.ruleTemplate.blocking.point")
    private float totalBlockingPoints; // It's for a duration from @validationStartDate  till the @numberOfWeeks


}
