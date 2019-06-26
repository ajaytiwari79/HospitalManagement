package com.kairos.dto.gdpr.agreement_template;

import lombok.*;

import javax.validation.constraints.*;

@Getter @Setter @NoArgsConstructor
public class AgreementTemplateDTO {

    protected Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    protected String name;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    protected String description;

    @NotNull(message = "error.message.templateType.notNull")
    protected Long templateTypeId;

    private boolean generalAgreementTemplate;


}
