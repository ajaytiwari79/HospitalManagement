package com.kairos.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.RiskSeverity;
import com.kairos.gdpr.ManagingOrganization;
import com.kairos.gdpr.Staff;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetDTO {

    private BigInteger id;

    @NotBlank(message = "name can't be empty ")
    @Pattern(message = "Number and Special characters are not allowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotBlank(message = "description  can't be  Empty ")
    private String description;


    @NotBlank(message = "Hosting Location can't be Empty")
    private String hostingLocation;

    @NotNull(message = "Managing department can't be empty")
    @Valid
    private ManagingOrganization managingDepartment;

    // @NotNull(message = "Asset Owner can't be Empty")
    //@Valid
    private Staff assetOwner;

    private List<BigInteger> storageFormats;

    private List<BigInteger> orgSecurityMeasures;

    private List<BigInteger> technicalSecurityMeasures;

    private BigInteger processingActivity;

    private BigInteger hostingProvider;

    private BigInteger hostingType;

    private BigInteger dataDisposal;

    @NotNull(message = "Asset  Types can't be null")
    private BigInteger assetType;


    public void setId(BigInteger id) { this.id = id; }

    private List<BigInteger> assetSubTypes = new ArrayList<>();

    private Integer dataRetentionPeriod;

    private Long minDataSubjectVolume;

    private Long maxDataSubjectVolume;

    private RiskSeverity risk;

    private Boolean active;

    public BigInteger getId() { return id; }

    public String getName() { return name.trim(); }

    public String getDescription() { return description; }

    public String getHostingLocation() { return hostingLocation; }

    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public Staff getAssetOwner() { return assetOwner; }

    public List<BigInteger> getStorageFormats() { return storageFormats; }

    public List<BigInteger> getOrgSecurityMeasures() { return orgSecurityMeasures; }

    public List<BigInteger> getTechnicalSecurityMeasures() { return technicalSecurityMeasures; }

    public BigInteger getProcessingActivity() { return processingActivity; }

    public BigInteger getHostingProvider() { return hostingProvider; }

    public BigInteger getHostingType() { return hostingType; }

    public BigInteger getDataDisposal() { return dataDisposal; }
    public BigInteger getAssetType() { return assetType; }

    public List<BigInteger> getAssetSubTypes() { return assetSubTypes; }

    public Integer getDataRetentionPeriod() { return dataRetentionPeriod; }

    public Long getMinDataSubjectVolume() { return minDataSubjectVolume; }

    public Long getMaxDataSubjectVolume() { return maxDataSubjectVolume; }

    public RiskSeverity getRisk() { return risk; }

    public Boolean getActive() { return active; }

    public AssetDTO() {
    }
}
