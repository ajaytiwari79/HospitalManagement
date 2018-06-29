package com.kairos.planning.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Citizen")
public class Citizen {
	public Citizen() {
		super();
	}

	//private long locationId;
	private String name;
	private String id;
	private Long externalId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public Citizen(String id,String name) {
		this.name=name;
		this.id=id;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public String toString(){
		return name;
	}

	/*public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}
*/
	public void setName(String name) {
		this.name = name;
	}
}
