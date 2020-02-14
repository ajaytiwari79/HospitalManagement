package com.kairos.wrappers.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.task.SkillExpertise;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by oodles on 22/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskUpdateDTO {

    private String id;
    private String resource;
    private Integer priority;
    private String start;
    private String end;
    private List<Long> forbiddenStaff;
    private List<Long> prefferedStaff;
    private List<String> skillsList;
    private String team;
    private String info1;
    private String info2;
    private Boolean updateAllByDemand;
    private Map<String, Object> timeWindow;
    private String jointEvents;
    private Boolean isMainTask;
    private Integer duration;
    private List<SkillExpertise> skillExpertiseList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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

    public Boolean getUpdateAllByDemand() {
        return updateAllByDemand;
    }

    public void setUpdateAllByDemand(Boolean updateAllByDemand) {
        this.updateAllByDemand = updateAllByDemand;
    }

    public Map<String, Object> getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(Map<String, Object> timeWindow) {
        this.timeWindow = timeWindow;
    }

    public String getJointEvents() {
        return jointEvents;
    }

    public void setJointEvents(String jointEvents) {
        this.jointEvents = jointEvents;
    }

    public Boolean getMainTask() {
        return isMainTask;
    }

    public void setMainTask(Boolean mainTask) {
        isMainTask = mainTask;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = (int)TimeUnit.SECONDS.toMinutes(duration);;
    }
    public List<SkillExpertise> getSkillExpertiseList() {
        return skillExpertiseList;
    }

    public void setSkillExpertiseList(List<SkillExpertise> skillExpertiseList) {
        this.skillExpertiseList = skillExpertiseList;
    }
}
