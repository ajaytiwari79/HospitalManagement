package com.kairos.planning.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.joda.time.DateTime;

@XStreamAlias("UnavailabilityList")
public class UnavailabilityRequest {
	private String id;
    //@XStreamConverter(JodaTimeConverter.class)
    private DateTime startTime;
    //@XStreamConverter(JodaTimeConverter.class)
    private DateTime endTime;
    private int weight;
    private Long externalId;
    private Employee employee;

    public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public UnavailabilityRequest() {
    }

    public UnavailabilityRequest(String id, int weight, DateTime startTime, DateTime endTime,Employee employee) {
        this.weight = weight;
        this.endTime = endTime;
        this.startTime = startTime;
        this.id = id;
        this.employee=employee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
