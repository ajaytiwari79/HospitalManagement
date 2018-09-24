package com.kairos.dto.user.country.agreement.cta.cta_response;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.system_setting.AccountTypeDTO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class AccessGroupDTO {
    private Long id;
    @NotEmpty(message = "error.name.notnull") @NotNull(message = "error.name.notnull")
    private String name;
    private String description;
    private AccessGroupRole role;
    private boolean enabled = true;
    @NotNull(message = "error.startDate.notnull")
    private List<AccountTypeDTO> accountTypes;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<Long> dayTypeIds;
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

    public List<AccountTypeDTO> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountTypeDTO> accountTypes) {
        this.accountTypes = accountTypes;
    }

    @AssertTrue(message = "Access group can't be blank")
    public boolean isValid() {
        return (this.name.trim().isEmpty())?false:true;
    }
}
