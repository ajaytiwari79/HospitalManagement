package com.kairos.persistence.model.dto.timeType;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by vipul on 10/11/17.
 */
public class PresenceTypeDTO {
    @NotEmpty(message = "error.PresenceTypeDTO.name.notEmpty")
    @NotNull(message = "error.PresenceTypeDTO.name.notnull")
    private String name;
    private Long Id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public PresenceTypeDTO() {
    }

    public PresenceTypeDTO(String name) {
        this.name = name;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    @Override
    public String toString() {
        return "PresenceTypeDTO{" +
                "name='" + name + '\'' +
                ", Id=" + Id +
                '}';
    }


}
