package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class StaffingLevelPublishDTO {
    @DateTimeFormat(pattern="yyyy-MM-dd")
    Date startDate;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate;
    private List<BigInteger> activityIds=new ArrayList<>();
    private List<Long> skillIds=new ArrayList<>();
}
