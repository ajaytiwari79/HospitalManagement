package com.kairos.response.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class AssetTypeResponseDTO {

    private Long id;
    private String name;
    private List<AssetTypeResponseDTO> subAssetTypes=new ArrayList<>();
    private List<OrganizationLevelRiskDTO> risks=new ArrayList<>();
    private Boolean hasSubAsset;
}
