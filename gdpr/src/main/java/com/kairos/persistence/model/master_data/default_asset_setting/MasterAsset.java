package com.kairos.persistence.model.master_data.default_asset_setting;


import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Document
public class MasterAsset extends MongoBaseEntity {


    @NotBlank(message = "Name can't be empty")
    private  String name;
    @NotBlank(message = "error.message.name.cannotbe.null.or.empty")
    private String description;
    private List<OrganizationType> organizationTypes;
    private List <OrganizationSubType> organizationSubTypes;
    private List <ServiceCategory> organizationServices;
    private List <SubServiceCategory> organizationSubServices;
    private Long countryId;
    private BigInteger assetTypeId;
    private BigInteger assetSubTypeId;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;


    public MasterAsset(String name, String description,Long countryId, List<OrganizationType> organizationTypes,
                       List<OrganizationSubType> organizationSubTypes, List<ServiceCategory> organizationServices, List<SubServiceCategory> organizationSubServices,SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.description = description;
        this.countryId=countryId;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationServices = organizationServices;
        this.organizationSubServices = organizationSubServices;
        this.suggestedDataStatus=suggestedDataStatus;

    }

    public MasterAsset(String name,  String description, Long countryId, LocalDate suggestedDate, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.description = description;
        this.countryId = countryId;
        this.suggestedDate = suggestedDate;
        this.suggestedDataStatus = suggestedDataStatus;
    }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public MasterAsset setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; return this; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public MasterAsset setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus;return this; }

    public Long getCountryId() { return countryId; }

    public BigInteger getAssetTypeId() { return assetTypeId; }

    public void setAssetTypeId(BigInteger assetTypeId) { this.assetTypeId = assetTypeId; }

    public BigInteger getAssetSubTypeId() { return assetSubTypeId; }

    public void setAssetSubTypeId(BigInteger assetSubTypeId) { this.assetSubTypeId = assetSubTypeId; }

    public MasterAsset setCountryId(Long countryId) { this.countryId = countryId; return this;}

    public String getName() {
        return name;
    }

    public MasterAsset setName(String name) { this.name = name;return this; }

    public String getDescription() {
        return description;
    }

    public MasterAsset setDescription(String description) { this.description = description;return this; }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public MasterAsset setOrganizationTypes(List<OrganizationType> organizationTypes) { this.organizationTypes = organizationTypes; return this;}

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public MasterAsset setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes;return this; }

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public MasterAsset setOrganizationServices(List<ServiceCategory> organizationServices) { this.organizationServices = organizationServices; return this;}

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public MasterAsset setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) { this.organizationSubServices = organizationSubServices; return this;}


    public MasterAsset() {
    }
}
