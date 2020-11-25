package com.kairos.dto.user.country.agreement.cta.cta_response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;
@Getter
@Setter
public class AccessGroupDTO {
    private Long id;
    @NotEmpty(message = "error.name.notnull") @NotNull(message = "error.name.notnull")
    private String name;
    private String description;
    private AccessGroupRole role;
    private boolean enabled = true;
    @NotNull(message = "error.startDate.notnull")
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<BigInteger> dayTypeIds;
    private boolean allowedDayTypes;

    @JsonIgnore
    @AssertTrue(message = "Access group can't be blank")
    public boolean isValid() {
        return !this.name.trim().isEmpty();
    }
}
