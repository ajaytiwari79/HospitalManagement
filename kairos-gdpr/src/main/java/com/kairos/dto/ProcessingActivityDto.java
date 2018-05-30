package com.kairos.dto;

import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import java.math.BigInteger;
import java.util.List;

public class ProcessingActivityDto {

    @NotNullOrEmpty(message = "error.message.name.cannot.be.null.or.empty")
    String name;
    @NotNullOrEmpty(message = "error.message.description.cannot.be.null.or.empty")
    String description;
    Long managingDepartmentOrganization;
    Long hostingCountryId;
    Long processOwnerStaff;
    List<BigInteger> dataSubjects;
    List<BigInteger> processingPurposes;
    BigInteger assetTypeid;
    BigInteger orgSecurityMeasureid;
    BigInteger technicalSecurityMeasure;
    Long organisationId;
    int dataRetentionPeriod;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getManagingDepartmentOrganization() {
        return managingDepartmentOrganization;
    }

    public Long getHostingCountryId() {
        return hostingCountryId;
    }

    public Long getProcessOwnerStaff() {
        return processOwnerStaff;
    }

    public List<BigInteger> getDataSubjects() {
        return dataSubjects;
    }

    public List<BigInteger> getProcessingPurposes() {
        return processingPurposes;
    }

    public BigInteger getAssetTypeid() {
        return assetTypeid;
    }

    public BigInteger getOrgSecurityMeasureid() {
        return orgSecurityMeasureid;
    }

    public BigInteger getTechnicalSecurityMeasure() {
        return technicalSecurityMeasure;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public int getDataRetentionPeriod() {
        return dataRetentionPeriod;
    }
}
