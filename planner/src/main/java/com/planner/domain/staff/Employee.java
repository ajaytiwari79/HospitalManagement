package com.planner.domain.staff;

import com.planner.domain.common.MongoBaseEntity;

import java.util.Set;

/**
 * @author pradeep
 * @date - 7/6/18
 */

public class Employee extends MongoBaseEntity{

    private String name;
    private Set<String> skills;
    private int efficiency;

    public Employee(String name, Set<String> skills, int efficiency) {
        this.name = name;
        this.skills = skills;
        this.efficiency = efficiency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Employee() {
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
