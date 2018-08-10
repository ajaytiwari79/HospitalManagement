package com.kairos.gdpr;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Staff {

    @NotNull
    private Long id;

    private String userName;

    @NotBlank(message = "Staff Name can't be empty ")
    private String firstName;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }


    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public Staff(Long id, String email, String userName, String firstName) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
    }

    public Staff() {
    }
}
