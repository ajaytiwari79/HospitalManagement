package com.kairos.persistence.model;

import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ShiftActivity {
    private BigInteger activityId;
    private Date startDate;
    private Date endDate;
    private int scheduledMinutes;
    private int durationMinutes;
    private Integer startTime;
    private Integer endTime;
    private String activityName;
    private String shortName;
    private String ultraShortName;
    //used in T&A view
    private BigInteger reasonCodeId;
    //used for adding absence type of activities.
    private BigInteger absenceReasonCodeId;
    private String remarks;
    //please don't use this id for any functionality this on ly for frontend
    private BigInteger id;
    private String timeType;
    private String backgroundColor;
    private boolean breakReplaced;
    private int payoutCtaBonusMinutes;
    private int timeBankCtaBonusMinutes;
    private String startLocation; // this is for the location from where activity will gets starts
    private String endLocation;   // this is for the location from where activity will gets ends
    private int plannedMinutesOfTimebank;
    private int plannedMinutesOfPayout;
    private int scheduledMinutesOfTimebank;
    private int scheduledMinutesOfPayout;
    private List<PlannedTime> plannedTimes;
    private List<ShiftActivity> childActivities;
    private boolean breakNotHeld;
    private Set<ShiftStatus> status = new HashSet<>();
    private transient BigInteger plannedTimeId;
    private boolean breakInterrupt;
    private TimeTypeEnum secondLevelTimeType;
    private BigInteger timeTypeId;
    private String methodForCalculatingTime;
}
