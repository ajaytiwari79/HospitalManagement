package com.kairos.persistence.model.data_inventory.processing_activity;


import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.data_inventory.asset.AssetMD;
import com.kairos.persistence.model.embeddables.ManagingOrganization;
import com.kairos.persistence.model.embeddables.Staff;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.*;
import com.kairos.persistence.model.risk_management.RiskMD;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ProcessingActivityMD extends BaseEntity {

    @NotBlank(message = "Name can't be empty")
    private String name;
    @NotBlank(message = "Description can't be empty")
    private String description;

    @Embedded
    private ManagingOrganization managingDepartment;

    @Embedded
    private Staff processOwner;

    Long countryId;

    @OneToMany(fetch = FetchType.LAZY)
    private List<ProcessingPurposeMD> processingPurposes  = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY)
    private List<DataSourceMD> dataSources  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<TransferMethodMD> transferMethods  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<AccessorPartyMD> accessorParties  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<ProcessingLegalBasisMD> processingLegalBasis  = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<AssetMD> linkedAssets  = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="processingActivity_id")
    private ProcessingActivityMD processingActivity;

    @OneToMany(mappedBy = "processingActivity", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private List<ProcessingActivityMD> subProcessingActivities  = new ArrayList<>();

    @OneToOne
    ResponsibilityTypeMD responsibilityType;

    private Integer controllerContactInfo;
    private Integer dpoContactInfo;
    private Integer jointControllerContactInfo;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private Integer dataRetentionPeriod;

    private boolean isSubProcessingActivity;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<RelatedDataSubject> dataSubjects = new ArrayList<>();


   private boolean active = true;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RiskMD> risks  = new ArrayList<RiskMD>();
    private boolean suggested;

    public ProcessingActivityMD() {
    }

    public ProcessingActivityMD(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public ProcessingActivityMD(String name, String description, boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public boolean isSuggested() { return suggested; }

    public void setSuggested(boolean suggested) { this.suggested = suggested; }

    public List<RelatedDataSubject> getDataSubjects() {
        return dataSubjects;
    }

    public void setDataSubjects(List<RelatedDataSubject> dataSubjects) {
        this.dataSubjects = dataSubjects;
    }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }


    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public ManagingOrganization getManagingDepartment() {
        return managingDepartment;
    }

    public void setManagingDepartment(ManagingOrganization managingDepartment) {
        this.managingDepartment = managingDepartment;
    }

    public Staff getProcessOwner() {
        return processOwner;
    }

    public void setProcessOwner(Staff processOwner) {
        this.processOwner = processOwner;
    }

    public List<ProcessingPurposeMD> getProcessingPurposes() {
        return processingPurposes;
    }

    public void setProcessingPurposes(List<ProcessingPurposeMD> processingPurposes) {
        this.processingPurposes = processingPurposes;
    }

    public List<DataSourceMD> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSourceMD> dataSources) {
        this.dataSources = dataSources;
    }

    public List<TransferMethodMD> getTransferMethods() {
        return transferMethods;
    }

    public void setTransferMethods(List<TransferMethodMD> transferMethods) {
        this.transferMethods = transferMethods;
    }

    public List<AccessorPartyMD> getAccessorParties() {
        return accessorParties;
    }

    public void setAccessorParties(List<AccessorPartyMD> accessorParties) {
        this.accessorParties = accessorParties;
    }

    public List<ProcessingLegalBasisMD> getProcessingLegalBasis() {
        return processingLegalBasis;
    }

    public void setProcessingLegalBasis(List<ProcessingLegalBasisMD> processingLegalBasis) {
        this.processingLegalBasis = processingLegalBasis;
    }

    public List<AssetMD> getLinkedAssets() {
        return linkedAssets;
    }

    public void setLinkedAssets(List<AssetMD> linkedAssets) {
        this.linkedAssets = linkedAssets;
    }

    public ProcessingActivityMD getProcessingActivity() {
        return processingActivity;
    }

    public void setProcessingActivity(ProcessingActivityMD processingActivity) {
        this.processingActivity = processingActivity;
    }

    public List<ProcessingActivityMD> getSubProcessingActivities() {
        return subProcessingActivities;
    }

    public void setSubProcessingActivities(List<ProcessingActivityMD> subProcessingActivities) {
        this.subProcessingActivities = subProcessingActivities;
    }

    public ResponsibilityTypeMD getResponsibilityType() {
        return responsibilityType;
    }

    public void setResponsibilityType(ResponsibilityTypeMD responsibilityType) {
        this.responsibilityType = responsibilityType;
    }

    public Integer getControllerContactInfo() {
        return controllerContactInfo;
    }

    public void setControllerContactInfo(Integer controllerContactInfo) {
        this.controllerContactInfo = controllerContactInfo;
    }

    public Integer getDpoContactInfo() {
        return dpoContactInfo;
    }

    public void setDpoContactInfo(Integer dpoContactInfo) {
        this.dpoContactInfo = dpoContactInfo;
    }

    public Integer getJointControllerContactInfo() {
        return jointControllerContactInfo;
    }

    public void setJointControllerContactInfo(Integer jointControllerContactInfo) {
        this.jointControllerContactInfo = jointControllerContactInfo;
    }

    public Long getMinDataSubjectVolume() {
        return minDataSubjectVolume;
    }

    public void setMinDataSubjectVolume(Long minDataSubjectVolume) {
        this.minDataSubjectVolume = minDataSubjectVolume;
    }

    public Long getMaxDataSubjectVolume() {
        return maxDataSubjectVolume;
    }

    public void setMaxDataSubjectVolume(Long maxDataSubjectVolume) {
        this.maxDataSubjectVolume = maxDataSubjectVolume;
    }

    public Integer getDataRetentionPeriod() {
        return dataRetentionPeriod;
    }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) {
        this.dataRetentionPeriod = dataRetentionPeriod;
    }

    public boolean isSubProcessingActivity() {
        return isSubProcessingActivity;
    }

    public void setSubProcessingActivity(boolean subProcessingActivity) {
        this.isSubProcessingActivity = subProcessingActivity;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<RiskMD> getRisks() {
        return risks;
    }

    public void setRisks(List<RiskMD> risks) {
        this.risks = risks;
    }

    @Override
    public void delete() {
        super.delete();
        this.setDeleted(true);
        this.getRisks().forEach( processingActivityRisk -> {
            processingActivityRisk.delete();
        });
        if(!this.getSubProcessingActivities().isEmpty()) {
            this.getSubProcessingActivities().forEach(subProcessingActivity -> {
                subProcessingActivity.delete();
            });
        }

    }
}
