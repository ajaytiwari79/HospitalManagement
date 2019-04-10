package com.kairos.persistence.model.embeddables;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Staff{

    @NotNull
    private Long staffId;

    private String lastName;

    @NotBlank(message = "error.message.staffName.notnull ")
    private String firstName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return Objects.equals(staffId, staff.staffId) &&
                Objects.equals(lastName, staff.lastName) &&
                Objects.equals(firstName, staff.firstName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(staffId, lastName, firstName);
    }
}
