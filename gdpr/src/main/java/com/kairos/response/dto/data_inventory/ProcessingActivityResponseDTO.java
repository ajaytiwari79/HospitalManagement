package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.ManagingOrganization;
import com.kairos.dto.gdpr.Staff;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityRelatedDataSubject;
import com.kairos.dto.gdpr.master_data.DataSubjectDTO;
import com.kairos.response.dto.common.*;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingBasicResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProcessingActivityResponseDTO {

    private Long id;
    private String name;
    private String description;
    private ManagingOrganization managingDepartment;
    private Staff processOwner;
    private List<ProcessingPurposeResponseDTO> processingPurposes;
    private List<DataSourceResponseDTO> dataSources;
    private List<AccessorPartyResponseDTO> accessorParties;
    private List<TransferMethodResponseDTO> transferMethods;
    private List<ProcessingLegalBasisResponseDTO> processingLegalBasis;
    private ResponsibilityTypeResponseDTO responsibilityType;
    private List<RiskBasicResponseDTO> risks;
    private Integer controllerContactInfo;
    private Integer dpoContactInfo;
    private Integer jointControllerContactInfo;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private Integer dataRetentionPeriod;
    private Boolean active;
    private Boolean suggested;
    private List<ProcessingActivityResponseDTO> subProcessingActivities=new ArrayList<>();
    private List<ProcessingActivityRelatedDataSubject> dataSubjects;


    public List<RiskBasicResponseDTO> getRisks() { return risks; }

    public void setRisks(List<RiskBasicResponseDTO> risks) { this.risks = risks; }

    public List<ProcessingActivityResponseDTO> getSubProcessingActivities() { return subProcessingActivities; }

    public void setSubProcessingActivities(List<ProcessingActivityResponseDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }

    public Boolean getActive() { return active; }

    public void setActive(Boolean active) { this.active = active; }

    public Boolean getSuggested() { return suggested; }

    public void setSuggested(Boolean suggested) { this.suggested = suggested; }

    public ResponsibilityTypeResponseDTO getResponsibilityType() { return responsibilityType; }

    public void setResponsibilityType(ResponsibilityTypeResponseDTO responsibilityType) { this.responsibilityType = responsibilityType; }

    public List<ProcessingLegalBasisResponseDTO> getProcessingLegalBasis() { return processingLegalBasis; }

    public void setProcessingLegalBasis(List<ProcessingLegalBasisResponseDTO> processingLegalBasis) { this.processingLegalBasis = processingLegalBasis; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<ProcessingPurposeResponseDTO> getProcessingPurposes() {
        return processingPurposes;
    }

    public void setProcessingPurposes(List<ProcessingPurposeResponseDTO> processingPurposes) {
        this.processingPurposes = processingPurposes;
    }

    public List<DataSourceResponseDTO> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSourceResponseDTO> dataSources) {
        this.dataSources = dataSources;
    }

    public List<AccessorPartyResponseDTO> getAccessorParties() {
        return accessorParties;
    }

    public void setAccessorParties(List<AccessorPartyResponseDTO> accessorParties) { this.accessorParties = accessorParties; }

    public List<TransferMethodResponseDTO> getTransferMethods() { return transferMethods; }

    public void setTransferMethods(List<TransferMethodResponseDTO> transferMethods) { this.transferMethods = transferMethods; }

    public Integer getControllerContactInfo() {
        return controllerContactInfo;
    }

    public void setControllerContactInfo(Integer controllerContactInfo) { this.controllerContactInfo = controllerContactInfo; }

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

    public List<ProcessingActivityRelatedDataSubject> getDataSubjects() {
        return dataSubjects;
    }

    public void setDataSubjects(List<ProcessingActivityRelatedDataSubject> dataSubjects) {
        this.dataSubjects = dataSubjects;
    }
}
