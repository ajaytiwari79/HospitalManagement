package com.kairos.persistence.model.country.default_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.user.expertise.Expertise;

import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 28/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationMappingDTO {
    private List<EmploymentType> employmentTypes;
    private List<Expertise> expertise;
    private List<Level> levels;
    private List<Object> regions;
    private List<Map<String, Object>> organizationTypeHierarchy;

    public List<EmploymentType> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentType> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public List<Expertise> getExpertise() {
        return expertise;
    }

    public void setExpertise(List<Expertise> expertise) {
        this.expertise = expertise;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public List<Object> getRegions() {
        return regions;
    }

    public void setRegions(List<Object> regions) {
        this.regions = regions;
    }

    public List<Map<String, Object>> getOrganizationTypeHierarchy() {
        return organizationTypeHierarchy;
    }

    public void setOrganizationTypeHierarchy(List<Map<String, Object>> organizationTypeHierarchy) {
        this.organizationTypeHierarchy = organizationTypeHierarchy;
    }
}
