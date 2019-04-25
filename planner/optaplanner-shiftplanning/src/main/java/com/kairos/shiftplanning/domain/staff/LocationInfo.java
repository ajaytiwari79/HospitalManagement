package com.kairos.shiftplanning.domain.staff;

public class LocationInfo {
	private String name;
	//private double logitude;
	//private double latitude;
	private double distance;
	private double time;
	private Long locationId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*public double getLogitude() {
		return logitude;
	}

	public void setLogitude(double logitude) {
		this.logitude = logitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}*/

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
}
