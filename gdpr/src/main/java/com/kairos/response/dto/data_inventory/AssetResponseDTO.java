package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.gdpr.AssetAssessor;
import com.kairos.persistence.model.embeddables.ManagingOrganization;
import com.kairos.persistence.model.embeddables.Staff;
import com.kairos.response.dto.common.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class AssetResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String hostingLocation;
    private ManagingOrganization managingDepartment;
    private Staff assetOwner;
    private List<StorageFormatResponseDTO> storageFormats;
    private List<OrganizationalSecurityMeasureResponseDTO> orgSecurityMeasures;
    private List<TechnicalSecurityMeasureResponseDTO> technicalSecurityMeasures;
    private HostingProviderResponseDTO hostingProvider;
    private HostingTypeResponseDTO hostingType;
    private DataDisposalResponseDTO dataDisposal;
    private AssetTypeBasicResponseDTO assetType;
    private AssetTypeBasicResponseDTO subAssetType;
    private Integer dataRetentionPeriod;
    private Long minDataSubjectVolume;
    private Long maxDataSubjectVolume;
    private boolean active;
    private AssetAssessor assetAssessor;
    private boolean suggested;
    private List<RelatedProcessingActivityResponseDTO> processingActivities;

}
