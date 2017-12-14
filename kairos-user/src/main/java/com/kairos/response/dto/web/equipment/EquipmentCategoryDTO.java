package com.kairos.response.dto.web.equipment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 12/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentCategoryDTO {
    @NotEmpty(message = "error.equipmentCategory.id.notNullAndEmpty") @NotNull(message = "error.equipmentCategory.id.notNullAndEmpty")
    private long id;

    public EquipmentCategoryDTO(){
        //default constructor
    }

    public EquipmentCategoryDTO(Long id){
        this.id = id;
    }
}
