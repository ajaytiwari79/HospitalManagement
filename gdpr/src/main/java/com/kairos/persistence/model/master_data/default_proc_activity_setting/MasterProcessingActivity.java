package com.kairos.persistence.model.master_data.default_proc_activity_setting;


import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "master_processing_activity")
public class MasterProcessingActivity extends MongoBaseEntity {

    @NotBlank(message = "Name can't be empty")
    private String name;
    private String description;
    private List<OrganizationType> organizationTypes;
    private List<OrganizationSubType> organizationSubTypes;
    private List<ServiceCategory> organizationServices;
    private List<SubServiceCategory> organizationSubServices;
    private List<BigInteger> subProcessingActivityIds;
    private Long countryId;
    private List<BigInteger> risks=new ArrayList<>();
    private boolean subProcess;
    private boolean hasSubProcessingActivity;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;


    public MasterProcessingActivity() {

    }


    public MasterProcessingActivity(String name, String description, SuggestedDataStatus suggestedDataStatus,List<OrganizationType> organizationTypes, List<OrganizationSubType> organizationSubTypes, List<ServiceCategory> organizationServices, List<SubServiceCategory> organizationSubServices) {
        this.name = name;
        this.description = description;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationServices = organizationServices;
        this.organizationSubServices = organizationSubServices;
        this.suggestedDataStatus=suggestedDataStatus;
    }

    public MasterProcessingActivity( String name, String description, Long countryId,SuggestedDataStatus suggestedDataStatus,LocalDate suggestedDate) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.subProcess = subProcess;
        this.suggestedDataStatus=suggestedDataStatus;
        this.suggestedDate=suggestedDate;
    }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public boolean isSubProcess() { return subProcess; }

    public MasterProcessingActivity setSubProcess(boolean subProcess) { this.subProcess = subProcess; return this;}

    public boolean isHasSubProcessingActivity() { return hasSubProcessingActivity; }

    public MasterProcessingActivity setHasSubProcessingActivity(boolean hasSubProcessingActivity) { this.hasSubProcessingActivity = hasSubProcessingActivity;return this; }

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

    public MasterProcessingActivity setName(String name) { this.name = name; return this; }

    public String getDescription() {
        return description;
    }

    public MasterProcessingActivity setDescription(String description) { this.description = description; return this; }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public MasterProcessingActivity setOrganizationTypes(List<OrganizationType> organizationTypes) { this.organizationTypes = organizationTypes;  return this;}

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public MasterProcessingActivity setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes; return this; }

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public MasterProcessingActivity setOrganizationServices(List<ServiceCategory> organizationServices) { this.organizationServices = organizationServices;  return this;}

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public MasterProcessingActivity setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) { this.organizationSubServices = organizationSubServices; return this;}

    public List<BigInteger> getRisks() { return risks;}

    public void setRisks(List<BigInteger> risks) { this.risks = risks; }


}
