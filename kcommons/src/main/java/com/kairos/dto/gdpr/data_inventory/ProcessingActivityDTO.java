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

    public String getName() { return name.trim(); }

    public String getDescription() { return description; }


    public ManagingOrganization getManagingDepartment() { return managingDepartment; }


    public Staff getProcessOwner() { return processOwner; }


    public List<BigInteger> getProcessingPurposes() { return processingPurposes; }


    public List<BigInteger> getDataSources() { return dataSources; }


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

    public ProcessingActivityDTO() {
    }
}
