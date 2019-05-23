package com.kairos.persistence.model.task_demand;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static com.kairos.constants.ActivityMessagesConstants.ERROR_TASKDEMAND_PRIORITY_RANGE;
import static com.kairos.constants.ActivityMessagesConstants.ERROR_TASKDEMAND_STAFFCOUNT_MINIMUMONE;
import static com.kairos.persistence.model.task_demand.TaskDemand.Status.VISITATED;

/**
 * Created by prabjot on 14/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "task_demands")
public class TaskDemand extends MongoBaseEntity {

    //@NotNull(message = "error.TaskDemand.taskTypeId.notnull") @NotEmpty(message = "error.TaskDemand.taskTypeId.blank")
    @Indexed
    protected BigInteger taskTypeId;

    @NotNull(message = "error.TaskDemand.startDate.notnull")
    protected Date startDate;

    //@NotNull(message = "error.TaskDemand.endDate.notnull")
    protected Date endDate;

    protected boolean needRehabilitation;

    @Min(value = 1, message = ERROR_TASKDEMAND_STAFFCOUNT_MINIMUMONE)
    protected int staffCount;
    protected Status status = VISITATED;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected WeekFrequency weekdayFrequency;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected List<TaskDemandVisit> weekdayVisits;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected long weekdaySupplierId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected WeekFrequency weekendFrequency;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected List<TaskDemandVisit> weekendVisits;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected long weekendSupplierId;

    protected Date nextVisit;
    protected long lastModifiedByStaffId;
    protected List<String> demandImages;
    protected boolean isShift;

    @NotNull(message = "error.TaskDemand.unitId.notnull")
    protected long unitId;

    @NotNull(message = "error.TaskDemand.citizenId.notnull")
    protected long citizenId;

    protected long createdByStaffId; // Who is creating Task Demand

    @Range(min=1, max=4, message = ERROR_TASKDEMAND_PRIORITY_RANGE)
    protected int priority;

    protected String remarks;

    protected boolean isDeleted = false;

    protected String visitourTeamId;

    private String kmdExternalId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected RecurrencePattern recurrencePattern;


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected long dailyFrequency;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected MonthlyFrequency monthlyFrequency;

    protected int endAfterOccurrence;

    protected int setupDuration;

    private Date taskCreatedTillDate;


    public TaskDemand() {
        //Default Constructor
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public boolean isNeedRehabilitation() {
        return needRehabilitation;
    }

    public void setNeedRehabilitation(boolean needRehabilitation) {
        this.needRehabilitation = needRehabilitation;
    }

    public WeekFrequency getWeekdayFrequency() {
        return weekdayFrequency;
    }

    public void setWeekdayFrequency(WeekFrequency weekdayFrequency) {
        this.weekdayFrequency = weekdayFrequency;
    }

    public long getWeekdaySupplierId() {
        return weekdaySupplierId;
    }

    public void setWeekdaySupplierId(long weekdaySupplierId) {
        this.weekdaySupplierId = weekdaySupplierId;
    }

    public WeekFrequency getWeekendFrequency() {
        return weekendFrequency;
    }

    public void setWeekendFrequency(WeekFrequency weekendFrequency) {
        this.weekendFrequency = weekendFrequency;
    }

    public long getWeekendSupplierId() {
        return weekendSupplierId;
    }

    public void setWeekendSupplierId(long weekendSupplierId) {
        this.weekendSupplierId = weekendSupplierId;
    }

    public Date getNextVisit() {
        return nextVisit;
    }

    public void setNextVisit(Date nextVisit) {
        this.nextVisit = nextVisit;
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

    public String getKmdExternalId() {
        return kmdExternalId;
    }

    public void setKmdExternalId(String kmdExternalId) {
        this.kmdExternalId = kmdExternalId;
    }

    public RecurrencePattern getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(RecurrencePattern recurrencePattern) {
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

    public int getSetupDuration() {
        return setupDuration;
    }

    public void setSetupDuration(int setupDuration) {
        this.setupDuration = setupDuration;
    }

    public Date getTaskCreatedTillDate() {
        return taskCreatedTillDate;
    }

    public void setTaskCreatedTillDate(Date taskCreatedTillDate) {
        this.taskCreatedTillDate = taskCreatedTillDate;
    }

    public enum WeekFrequency{

        ONE_WEEK("One week"),
        TWO_WEEK("Two week"),
        THREE_WEEK("Three week"),
        FOUR_WEEK("Four week");


        public String value;

        WeekFrequency(String value) {
            this.value = value;
        }

        public static WeekFrequency getByValue(String value){
            for(WeekFrequency weekFrequency : WeekFrequency.values()){
                if(weekFrequency.value.equals(value)){
                    return weekFrequency;
                }
            }
            return null;
        }
    }

    public enum Status{

        VISITATED("Visitated"), //When demand is created
        GENERATED("Generated"), //When demand dragged&dropped in planner
        UPDATED("Updated"),     //When Demand is updated (assign this status, when demand's status is generated while updating it)
        PLANNED("Planned"),     //When any of the task is planned
        DELIVERED("Delivered"); //When any of the task is delivered


        public String value;

        Status(String value) {
            this.value = value;
        }

        public static Status getByValue(String value){
            for(Status status : Status.values()){
                if(status.value.equals(value)){
                    return status;
                }
            }
            return null;
        }
    }

    public enum RecurrencePattern{

        DAILY("Daily"),
        WEEKLY("Weekly"),
        MONTHLY("Monthly");
        public String value;

        RecurrencePattern(String value) {
            this.value = value;
        }

        public static RecurrencePattern getByValue(String value){
            for(RecurrencePattern recurrencePattern : RecurrencePattern.values()){
                if(recurrencePattern.value.equals(value)){
                    return recurrencePattern;
                }
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "{TaskDemand={" +
                "taskTypeId=" + taskTypeId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", needRehabilitation=" + needRehabilitation +
                ", staffCount=" + staffCount +
                ", status=" + status +
                ", weekdayFrequency=" + weekdayFrequency +
                ", weekdayVisits=" + weekdayVisits +
                ", weekdaySupplierId=" + weekdaySupplierId +
                ", weekendFrequency=" + weekendFrequency +
                ", weekendVisits=" + weekendVisits +
                ", weekendSupplierId=" + weekendSupplierId +
                ", nextVisit=" + nextVisit +
                ", lastModifiedByStaffId=" + lastModifiedByStaffId +
                ", demandImages=" + demandImages +
                ", isShift=" + isShift +
                ", unitId=" + unitId +
                ", citizenId=" + citizenId +
                ", createdByStaffId=" + createdByStaffId +
                ", priority=" + priority +
                ", remarks='" + remarks + '\'' +
                ", isDeleted=" + isDeleted +
                ", visitourTeamId='" + visitourTeamId + '\'' +
                '}'+
                '}';
    }
}
