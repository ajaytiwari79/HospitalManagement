package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionnaireTemplateDTO {


    @NotBlank(message = "name.cannot.be.empty.or.null")
    @Pattern(message = "Number and Special character are not allowed for Title",regexp ="^[a-zA-Z\\s]+$" )
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Template type cannot be empty ")
    private String templateType;

    private BigInteger assetType;

    private Long countryId;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateType() {
        return templateType.trim();
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public BigInteger getAssetType() {
        return assetType;
    }

    public void setAssetType(BigInteger assetType) {
        this.assetType = assetType;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public MasterQuestionnaireTemplateDTO() {
    }
}
