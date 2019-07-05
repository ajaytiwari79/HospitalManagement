package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import lombok.*;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@NoArgsConstructor
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

   }
