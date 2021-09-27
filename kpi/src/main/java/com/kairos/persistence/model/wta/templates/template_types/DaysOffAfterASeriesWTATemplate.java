package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import static com.kairos.commons.utils.DateUtils.asLocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DaysOffAfterASeriesWTATemplate extends WTABaseRuleTemplate {

    private static final long serialVersionUID = -7145432848747779050L;
    @Positive
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private int nightShiftSequence;
    private boolean restingTimeAllowed;
    private int restingTime;
    private transient DateTimeInterval interval;

    public DaysOffAfterASeriesWTATemplate() {
        this.wtaTemplateType = WTATemplateType.DAYS_OFF_AFTER_A_SERIES;
    }


}
