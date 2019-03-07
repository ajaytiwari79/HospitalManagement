package com.kairos.dto.gdpr;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Staff {

    @NotNull(message = "error.message.staffid.notnull")
    private Long staffId;
    private String lastName;
    @NotBlank(message = "error.message.staffName.notnull")
    private String firstName;

    public Long getStaffId() { return staffId; }

    public void setStaffId(Long staffId) { this.staffId = staffId; }

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
    public Staff() {
    }
}
