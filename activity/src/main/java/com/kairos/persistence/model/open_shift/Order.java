package com.kairos.persistence.model.open_shift;

import com.kairos.dto.activity.open_shift.*;
import com.kairos.persistence.model.common.MongoBaseEntity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Document
public class Order extends MongoBaseEntity {

    private Long unitId;
    private BigInteger activityId;
    private Long plannedTimeId;
    private Long contactPersonId;
    private String contactPersonMobile;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime fromTime;
    private LocalTime toTime;
    private WeekType weekType;
    private List<DayOfWeek> dayType;
    private Long reasonCodeId;
    private String noteForPlanner;
    private Long expertiseId;
    private List<Long> skillLevel;
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
    private PlannerNotificationInfo plannerNotificationInfo;



    public List<DayOfWeek> getDayType() {
        return dayType;
    }

    public void setDayType(List<DayOfWeek> dayType) {
        this.dayType = dayType;
    }

    public ShiftAssignmentCriteria getShiftAssignmentCriteria() {
        return shiftAssignmentCriteria;
    }

    public void setShiftAssignmentCriteria(ShiftAssignmentCriteria shiftAssignmentCriteria) {
        this.shiftAssignmentCriteria = shiftAssignmentCriteria;
    }


    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
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
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
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

    public WeekType getWeekType() {
        return weekType;
    }

    public void setWeekType(WeekType weekType) {
        this.weekType = weekType;
    }


    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }

    public String getNoteForPlanner() {
        return noteForPlanner;
    }

    public void setNoteForPlanner(String noteForPlanner) {
        this.noteForPlanner = noteForPlanner;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public List<Long> getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(List<Long> skillLevel) {
        this.skillLevel = skillLevel;
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



    public BringVehicle getBringVehicle() {
        return bringVehicle;
    }

    public void setBringVehicle(BringVehicle bringVehicle) {
        this.bringVehicle = bringVehicle;
    }

    public DeadlineRule getDeadlineRule() {
        return deadlineRule;
    }

    public void setDeadlineRule(DeadlineRule deadlineRule) {
        this.deadlineRule = deadlineRule;
    }
    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }


    public List<Long> getFunctionIds() {
        return functionIds;
    }

    public void setFunctionIds(List<Long> functionIds) {
        this.functionIds = functionIds;
    }

    public PlannerNotificationInfo getPlannerNotificationInfo() {
        return plannerNotificationInfo;
    }

    public void setPlannerNotificationInfo(PlannerNotificationInfo plannerNotificationInfo) {
        this.plannerNotificationInfo = plannerNotificationInfo;
    }

}
