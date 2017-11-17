package com.kairos.persistence.model.query_wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.user.country.BusinessType;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 6/9/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class OrganizationCreationData {

    private List<Map<String,Object>> zipCodes;
    private java.util.List<BusinessType> businessTypes;
    private List<Map<String,Object>> organizationTypes;
    private List<Level> levels;

    public List<Level> getLevels() {
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public List<BusinessType> getBusinessTypes() {
        return businessTypes;
    }

    public void setBusinessTypes(List<BusinessType> businessTypes) {
        this.businessTypes = businessTypes;
    }

    public List<Map<String, Object>> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<Map<String, Object>> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<Map<String, Object>> getZipCodes() {
        return zipCodes;
    }

    public void setZipCodes(List<Map<String, Object>> zipCodes) {
        this.zipCodes = zipCodes;
    }
}
