package com.kairos.persistence.model.user.phase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotNull;

/**
 * Created by pawanmandhan on 29/8/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class Phase extends UserBaseEntity {

    @NotNull(message = "error.phase.name.notnull")
    private String name;
    private String description;
    private long duration;
    private boolean disabled;
    private int sequence;
    private int constructionPhaseStartsAtDay;
    private int activityAccess;
    //activityAccess status
    // 0- activities non editable
    // 1- allowUpdateActivities , restrict new activity creation
    // 2- allowNewActivities & update


    public Phase() {
    }


    public Phase(String name, String description, boolean disabled, int sequence, int constructionPhaseStartsAtDay, int activityAccess,long duration) {
        this.name = name;
        this.description = description;
        this.disabled = disabled;
        this.sequence = sequence;
        this.constructionPhaseStartsAtDay = constructionPhaseStartsAtDay;
        this.activityAccess = activityAccess;
        this.duration=duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getConstructionPhaseStartsAtDay() {
        return constructionPhaseStartsAtDay;
    }

    public void setConstructionPhaseStartsAtDay(int constructionPhaseStartsAtDay) {
        this.constructionPhaseStartsAtDay = constructionPhaseStartsAtDay;
    }

    public int getActivityAccess() {
        return activityAccess;
    }

    public void setActivityAccess(int activityAccess) {
        this.activityAccess = activityAccess;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
