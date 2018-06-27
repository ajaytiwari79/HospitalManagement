package com.kairos.persistence.model.query_wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.client.dto.organization.CompanyType;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.country.BusinessType;
import com.kairos.persistence.model.country.CompanyCategory;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.*;

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
    private List<Map<String,Object>> serviceTypes;
    private List<CompanyCategory> companyCategories;
    private List<HashMap<String,String>> companyTypes;
    private List<HashMap<String,String>> companyUnitTypes;

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


    public List<Map<String, Object>> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<Map<String, Object>> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public List<CompanyCategory> getCompanyCategories() {
        return companyCategories;
    }

    public void setCompanyCategories(List<CompanyCategory> companyCategories) {
        this.companyCategories = companyCategories;
    }

    public List<HashMap<String, String>> getCompanyTypes() {
        return companyTypes;
    }

    public void setCompanyTypes(List<HashMap<String, String>> companyTypes) {
        this.companyTypes = companyTypes;
    }

    public List<HashMap<String, String>> getCompanyUnitTypes() {
        return companyUnitTypes;
    }

    public void setCompanyUnitTypes(List<HashMap<String, String>> companyUnitTypes) {
        this.companyUnitTypes = companyUnitTypes;
    }
}
