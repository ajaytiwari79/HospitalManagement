package com.kairos.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class NoOfSequenceShiftWTATemplate extends WTABaseRuleTemplate{

    //private int sequence;
    @Positive
    private long intervalLength;
    @NotEmpty(message = "message.ruleTemplate.interval.notNull")
    private String intervalUnit;
    private boolean restingTimeAllowed;
    private int restingTime;
    private PartOfDay sequenceShiftFrom;
    private PartOfDay sequenceShiftTo;
    private transient DateTimeInterval interval;

    private List<BigInteger> timeTypeIds = new ArrayList<>();

    public NoOfSequenceShiftWTATemplate() {
        wtaTemplateType=WTATemplateType.NO_OF_SEQUENCE_SHIFT;
    }



}
