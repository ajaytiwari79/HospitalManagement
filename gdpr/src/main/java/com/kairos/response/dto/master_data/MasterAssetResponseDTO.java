package com.kairos.response.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.*;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.response.dto.common.AssetTypeBasicResponseDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class MasterAssetResponseDTO {

    private Long id;
    private String name;
    private String description;
    private List<OrganizationTypeDTO> organizationTypes;
    private List<OrganizationSubTypeDTO> organizationSubTypes;
    private List<ServiceCategoryDTO> organizationServices;
    private List<SubServiceCategoryDTO> organizationSubServices;
    private AssetTypeBasicResponseDTO assetType;
    private AssetTypeBasicResponseDTO subAssetType;
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;


    public MasterAssetResponseDTO(Long id, String name,  String description, LocalDate suggestedDate, SuggestedDataStatus suggestedDataStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.suggestedDate = suggestedDate;
        this.suggestedDataStatus = suggestedDataStatus;
    }

}
