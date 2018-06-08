package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

import java.util.Set;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@PlanningEntity
public class Task extends TaskOrShift{
    private String id;
    private int intallationNo;
    private Double lattitude;
    private Double longitude;
    private Set<String> skills;
    private int duration;
    private String streetName;
    private int houseNo;
    private String block;
    private int floorNo;
    private int post;
    private String city;
    @PlanningVariable(valueRangeProviderRefs = {
            "tasks","shifts" }, graphType = PlanningVariableGraphType.CHAINED)
    private TaskOrShift prevTaskOrShift;


    @AnchorShadowVariable(sourceVariableName = "prevTaskOrShift")
    private Shift shift;


    public Task(String id,int intallationNo, Double lattitude, Double longitude, Set<String> skills, int duration, String streetName, int houseNo, String block, int floorNo, int post, String city) {
        this.id = id;
        this.intallationNo = intallationNo;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.skills = skills;
        this.duration = duration;
        this.streetName = streetName;
        this.houseNo = houseNo;
        this.block = block;
        this.floorNo = floorNo;
        this.post = post;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaskOrShift getPrevTaskOrShift() {
        return prevTaskOrShift;
    }

    public void setPrevTaskOrShift(TaskOrShift prevTaskOrShift) {
        this.prevTaskOrShift = prevTaskOrShift;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(int houseNo) {
        this.houseNo = houseNo;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public Task() {
    }

    public Task(int intallationNo, Double lattitude, Double longitude) {
        this.intallationNo = intallationNo;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public int getIntallationNo() {
        return intallationNo;
    }

    public void setIntallationNo(int intallationNo) {
        this.intallationNo = intallationNo;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}
