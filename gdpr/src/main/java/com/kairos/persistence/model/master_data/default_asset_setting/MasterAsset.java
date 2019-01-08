package com.kairos.persistence.model.master_data.default_asset_setting;


import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
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
    private List<OrganizationTypeDTO> organizationTypeDTOS;
    private List <OrganizationSubTypeDTO> organizationSubTypeDTOS;
    private List <ServiceCategoryDTO> organizationServices;
    private List <SubServiceCategoryDTO> organizationSubServices;
    private Long countryId;
    private BigInteger assetTypeId;
    private BigInteger assetSubTypeId;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;


    public MasterAsset(String name, String description, Long countryId, List<OrganizationTypeDTO> organizationTypeDTOS,
                       List<OrganizationSubTypeDTO> organizationSubTypeDTOS, List<ServiceCategoryDTO> organizationServices, List<SubServiceCategoryDTO> organizationSubServices, SuggestedDataStatus suggestedDataStatus) {
        this.name = name;
        this.description = description;
        this.countryId=countryId;
        this.organizationTypeDTOS = organizationTypeDTOS;
        this.organizationSubTypeDTOS = organizationSubTypeDTOS;
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

    public List<OrganizationTypeDTO> getOrganizationTypeDTOS() {
        return organizationTypeDTOS;
    }

    public MasterAsset setOrganizationTypeDTOS(List<OrganizationTypeDTO> organizationTypeDTOS) { this.organizationTypeDTOS = organizationTypeDTOS; return this;}

    public List<OrganizationSubTypeDTO> getOrganizationSubTypeDTOS() {
        return organizationSubTypeDTOS;
    }

    public MasterAsset setOrganizationSubTypeDTOS(List<OrganizationSubTypeDTO> organizationSubTypeDTOS) { this.organizationSubTypeDTOS = organizationSubTypeDTOS;return this; }

    public List<ServiceCategoryDTO> getOrganizationServices() {
        return organizationServices;
    }

    public MasterAsset setOrganizationServices(List<ServiceCategoryDTO> organizationServices) { this.organizationServices = organizationServices; return this;}

    public List<SubServiceCategoryDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public MasterAsset setOrganizationSubServices(List<SubServiceCategoryDTO> organizationSubServices) { this.organizationSubServices = organizationSubServices; return this;}


    public MasterAsset() {
    }
}
