package com.kairos.dto.gdpr.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionnaireTemplateDTO {

    private BigInteger id;

    @NotBlank(message = "name.cannot.be.empty.or.null")
    @Pattern(message = "Number and Special character are not allowed for Title",regexp ="^[a-zA-Z\\s]+$" )
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Template type cannot be empty ")
    private String templateType;

    private BigInteger assetType;

    public String getName() {
        return name.trim();
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

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public MasterQuestionnaireTemplateDTO() {
    }
}
