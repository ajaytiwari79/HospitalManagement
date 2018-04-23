package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by pavan on 20/4/18.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class BreaksInShift extends WTABaseRuleTemplate{
    private int shiftDuration;
    private int noOfBreaks;
    private int breakDuration;
    private int earliestDurationMinutes;
    private int latestDurationMinutes;
    private List<Long> activities; //Multiple activities

    public BreaksInShift() {
        //Default Constructor
    }
    public BreaksInShift(String name, String templateType, boolean disabled, String description, int shiftDuration, int noOfBreaks, int breakDuration,
                         int earliestDurationMinutes, int latestDurationMinutes, List<Long> activities) {
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.shiftDuration = shiftDuration;
        this.noOfBreaks = noOfBreaks;
        this.breakDuration = breakDuration;
        this.earliestDurationMinutes = earliestDurationMinutes;
        this.latestDurationMinutes = latestDurationMinutes;
        this.activities = activities;
    }

    public int getShiftDuration() {
        return shiftDuration;
    }

    public void setShiftDuration(int shiftDuration) {
        this.shiftDuration = shiftDuration;
    }

    public int getNoOfBreaks() {
        return noOfBreaks;
    }

    public void setNoOfBreaks(int noOfBreaks) {
        this.noOfBreaks = noOfBreaks;
    }

    public int getBreakDuration() {
        return breakDuration;
    }

    public void setBreakDuration(int breakDuration) {
        this.breakDuration = breakDuration;
    }

    public int getEarliestDurationMinutes() {
        return earliestDurationMinutes;
    }

    public void setEarliestDurationMinutes(int earliestDurationMinutes) {
        this.earliestDurationMinutes = earliestDurationMinutes;
    }

    public int getLatestDurationMinutes() {
        return latestDurationMinutes;
    }

    public void setLatestDurationMinutes(int latestDurationMinutes) {
        this.latestDurationMinutes = latestDurationMinutes;
    }

    public List<Long> getActivities() {
        return activities;
    }

    public void setActivities(List<Long> activities) {
        this.activities = activities;
    }
}
