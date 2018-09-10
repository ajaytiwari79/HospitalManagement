package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.staff.CurrentAddress;

/**
 * Created by oodles on 19/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    public String getWorkTelephone() {
        return workTelephone;
    }

    public void setWorkTelephone(String workTelephone) {
        this.workTelephone = workTelephone;
    }

    public String getHomeTelephone() {
        return homeTelephone;
    }

    public void setHomeTelephone(String homeTelephone) {
        this.homeTelephone = homeTelephone;
    }

    public String getMobileTelephone() {
        return mobileTelephone;
    }

    public void setMobileTelephone(String mobileTelephone) {
        this.mobileTelephone = mobileTelephone;
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

    public PatientState getPatientState() {
        return patientState;
    }

    public void setPatientState(PatientState patientState) {
        this.patientState = patientState;
    }

    public CurrentAddress getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(CurrentAddress primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public PatientIdentifier getPatientIdentifier() {
        return patientIdentifier;
    }

    public void setPatientIdentifier(PatientIdentifier patientIdentifier) {
        this.patientIdentifier = patientIdentifier;
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


    public String getFirstName ()
    {
        return firstName;
    }

    public void setFirstName (String firstName)
    {
        this.firstName = firstName;
    }

    public PatientLinks get_links() {
        return _links;
    }

    public void set_links(PatientLinks _links) {
        this._links = _links;
    }

    public CurrentAddress getSecondaryAddress() {
        return secondaryAddress;
    }

    public void setSecondaryAddress(CurrentAddress secondaryAddress) {
        this.secondaryAddress = secondaryAddress;
    }

    public CurrentAddress getPlanningAddress() {
        return planningAddress;
    }

    public void setPlanningAddress(CurrentAddress planningAddress) {
        this.planningAddress = planningAddress;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getSecondaryEmailAddress() {
        return secondaryEmailAddress;
    }

    public void setSecondaryEmailAddress(String secondaryEmailAddress) {
        this.secondaryEmailAddress = secondaryEmailAddress;
    }

    public String getPrimaryEmailAddress() {
        return primaryEmailAddress;
    }

    public void setPrimaryEmailAddress(String primaryEmailAddress) {
        this.primaryEmailAddress = primaryEmailAddress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /* @Override
    public String toString()
    {
        return "ClassPojo [middleName = "+middleName+", lastName = "+lastName+", fullReversedName = "+fullReversedName+", currentAddressIndicator = "+currentAddressIndicator+", _links = "+_links+", patientStatus = "+patientStatus+", workPhoneNumber = "+workPhoneNumber+", homePhoneNumber = "+homePhoneNumber+", patientState = "+patientState+", mobilePhoneNumber = "+mobilePhoneNumber+", version = "+version+", id = "+id+", currentAddress = "+currentAddress+", age = "+age+", gender = "+gender+", fullName = "+fullName+", patientIdentifier = "+patientIdentifier+", firstName = "+firstName+"]";
    }*/

}
