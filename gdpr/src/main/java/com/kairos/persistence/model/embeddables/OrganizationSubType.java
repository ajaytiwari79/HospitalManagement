package com.kairos.persistence.model.embeddables;


import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Embeddable
public class OrganizationSubType {

    @NotNull(message = "id can't be null")
    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrganizationSubType() {
    }

    public OrganizationSubType(@NotNull(message = "id can't be null") Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
