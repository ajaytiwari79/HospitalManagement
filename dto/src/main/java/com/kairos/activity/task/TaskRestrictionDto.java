package com.kairos.activity.task;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by prabjot on 11/7/17.
 */
public class TaskRestrictionDto {

    private boolean allowedEngineers;
    private boolean notAllowedEngineers;
    private boolean team;
    private boolean skills;
    private boolean removeFix;
    private Integer slaTime;
    private boolean isReduction;
    private Integer priority;
    private Integer percentageDuration;
    private String delayPenalty;
    private Integer extraPenalty;
    private long citizenId;

    public boolean isAllowedEngineers() {
        return allowedEngineers;
    }

    public void setAllowedEngineers(boolean allowedEngineers) {
        this.allowedEngineers = allowedEngineers;
    }

    public boolean isNotAllowedEngineers() {
        return notAllowedEngineers;
    }

    public void setNotAllowedEngineers(boolean notAllowedEngineers) {
        this.notAllowedEngineers = notAllowedEngineers;
    }

    public boolean isTeam() {
        return team;
    }

    public void setTeam(boolean team) {
        this.team = team;
    }

    public boolean isSkills() {
        return skills;
    }

    public long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(long citizenId) {
        this.citizenId = citizenId;
    }

    public void setSkills(boolean skills) {
        this.skills = skills;
    }

    public boolean isRemoveFix() {
        return removeFix;
    }

    public void setRemoveFix(boolean removeFix) {
        this.removeFix = removeFix;
    }

    public Integer getSlaTime() {
        return slaTime;
    }

    public void setSlaTime(Integer slaTime) {
        this.slaTime = slaTime;
    }

    public boolean isReduction() {
        return isReduction;
    }

    @JsonProperty(value = "isReduction")
    public void setReduction(boolean reduction) {
        isReduction = reduction;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPercentageDuration() {
        return percentageDuration;
    }

    public void setPercentageDuration(Integer percentageDuration) {
        this.percentageDuration = percentageDuration;
    }

    public String getDelayPenalty() {
        return delayPenalty;
    }

    public void setDelayPenalty(String delayPenalty) {
        this.delayPenalty = delayPenalty;
    }

    public Integer getExtraPenalty() {
        return extraPenalty;
    }

    public void setExtraPenalty(Integer extraPenalty) {
        this.extraPenalty = extraPenalty;
    }
}
