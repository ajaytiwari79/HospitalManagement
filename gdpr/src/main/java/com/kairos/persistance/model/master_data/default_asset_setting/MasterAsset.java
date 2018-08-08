package com.kairos.persistance.model.master_data.default_asset_setting;


import com.kairos.dto.OrganizationSubTypeDTO;
import com.kairos.dto.OrganizationTypeDTO;
import com.kairos.dto.ServiceCategoryDTO;
import com.kairos.dto.SubServiceCategoryDTO;
import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "master_asset")
public class MasterAsset extends MongoBaseEntity {


    @NotBlank(message = "Name can't be empty")
    private  String name;

    @NotBlank(message = "error.message.name.cannotbe.null.or.empty")
    private String description;

    @NotNull(message = "error.message.cannot.be.null")
    private List<OrganizationTypeDTO> organizationTypes;

    @NotNull
    private List <OrganizationSubTypeDTO> organizationSubTypes;
    @NotNull
    private List <ServiceCategoryDTO> organizationServices;
    @NotNull
    private List <SubServiceCategoryDTO> organizationSubServices;

    private Long countryId;

    @NotNull(message = "Asset type can't be null")
    private BigInteger assetType;

    @NotNull(message = "Asset Sub type can't be null")
    private List<BigInteger> assetSubTypes;

    public BigInteger getAssetType() { return assetType; }

    public void setAssetType(BigInteger assetType) { this.assetType = assetType; }

    public Long getCountryId() {
        return countryId;
    }

    public List<BigInteger> getAssetSubTypes() { return assetSubTypes; }

    public void setAssetSubTypes(List<BigInteger> assetSubTypes) { this.assetSubTypes = assetSubTypes; }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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

    public List<OrganizationTypeDTO> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeDTO> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubTypeDTO> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<ServiceCategoryDTO> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategoryDTO> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<SubServiceCategoryDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategoryDTO> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public MasterAsset(String name, String description, List<OrganizationTypeDTO> organizationTypes,
                       List<OrganizationSubTypeDTO> organizationSubTypes,  List<ServiceCategoryDTO> organizationServices, List<SubServiceCategoryDTO> organizationSubServices) {
        this.name = name;
        this.description = description;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationServices = organizationServices;
        this.organizationSubServices = organizationSubServices;

    }
}
