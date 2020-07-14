package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;

@Getter
@Setter
public class StaffingLevelPublishDTO {
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate;
    private Set<LocalDate> weekDates;
    private LocalDate selectedDate;

    private Set<BigInteger> activityIds=new HashSet<>();
    private Set<Long> skillIds=new HashSet<>();
    private boolean publishAbsence;
}
