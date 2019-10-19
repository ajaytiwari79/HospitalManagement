package com.kairos.dto.activity.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.WTATemplateType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class NoOfSequenceShiftWTATemplateDTO extends WTABaseRuleTemplateDTO
{
    private int sequence;
    private boolean restingTimeAllowed;
    private int restingTime;
    private PartOfDay sequenceShiftFrom;
    private PartOfDay sequenceShiftTo;

    private List<BigInteger> plannedTimeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();

    public NoOfSequenceShiftWTATemplateDTO() {
        wtaTemplateType= WTATemplateType.NO_OF_SEQUENCE_SHIFT;
    }


}
