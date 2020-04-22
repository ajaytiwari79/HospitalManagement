package com.kairos.shiftplanning.domain.timetype;

import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.TimeTypes;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XStreamAlias("TimeType")
public class TimeType {


    private BigInteger id;
    private String name;
    private TimeTypeEnum timeTypeEnum;
    private boolean breakNotHeldValid;
    private TimeTypes timeTypes;

}
