package com.kairos.wrapper;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by prabjot on 9/11/17.
 */
public class UpdateOrganizationTypeDTO {

    private Long id;
    @NotBlank(message = "error.OrganizationType.name.notEmpty")
    private String name;

    private List<Long> levelsToUpdate;

    private List<Long> levelsToDelete;

    public UpdateOrganizationTypeDTO() {
        //default constructor
    }

    public UpdateOrganizationTypeDTO(String name, List<Long> levelsToUpdate, List<Long> levelsToDelete) {
        this.name = name;
        this.levelsToUpdate = levelsToUpdate;
        this.levelsToDelete = levelsToDelete;
    }

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

    public List<Long> getLevelsToUpdate() {
        return Optional.ofNullable(levelsToUpdate).orElse(new ArrayList<>());
    }

    public void setLevelsToUpdate(List<Long> levelsToUpdate) {
        this.levelsToUpdate = levelsToUpdate;
    }

    public List<Long> getLevelsToDelete() {
        return Optional.ofNullable(levelsToDelete).orElse(new ArrayList<>());
    }

    public void setLevelsToDelete(List<Long> levelsToDelete) {
        this.levelsToDelete = levelsToDelete;
    }
}
