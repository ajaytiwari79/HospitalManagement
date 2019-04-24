package com.planner.domain.staff;

import com.planner.domain.common.BaseEntity;

import java.util.List;

//import org.springframework.data.cassandra.core.mapping.Table;

//@Table
public class PlanningStaff extends BaseEntity {

    private String firstName;
    private String lastName;
    private List<String> skillWithIds;
    private Double costPerHour;

    public Double getCostPerHour() {
        return costPerHour;
    }

    public void setCostPerHour(Double costPerHour) {
        this.costPerHour = costPerHour;
    }

    public List<String> getSkillWithIds() {
        return skillWithIds;
    }

    public void setSkillWithIds(List<String> skillWithIds) {
        this.skillWithIds = skillWithIds;
    }

    public List<String> getSkillIds() {
        return skillWithIds;
    }

    public void setSkillIds(List<String> skillIds) {
        this.skillWithIds = skillIds;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
