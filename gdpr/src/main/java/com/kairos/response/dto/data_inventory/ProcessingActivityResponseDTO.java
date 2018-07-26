package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistance.model.data_inventory.ManagingOrganization;
import com.kairos.persistance.model.data_inventory.Staff;
import com.kairos.response.dto.common.AccessorPartyReponseDTO;
import com.kairos.response.dto.common.DataSourceReponseDTO;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import com.kairos.response.dto.common.TransferMethodResponseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityResponseDTO {

    private BigInteger id;

    @NotBlank(message = "Name can't be empty")
    private String name;

    @NotBlank(message = "Discription can't be empty")
    private String description;

    @NotNull(message = "Mangaing department can't be null")
    private ManagingOrganization managingDepartment;

    @NotNull(message = "Process Owner can't be null")
    private Staff processOwner;

    private List<ProcessingPurposeResponseDTO> processingPurposes;

    private List<DataSourceReponseDTO> dataSources;

    private List<AccessorPartyReponseDTO> accessorParties;

    private List<TransferMethodResponseDTO> sourceTransferMethods;

    private List<TransferMethodResponseDTO> destinationTransferMethods;

    private Integer controllerContactInfo;

    private Integer dpoContactInfo;

    private Integer jointControllerContactInfo;

    private Long minDataSubjectVolume;

    private Long maxDataSubjectVolume;

    private Integer dataRetentionPeriod;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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

    public List<DataSourceReponseDTO> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSourceReponseDTO> dataSources) {
        this.dataSources = dataSources;
    }

    public List<AccessorPartyReponseDTO> getAccessorParties() {
        return accessorParties;
    }

    public void setAccessorParties(List<AccessorPartyReponseDTO> accessorParties) {
        this.accessorParties = accessorParties;
    }

    public List<TransferMethodResponseDTO> getSourceTransferMethods() {
        return sourceTransferMethods;
    }

    public void setSourceTransferMethods(List<TransferMethodResponseDTO> sourceTransferMethods) {
        this.sourceTransferMethods = sourceTransferMethods;
    }

    public List<TransferMethodResponseDTO> getDestinationTransferMethods() {
        return destinationTransferMethods;
    }

    public void setDestinationTransferMethods(List<TransferMethodResponseDTO> destinationTransferMethods) {
        this.destinationTransferMethods = destinationTransferMethods;
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
}
