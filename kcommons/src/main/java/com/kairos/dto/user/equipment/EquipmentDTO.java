package com.kairos.dto.user.equipment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 12/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentDTO {

    private Long id;

    @NotBlank(message = "error.equipment.name.notNullAndEmpty")
    private String name;
    private String description;
    public EquipmentCategoryDTO equipmentCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public EquipmentCategoryDTO getEquipmentCategory() {
        return equipmentCategory;
    }

    public void setEquipmentCategory(EquipmentCategoryDTO equipmentCategory) {
        this.equipmentCategory = equipmentCategory;
    }

    public EquipmentDTO(){
        //default constructor
    }

    public EquipmentDTO(String name, String description, EquipmentCategoryDTO equipmentCategory){
        this.name = name;
        this.description = description;
        this.equipmentCategory = equipmentCategory;
    }
}
