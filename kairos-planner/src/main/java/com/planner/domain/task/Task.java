package com.planner.domain.task;

import com.planner.domain.MongoBaseEntity;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author pradeep
 * @date - 7/6/18
 */

public class Task extends MongoBaseEntity{

    private String id;
    private String name;
    private Long staffId;
    private Long intallationNumber;
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
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedEndTime;
    private Date startTime;
    private Date endTime;


    public Task(String id, Long intallationNumber, Double lattitude, Double longitude, String skill, int duration, String streetName, int houseNo, String block, int floorNo, int post, String city) {
        this.id = id;
        this.intallationNumber = intallationNumber;
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

    public Task() {
    }

    public Task(Long intallationNumber, Double lattitude, Double longitude) {
        this.intallationNumber = intallationNumber;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public Long getIntallationNumber() {
        return intallationNumber;
    }

    public void setIntallationNumber(Long intallationNumber) {
        this.intallationNumber = intallationNumber;
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
