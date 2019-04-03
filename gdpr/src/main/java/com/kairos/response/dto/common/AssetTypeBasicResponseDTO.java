package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AssetTypeBasicResponseDTO {

    private Long id;
    private String name;
    private Long organizationId;
    private SuggestedDataStatus suggestedDataStatus;
    private Boolean subAssetType;
    private List<OrganizationLevelRiskDTO> risks;

    public AssetTypeBasicResponseDTO(Long id, String name, Boolean subAssetType) {
        this.id = id;
        this.name = name;
        this.subAssetType = subAssetType;
    }

    public AssetTypeBasicResponseDTO(Long id, String name, Boolean subAssetType, List<OrganizationLevelRiskDTO> risks) {
        this.id = id;
        this.name = name;
        this.subAssetType = subAssetType;
        this.risks = risks;
    }

    public AssetTypeBasicResponseDTO() {
    }


    public Boolean getSubAssetType() { return subAssetType; }

    public void setSubAssetType(Boolean subAssetType) { this.subAssetType = subAssetType; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<OrganizationLevelRiskDTO> getRisks() { return risks; }

    public void setRisks(List<OrganizationLevelRiskDTO> risks) { this.risks = risks; }
}
