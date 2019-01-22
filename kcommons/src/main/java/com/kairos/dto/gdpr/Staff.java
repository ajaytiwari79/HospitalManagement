package com.kairos.dto.gdpr;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Staff {

    @NotNull
    private Long staffId;


    private String lastName;

    @NotBlank(message = "Staff Name can't be empty ")
    private String firstName;

    public Long getId() { return staffId; }

    public void setId(Long id) { this.staffId = id; }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public Staff(Long id,String lastName, String firstName) {
        this.staffId = id;
        this.lastName = lastName;
        this.firstName = firstName;
    }

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

    public Staff() {
    }
}
