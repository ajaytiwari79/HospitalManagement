package com.kairos.planning.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("Vehicle")
//@PlanningEntity
public class Vehicle  {//extends TaskOrEmployee
	private Long id;
	private String type;
	private Double speedKmpm;
	//@PlanningVariable(valueRangeProviderRefs = {"employeeRange"})
	//private Employee employee;
    private List<Skill> requiredSkillList;
    public List<Skill> getRequiredSkillList() {
        return requiredSkillList;
    }
    public void setRequiredSkillList(List<Skill> requiredSkillList) {
        this.requiredSkillList = requiredSkillList;
    }
	public Double getSpeedKmpm() {
		return speedKmpm;
	}

	public void setSpeedKmpm(Double speedKmpm) {
		this.speedKmpm = speedKmpm;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	//@Override
	public Vehicle getVehicle() {
		return this;
	}

	public String getLabel() {
		return id + "";
	}
	private Location location;

	public void setLocation(Location location) {
		this.location = location;
	}
	
	//@Override
	public Location getLocation() {
		return location;
	}

	public String toString() {
		return id + "-" + type;
	}

	/*public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}*/
}
