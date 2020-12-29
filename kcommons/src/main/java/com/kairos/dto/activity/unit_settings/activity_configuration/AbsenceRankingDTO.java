package com.kairos.dto.activity.unit_settings.activity_configuration;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
public class AbsenceRankingDTO {
    private BigInteger id;
    private Long expertiseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<BigInteger,Integer> activityRankings;

    @AssertTrue(message = "Rank Must be Unique")
    public boolean isValid() {
        List<Integer> valuesList = new ArrayList<>(activityRankings.values());
        Set<Integer> valuesSet = new HashSet<>(activityRankings.values());
        return valuesList.size()==valuesSet.size();
    }
}
