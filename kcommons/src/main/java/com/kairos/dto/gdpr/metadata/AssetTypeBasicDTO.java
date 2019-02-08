package com.kairos.dto.gdpr.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetTypeBasicDTO {

    private Long id;
    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @Valid
    private List<AssetTypeBasicDTO> subAssetTypes = new ArrayList<>();

    private Boolean subAssetType;


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name.trim(); }

    public void setName(String name) { this.name = name; }

    public List<AssetTypeBasicDTO> getSubAssetTypes() { return subAssetTypes; }

    public void setSubAssetTypes(List<AssetTypeBasicDTO> subAssetTypes) { this.subAssetTypes = subAssetTypes; }

    public Boolean getSubAssetType() {
        return subAssetType;
    }

    public void setSubAssetType(Boolean subAssetType) {
        this.subAssetType = subAssetType;
    }
}