package com.kairos.response.dto.web.planning.vrpPlanning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.TaskTypeSettingDTO;

import java.util.Set;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDTO {

    private String id;
    private String name;
    private Set<String> skills;
    private int efficiency;

    public EmployeeDTO() {
    }

    public EmployeeDTO(String id, String name, Set<String> skills,int efficiency) {
        this.id = id;
        this.name = name;
        this.skills = skills;
        this.efficiency = efficiency;
    }

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
