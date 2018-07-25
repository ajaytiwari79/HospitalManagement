package com.kairos.vrp.vrpPlanning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO {

    private String id;
    private String name;
    private Long staffId;
    private Long installationNumber;
    private Double latitude;
    private Double longitude;
    private Set<String> skills;
    private int duration;
    private int actualDuration;
    private String streetName;
    private int drivingDistance;
    private int houseNo;
    private String block;
    private int floorNo;
    private int post;
    private String citizenName;
    private String city;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedEndTime;
    private Long startTime;
    private Long endTime;
    private String shiftId;
    private String color;
    private boolean breakTime;
    private int drivingTime;
    private boolean escalated;


    public TaskDTO() {
    }

    public TaskDTO(String id, Long installationNumber, Double latitude, Double longitude, Set<String> skills, int duration, String streetName, int houseNo, String block, int floorNo, int post, String city) {
        this.id = id;
        this.installationNumber = installationNumber;
        this.latitude = latitude;
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


    public int getActualDuration() {
        return actualDuration;
    }

    public void setActualDuration(int actualDuration) {
        this.actualDuration = actualDuration;
    }

    public boolean isEscalated() {
        return escalated;
    }

    public void setEscalated(boolean escalated) {
        this.escalated = escalated;
    }

    public int getDrivingTime() {
        return drivingTime;
    }

    public void setDrivingTime(int drivingTime) {
        this.drivingTime = drivingTime;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }
    public int getDrivingDistance() {
        return drivingDistance;
    }

    public void setDrivingDistance(int drivingDistance) {
        this.drivingDistance = drivingDistance;
    }

    public boolean isBreakTime() {
        return breakTime;
    }

    public void setBreakTime(boolean breakTime) {
        this.breakTime = breakTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getPlannedEndTime() {
        return plannedEndTime;
    }

    public void setPlannedEndTime(LocalDateTime plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
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

    public LocalDateTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(LocalDateTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }


}
