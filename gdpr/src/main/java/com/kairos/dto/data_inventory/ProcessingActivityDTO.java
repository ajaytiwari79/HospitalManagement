package com.kairos.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.metadata.*;
import com.kairos.persistance.model.data_inventory.ManagingOrganization;
import com.kairos.persistance.model.data_inventory.Staff;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityDTO {


    @NotBlank(message = "Name can't be empty")
    @Pattern(message = "Numbers and Special characters are not allowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotBlank(message = "Discription can't be empty")
    private String description;

    @NotNull(message = "Mangaing department can't be null")
    private ManagingOrganization managingDepartment;

    //@NotNull(message = "Process Owner can't be null")
    private Staff processOwner;

    private List<ProcessingPurposeDTO> processingPurposes;

    private List<DataSourceDTO> dataSources;

    private List<TransferMethodDTO> transferMethods;


    private List<AccessorPartyDTO> accessorParties;

    private List<ProcessingLegalBasisDTO> processingLegalBasis;

    private ResponsibilityTypeDTO responsibilityType;

    private Integer controllerContactInfo;

    private Integer dpoContactInfo;

    private Integer jointControllerContactInfo;

    private Long minDataSubjectVolume;

    private Long maxDataSubjectVolume;

    private Integer dataRetentionPeriod;


    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) { this.managingDepartment = managingDepartment; }

    public Staff getProcessOwner() { return processOwner; }

    public void setProcessOwner(Staff processOwner) { this.processOwner = processOwner; }

    public List<ProcessingPurposeDTO> getProcessingPurposes() { return processingPurposes; }

    public void setProcessingPurposes(List<ProcessingPurposeDTO> processingPurposes) { this.processingPurposes = processingPurposes; }

    public List<DataSourceDTO> getDataSources() { return dataSources; }

    public void setDataSources(List<DataSourceDTO> dataSources) { this.dataSources = dataSources; }

    public List<TransferMethodDTO> getTransferMethods() { return transferMethods; }

    public void setTransferMethods(List<TransferMethodDTO> transferMethods) { this.transferMethods = transferMethods; }

    public List<AccessorPartyDTO> getAccessorParties() { return accessorParties; }

    public void setAccessorParties(List<AccessorPartyDTO> accessorParties) { this.accessorParties = accessorParties; }

    public List<ProcessingLegalBasisDTO> getProcessingLegalBasis() { return processingLegalBasis; }

    public void setProcessingLegalBasis(List<ProcessingLegalBasisDTO> processingLegalBasis) { this.processingLegalBasis = processingLegalBasis; }

    public ResponsibilityTypeDTO getResponsibilityType() { return responsibilityType; }

    public void setResponsibilityType(ResponsibilityTypeDTO responsibilityType) { this.responsibilityType = responsibilityType; }

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
