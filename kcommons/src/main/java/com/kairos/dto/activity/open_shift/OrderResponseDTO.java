package com.kairos.dto.activity.open_shift;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Getter
@Setter
public class OrderResponseDTO {
    private Long unitId;
    private BigInteger activityId;
    private Long plannedTimeId;
    private Long contactPersonId;
    private String contactPersonMobile;
    private LocalDate StartDate;
    private LocalDate endDate;
    private LocalTime fromTime;
    private LocalTime toTime;
    private Set<WeekType> weekType;
    private List<DayOfWeek> dayType = new ArrayList<DayOfWeek>();
    private Long reasonCodeId;
    private String noteForPlanner;
    private Long expertiseId;
    private List<Long> skillLevel = new ArrayList<Long>();
    private List<Long> functionIds;
    private boolean acuteOrder;
    private StartFrom startFrom;
    private Integer priority;
    private Integer feedback;
    private String noteForCandidate;
    private LocalDate deadline;
    private OrderNotificationsCriteriaForPlanner notificationsCriteria;
    private ShiftAssignmentCriteria shiftAssignmentCriteria;
    private BringVehicle bringVehicle;
    private DeadlineRule deadlineRule;
    private BigInteger id;
    private PlannerNotificationInfo plannerNotificationInfo;
}
