package com.kairos.response.dto.web;

import com.kairos.persistence.model.organization.Level;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by prabjot on 9/11/17.
 */
public class UpdateOrganizationTypeDTO {

    @NotEmpty(message = "error.OrganizationType.name.notEmpty") @NotNull(message = "error.OrganizationType.name.notnull")
    private String name;

    private List<Level> levelsToUpdate;

    private List<Long> levelsToDelete;

    public UpdateOrganizationTypeDTO() {
        //default constructor
    }

    public UpdateOrganizationTypeDTO(String name, List<Level> levelsToUpdate, List<Long> levelsToDelete) {
        this.name = name;
        this.levelsToUpdate = levelsToUpdate;
        this.levelsToDelete = levelsToDelete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Level> getLevelsToUpdate() {
        return levelsToUpdate;
    }

    public void setLevelsToUpdate(List<Level> levelsToUpdate) {
        this.levelsToUpdate = levelsToUpdate;
    }

    public List<Long> getLevelsToDelete() {
        return levelsToDelete;
    }

    public void setLevelsToDelete(List<Long> levelsToDelete) {
        this.levelsToDelete = levelsToDelete;
    }
}
