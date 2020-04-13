package com.kairos.shiftplanning.domain.staff;

import com.kairos.shiftplanning.domain.activity.Activity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class BreakSettings{
    private Long countryId;
    private Long shiftDurationInMinute;
    private Long breakDurationInMinute;
    private Long expertiseId;
    private Activity activity;
    private boolean primary;
    private boolean includeInPlanning;

}
