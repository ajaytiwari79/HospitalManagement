package com.kairos.dto.gdpr.agreement_template;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

public class AgreementTemplateDTO {

    protected Long id;
    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    protected String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    protected String description;
    @NotNull(message = "error.message.templateType.notNull")
    protected Long templateTypeId;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Long getTemplateTypeId() { return templateTypeId; }

    public void setTemplateTypeId(Long templateTypeId) { this.templateTypeId = templateTypeId; }
}
