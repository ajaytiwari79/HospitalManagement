package com.kairos.persistence.model.template_type;

import com.kairos.persistence.model.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Auther
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TemplateType extends BaseEntity {


    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.special.character.notAllowed",regexp = "^[a-zA-Z0-9\\s]+$")
    private String name;
    private Long countryId;
    private Long organizationId;

    public String getName() {
        return name.trim();
    }

}