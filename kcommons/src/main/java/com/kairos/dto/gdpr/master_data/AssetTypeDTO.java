package com.kairos.dto.gdpr.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.BasicRiskDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetTypeDTO {

    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @Valid
    private List<BasicRiskDTO> risks = new ArrayList<>();

    @Valid
    private List<AssetTypeDTO> subAssetTypes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssetTypeDTO> getSubAssetTypes() {
        return subAssetTypes;
    }

    public void setSubAssetTypes(List<AssetTypeDTO> subAssetTypes) {
        this.subAssetTypes = subAssetTypes;
    }

    public AssetTypeDTO(String name) {
        this.name = name;
    }

    public List<BasicRiskDTO> getRisks() { return risks; }

    public void setRisks(List<BasicRiskDTO> risks) { this.risks = risks; }

    public AssetTypeDTO() {
    }
}
