package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireAssetTypeDTO {

    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.name.special.character.notAllowed", regexp = "^[a-zA-Z0-9\\s]+$")
    private String name;

    private boolean subAssetType;

    public String getName() {
        return name.trim();
    }
}
