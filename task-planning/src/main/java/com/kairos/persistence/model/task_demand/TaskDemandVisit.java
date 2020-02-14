package com.kairos.persistence.model.task_demand;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by oodles on 3/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class TaskDemandVisit extends MongoBaseEntity {

    protected int visitCount;
    protected long timeSlotId;
    protected String timeSlotName;
    protected boolean isPlanned;
    protected int visitDuration;
    protected String preferredHour;
    protected String preferredMinute;
    protected String preferredTime;

    public TaskDemandVisit(){
    //Default Constructor
    }


    public String getTimeSlotName() {
        return timeSlotName;
    }

    public void setTimeSlotName(String timeSlotName) {
        this.timeSlotName = timeSlotName;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

   public boolean isPlanned() {
       return isPlanned;
   }

    public void setPlanned(boolean planned) {
        isPlanned = planned;
    }

    public int getVisitDuration() {
        return visitDuration;
    }

    public void setVisitDuration(int visitDuration) {
        this.visitDuration = visitDuration;
    }

    public String getPreferredHour() {
        return preferredHour;
    }

    public void setPreferredHour(String preferredHour) {
        this.preferredHour = preferredHour;
    }

    public String getPreferredMinute() {
        return preferredMinute;
    }

    public void setPreferredMinute(String preferredMinute) {
        this.preferredMinute = preferredMinute;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

}
