package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties
public class MetaDataDTO {

    @NotNull(message = "error.message.id.null")
    private Long metadataId;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;

    public Long getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(Long metadataId) {
        this.metadataId = metadataId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MetaDataDTO() {
    }
}
