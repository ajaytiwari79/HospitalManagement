package com.kairos.dto.gdpr.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseDTO {

    protected Long id;
    @NotBlank(message = "error.message.title.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    protected String title;
    @Valid
    protected List<ClauseTagDTO> tags = new ArrayList<>();

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    protected String description;

    @NotEmpty(message = "error.message.templateType.notNull")
    private List<Long> templateTypes;

    public ClauseDTO() {
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public List<ClauseTagDTO> getTags() { return tags; }

    public void setTags(List<ClauseTagDTO> tags) { this.tags = tags; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public List<Long> getTemplateTypes() {
        return templateTypes;
    }

    public void setTemplateTypes(List<Long> templateTypes) {
        this.templateTypes = templateTypes;
    }
}
