package com.kairos.shiftplanning.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.UUID;
@XStreamAlias("Absence")
public class Absence {
    private UUID id;
    private EmployeePlanningFact employee;
    private DateTime start;
    private DateTime end;
    private String type;

    public Absence(UUID id, EmployeePlanningFact employee, DateTime start, DateTime end, String type) {
        this.id = id;
        this.employee = employee;
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public Absence() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public EmployeePlanningFact getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeePlanningFact employee) {
        this.employee = employee;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public Interval getInterval(){
        return new Interval(start,end);
    }
}
