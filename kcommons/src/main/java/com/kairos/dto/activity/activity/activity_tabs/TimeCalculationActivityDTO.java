package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.TimeCalaculationType;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Created by vipul on 23/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TimeCalculationActivityDTO {

    private Long activityId;
    private String methodForCalculatingTime;
    private TimeCalaculationType fullDayCalculationType;
    private TimeCalaculationType fullWeekCalculationType;
    private Boolean allowBreakReduction;
    private Long fixedTimeValue;
    private String methodForCalculatingTimeInMonths;
    private List<String> balanceType;
    private Boolean multiplyWith;
    private Double multiplyWithValue;
    private Boolean multiplyByVacationFactor;
    private Boolean multiplyByFinalSchedule;
    private String breakTemplates;
    private List<Long> dayTypes;
    private DayOfWeek fullWeekStart;
    private DayOfWeek fullWeekEnd;
    private int historyDuration;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime defaultStartTime;
    private boolean availableAllowActivity;
}
