package com.kairos.response.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.common.RiskBasicResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class AssetTypeRiskResponseDTO {

    @NotNull(message = "error.message.id.notnull")
    private Long id;
    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;
    private List<RiskBasicResponseDTO> risks=new ArrayList<>();
    private Boolean hasSubAsset;
    private List<AssetTypeRiskResponseDTO> subAssetTypes=new ArrayList<>();

    public AssetTypeRiskResponseDTO(@NotNull(message = "error.message.id.notnull") Long id, @NotBlank(message = "error.message.name.notNull.orEmpty") String name) {
        this.id = id;
        this.name = name;
    }
}
