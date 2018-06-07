package com.kairos.planner.vrp.taskplanning.model;

import java.util.Set;

public class Employee {
    private String id;
    private Set<String> skills;
    private int efficiency;

    public Employee(String id, Set<String> skills, int efficiency) {
        this.id = id;
        this.skills = skills;
        this.efficiency = efficiency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }
}
