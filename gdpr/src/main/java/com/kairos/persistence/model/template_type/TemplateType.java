package com.kairos.persistence.model.template_type;

import com.kairos.persistence.model.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Auther vikash patwal
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TemplateType extends BaseEntity {


    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;
    private Long countryId;
    private Long organizationId;

    public String getName() {
        return name.trim();
    }

}