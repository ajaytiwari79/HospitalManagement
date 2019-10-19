package com.kairos.dto.user.equipment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 12/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class EquipmentCategoryDTO {
    @NotEmpty(message = "error.equipmentCategory.id.notNullAndEmpty") @NotNull(message = "error.equipmentCategory.id.notNullAndEmpty")
    private long id;

    private String name;
    private String description;

}
