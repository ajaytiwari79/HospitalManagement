package com.kairos.dto.user.equipment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Created by prerna on 12/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class EquipmentDTO {

    private Long id;

    @NotBlank(message = "error.equipment.name.notNullAndEmpty")
    private String name;
    private String description;
    public EquipmentCategoryDTO equipmentCategory;
}
