package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.ManagingOrganization;
import com.kairos.dto.gdpr.Staff;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityDTO {


    private Long id;
    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    @NotNull(message = "Managaing Department cannot be Null")
    private ManagingOrganization managingDepartment;
    @NotNull(message = "error.message.processOwner.notNull")
    private Staff processOwner;
    private Set<Long> processingPurposes;
    private Set<Long> dataSources;
    private Set<Long> transferMethods;
    private Set<Long> accessorParties;
    private Set<Long> processingLegalBasis;
    private List<ProcessingActivityDTO> subProcessingActivities=new ArrayList<>();
    private List<ProcessingActivityRelatedDataSubject> dataSubjectSet=new ArrayList<>();
    private Long responsibilityType;
    private Integer controllerContactInfo;
    private Integer dpoContactInfo;
    private Integer jointControllerContactInfo;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private Integer dataRetentionPeriod;
    private boolean suggestToCountryAdmin;
    private boolean suggested=false;

    @Valid
    private List<OrganizationLevelRiskDTO> risks = new ArrayList<>();

    public boolean isSuggested() { return suggested; }

    public void setSuggested(boolean suggested) { this.suggested = suggested; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public List<ProcessingActivityDTO> getSubProcessingActivities() { return subProcessingActivities; }

    public String getName() { return name.trim(); }

    public String getDescription() { return description; }

    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public Staff getProcessOwner() { return processOwner; }

    public Set<Long> getProcessingPurposes() { return processingPurposes; }

    public Set<Long> getDataSources() { return dataSources; }

    public void setName(String name) { this.name = name; }

    public void setDescription(String description) { this.description = description; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) {this.managingDepartment = managingDepartment; }

    public void setProcessOwner(Staff processOwner) { this.processOwner = processOwner; }

    public void setProcessingPurposes(Set<Long> processingPurposes) { this.processingPurposes = processingPurposes; }

    public void setDataSources(Set<Long> dataSources) { this.dataSources = dataSources; }

    public void setTransferMethods(Set<Long> transferMethods) { this.transferMethods = transferMethods; }

    public void setAccessorParties(Set<Long> accessorParties) { this.accessorParties = accessorParties; }

    public void setProcessingLegalBasis(Set<Long> processingLegalBasis) { this.processingLegalBasis = processingLegalBasis; }

    public void setSubProcessingActivities(List<ProcessingActivityDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }

    public void setResponsibilityType(Long responsibilityType) { this.responsibilityType = responsibilityType; }

    public void setControllerContactInfo(Integer controllerContactInfo) { this.controllerContactInfo = controllerContactInfo; }

    public void setDpoContactInfo(Integer dpoContactInfo) { this.dpoContactInfo = dpoContactInfo; }

    public void setJointControllerContactInfo(Integer jointControllerContactInfo) { this.jointControllerContactInfo = jointControllerContactInfo; }

    public void setMinDataSubjectVolume(Long minDataSubjectVolume) { this.minDataSubjectVolume = minDataSubjectVolume; }

    public void setMaxDataSubjectVolume(Long maxDataSubjectVolume) { this.maxDataSubjectVolume = maxDataSubjectVolume; }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) { this.dataRetentionPeriod = dataRetentionPeriod; }

    public boolean isSuggestToCountryAdmin() { return suggestToCountryAdmin; }

    public void setSuggestToCountryAdmin(boolean suggestToCountryAdmin) { this.suggestToCountryAdmin = suggestToCountryAdmin; }

    public Set<Long> getTransferMethods() { return transferMethods; }

    public Set<Long> getAccessorParties() { return accessorParties; }

    public Set<Long> getProcessingLegalBasis() { return processingLegalBasis; }

    public Long getResponsibilityType() { return responsibilityType; }

    public Integer getControllerContactInfo() { return controllerContactInfo; }

    public Integer getDpoContactInfo() { return dpoContactInfo; }

    public Integer getJointControllerContactInfo() { return jointControllerContactInfo; }

    public Long getMinDataSubjectVolume() { return minDataSubjectVolume; }

    public Long getMaxDataSubjectVolume() { return maxDataSubjectVolume; }

    public Integer getDataRetentionPeriod() { return dataRetentionPeriod; }

    public List<ProcessingActivityRelatedDataSubject> getDataSubjectSet() {
        return dataSubjectSet;
    }

    public void setDataSubjectSet(List<ProcessingActivityRelatedDataSubject> dataSubjectSet) {
        this.dataSubjectSet = dataSubjectSet;
    }

    public List<OrganizationLevelRiskDTO> getRisks() {
        return risks;
    }

    public void setRisks(List<OrganizationLevelRiskDTO> risks) {
        this.risks = risks;
    }

    public ProcessingActivityDTO() {
    }
}
