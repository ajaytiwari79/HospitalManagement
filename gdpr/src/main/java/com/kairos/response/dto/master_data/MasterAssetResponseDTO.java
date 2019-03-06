package com.kairos.response.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.response.dto.common.AssetTypeBasicResponseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterAssetResponseDTO {

    @NotNull(message = "error.message.id.notnull")
    private Long id;
    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    private List<OrganizationTypeDTO> organizationTypes;
    private List<OrganizationSubTypeDTO> organizationSubTypes;
    private List<ServiceCategoryDTO> organizationServices;
    private List<SubServiceCategoryDTO> organizationSubServices;
    private AssetTypeBasicResponseDTO assetType;
    private AssetTypeBasicResponseDTO assetSubType;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;


    public MasterAssetResponseDTO(@NotNull(message = "error.message.id.notnull") Long id, @NotBlank(message = "error.message.name.notNull.orEmpty") String name, @NotBlank(message = "error.message.description.notNull.orEmpty") String description,LocalDate suggestedDate, SuggestedDataStatus suggestedDataStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.suggestedDate = suggestedDate;
        this.suggestedDataStatus = suggestedDataStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssetTypeBasicResponseDTO getAssetType() { return assetType; }

    public void setAssetType(AssetTypeBasicResponseDTO assetType) { this.assetType = assetType; }

    public AssetTypeBasicResponseDTO getAssetSubType() { return assetSubType; }

    public void setAssetSubType(AssetTypeBasicResponseDTO assetSubType) { this.assetSubType = assetSubType; }

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

    public void setOrganizationServices(List<ServiceCategoryDTO> organizationServices) { this.organizationServices = organizationServices; }

    public List<SubServiceCategoryDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategoryDTO> organizationSubServices) { this.organizationSubServices = organizationSubServices; }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }
}
