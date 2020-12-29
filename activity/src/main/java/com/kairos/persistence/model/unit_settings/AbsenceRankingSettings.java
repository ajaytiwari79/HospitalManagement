package com.kairos.persistence.model.unit_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;

public class AbsenceRankingSettings extends MongoBaseEntity {
    private Long expertiseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<BigInteger,Integer> activityRankings;

}
