package com.planner.domain.task;

import com.planner.domain.common.BaseEntity;
import com.planner.enums.TaskStatus;

import java.util.Date;
import java.util.List;

//import org.springframework.data.cassandra.core.mapping.Table;

//@Table
public class PlanningTask extends BaseEntity {

    private String tasktypeId;
    private String taskName;
    private int priority;
    private String citizenId;
    private TaskStatus status;
    private String locationId;
    private int durationInMin;
    private Date firstStartDateTime;
    private Date firstEndDateTime;
    private List<String> skillWithIds;
    private Date secondStartDateTime;
    private Date secondEndDateTime;
    private int firstStartSlaDurationMin;
    private int secondStartSlaDurationMin;
    private int firstEndSlaDurationMin;
    private int secondEndSlaDurationMin;
    private String relatedTaskid;
    private boolean isMultiStaffTask;
    private boolean isEnabled;

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public List<String> getSkillWithIds() {
        return skillWithIds;
    }

    public void setSkillWithIds(List<String> skillWithIds) {
        this.skillWithIds = skillWithIds;
    }

    public PlanningTask() {
        super();
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public boolean isMultiStaffTask() {
        return isMultiStaffTask;
    }

    public void setMultiStaffTask(boolean multiStaffTask) {
        isMultiStaffTask = multiStaffTask;
    }

    public String getRelatedTaskid() {
        return relatedTaskid;
    }

    public void setRelatedTaskid(String relatedTaskid) {
        this.relatedTaskid = relatedTaskid;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getTasktypeId() {
        return tasktypeId;
    }

    public void setTasktypeId(String tasktypeId) {
        this.tasktypeId = tasktypeId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public int getDurationInMin() {
        return durationInMin;
    }

    public void setDurationInMin(int durationInMin) {
        this.durationInMin = durationInMin;
    }

    public Date getFirstStartDateTime() {
        return firstStartDateTime;
    }

    public void setFirstStartDateTime(Date firstStartDateTime) {
        this.firstStartDateTime = firstStartDateTime;
    }

    public Date getFirstEndDateTime() {
        return firstEndDateTime;
    }

    public void setFirstEndDateTime(Date firstEndDateTime) {
        this.firstEndDateTime = firstEndDateTime;
    }

    public Date getSecondStartDateTime() {
        return secondStartDateTime;
    }

    public void setSecondStartDateTime(Date secondStartDateTime) {
        this.secondStartDateTime = secondStartDateTime;
    }

    public Date getSecondEndDateTime() {
        return secondEndDateTime;
    }

    public void setSecondEndDateTime(Date secondEndDateTime) {
        this.secondEndDateTime = secondEndDateTime;
    }

    public int getFirstStartSlaDurationMin() {
        return firstStartSlaDurationMin;
    }

    public void setFirstStartSlaDurationMin(int firstStartSlaDurationMin) {
        this.firstStartSlaDurationMin = firstStartSlaDurationMin;
    }

    public int getSecondStartSlaDurationMin() {
        return secondStartSlaDurationMin;
    }

    public void setSecondStartSlaDurationMin(int secondStartSlaDurationMin) {
        this.secondStartSlaDurationMin = secondStartSlaDurationMin;
    }

    public int getFirstEndSlaDurationMin() {
        return firstEndSlaDurationMin;
    }

    public void setFirstEndSlaDurationMin(int firstEndSlaDurationMin) {
        this.firstEndSlaDurationMin = firstEndSlaDurationMin;
    }

    public int getSecondEndSlaDurationMin() {
        return secondEndSlaDurationMin;
    }

    public void setSecondEndSlaDurationMin(int secondEndSlaDurationMin) {
        this.secondEndSlaDurationMin = secondEndSlaDurationMin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanningTask that = (PlanningTask) o;

        if (priority != that.priority) return false;
        if (citizenId != that.citizenId) return false;
        if (durationInMin != that.durationInMin) return false;
        if (firstStartSlaDurationMin != that.firstStartSlaDurationMin) return false;
        if (secondStartSlaDurationMin != that.secondStartSlaDurationMin) return false;
        if (firstEndSlaDurationMin != that.firstEndSlaDurationMin) return false;
        if (secondEndSlaDurationMin != that.secondEndSlaDurationMin) return false;
        if (isMultiStaffTask != that.isMultiStaffTask) return false;
        if (isEnabled != that.isEnabled) return false;
        if (tasktypeId != null ? !tasktypeId.equals(that.tasktypeId) : that.tasktypeId != null) return false;
        if (taskName != null ? !taskName.equals(that.taskName) : that.taskName != null) return false;
        if (status != that.status) return false;
        if (locationId != null ? !locationId.equals(that.locationId) : that.locationId != null) return false;
        if (firstStartDateTime != null ? !firstStartDateTime.equals(that.firstStartDateTime) : that.firstStartDateTime != null)
            return false;
        if (firstEndDateTime != null ? !firstEndDateTime.equals(that.firstEndDateTime) : that.firstEndDateTime != null)
            return false;
        if (skillWithIds != null ? !skillWithIds.equals(that.skillWithIds) : that.skillWithIds != null) return false;
        if (secondStartDateTime != null ? !secondStartDateTime.equals(that.secondStartDateTime) : that.secondStartDateTime != null)
            return false;
        if (secondEndDateTime != null ? !secondEndDateTime.equals(that.secondEndDateTime) : that.secondEndDateTime != null)
            return false;
        return relatedTaskid != null ? relatedTaskid.equals(that.relatedTaskid) : that.relatedTaskid == null;
    }

    @Override
    public int hashCode() {
        int result = tasktypeId != null ? tasktypeId.hashCode() : 0;
        result = 31 * result + (taskName != null ? taskName.hashCode() : 0);
        result = 31 * result + priority;
        result = 31 * result + (citizenId != null ? citizenId.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (locationId != null ? locationId.hashCode() : 0);
        result = 31 * result + durationInMin;
        result = 31 * result + (firstStartDateTime != null ? firstStartDateTime.hashCode() : 0);
        result = 31 * result + (firstEndDateTime != null ? firstEndDateTime.hashCode() : 0);
        result = 31 * result + (skillWithIds != null ? skillWithIds.hashCode() : 0);
        result = 31 * result + (secondStartDateTime != null ? secondStartDateTime.hashCode() : 0);
        result = 31 * result + (secondEndDateTime != null ? secondEndDateTime.hashCode() : 0);
        result = 31 * result + firstStartSlaDurationMin;
        result = 31 * result + secondStartSlaDurationMin;
        result = 31 * result + firstEndSlaDurationMin;
        result = 31 * result + secondEndSlaDurationMin;
        result = 31 * result + (relatedTaskid != null ? relatedTaskid.hashCode() : 0);
        result = 31 * result + (isMultiStaffTask ? 1 : 0);
        result = 31 * result + (isEnabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PlanningTask{" +
                "tasktypeId='" + tasktypeId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", priority=" + priority +
                ", citizenId=" + citizenId +
                ", status=" + status +
                ", locationId='" + locationId + '\'' +
                ", durationInMin=" + durationInMin +
                ", firstStartDateTime=" + firstStartDateTime +
                ", firstEndDateTime=" + firstEndDateTime +
                ", skills=" + skillWithIds +
                ", secondStartDateTime=" + secondStartDateTime +
                ", secondEndDateTime=" + secondEndDateTime +
                ", firstStartSlaDurationMin=" + firstStartSlaDurationMin +
                ", secondStartSlaDurationMin=" + secondStartSlaDurationMin +
                ", firstEndSlaDurationMin=" + firstEndSlaDurationMin +
                ", secondEndSlaDurationMin=" + secondEndSlaDurationMin +
                ", relatedTaskid='" + relatedTaskid + '\'' +
                ", isMultiStaffTask=" + isMultiStaffTask +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
