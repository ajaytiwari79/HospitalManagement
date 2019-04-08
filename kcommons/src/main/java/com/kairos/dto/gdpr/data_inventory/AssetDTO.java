package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.ManagingOrganization;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.RiskSeverity;
import com.kairos.enums.gdpr.AssetAssessor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class AssetDTO {

    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;

    @NotBlank(message = "error.message.HostingLocation.notSelected")
    private String hostingLocation;

    @NotNull(message = "error.message.managingDepartment.notNull")
    private ManagingOrganization managingDepartment;

    @NotNull(message = "error.message.assetOwner.notNull")
    private Staff assetOwner;

    private Set<Long> storageFormats;
    private Set<Long> orgSecurityMeasures;
    private Set<Long> technicalSecurityMeasures;
    private Long processingActivity;
    private Long hostingProvider;
    private Long hostingType;
    private Long dataDisposal;

    private Integer dataRetentionPeriod;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private RiskSeverity riskLevel;
    private AssetAssessor assetAssessor;
    private boolean suggested;

    @NotNull(message = "error.message.assetType.notNull")
    private AssetTypeOrganizationLevelDTO assetType;
    private AssetTypeOrganizationLevelDTO subAssetType;
    private Set<BigInteger> processingActivityIds;
    private Set<BigInteger> subProcessingActivityIds;




}
