package com.kairos.response.dto.web.planning.vrpPlanning;

import java.util.Set;

/**
 * @author pradeep
 * @date - 20/6/18
 */

public class EmployeeDTO {

    private String id;
    private String name;
    private Set<String> skills;
    private int efficiency;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
