package com.planner.domain.location;

import com.planner.domain.common.MongoBaseEntity;

//import org.springframework.data.cassandra.core.mapping.Table;


//@Table
public class LocationDistance extends MongoBaseEntity {

	private String firstLocationId;
	private String secondLocationId;
	private Double distanceByCar;
	private Double timeByCar;
	private Double distanceByBicycle;
	private Double timeByBicycle;
	private Double distanceByBike;
	private Double timeByBike;
	private Double distanceByTruck;
	private Double timeBytruck;
	private long firstInstallationNo;
	private long secondInstallationNo;

	public LocationDistance() {
	}

	public LocationDistance(long firstInstallationNo, long secondInstallationNo, Double distanceByCar, Double timeByCar) {
		this.distanceByCar = distanceByCar;
		this.timeByCar = timeByCar;
		this.firstInstallationNo = firstInstallationNo;
		this.secondInstallationNo = secondInstallationNo;
	}

	public long getFirstInstallationNo() {
		return firstInstallationNo;
	}

	public void setFirstInstallationNo(long firstInstallationNo) {
		this.firstInstallationNo = firstInstallationNo;
	}

	public long getSecondInstallationNo() {
		return secondInstallationNo;
	}

	public void setSecondInstallationNo(long secondInstallationNo) {
		this.secondInstallationNo = secondInstallationNo;
	}

	public String getFirstLocationId() {
		return firstLocationId;
	}

	public void setFirstLocationId(String firstLocationId) {
		this.firstLocationId = firstLocationId;
	}

	public String getSecondLocationId() {
		return secondLocationId;
	}

	public void setSecondLocationId(String secondLocationId) {
		this.secondLocationId = secondLocationId;
	}

	public Double getDistanceByCar() {
		return distanceByCar;
	}

	public void setDistanceByCar(Double distanceByCar) {
		this.distanceByCar = distanceByCar;
	}

	public Double getTimeByCar() {
		return timeByCar;
	}

	public void setTimeByCar(Double timeByCar) {
		this.timeByCar = timeByCar;
	}

	public Double getDistanceByBicycle() {
		return distanceByBicycle;
	}

	public void setDistanceByBicycle(Double distanceByBicycle) {
		this.distanceByBicycle = distanceByBicycle;
	}

	public Double getTimeByBicycle() {
		return timeByBicycle;
	}

	public void setTimeByBicycle(Double timeByBicycle) {
		this.timeByBicycle = timeByBicycle;
	}

	public Double getDistanceByBike() {
		return distanceByBike;
	}

	public void setDistanceByBike(Double distanceByBike) {
		this.distanceByBike = distanceByBike;
	}

	public Double getTimeByBike() {
		return timeByBike;
	}

	public void setTimeByBike(Double timeByBike) {
		this.timeByBike = timeByBike;
	}

	public Double getDistanceByTruck() {
		return distanceByTruck;
	}

	public void setDistanceByTruck(Double distanceByTruck) {
		this.distanceByTruck = distanceByTruck;
	}

	public Double getTimeBytruck() {
		return timeBytruck;
	}

	public void setTimeBytruck(Double timeBytruck) {
		this.timeBytruck = timeBytruck;
	}
}
