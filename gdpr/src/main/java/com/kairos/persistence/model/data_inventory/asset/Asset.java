package com.kairos.persistence.model.data_inventory.asset;


import com.kairos.enums.gdpr.AssetAssessor;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.ManagingOrganization;
import com.kairos.persistence.model.embeddables.Staff;
import com.kairos.persistence.model.master_data.default_asset_setting.*;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "assetWithProcessingActivity",
                classes = @ConstructorResult(
                        targetClass = AssetBasicResponseDTO.class,
                        columns = {
                                @ColumnResult(name = "id"),
                                @ColumnResult(name = "name"),
                                @ColumnResult(name = "processingActivityId", type= BigInteger.class),
                                @ColumnResult(name = "processingActivityName", type=String.class),
                                @ColumnResult(name = "subProcessingActivity", type = boolean.class),
                                @ColumnResult(name = "parentProcessingActivityId",type = BigInteger.class),
                                @ColumnResult(name = "parentProcessingActivityName",type = String.class)
                        }
                )
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "getAllAssetRelatedProcessingActivityData",resultSetMapping = "assetWithProcessingActivity",resultClass = AssetBasicResponseDTO.class,
                query = " select AST.id as id,AST.name  as name,PA.id as processingActivityId , PA.name as processingActivityName , PA.is_sub_processing_activity as subProcessingActivity , PPA.id as parentProcessingActivityId ,PPA.name as parentProcessingActivityName from asset AST" +
                        " left join processing_activity_assets PAA on PAA.assets_id=AST.id " +
                        " left join processing_activity PA on PA.id = PAA.processing_activity_id " +
                        " left join processing_activity PPA on PA.processing_activity_id = PPA.id" +
                        " where AST.organization_id = ?1 and AST.deleted = false and PA.id is not null"),
})
public class Asset extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    private Long countryId;
    private String hostingLocation;
    @Embedded
    private ManagingOrganization managingDepartment;
    @Embedded
    private Staff assetOwner;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<StorageFormat> storageFormats  = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY)
    private List<OrganizationalSecurityMeasure> orgSecurityMeasures  = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY)
    private List<TechnicalSecurityMeasure> technicalSecurityMeasures  = new ArrayList<>();
    @OneToOne
    private HostingProvider hostingProvider;
    @OneToOne
    private HostingType hostingType;
    @OneToOne
    private DataDisposal dataDisposal;
    @OneToOne
    private AssetType assetType;
    @OneToOne
    private AssetType subAssetType;
    private Integer dataRetentionPeriod;
    @NotNull(message = "Status can't be empty")
    private boolean active=true;
    private boolean suggested;
    private AssetAssessor assetAssessor;
    private Long organizationId;


    public Asset(String name, String description, String hostingLocation, ManagingOrganization managingDepartment, Staff assetOwner) {
        this.name = name;
        this.description = description;
        this.hostingLocation=hostingLocation;
        this.assetOwner=assetOwner;
        this.managingDepartment=managingDepartment;
    }


    public Asset(String name, String description, boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }
    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }


    public Asset() {
    }

    public Asset(Long id ) {
        this.id = id;
    }



    public boolean isSuggested() { return suggested; }

    public void setSuggested(boolean suggested) { this.suggested = suggested; }

    public AssetAssessor getAssetAssessor() { return assetAssessor; }

    public void setAssetAssessor(AssetAssessor assetAssessor) { this.assetAssessor = assetAssessor; }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCountryId() { return countryId; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public Integer getDataRetentionPeriod() { return dataRetentionPeriod; }

    public void setDataRetentionPeriod(Integer dataRetentionPeriod) { this.dataRetentionPeriod = dataRetentionPeriod; }

    public String getHostingLocation() { return hostingLocation; }

    public void setHostingLocation(String hostingLocation) { this.hostingLocation = hostingLocation; }
    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) { this.managingDepartment = managingDepartment; }

    public Staff getAssetOwner() { return assetOwner; }

    public void setAssetOwner(Staff assetOwner) { this.assetOwner = assetOwner; }

    public List<StorageFormat> getStorageFormats() {
        return storageFormats;
    }

    public void setStorageFormats(List<StorageFormat> storageFormats) {
        this.storageFormats = storageFormats;
    }

    public List<OrganizationalSecurityMeasure> getOrgSecurityMeasures() {
        return orgSecurityMeasures;
    }

    public void setOrgSecurityMeasures(List<OrganizationalSecurityMeasure> orgSecurityMeasures) {
        this.orgSecurityMeasures = orgSecurityMeasures;
    }

    public List<TechnicalSecurityMeasure> getTechnicalSecurityMeasures() {
        return technicalSecurityMeasures;
    }

    public void setTechnicalSecurityMeasures(List<TechnicalSecurityMeasure> technicalSecurityMeasures) {
        this.technicalSecurityMeasures = technicalSecurityMeasures;
    }

    public HostingProvider getHostingProvider() {
        return hostingProvider;
    }

    public void setHostingProvider(HostingProvider hostingProvider) {
        this.hostingProvider = hostingProvider;
    }

    public HostingType getHostingType() {
        return hostingType;
    }

    public void setHostingType(HostingType hostingType) {
        this.hostingType = hostingType;
    }

    public DataDisposal getDataDisposal() {
        return dataDisposal;
    }

    public void setDataDisposal(DataDisposal dataDisposal) {
        this.dataDisposal = dataDisposal;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public AssetType getSubAssetType() {
        return subAssetType;
    }

    public void setSubAssetType(AssetType subAssetType) {
        this.subAssetType = subAssetType;
    }


}


