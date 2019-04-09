package com.kairos.shiftplanning.domain.staff;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("UnavailabilityList")
public class UnavailabilityRequest {
	private Long id;
    //@XStreamConverter(JodaTimeConverter.class)
    private DateTime startTime;
    //@XStreamConverter(JodaTimeConverter.class)
    private DateTime endTime;
    private int weight;
    private Employee employee;

    public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public UnavailabilityRequest() {
    }

    public UnavailabilityRequest(Long id, int weight, DateTime startTime, DateTime endTime,Employee employee) {
        this.weight = weight;
        this.endTime = endTime;
        this.startTime = startTime;
        this.id = id;
        this.employee=employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
