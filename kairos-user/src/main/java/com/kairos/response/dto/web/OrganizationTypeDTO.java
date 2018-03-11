package com.kairos.response.dto.web;

import com.kairos.persistence.model.organization.Level;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by prabjot on 8/11/17.
 */
public class OrganizationTypeDTO {

    @NotEmpty(message = "error.OrganizationType.name.notEmpty") @NotNull(message = "error.OrganizationType.name.notnull")
    private String name;
    private List<Level> levels;

    public OrganizationTypeDTO() {
        //default constructor
    }

    public OrganizationTypeDTO(String name, List<Level> levels) {
        this.name = name;
        this.levels = levels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }
}
