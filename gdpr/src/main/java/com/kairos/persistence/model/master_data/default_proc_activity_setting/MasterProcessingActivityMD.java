package com.kairos.persistence.model.master_data.default_proc_activity_setting;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.master_data.default_asset_setting.*;
import com.kairos.persistence.model.risk_management.RiskMD;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MasterProcessingActivityMD extends BaseEntity {

    @NotBlank(message = "Name can't be empty")
    private String name;
    private String description;

    @ElementCollection
    private List<OrganizationType> organizationTypes = new ArrayList<>();

    @ElementCollection
    private List <OrganizationSubType> organizationSubTypes = new ArrayList<>();

    @ElementCollection
    private List <ServiceCategory> organizationServices = new ArrayList<>();

    @ElementCollection
    private List <SubServiceCategory> organizationSubServices = new ArrayList<>();

    @OneToMany(mappedBy = "processingActivity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RiskMD> risks  = new ArrayList<RiskMD>();

    @ManyToOne
    @JoinColumn(name="masterProcessingActivity_id")
    private MasterProcessingActivityMD masterProcessingActivity;

    @OneToMany(mappedBy="masterProcessingActivity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MasterProcessingActivityMD> subProcessingActivities =new ArrayList<MasterProcessingActivityMD>();

    private Long countryId;
    private boolean subProcessActivity;
    private boolean hasSubProcessingActivity;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;

    public MasterProcessingActivityMD() {

    }


   public MasterProcessingActivityMD(String name, String description, SuggestedDataStatus suggestedDataStatus, Long countryId) {
        this.name = name;
        this.description = description;
        this.suggestedDataStatus=suggestedDataStatus;
        this.countryId = countryId;
    }

    public MasterProcessingActivityMD(String name, String description, Long countryId, SuggestedDataStatus suggestedDataStatus, LocalDate suggestedDate) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.subProcessActivity = subProcessActivity;
        this.suggestedDataStatus=suggestedDataStatus;
        this.suggestedDate=suggestedDate;
    }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public boolean isSubProcessActivity() { return subProcessActivity; }

    public MasterProcessingActivityMD setSubProcessActivity(boolean subProcessActivity) { this.subProcessActivity = subProcessActivity; return this;}

    public boolean isHasSubProcessingActivity() { return hasSubProcessingActivity; }

    public MasterProcessingActivityMD setHasSubProcessingActivity(boolean hasSubProcessingActivity) { this.hasSubProcessingActivity = hasSubProcessingActivity;return this; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public MasterProcessingActivityMD setName(String name) { this.name = name; return this; }

    public String getDescription() {
        return description;
    }

    public MasterProcessingActivityMD setDescription(String description) { this.description = description; return this; }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationType> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategory> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public List<RiskMD> getRisks() {
        return risks;
    }

    public void setRisks(List<RiskMD> risks) {
        this.risks = risks;
    }

    public MasterProcessingActivityMD getMasterProcessingActivity() {
        return masterProcessingActivity;
    }

    public void setMasterProcessingActivity(MasterProcessingActivityMD masterProcessingActivity) {
        this.masterProcessingActivity = masterProcessingActivity;
    }

    public List<MasterProcessingActivityMD> getSubProcessingActivities() {
        return subProcessingActivities;
    }

    public void setSubProcessingActivities(List<MasterProcessingActivityMD> subProcessingActivities) {
        this.subProcessingActivities = subProcessingActivities;
    }
}
