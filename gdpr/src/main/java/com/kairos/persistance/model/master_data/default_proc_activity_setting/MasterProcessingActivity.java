package com.kairos.persistance.model.master_data.default_proc_activity_setting;


import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;
import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "master_processing_activity")
public class MasterProcessingActivity extends MongoBaseEntity {

    @NotBlank(message = "Name can't be empty")
    private String name;

    @NotBlank(message = "Description can't be empty")
    private String description;

    @NotNull
    private List<OrganizationType> organizationTypes;

    @NotNull
    private List<OrganizationSubType> organizationSubTypes;
    @NotNull
    private List<ServiceCategory> organizationServices;
    @NotNull
    private List<SubServiceCategory> organizationSubServices;

    private List<BigInteger> subProcessingActivityIds;

    private Long countryId;

    private List<BigInteger> risks=new ArrayList<>();

    private Boolean isSubProcess=false;

    private Boolean hasSubProcessingActivity=false;

    public Boolean getHasSubProcessingActivity() {
        return hasSubProcessingActivity;
    }

    public void setHasSubProcessingActivity(Boolean hasSubProcessingActivity) { this.hasSubProcessingActivity = hasSubProcessingActivity; }

    public Boolean getSubProcess() {
        return isSubProcess;
    }

    public void setSubProcess(Boolean subProcess) {
        isSubProcess = subProcess;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<BigInteger> getSubProcessingActivityIds() {
        return subProcessingActivityIds;
    }

    public void setSubProcessingActivityIds(List<BigInteger> subProcessingActivityIds) { this.subProcessingActivityIds = subProcessingActivityIds; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationType> organizationTypes) { this.organizationTypes = organizationTypes; }

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes; }

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategory> organizationServices) { this.organizationServices = organizationServices; }

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) { this.organizationSubServices = organizationSubServices; }

    public List<BigInteger> getRisks() { return risks;}

    public void setRisks(List<BigInteger> risks) { this.risks = risks; }

    public MasterProcessingActivity() {

    }

    public MasterProcessingActivity(String name, String description, List<OrganizationType> organizationTypes, List<OrganizationSubType> organizationSubTypes, List<ServiceCategory> organizationServices, List<SubServiceCategory> organizationSubServices) {
        this.name = name;
        this.description = description;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationServices = organizationServices;
        this.organizationSubServices = organizationSubServices;
    }
}
