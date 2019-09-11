package com.kairos.persistence.model.master_data.data_category_element;

import com.kairos.persistence.model.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DataElement extends BaseEntity {


    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    private Long countryId;
    private Long organizationId;

    public DataElement(String name, Long countryId) {
        this.name = name;
        this.countryId = countryId;
    }

    public DataElement(Long organizationId,String name ) {
        this.organizationId = organizationId;
        this.name = name;
    }

    public DataElement(@NotBlank(message = "error.message.name.notNull.orEmpty") @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$") String name) {
        this.name = name;
    }
}
