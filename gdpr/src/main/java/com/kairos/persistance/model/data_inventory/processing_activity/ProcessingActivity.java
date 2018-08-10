package com.kairos.persistance.model.data_inventory.processing_activity;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.data_inventory.ManagingOrganization;
import com.kairos.persistance.model.data_inventory.Staff;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "processing_activity")
public class ProcessingActivity extends MongoBaseEntity {


    @NotBlank(message = "Name can't be empty")
    private String name;

    @NotBlank(message = "Discription can't be empty")
    private String description;

    @NotNull(message = "Mangaing department can't be null")
    private ManagingOrganization managingDepartment;

    @NotNull(message = "Process Owner can't be null")
    private Staff processOwner;

    private List<BigInteger> processingPurposes;

    private List<BigInteger> dataSources;

    private List<BigInteger> dataDestinations;

    private List<BigInteger> sourceTransferMethods;

    private List<BigInteger> destinationTransferMethods;


    private Integer controllerContactInfo;

    private Integer dpoContactInfo;

    private Integer jointControllerContactInfo;

    private Long minDataSubjectVolume;

    private Long maxDataSubjectVolume;

    private Integer dataRetentionPeriod;

    private List<BigInteger> accessorParties;

    private Long countryId;

    private Boolean isActive;

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

    public List<BigInteger> getDataDestinations() { return dataDestinations; }

    public void setDataDestinations(List<BigInteger> dataDestinations) { this.dataDestinations = dataDestinations; }

    public List<BigInteger> getSourceTransferMethods() { return sourceTransferMethods; }

    public void setSourceTransferMethods(List<BigInteger> sourceTransferMethods) { this.sourceTransferMethods = sourceTransferMethods; }

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

    public Long getCountryId() { return countryId;
    }

    public void setCountryId(Long countryId) { this.countryId = countryId;
    }

    public List<BigInteger> getDestinationTransferMethods() {
        return destinationTransferMethods;
    }

    public void setDestinationTransferMethods(List<BigInteger> destinationTransferMethods) {
        this.destinationTransferMethods = destinationTransferMethods;
    }

    public Boolean getActive() { return isActive; }

    public void setActive(Boolean active) { isActive = active; }
    public ProcessingActivity(String name, String description, Long countryId, ManagingOrganization managingDepartment, Staff processOwner) {
        this.name = name;
        this.description = description;
        this.managingDepartment = managingDepartment;
        this.processOwner = processOwner;
        this.countryId=countryId;
    }

    public ProcessingActivity(String name,  String description, Long countryId, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.isActive = isActive;
    }
}
