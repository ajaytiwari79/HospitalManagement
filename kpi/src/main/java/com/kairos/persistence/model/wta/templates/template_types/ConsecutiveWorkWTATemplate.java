package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE3
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ConsecutiveWorkWTATemplate extends WTABaseRuleTemplate {

    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.DAY);
    private Set<BigInteger> plannedTimeIds = new HashSet<>();
    private Set<BigInteger> timeTypeIds = new HashSet<>();
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;
    @Positive(message = "message.ruleTemplate.interval.notNull")
    private int intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private transient DateTimeInterval interval;

    public ConsecutiveWorkWTATemplate() {
        this.wtaTemplateType = WTATemplateType.CONSECUTIVE_WORKING_PARTOFDAY;
    }


}
