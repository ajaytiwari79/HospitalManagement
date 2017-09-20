package com.kairos.persistence.model.user.phase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;

/**
 * Created by pawanmandhan on 29/8/17.
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhaseDTO {

    private long id;
    @NotNull(message = "error.phase.name.notnull")
    private String name;
    private String description;
    private long duration;
    private boolean disabled;

    private int sequence;
    private int constructionPhaseStartsAtDay;
    private int activityAccess;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
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
}
