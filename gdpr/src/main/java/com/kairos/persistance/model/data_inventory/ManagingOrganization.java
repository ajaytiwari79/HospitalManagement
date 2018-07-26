package com.kairos.persistance.model.data_inventory;

import com.kairos.utils.custom_annotation.NotNullOrEmpty;
import org.javers.core.metamodel.annotation.ValueObject;

import javax.validation.constraints.NotNull;


@ValueObject
public class ManagingOrganization {


    @NotNull
    private Long id;

    @NotNullOrEmpty(message = "ManagingOrganization name can't be empty")
    private String name;

    @NotNullOrEmpty(message = "email can't be empty")
    private String email;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public ManagingOrganization(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public ManagingOrganization() {
    }
}
