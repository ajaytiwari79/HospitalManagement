package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class TemplateTypeDTO {

    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;

}
