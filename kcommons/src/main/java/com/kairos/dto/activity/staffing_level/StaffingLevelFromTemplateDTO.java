package com.kairos.dto.activity.staffing_level;
/*
 *Created By Pavan on 14/8/18
 *
 */

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class StaffingLevelFromTemplateDTO {

    private BigInteger templateId;
    private List<DateWiseActivityDTO> activitiesByDate;
}
