package com.kairos.user.access_group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.OrganizationCategory;
import com.kairos.user.access_permission.AccessGroupRole;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by prerna on 5/3/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryAccessGroupDTO {

    private Long id;
    @NotNull
    private String name;
    private String description;
    @NotNull
    private OrganizationCategory organizationCategory;
    private AccessGroupRole role;
    private boolean enabled = true;
    private Set<Long> accountTypes = new HashSet<>();

    public CountryAccessGroupDTO() {
        // default constructor
    }

    public CountryAccessGroupDTO(String name, String description, OrganizationCategory category, AccessGroupRole role) {
        this.name = name;
        this.description = description;
        this.organizationCategory = category;
        this.role = role;
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

    public OrganizationCategory getOrganizationCategory() {
        return organizationCategory;
    }

    public void setOrganizationCategory(OrganizationCategory organizationCategory) {
        this.organizationCategory = organizationCategory;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AccessGroupRole getRole() {
        return role;
    }

    public void setRole(AccessGroupRole role) {
        this.role = role;
    }

    public Set<Long> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(Set<Long> accountTypes) {
        this.accountTypes = accountTypes;
    }

    @AssertTrue(message = "Access group can't be blank")
    public boolean isValid() {
        return (this.name.trim().isEmpty()) ? false : true;
    }
}
