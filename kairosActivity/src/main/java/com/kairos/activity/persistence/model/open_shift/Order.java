package com.kairos.activity.persistence.model.open_shift;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.response.dto.web.open_shift.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Document
public class Order extends MongoBaseEntity {
    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    private Long unitId;
    private Long activityId;
    private Long plannedTimeId;
    private Long contactPersonId;
    private String contactPersonMobile;
    private LocalDate StartDate;
    private LocalDate endDate;
    private LocalTime fromTime;
    private LocalTime toTime;
    private Integer MinOpenShiftHours;
    private WeekType weekType;
    private DayTypeOrder dayType;
    private Long reasonCodeId;
    private String notForPlanner;
    private Long experitiseId;
    private List<Long> skillLevel;
    private Long functionId;
    private boolean acuteOrder;
    private StartFrom startFrom;
    private OpenShiftPrioirty priority;
    private Integer feedback;
    private String noteForCandidate;
    private LocalDate deadline;
    private OrderNotificationsCriteriaForPlanner notificationsCriteria;
    private ShiftAssignmentCriteria siftAssignmentCriteria;
    private BringYour bringYour;
    private DeadlineRule deadlineRule;
    private ShiftAssignmentCriteria shiftAssignmentCriteria;


    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getPlannedTimeId() {
        return plannedTimeId;
    }

    public void setPlannedTimeId(Long plannedTimeId) {
        this.plannedTimeId = plannedTimeId;
    }

    public Long getContactPersonId() {
        return contactPersonId;
    }

    public void setContactPersonId(Long contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public String getContactPersonMobile() {
        return contactPersonMobile;
    }

    public void setContactPersonMobile(String contactPersonMobile) {
        this.contactPersonMobile = contactPersonMobile;
    }

    public LocalDate getStartDate() {
        return StartDate;
    }

    public void setStartDate(LocalDate startDate) {
        StartDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }

    public Integer getMinOpenShiftHours() {
        return MinOpenShiftHours;
    }

    public void setMinOpenShiftHours(Integer minOpenShiftHours) {
        MinOpenShiftHours = minOpenShiftHours;
    }

    public WeekType getWeekType() {
        return weekType;
    }

    public void setWeekType(WeekType weekType) {
        this.weekType = weekType;
    }

    public DayTypeOrder getDayType() {
        return dayType;
    }

    public void setDayType(DayTypeOrder dayType) {
        this.dayType = dayType;
    }

    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }

    public String getNotForPlanner() {
        return notForPlanner;
    }

    public void setNotForPlanner(String notForPlanner) {
        this.notForPlanner = notForPlanner;
    }

    public Long getExperitiseId() {
        return experitiseId;
    }

    public void setExperitiseId(Long experitiseId) {
        this.experitiseId = experitiseId;
    }

    public List<Long> getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(List<Long> skillLevel) {
        this.skillLevel = skillLevel;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public boolean isAcuteOrder() {
        return acuteOrder;
    }

    public void setAcuteOrder(boolean acuteOrder) {
        this.acuteOrder = acuteOrder;
    }

    public StartFrom getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(StartFrom startFrom) {
        this.startFrom = startFrom;
    }

    public OpenShiftPrioirty getPriority() {
        return priority;
    }

    public void setPriority(OpenShiftPrioirty priority) {
        this.priority = priority;
    }

    public Integer getFeedback() {
        return feedback;
    }

    public void setFeedback(Integer feedback) {
        this.feedback = feedback;
    }

    public String getNoteForCandidate() {
        return noteForCandidate;
    }

    public void setNoteForCandidate(String noteForCandidate) {
        this.noteForCandidate = noteForCandidate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public OrderNotificationsCriteriaForPlanner getNotificationsCriteria() {
        return notificationsCriteria;
    }

    public void setNotificationsCriteria(OrderNotificationsCriteriaForPlanner notificationsCriteria) {
        this.notificationsCriteria = notificationsCriteria;
    }

    public ShiftAssignmentCriteria getSiftAssignmentCriteria() {
        return siftAssignmentCriteria;
    }

    public void setSiftAssignmentCriteria(ShiftAssignmentCriteria siftAssignmentCriteria) {
        this.siftAssignmentCriteria = siftAssignmentCriteria;
    }

    public BringYour getBringYour() {
        return bringYour;
    }

    public void setBringYour(BringYour bringYour) {
        this.bringYour = bringYour;
    }

    public DeadlineRule getDeadlineRule() {
        return deadlineRule;
    }

    public void setDeadlineRule(DeadlineRule deadlineRule) {
        this.deadlineRule = deadlineRule;
    }

    public ShiftAssignmentCriteria getShiftAssignmentCriteria() {
        return shiftAssignmentCriteria;
    }

    public void setShiftAssignmentCriteria(ShiftAssignmentCriteria shiftAssignmentCriteria) {
        this.shiftAssignmentCriteria = shiftAssignmentCriteria;
    }




}
