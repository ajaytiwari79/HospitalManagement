package com.kairos.persistance.model.processing_activity;


import com.kairos.persistance.model.master_data.AssetType;
import com.kairos.persistance.model.master_data.OrganizationalSecurityMeasure;
import com.kairos.persistance.model.master_data.TechnicalSecurityMeasure;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.master_data.DataSubject;
import com.kairos.persistance.model.master_data.ProcessingPurpose;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "processing_activity")
public class ProcessingActivity extends MongoBaseEntity {

    @NotNullOrEmpty(message = "ProcessingActivity.name.cannot.be.null.or.empty")
    String name;
    @NotNullOrEmpty(message = "ProcessingActivity.description.cannot.be.null.or.empty")
    String description;
    Long managingDepartmentOrganization;
    Long hostingCountryId;
    Long processOwnerStaff;
    List<DataSubject> dataSubjects;
    List<ProcessingPurpose> processingPurposes;
    AssetType assetType;
    OrganizationalSecurityMeasure organizationalSecurityMeasure;
    TechnicalSecurityMeasure technicalSecurityMeasure;
    Long organisationId;
    int dataRetentionPeriod;


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

    public List<DataSubject> getDataSubjects() {
        return dataSubjects;
    }

    public void setDataSubjects(List<DataSubject> dataSubjects) {
        this.dataSubjects = dataSubjects;
    }

    public List<ProcessingPurpose> getProcessingPurposes() {
        return processingPurposes;
    }

    public void setProcessingPurposes(List<ProcessingPurpose> processingPurposes) {
        this.processingPurposes = processingPurposes;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public OrganizationalSecurityMeasure getOrganizationalSecurityMeasure() {
        return organizationalSecurityMeasure;
    }

    public void setOrganizationalSecurityMeasure(OrganizationalSecurityMeasure organizationalSecurityMeasure) {
        this.organizationalSecurityMeasure = organizationalSecurityMeasure;
    }

    public TechnicalSecurityMeasure getTechnicalSecurityMeasure() {
        return technicalSecurityMeasure;
    }

    public void setTechnicalSecurityMeasure(TechnicalSecurityMeasure technicalSecurityMeasure) {
        this.technicalSecurityMeasure = technicalSecurityMeasure;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public int getDataRetentionPeriod() {
        return dataRetentionPeriod;
    }

    public void setDataRetentionPeriod(int dataRetentionPeriod) {
        this.dataRetentionPeriod = dataRetentionPeriod;
    }
}
