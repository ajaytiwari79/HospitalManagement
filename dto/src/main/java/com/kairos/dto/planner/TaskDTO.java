package com.kairos.dto.planner;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

/**
 * @author pradeep
 * @date - 7/6/18
 */

public class TaskDTO {

    private int intallationNo;
    private Double lattitude;
    private Double longitude;
    private String skill;
    private int duration;
    private String streetName;
    private int houseNo;
    private String block;
    private int floorNo;
    private int post;
    private String city;


    public TaskDTO(int intallationNo, Double lattitude, Double longitude, String skill, int duration, String streetName, int houseNo, String block, int floorNo, int post, String city) {
        this.intallationNo = intallationNo;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.skill = skill;
        this.duration = duration;
        this.streetName = streetName;
        this.houseNo = houseNo;
        this.block = block;
        this.floorNo = floorNo;
        this.post = post;
        this.city = city;
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

    public String getSkill() {
        return skill;
    }

    public void setSkills(String skill) {
        this.skill = skill;
    }

    public TaskDTO() {
    }

    public TaskDTO(int intallationNo, Double lattitude, Double longitude) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TaskDTO taskDTO = (TaskDTO) o;

        return new EqualsBuilder()
                .append(intallationNo, taskDTO.intallationNo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(intallationNo)
                .toHashCode();
    }
}
