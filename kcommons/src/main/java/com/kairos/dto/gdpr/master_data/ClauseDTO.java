package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseDTO {

    protected Long id;

    @NotBlank(message = "error.message.title.notNull.orEmpty")
    @Pattern(message = "error.message.name.special.character.notAllowed", regexp = "^[a-zA-Z0-9\\s]+$")
    protected String title;

    @Valid
    protected List<ClauseTagDTO> tags = new ArrayList<>();

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    protected String description;

    @NotEmpty(message = "error.message.templateType.notNull")
    private List<Long> templateTypes;


  }
