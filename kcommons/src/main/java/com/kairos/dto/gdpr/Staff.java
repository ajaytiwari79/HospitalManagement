package com.kairos.dto.gdpr;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Staff {

    @NotNull
    private Long id;

    private String lastName;

    @NotBlank(message = "Staff Name can't be empty ")
    private String firstName;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public Staff(Long id,String lastName, String firstName) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public Staff() {
    }
}
