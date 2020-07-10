package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.util.*;

@Getter
@Setter
public class StaffingLevelPublishDTO {
    @DateTimeFormat(pattern="yyyy-MM-dd")
    Date startDate;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate;
    private Set<BigInteger> activityIds=new HashSet<>();
    private Set<Long> skillIds=new HashSet<>();
}
