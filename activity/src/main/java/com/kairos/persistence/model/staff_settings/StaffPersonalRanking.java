package com.kairos.persistence.model.staff_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.time.LocalDate;


public class StaffPersonalRanking extends MongoBaseEntity {
    private Long staffId;
    private Long teamId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int ranking;
    private BigInteger activityId;
}
