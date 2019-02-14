package com.kairos.persistence.model.master_data.default_proc_activity_setting;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;
import com.kairos.persistence.model.embeddables.ServiceCategory;
import com.kairos.persistence.model.embeddables.SubServiceCategory;
import com.kairos.persistence.model.risk_management.Risk;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MasterProcessingActivity extends BaseEntity {

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Risk> risks  = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="masterProcessingActivity_id")
    private MasterProcessingActivity masterProcessingActivity;

    @OneToMany(mappedBy="masterProcessingActivity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MasterProcessingActivity> subProcessingActivities = new ArrayList<>();

    private Long countryId;
    private boolean subProcessActivity;
    private boolean hasSubProcessingActivity;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;

    public MasterProcessingActivity() {

    }


   public MasterProcessingActivity(String name, String description, SuggestedDataStatus suggestedDataStatus, Long countryId) {
        this.name = name;
        this.description = description;
        this.suggestedDataStatus=suggestedDataStatus;
        this.countryId = countryId;
    }

    public MasterProcessingActivity(String name, String description, Long countryId, SuggestedDataStatus suggestedDataStatus, LocalDate suggestedDate) {
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

    public MasterProcessingActivity setSubProcessActivity(boolean subProcessActivity) { this.subProcessActivity = subProcessActivity; return this;}

    public boolean isHasSubProcessingActivity() { return hasSubProcessingActivity; }

    public MasterProcessingActivity setHasSubProcessingActivity(boolean hasSubProcessingActivity) { this.hasSubProcessingActivity = hasSubProcessingActivity;return this; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

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

    public List<Risk> getRisks() {
        return risks;
    }

    public void setRisks(List<Risk> risks) {
        this.risks = risks;
    }

    public MasterProcessingActivity getMasterProcessingActivity() {
        return masterProcessingActivity;
    }

    public void setMasterProcessingActivity(MasterProcessingActivity masterProcessingActivity) {
        this.masterProcessingActivity = masterProcessingActivity;
    }

    public List<MasterProcessingActivity> getSubProcessingActivities() {
        return subProcessingActivities;
    }

    public void setSubProcessingActivities(List<MasterProcessingActivity> subProcessingActivities) {
        this.subProcessingActivities = subProcessingActivities;
    }
}
