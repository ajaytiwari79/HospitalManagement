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
    private Sector sector;
    private List<StaffExpertiseQueryResult> expertiseWithExperience;

    public SectorAndStaffExpertiseQueryResult() {
        //Default Constructor
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public List<StaffExpertiseQueryResult> getExpertiseWithExperience() {
        return expertiseWithExperience;
    }

    public void setExpertiseWithExperience(List<StaffExpertiseQueryResult> expertiseWithExperience) {
        this.expertiseWithExperience = expertiseWithExperience;
    }
}
