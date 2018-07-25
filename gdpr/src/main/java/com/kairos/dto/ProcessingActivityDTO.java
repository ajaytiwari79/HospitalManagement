package com.kairos.dto;

import com.kairos.utils.custom_annotation.NotNullOrEmpty;

import java.math.BigInteger;
import java.util.List;

public class ProcessingActivityDTO {

    @NotNullOrEmpty(message = "error.message.name.cannot.be.null.or.empty")
    String name;
    @NotNullOrEmpty(message = "error.message.description.cannot.be.null.or.empty")
    String description;
    Long managingDepartmentOrganization;
    Long hostingCountryId;
    Long processOwnerStaff;
    List<BigInteger> dataSubjects;
    List<BigInteger> processingPurposes;
    BigInteger assetTypeId;
    BigInteger orgSecurityMeasureId;
    BigInteger technicalSecurityMeasure;
    Long organisationId;
    int dataRetentionPeriod;

      public int getDataRetentionPeriod() {
        return dataRetentionPeriod;
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

    public Long getManagingDepartmentOrganization() {
        return managingDepartmentOrganization;
    }

    public void setManagingDepartmentOrganization(Long managingDepartmentOrganization) {
        this.managingDepartmentOrganization = managingDepartmentOrganization;
    }

    public Long getHostingCountryId() {
        return hostingCountryId;
    }

    public void setHostingCountryId(Long hostingCountryId) {
        this.hostingCountryId = hostingCountryId;
    }

    public Long getProcessOwnerStaff() {
        return processOwnerStaff;
    }

    public void setProcessOwnerStaff(Long processOwnerStaff) {
        this.processOwnerStaff = processOwnerStaff;
    }

    public List<BigInteger> getDataSubjects() {
        return dataSubjects;
    }

    public void setDataSubjects(List<BigInteger> dataSubjects) {
        this.dataSubjects = dataSubjects;
    }

    public List<BigInteger> getProcessingPurposes() {
        return processingPurposes;
    }

    public void setProcessingPurposes(List<BigInteger> processingPurposes) {
        this.processingPurposes = processingPurposes;
    }

    public BigInteger getAssetTypeId() {
        return assetTypeId;
    }

    public void setAssetTypeId(BigInteger assetTypeId) {
        this.assetTypeId = assetTypeId;
    }

    public BigInteger getOrgSecurityMeasureId() {

        return orgSecurityMeasureId;
    }

    public void setOrgSecurityMeasureId(BigInteger orgSecurityMeasureId) {
        this.orgSecurityMeasureId = orgSecurityMeasureId;
    }

    public BigInteger getTechnicalSecurityMeasure() {
        return technicalSecurityMeasure;
    }

    public void setTechnicalSecurityMeasure(BigInteger technicalSecurityMeasure) {
        this.technicalSecurityMeasure = technicalSecurityMeasure;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public void setDataRetentionPeriod(int dataRetentionPeriod) {
        this.dataRetentionPeriod = dataRetentionPeriod;
    }
}
