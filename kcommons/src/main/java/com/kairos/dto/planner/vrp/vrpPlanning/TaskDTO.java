package com.kairos.dto.planner.vrp.vrpPlanning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
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
}
