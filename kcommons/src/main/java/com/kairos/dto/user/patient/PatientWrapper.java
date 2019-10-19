package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.staff.CurrentAddress;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 19/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PatientWrapper {


    private String middleName;

    private String lastName;

    private String fullReversedName;

    private String currentAddressIndicator;

    private PatientLinks _links;

    private String patientStatus;

    private String workTelephone;

    private String homeTelephone;

    private PatientState patientState;

    private String mobileTelephone;

    private String version;

    private String id;

    private CurrentAddress primaryAddress;

    private CurrentAddress secondaryAddress;

    private CurrentAddress planningAddress;

    private String maritalStatus;

    private String secondaryEmailAddress;

    private String primaryEmailAddress;

    private String age;

    private String gender;

    private String fullName;

    private PatientIdentifier patientIdentifier;

    private String firstName;

    private String notes;


}
