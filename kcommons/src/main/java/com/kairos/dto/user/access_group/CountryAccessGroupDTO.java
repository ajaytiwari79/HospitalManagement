package com.kairos.dto.user.access_group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.OrganizationCategory;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
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
    private Set<Long> dayTypeIds;
    private boolean allowedDayTypes;

    @AssertTrue(message = "Access group can't be blank")
    public boolean isValid() {
        return !this.name.trim().isEmpty();
    }
}
