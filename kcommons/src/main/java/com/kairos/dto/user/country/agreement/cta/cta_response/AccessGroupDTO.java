package com.kairos.dto.user.country.agreement.cta.cta_response;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

public class AccessGroupDTO {
    private Long id;
    @NotEmpty(message = "error.name.notnull") @NotNull(message = "error.name.notnull")
    private String name;
    private String description;
    private AccessGroupRole role;
    private boolean enabled = true;
    public AccessGroupDTO() {
        //default constructor
    }

    public AccessGroupDTO(Long id, String name, String description, AccessGroupRole role) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccessGroupRole getRole() {
        return role;
    }

    public void setRole(AccessGroupRole role) {
        this.role = role;
    }

    @AssertTrue(message = "Access group can't be blank")
    public boolean isValid() {
        return (this.name.trim().isEmpty())?false:true;
    }
}
