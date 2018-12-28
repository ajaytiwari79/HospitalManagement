package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.ManagingOrganization;
import com.kairos.dto.gdpr.Staff;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityDTO {


    private BigInteger id;
    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    @NotNull(message = "Managaing Department cannot be Null")
    private ManagingOrganization managingDepartment;
    @NotNull(message = "error.message.processOwner.notNull")
    private Staff processOwner;
    private List<BigInteger> processingPurposes;
    private List<BigInteger> dataSources;
    private List<BigInteger> transferMethods;
    private List<BigInteger> accessorParties;
    private List<BigInteger> processingLegalBasis;
    private List<ProcessingActivityDTO> subProcessingActivities=new ArrayList<>();
    private List<ProcessingActivityRelatedDataSubject> dataSubjectList=new ArrayList<>();
    private BigInteger responsibilityType;
    private Integer controllerContactInfo;
    private Integer dpoContactInfo;
    private Integer jointControllerContactInfo;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private Integer dataRetentionPeriod;
    private boolean suggestToCountryAdmin;
    private boolean suggested=false;

    public boolean isSuggested() { return suggested; }

    public void setSuggested(boolean suggested) { this.suggested = suggested; }

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public List<ProcessingActivityDTO> getSubProcessingActivities() { return subProcessingActivities; }

    public String getName() { return name.trim(); }

    public String getDescription() { return description; }

    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public Staff getProcessOwner() { return processOwner; }

    public List<BigInteger> getProcessingPurposes() { return processingPurposes; }

    public List<BigInteger> getDataSources() { return dataSources; }

    public void setName(String name) { this.name = name; }

    public void setDescription(String description) { this.description = description; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) {this.managingDepartment = managingDepartment; }

    public void setProcessOwner(Staff processOwner) { this.processOwner = processOwner; }

    public void setProcessingPurposes(List<BigInteger> processingPurposes) { this.processingPurposes = processingPurposes; }

    public void setDataSources(List<BigInteger> dataSources) { this.dataSources = dataSources; }

    public void setTransferMethods(List<BigInteger> transferMethods) { this.transferMethods = transferMethods; }

    public void setAccessorParties(List<BigInteger> accessorParties) { this.accessorParties = accessorParties; }

    public void setProcessingLegalBasis(List<BigInteger> processingLegalBasis) { this.processingLegalBasis = processingLegalBasis; }

    public void setSubProcessingActivities(List<ProcessingActivityDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }

    public void setResponsibilityType(BigInteger responsibilityType) { this.responsibilityType = responsibilityType; }

    public void setControllerContactInfo(Integer controllerContactInfo) { this.controllerContactInfo = controllerContactInfo; }

    public void setDpoContactInfo(Integer dpoContactInfo) { this.dpoContactInfo = dpoContactInfo; }

    public void setJointControllerContactInfo(Integer jointControllerContactInfo) { this.jointControllerContactInfo = jointControllerContactInfo; }

    public void setMinDataSubjectVolume(Long minDataSubjectVolume) { this.minDataSubjectVolume = minDataSubjectVolume; }

    public void setMaxDataSubjectVolume(Long maxDataSubjectVolume) { this.maxDataSubjectVolume = maxDataSubjectVolume; }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) { this.dataRetentionPeriod = dataRetentionPeriod; }

    public boolean isSuggestToCountryAdmin() { return suggestToCountryAdmin; }

    public void setSuggestToCountryAdmin(boolean suggestToCountryAdmin) { this.suggestToCountryAdmin = suggestToCountryAdmin; }

    public List<BigInteger> getTransferMethods() { return transferMethods; }

    public List<BigInteger> getAccessorParties() { return accessorParties; }

    public List<BigInteger> getProcessingLegalBasis() { return processingLegalBasis; }

    public BigInteger getResponsibilityType() { return responsibilityType; }

    public Integer getControllerContactInfo() { return controllerContactInfo; }

    public Integer getDpoContactInfo() { return dpoContactInfo; }

    public Integer getJointControllerContactInfo() { return jointControllerContactInfo; }

    public Long getMinDataSubjectVolume() { return minDataSubjectVolume; }

    public Long getMaxDataSubjectVolume() { return maxDataSubjectVolume; }

    public Integer getDataRetentionPeriod() { return dataRetentionPeriod; }

    public List<ProcessingActivityRelatedDataSubject> getDataSubjectList() {
        return dataSubjectList;
    }

    public void setDataSubjectList(List<ProcessingActivityRelatedDataSubject> dataSubjectList) {
        this.dataSubjectList = dataSubjectList;
    }

    public ProcessingActivityDTO() {
    }
}
