package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.staff.CurrentAddress;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 12/5/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class RelatedPatient {
    private String middleName;

    private String lastName;

    private String fullReversedName;

    private String currentAddressIndicator;

    private String patientStatus;

    private String workPhoneNumber;

    private String homePhoneNumber;

    private String mobilePhoneNumber;

    private String version;

    private String id;

    private CurrentAddress currentAddress;

    private String age;

    private String gender;

    private String fullName;

    private PatientIdentifier patientIdentifier;

    private String firstName;


    @Override
    public String toString()
    {
        return "ClassPojo [middleName = "+middleName+", lastName = "+lastName+", fullReversedName = "+fullReversedName+", currentAddressIndicator = "+currentAddressIndicator+", patientStatus = "+patientStatus+", workPhoneNumber = "+workPhoneNumber+", homePhoneNumber = "+homePhoneNumber+", mobilePhoneNumber = "+mobilePhoneNumber+", version = "+version+", id = "+id+", currentAddress = "+currentAddress+", age = "+age+", gender = "+gender+", fullName = "+fullName+", patientIdentifier = "+patientIdentifier+", firstName = "+firstName+"]";
    }
}
