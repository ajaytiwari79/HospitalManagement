package com.kairos.persistence.model.unit_settings;

import com.kairos.enums.PriorityFor;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
public class AbsenceRankingSettings extends MongoBaseEntity {
    private Long expertiseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<BigInteger,Integer> activityRankings=new HashMap<>();
    private Long countryId;
    private boolean published;
    // it's used to check in case of having draft copy
    private BigInteger draftId;
    private Long unitId;
    private PriorityFor priorityFor;

}
