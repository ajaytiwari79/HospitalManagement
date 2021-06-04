package com.kairos.dto.activity.unit_settings.activity_configuration;

import com.kairos.enums.PriorityFor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@Getter
@Setter
@NoArgsConstructor
public class ActivityRankingDTO {
    private BigInteger id;
    private Long expertiseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LinkedHashSet<BigInteger> fullDayActivities=new LinkedHashSet<>();
    private LinkedHashSet<BigInteger> fullWeekActivities=new LinkedHashSet<>();
    private LinkedHashSet<BigInteger> presenceActivities=new LinkedHashSet<>();
    private Long countryId;
    private boolean published;
    private Long unitId;
    private PriorityFor priorityFor;


    public ActivityRankingDTO(Long expertiseId, LocalDate startDate, LocalDate endDate, Long countryId, boolean published) {
        this.expertiseId = expertiseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.countryId = countryId;
        this.published = published;
    }
}
