package com.kairos.dto.planner.vrp;

import lombok.Getter;
import lombok.Setter;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@Getter
@Setter
public class VRPClientDTO {

    private String firstName;
    private Long id;
    private Long installationNumber;
    private Double latitude;
    private Double longitude;
    private String streetName;
    private int houseNumber;
    private String block;
    private int floorNumber;
    private int zipCode;
    private String city;
}
