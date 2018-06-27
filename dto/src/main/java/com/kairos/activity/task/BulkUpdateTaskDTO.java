package com.kairos.activity.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prabjot on 27/6/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BulkUpdateTaskDTO {

    private List<BigInteger> taskIds;
    private Integer slaDuration;
    private boolean isReduced;
    private String team;
    private String info1;
    private String info2;
    private List<Long> forbiddenStaff;
    private List<Long> prefferedStaff;
    private List<String> skillsList;
    private Boolean removeTeam;
    private Boolean removeNotAllowedStaff;
    private Boolean removeAllowedStaff;
    private Boolean removeSkills;
    private Integer priority;
    private Integer percentageDuration;

    public Integer getPercentageDuration() {
        return percentageDuration;
    }

    public void setPercentageDuration(Integer percentageDuration) {
        this.percentageDuration = percentageDuration;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<BigInteger> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<BigInteger> taskIds) {
        this.taskIds = taskIds;
    }

    public Integer getSlaDuration() {
        return slaDuration;
    }

    public void setSlaDuration(Integer slaDuration) {
        this.slaDuration = slaDuration;
    }

    public boolean isReduced() {
        return isReduced;
    }

    @JsonProperty(value = "isReduced")
    public void setReduced(boolean reduced) {
        isReduced = reduced;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public List<Long> getForbiddenStaff() {
        return forbiddenStaff;
    }

    public void setForbiddenStaff(List<Long> forbiddenStaff) {
        this.forbiddenStaff = forbiddenStaff;
    }

    public List<Long> getPrefferedStaff() {
        return prefferedStaff;
    }

    public void setPrefferedStaff(List<Long> prefferedStaff) {
        this.prefferedStaff = prefferedStaff;
    }

    public List<String> getSkillsList() {
        return skillsList;
    }

    public void setSkillsList(List<String> skillsList) {
        this.skillsList = skillsList;
    }

    public Boolean getRemoveTeam() {
        return removeTeam;
    }

    public void setRemoveTeam(Boolean removeTeam) {
        this.removeTeam = removeTeam;
    }

    public Boolean getRemoveNotAllowedStaff() {
        return removeNotAllowedStaff;
    }

    public void setRemoveNotAllowedStaff(Boolean removeNotAllowedStaff) {
        this.removeNotAllowedStaff = removeNotAllowedStaff;
    }

    public Boolean getRemoveAllowedStaff() {
        return removeAllowedStaff;
    }

    public void setRemoveAllowedStaff(Boolean removeAllowedStaff) {
        this.removeAllowedStaff = removeAllowedStaff;
    }

    public Boolean getRemoveSkills() {
        return removeSkills;
    }

    public void setRemoveSkills(Boolean removeSkills) {
        this.removeSkills = removeSkills;
    }
}
