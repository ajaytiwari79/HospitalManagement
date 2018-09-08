package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.staff.CurrentAddress;

/**
 * Created by oodles on 12/5/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    public String getMiddleName ()
    {
        return middleName;
    }

    public void setMiddleName (String middleName)
    {
        this.middleName = middleName;
    }

    public String getLastName ()
    {
        return lastName;
    }

    public void setLastName (String lastName)
    {
        this.lastName = lastName;
    }

    public String getFullReversedName ()
    {
        return fullReversedName;
    }

    public void setFullReversedName (String fullReversedName)
    {
        this.fullReversedName = fullReversedName;
    }

    public String getCurrentAddressIndicator ()
    {
        return currentAddressIndicator;
    }

    public void setCurrentAddressIndicator (String currentAddressIndicator)
    {
        this.currentAddressIndicator = currentAddressIndicator;
    }

    public String getPatientStatus ()
    {
        return patientStatus;
    }

    public void setPatientStatus (String patientStatus)
    {
        this.patientStatus = patientStatus;
    }

    public String getWorkPhoneNumber ()
    {
        return workPhoneNumber;
    }

    public void setWorkPhoneNumber (String workPhoneNumber)
    {
        this.workPhoneNumber = workPhoneNumber;
    }

    public String getHomePhoneNumber ()
    {
        return homePhoneNumber;
    }

    public void setHomePhoneNumber (String homePhoneNumber)
    {
        this.homePhoneNumber = homePhoneNumber;
    }

    public String getMobilePhoneNumber ()
    {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber (String mobilePhoneNumber)
    {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getVersion ()
    {
        return version;
    }

    public void setVersion (String version)
    {
        this.version = version;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public CurrentAddress getCurrentAddress ()
    {
        return currentAddress;
    }

    public void setCurrentAddress (CurrentAddress currentAddress)
    {
        this.currentAddress = currentAddress;
    }

    public String getAge ()
    {
        return age;
    }

    public void setAge (String age)
    {
        this.age = age;
    }

    public String getGender ()
    {
        return gender;
    }

    public void setGender (String gender)
    {
        this.gender = gender;
    }

    public String getFullName ()
    {
        return fullName;
    }

    public void setFullName (String fullName)
    {
        this.fullName = fullName;
    }

    public PatientIdentifier getPatientIdentifier ()
    {
        return patientIdentifier;
    }

    public void setPatientIdentifier (PatientIdentifier patientIdentifier)
    {
        this.patientIdentifier = patientIdentifier;
    }

    public String getFirstName ()
    {
        return firstName;
    }

    public void setFirstName (String firstName)
    {
        this.firstName = firstName;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [middleName = "+middleName+", lastName = "+lastName+", fullReversedName = "+fullReversedName+", currentAddressIndicator = "+currentAddressIndicator+", patientStatus = "+patientStatus+", workPhoneNumber = "+workPhoneNumber+", homePhoneNumber = "+homePhoneNumber+", mobilePhoneNumber = "+mobilePhoneNumber+", version = "+version+", id = "+id+", currentAddress = "+currentAddress+", age = "+age+", gender = "+gender+", fullName = "+fullName+", patientIdentifier = "+patientIdentifier+", firstName = "+firstName+"]";
    }
}
