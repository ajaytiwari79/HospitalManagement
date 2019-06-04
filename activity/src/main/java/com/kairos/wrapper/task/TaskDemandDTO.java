package com.kairos.wrapper.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.task_demand.MonthlyFrequency;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.persistence.model.task_demand.TaskDemandVisit;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static com.kairos.constants.ActivityMessagesConstants.ERROR_TASKDEMAND_PRIORITY_RANGE;
import static com.kairos.constants.ActivityMessagesConstants.ERROR_TASKDEMAND_STAFFCOUNT_MINIMUMONE;

/**
 * Created by oodles on 31/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDemandDTO {
    //@NotNull(message = "error.TaskDemand.taskTypeId.notnull") @NotEmpty(message = "error.TaskDemand.taskTypeId.blank")
    protected BigInteger taskTypeId;



    protected boolean needRehabilitation;

    @Min(value = 1, message = ERROR_TASKDEMAND_STAFFCOUNT_MINIMUMONE)
    protected int staffCount;
    protected TaskDemand.Status status = TaskDemand.Status.VISITATED;
    @NotNull(message = "error.TaskDemand.startDate.notnull")
    protected Date startDate;

   // @NotNull(message = "error.TaskDemand.endDate.notnull")
    protected Date endDate;
    protected Date nextVisit;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected TaskDemand.WeekFrequency weekdayFrequency;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected List<TaskDemandVisit> weekdayVisits;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected long weekdaySupplierId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected TaskDemand.WeekFrequency weekendFrequency;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected List<TaskDemandVisit> weekendVisits;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected long weekendSupplierId;

    protected long lastModifiedByStaffId;
    protected List<String> demandImages;
    protected boolean isShift;

    @NotNull(message = "error.TaskDemand.unitId.notnull")
    protected long unitId;
    @NotNull
    private String dayName;

    @NotNull(message = "error.TaskDemand.citizenId.notnull")
    protected long citizenId;

    protected long createdByStaffId; // Who is creating Task Demand

    @Range(min=1, max=4, message = ERROR_TASKDEMAND_PRIORITY_RANGE)
    protected int priority;

    protected String remarks;

    protected boolean isDeleted = false;

    protected String visitourTeamId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected TaskDemand.RecurrencePattern recurrencePattern;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected long dailyFrequency;


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected MonthlyFrequency monthlyFrequency;

    protected int endAfterOccurrence;

    protected int setupDuration;

    // Constructors
    public TaskDemandDTO() {
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public int getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(int staffCount) {
        this.staffCount = staffCount;
    }

    public TaskDemand.Status getStatus() {
        return status;
    }

    public void setStatus(TaskDemand.Status status) {
        this.status = status;
    }

    public List<TaskDemandVisit> getWeekdayVisits() {
        return weekdayVisits;
    }

    public void setWeekdayVisits(List<TaskDemandVisit> weekdayVisits) {
        this.weekdayVisits = weekdayVisits;
    }

    public List<TaskDemandVisit> getWeekendVisits() {
        return weekendVisits;
    }

    public void setWeekendVisits(List<TaskDemandVisit> weekendVisits) {
        this.weekendVisits = weekendVisits;
    }

    public BigInteger getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(BigInteger taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public boolean isNeedRehabilitation() {
        return needRehabilitation;
    }

    public void setNeedRehabilitation(boolean needRehabilitation) {
        this.needRehabilitation = needRehabilitation;
    }

    public TaskDemand.WeekFrequency getWeekdayFrequency() {
        return weekdayFrequency;
    }

    public void setWeekdayFrequency(TaskDemand.WeekFrequency weekdayFrequency) {
        this.weekdayFrequency = weekdayFrequency;
    }

    public long getWeekdaySupplierId() {
        return weekdaySupplierId;
    }

    public void setWeekdaySupplierId(long weekdaySupplierId) {
        this.weekdaySupplierId = weekdaySupplierId;
    }

    public TaskDemand.WeekFrequency getWeekendFrequency() {
        return weekendFrequency;
    }

    public void setWeekendFrequency(TaskDemand.WeekFrequency weekendFrequency) {
        this.weekendFrequency = weekendFrequency;
    }

    public long getWeekendSupplierId() {
        return weekendSupplierId;
    }

    public void setWeekendSupplierId(long weekendSupplierId) {
        this.weekendSupplierId = weekendSupplierId;
    }

    public long getLastModifiedByStaffId() {
        return lastModifiedByStaffId;
    }

    public void setLastModifiedByStaffId(long lastModifiedByStaffId) {
        this.lastModifiedByStaffId = lastModifiedByStaffId;
    }

    public List<String> getDemandImages() {
        return demandImages;
    }

    public void setDemandImages(List<String> demandImages) {
        this.demandImages = demandImages;
    }

    public boolean isShift() {
        return isShift;
    }

    public void setShift(boolean shift) {
        isShift = shift;
    }

    public long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(long citizenId) {
        this.citizenId = citizenId;
    }

    public long getCreatedByStaffId() {
        return createdByStaffId;
    }

    public void setCreatedByStaffId(long createdByStaffId) {
        this.createdByStaffId = createdByStaffId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getVisitourTeamId() {
        return visitourTeamId;
    }

    public void setVisitourTeamId(String visitourTeamId) {
        this.visitourTeamId = visitourTeamId;
    }

    public TaskDemand.RecurrencePattern getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(TaskDemand.RecurrencePattern recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public long getDailyFrequency() {
        return dailyFrequency;
    }

    public void setDailyFrequency(long dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
    }


    public MonthlyFrequency getMonthlyFrequency() {
        return monthlyFrequency;
    }

    public void setMonthlyFrequency(MonthlyFrequency monthlyFrequency) {
        this.monthlyFrequency = monthlyFrequency;
    }

    public int getEndAfterOccurrence() {
        return endAfterOccurrence;
    }

    public void setEndAfterOccurrence(int endAfterOccurrence) {
        this.endAfterOccurrence = endAfterOccurrence;
    }

    @Override
    public String toString() {
        return "TaskDemandDTO{" +
                "taskTypeId=" + taskTypeId +
                ", needRehabilitation=" + needRehabilitation +
                ", staffCount=" + staffCount +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", nextVisit=" + nextVisit +
                ", weekdayFrequency=" + weekdayFrequency +
                ", weekdayVisits=" + weekdayVisits +
                ", weekdaySupplierId=" + weekdaySupplierId +
                ", weekendFrequency=" + weekendFrequency +
                ", weekendVisits=" + weekendVisits +
                ", weekendSupplierId=" + weekendSupplierId +
                ", lastModifiedByStaffId=" + lastModifiedByStaffId +
                ", demandImages=" + demandImages +
                ", isShift=" + isShift +
                ", unitId=" + unitId +
                ", dayName='" + dayName + '\'' +
                ", citizenId=" + citizenId +
                ", createdByStaffId=" + createdByStaffId +
                ", priority=" + priority +
                ", remarks='" + remarks + '\'' +
                ", isDeleted=" + isDeleted +
                ", visitourTeamId='" + visitourTeamId + '\'' +
                '}';
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getNextVisit() {
        return nextVisit;
    }

    public void setNextVisit(Date nextVisit) {
        this.nextVisit = nextVisit;
    }

    public int getSetupDuration() {
        return setupDuration;
    }

    public void setSetupDuration(int setupDuration) {
        this.setupDuration = setupDuration;
    }
}
