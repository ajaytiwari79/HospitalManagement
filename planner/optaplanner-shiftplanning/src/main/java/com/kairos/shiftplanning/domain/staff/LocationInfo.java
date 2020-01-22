package com.kairos.shiftplanning.domain.staff;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationInfo {
	private String name;
	private double distance;
	private double time;
	private Long locationId;

}
