package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kairos.enums.shift.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author pradeep
 * @date - 26/9/18
 */
@Getter
@Setter
public class ShiftTemplateActivity implements Serializable {

    private BigInteger activityId;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private int scheduledMinutes;
    private int durationMinutes;
    private String activityName;
    private long bid;
    private long pId;
    private String remarks;
    private BigInteger id;
    private String timeType;
    private String backgroundColor;
    private boolean haltBreak;
    private BigInteger plannedTimeId;
    private Set<ShiftStatus> status = new HashSet<>(Arrays.asList(ShiftStatus.REQUEST));
    private List<ShiftTemplateActivity> childActivities;
    private BigInteger absenceReasonCodeId;

}
