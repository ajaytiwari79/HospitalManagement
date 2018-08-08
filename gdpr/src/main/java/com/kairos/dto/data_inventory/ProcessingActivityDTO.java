package com.kairos.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistance.model.data_inventory.ManagingOrganization;
import com.kairos.persistance.model.data_inventory.Staff;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityDTO {


    private BigInteger id;

    @NotBlank(message = "Name can't be empty")
    @Pattern(message = "Numbers and Special characters are not allowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotBlank(message = "Description can't be empty")
    private String description;

    @NotNull(message = "Managing department can't be null")
    private ManagingOrganization managingDepartment;

    //@NotNull(message = "Process Owner can't be null")
    private Staff processOwner;

    private List<BigInteger> processingPurposes;

    private List<BigInteger> dataSources;

    private List<BigInteger> transferMethods;

    private List<BigInteger> accessorParties;

    private List<BigInteger> processingLegalBasis;

    private List<ProcessingActivityDTO> subProcessingActivities=new ArrayList<>();

    private BigInteger responsibilityType;

    private Integer controllerContactInfo;

    private Integer dpoContactInfo;

    private Integer jointControllerContactInfo;

    private Long minDataSubjectVolume;

    private Long maxDataSubjectVolume;

    private Integer dataRetentionPeriod;

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public List<ProcessingActivityDTO> getSubProcessingActivities() { return subProcessingActivities; }

    public void setSubProcessingActivities(List<ProcessingActivityDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }

    public String getName() { return name.trim(); }

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

    public List<BigInteger> getTransferMethods() { return transferMethods; }

    public void setTransferMethods(List<BigInteger> transferMethods) { this.transferMethods = transferMethods; }

    public List<BigInteger> getAccessorParties() { return accessorParties; }

    public void setAccessorParties(List<BigInteger> accessorParties) { this.accessorParties = accessorParties; }

    public List<BigInteger> getProcessingLegalBasis() { return processingLegalBasis; }

    public void setProcessingLegalBasis(List<BigInteger> processingLegalBasis) { this.processingLegalBasis = processingLegalBasis; }

    public BigInteger getResponsibilityType() { return responsibilityType; }

    public void setResponsibilityType(BigInteger responsibilityType) { this.responsibilityType = responsibilityType; }

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
}
