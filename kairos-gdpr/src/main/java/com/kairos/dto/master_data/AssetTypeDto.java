package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetTypeDto {


    private BigInteger id;

    @NotNullOrEmpty(message = "Title cannot be empty ")
    @Pattern(message = "Numbers and Special characters are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @Valid
    private List<AssetTypeDto> subAssetTypes;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssetTypeDto> getSubAssetTypes() {
        return subAssetTypes;
    }

    public void setSubAssetTypes(List<AssetTypeDto> subAssetTypes) {
        this.subAssetTypes = subAssetTypes;
    }
}
