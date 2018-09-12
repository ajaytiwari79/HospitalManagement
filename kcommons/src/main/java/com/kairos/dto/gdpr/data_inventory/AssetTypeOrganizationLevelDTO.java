package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetTypeOrganizationLevelDTO {

    private BigInteger id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @Valid
    private List<OrganizationLevelRiskDTO> risks = new ArrayList<>();
    @Valid
    private List<AssetTypeOrganizationLevelDTO> subAssetTypes = new ArrayList<>();


    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<OrganizationLevelRiskDTO> getRisks() { return risks; }

    public void setRisks(List<OrganizationLevelRiskDTO> risks) { this.risks = risks; }

    public List<AssetTypeOrganizationLevelDTO> getSubAssetTypes() { return subAssetTypes; }

    public void setSubAssetTypes(List<AssetTypeOrganizationLevelDTO> subAssetTypes) { this.subAssetTypes = subAssetTypes; }
}
