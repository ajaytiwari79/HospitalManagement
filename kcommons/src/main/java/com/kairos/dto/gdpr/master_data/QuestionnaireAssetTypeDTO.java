package com.kairos.dto.gdpr.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.BasicRiskDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireAssetTypeDTO {

    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    private boolean subAssetType;

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

    public QuestionnaireAssetTypeDTO(String name) {
        this.name = name;
    }

    public boolean isSubAssetType() {
        return subAssetType;
    }

    public void setSubAssetType(boolean subAssetType) {
        this.subAssetType = subAssetType;
    }

    public QuestionnaireAssetTypeDTO(Long id, @NotBlank(message = "error.message.name.notNull.orEmpty") @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$") String name, boolean subAssetType) {
        this.id = id;
        this.name = name;
        this.subAssetType = subAssetType;
    }

    public QuestionnaireAssetTypeDTO() {
    }
}
