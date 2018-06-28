package com.kairos.user.equipment;

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

    private String name;
    private String description;

    public EquipmentCategoryDTO(){
        //default constructor
    }

    public EquipmentCategoryDTO(Long id){
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
