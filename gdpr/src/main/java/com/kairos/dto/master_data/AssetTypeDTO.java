package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custom_annotation.NotNullOrEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetTypeDTO {

    private BigInteger id;

    @NotNullOrEmpty(message = "Name cannot be empty ")
    @Pattern(message = "Numbers and Special characters are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @Valid
    private List<AssetTypeDTO> subAssetTypes=new ArrayList<>();

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

    public List<AssetTypeDTO> getSubAssetTypes() {
        return subAssetTypes;
    }

    public void setSubAssetTypes(List<AssetTypeDTO> subAssetTypes) {
        this.subAssetTypes = subAssetTypes;
    }
}
