package com.planner.domain.task;

import com.planner.domain.common.MongoBaseEntity;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author pradeep
 * @date - 7/6/18
 */

public class Task extends MongoBaseEntity{

    private String name;
    private Long staffId;
    private Long installationNumber;
    private Double latitude;
    private Double longitude;
    private String skill;
    private int duration;
    private int drivingDistance;
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
    private boolean breakTime;
    private String shiftId;
    private int drivingTime;
    private boolean escalated;

    public int getDrivingTime() {
        return drivingTime;
    }

    public void setDrivingTime(int drivingTime) {
        this.drivingTime = drivingTime;
    }

    public Task(String id, Long installationNumber, Double latitude, Double longitude, String skill, int duration, String streetName, int houseNo, String block, int floorNo, int post, String city) {
        //this.id = id;
        this.installationNumber = installationNumber;
        this.latitude = latitude;
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

    public boolean isEscalated() {
        return escalated;
    }

    public void setEscalated(boolean escalated) {
        this.escalated = escalated;
    }

    public int getDrivingDistance() {
        return drivingDistance;
    }

    public void setDrivingDistance(int drivingDistance) {
        this.drivingDistance = drivingDistance;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public boolean isBreakTime() {
        return breakTime;
    }

    public void setBreakTime(boolean breakTime) {
        this.breakTime = breakTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public LocalDateTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(LocalDateTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public LocalDateTime getPlannedEndTime() {
        return plannedEndTime;
    }

    public void setPlannedEndTime(LocalDateTime plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public Task(Long installationNumber, Double latitude, Double longitude) {
        this.installationNumber = installationNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getInstallationNumber() {
        return installationNumber;
    }

    public void setInstallationNumber(Long installationNumber) {
        this.installationNumber = installationNumber;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }



}
