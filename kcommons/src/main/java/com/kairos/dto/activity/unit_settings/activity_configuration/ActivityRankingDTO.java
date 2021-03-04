package com.kairos.dto.activity.unit_settings.activity_configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class ActivityRankingDTO {
    private BigInteger id;
    private Long expertiseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<BigInteger> fullDayActivities=new LinkedHashSet<>();
    private Set<BigInteger> fullWeekActivities=new LinkedHashSet<>();
    private Set<BigInteger> presenceActivities=new LinkedHashSet<>();
    private Long unitId;
    private Long countryId;
    private boolean published;

    public ActivityRankingDTO(Long expertiseId, LocalDate startDate, LocalDate endDate, Long countryId, boolean published) {
        this.expertiseId = expertiseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.countryId = countryId;
        this.published = published;
    }
}
