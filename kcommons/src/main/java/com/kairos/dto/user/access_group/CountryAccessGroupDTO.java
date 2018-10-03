package com.kairos.dto.user.access_group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.OrganizationCategory;
import com.kairos.dto.user.access_permission.AccessGroupRole;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
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
    private Set<Long> accountTypeIds = new HashSet<>();
    @NotNull(message = "error.startDate.notnull")
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull(message = "error.dayTypeIds.notnull")
    private Set<Long> dayTypeIds;

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
        return name.trim();
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

    public Set<Long> getAccountTypeIds() {
        return accountTypeIds;
    }

    public void setAccountTypeIds(Set<Long> accountTypeIds) {
        this.accountTypeIds = accountTypeIds;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<Long> getDayTypeIds() {
        return dayTypeIds;
    }

    public void setDayTypeIds(Set<Long> dayTypeIds) {
        this.dayTypeIds = dayTypeIds;
    }

    @AssertTrue(message = "Access group can't be blank")
    public boolean isValid() {
        return (this.name.trim().isEmpty()) ? false : true;
    }
}
