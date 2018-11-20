package com.kairos.persistence.model.staff;
/*
 *Created By Pavan on 12/11/18
 *
 */

import com.kairos.persistence.model.organization.union.Sector;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class SectorAndStaffExpertiseQueryResult {
    private Long id;
    private String name;
    private List<StaffExpertiseQueryResult> expertiseWithExperience;

    public SectorAndStaffExpertiseQueryResult() {
        //Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StaffExpertiseQueryResult> getExpertiseWithExperience() {
        return expertiseWithExperience;
    }

    public void setExpertiseWithExperience(List<StaffExpertiseQueryResult> expertiseWithExperience) {
        this.expertiseWithExperience = expertiseWithExperience;
    }
}
