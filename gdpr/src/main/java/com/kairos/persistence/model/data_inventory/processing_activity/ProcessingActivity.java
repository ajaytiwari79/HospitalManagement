package com.kairos.persistence.model.data_inventory.processing_activity;


import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.dto.gdpr.ManagingOrganization;
import com.kairos.dto.gdpr.Staff;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "processing_activity")
public class ProcessingActivity extends MongoBaseEntity {

    @NotBlank(message = "Name can't be empty")
    private String name;
    @NotBlank(message = "Description can't be empty")
    private String description;
    private ManagingOrganization managingDepartment;
    private Staff processOwner;
    private List<ProcessingActivityRelatedDataSubject> dataSubjects = new ArrayList<>();
    private List<BigInteger> linkedAssets = new ArrayList<>();
    private List<BigInteger> processingPurposes;
    private List<BigInteger> dataSources;
    private List<BigInteger> transferMethods;
    private List<BigInteger> accessorParties;
    private List<BigInteger> processingLegalBasis;
    private List<BigInteger> subProcessingActivities = new ArrayList<>();
    private BigInteger responsibilityType;
    private Integer controllerContactInfo;
    private Integer dpoContactInfo;
    private Integer jointControllerContactInfo;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private Integer dataRetentionPeriod;
    private boolean active = true;
    private boolean subProcess;
    private Set<BigInteger> risks = new HashSet<>();
    private boolean suggested;

    public ProcessingActivity() {
    }

    public ProcessingActivity(String name, String description, ManagingOrganization managingDepartment, Staff processOwner) {
        this.name = name;
        this.description = description;
        this.managingDepartment = managingDepartment;
        this.processOwner = processOwner;
    }

    public ProcessingActivity(String name, String description, boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public boolean isSuggested() { return suggested; }

    public void setSuggested(boolean suggested) { this.suggested = suggested; }

    public Set<BigInteger> getRisks() { return risks; }

    public void setRisks(Set<BigInteger> risks) { this.risks = risks; }

    public List<ProcessingActivityRelatedDataSubject> getDataSubjects() { return dataSubjects; }
    public void setDataSubjects(List<ProcessingActivityRelatedDataSubject> dataSubjects) { this.dataSubjects = dataSubjects; }

    public List<BigInteger> getSubProcessingActivities() { return subProcessingActivities; }

    public void setSubProcessingActivities(List<BigInteger> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    public boolean isSubProcess() {
        return subProcess;
    }

    public void setSubProcess(boolean subProcess) { this.subProcess = subProcess; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) { this.managingDepartment = managingDepartment; }

    public Staff getProcessOwner() { return processOwner; }

    public void setProcessOwner(Staff processOwner) { this.processOwner = processOwner; }

    public List<BigInteger> getProcessingPurposes() { return processingPurposes; }

    public void setProcessingPurposes(List<BigInteger> processingPurposes) { this.processingPurposes = processingPurposes; }

    public List<BigInteger> getDataSources() { return dataSources; }

    public void setDataSources(List<BigInteger> dataSources) { this.dataSources = dataSources; }

    public Integer getControllerContactInfo() { return controllerContactInfo; }

    public void setControllerContactInfo(Integer controllerContactInfo) { this.controllerContactInfo = controllerContactInfo; }

    public Integer getDpoContactInfo() { return dpoContactInfo; }

    public void setDpoContactInfo(Integer dpoContactInfo) { this.dpoContactInfo = dpoContactInfo; }

    public Integer getJointControllerContactInfo() { return jointControllerContactInfo; }

    public void setJointControllerContactInfo(Integer jointControllerContactInfo) { this.jointControllerContactInfo = jointControllerContactInfo; }

    public Long getMinDataSubjectVolume() { return minDataSubjectVolume; }

    public void setMinDataSubjectVolume(Long minDataSubjectVolume) { this.minDataSubjectVolume = minDataSubjectVolume; }

    public Long getMaxDataSubjectVolume() { return maxDataSubjectVolume; }

    public void setMaxDataSubjectVolume(Long maxDataSubjectVolume) { this.maxDataSubjectVolume = maxDataSubjectVolume; }

    public Integer getDataRetentionPeriod() { return dataRetentionPeriod; }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) { this.dataRetentionPeriod = dataRetentionPeriod; }

    public List<BigInteger> getAccessorParties() { return accessorParties; }

    public void setAccessorParties(List<BigInteger> accessorParties) { this.accessorParties = accessorParties; }

    public List<BigInteger> getTransferMethods() { return transferMethods; }

    public void setTransferMethods(List<BigInteger> transferMethods) { this.transferMethods = transferMethods; }

    public BigInteger getResponsibilityType() { return responsibilityType; }

    public void setResponsibilityType(BigInteger responsibilityType) { this.responsibilityType = responsibilityType; }

    public List<BigInteger> getProcessingLegalBasis() { return processingLegalBasis; }

    public void setProcessingLegalBasis(List<BigInteger> processingLegalBasis) { this.processingLegalBasis = processingLegalBasis; }

    public List<BigInteger> getLinkedAssets() { return linkedAssets; }

    public void setLinkedAssets(List<BigInteger> linkedAssets) { this.linkedAssets = linkedAssets; }


}
