package com.kairos.persistence.model.staffing_level;
/*
 *Created By Pavan on 9/10/18
 *
 */

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
public class StaffingLevelActivityRank extends MongoBaseEntity {
    private BigInteger activityId;
    private LocalDate staffingLevelDate;
    private BigInteger staffingLevelId;
    private int rank;
}
