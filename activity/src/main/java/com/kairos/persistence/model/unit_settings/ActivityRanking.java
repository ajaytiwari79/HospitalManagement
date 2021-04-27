package com.kairos.persistence.model.unit_settings;

import com.kairos.enums.PriorityFor;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class ActivityRanking extends MongoBaseEntity {
    private static final long serialVersionUID = -3722777805221769643L;
    private Long expertiseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<BigInteger> fullDayActivities=new LinkedHashSet<>();
    private Set<BigInteger> fullWeekActivities=new LinkedHashSet<>();
    private Set<BigInteger> presenceActivities=new LinkedHashSet<>();
    private Long unitId;
    private Long countryId;
    private boolean published;
    // it's used to check in case of having draft copy
    private BigInteger draftId;
    private PriorityFor priorityFor;

    public ActivityRanking(Long expertiseId, LocalDate startDate, LocalDate endDate, Set<BigInteger> fullDayActivities, Set<BigInteger> fullWeekActivities, Long countryId, boolean published){
        this.expertiseId = expertiseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fullDayActivities = fullDayActivities;
        this.fullWeekActivities = fullWeekActivities;
        this.countryId = countryId;
        this.published = published;
    }

    public ActivityRanking(LocalDate startDate, LocalDate endDate, Set<BigInteger> presenceActivities, Long unitId, boolean published){
        this.startDate = startDate;
        this.endDate = endDate;
        this.presenceActivities = presenceActivities;
        this.unitId = unitId;
        this.published = published;
    }

}
