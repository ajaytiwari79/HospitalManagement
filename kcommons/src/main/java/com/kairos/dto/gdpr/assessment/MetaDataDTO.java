package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties
@Getter @Setter @NoArgsConstructor
public class MetaDataDTO {

    @NotNull(message = "error.message.id.null")
    private Long metadataId;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;

}
