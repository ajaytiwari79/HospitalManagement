package com.kairos.persistance.model.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custom_annotation.NotNullOrEmpty;
import org.javers.core.metamodel.annotation.ValueObject;

import javax.validation.constraints.NotNull;


@JsonIgnoreProperties(ignoreUnknown = true)
@ValueObject
public class Staff {

    @NotNull
    private Long id;

    @NotNullOrEmpty(message = "Staff Email can't be empty")
    private String email;

    private String userName;

    @NotNullOrEmpty(message = "Staff Name can't be empty ")
    private String firstName;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public Staff(Long id, String email, String userName, String firstName) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.firstName = firstName;
    }

    public Staff() {
    }
}
