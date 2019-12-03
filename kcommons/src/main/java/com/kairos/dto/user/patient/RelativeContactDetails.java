package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class RelativeContactDetails {
    private String maritalStatus;

    private String phoneNumber;

    private String secondaryPhoneNumber;

    private String firstName;

    private String lastName;

}