package com.kairos.shiftplanning.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Citizen")
public class Citizen {
	public Citizen() {
		super();
	}

	private long locationId;
	private String name;
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public Citizen(Long id,String name) {
		this.name=name;
		this.id=id;
	}
	public String toString(){
		return name;
	}

	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}

	public void setName(String name) {
		this.name = name;
	}
}
