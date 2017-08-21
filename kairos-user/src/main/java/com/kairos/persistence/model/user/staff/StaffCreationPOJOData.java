package com.kairos.persistence.model.user.staff;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.kairos.persistence.Gender;

/**
 * Created by pankaj on 7/3/17.
 */
public class StaffCreationPOJOData {
    @NotNull(message = "error.staff.firstname.notnull")
    private String firstName;
    @NotNull(message = "error.staff.lastname.notnull")
    private String lastName;
    private String cprNumber;
    private String familyName;
    private String workPhone;
    private String privateEmail;
    private Gender gender;
    private Long engineerTypeId;
    private Date employedSince;
    private Date inactiveFrom;
    private boolean isActive;
    private String privatePhone;
    private String workEmail;
    private String userName;
    @NotNull(message = "error.staff.externalid.notnull")
    private Long externalId;

    public StaffCreationPOJOData() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getPrivateEmail() {
        return privateEmail;
    }

    public void setPrivateEmail(String privateEmail) {
        this.privateEmail = privateEmail;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Long getEngineerTypeId() {
        return engineerTypeId;
    }

    public void setEngineerTypeId(Long engineerTypeId) {
        this.engineerTypeId = engineerTypeId;
    }

    public long getEmployedSince() {
        if(employedSince == null)
            return 0;
        return employedSince.getTime();
    }

    public void setEmployedSince(Date employedSince) {
        this.employedSince = employedSince;
    }

    public long getInactiveFrom() {
        if(inactiveFrom == null)
            return 0;
        return inactiveFrom.getTime();
    }

    public void setInactiveFrom(Date inactiveFrom) {
        this.inactiveFrom = inactiveFrom;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPrivatePhone() {
        return privatePhone;
    }

    public void setPrivatePhone(String privatePhone) {
        this.privatePhone = privatePhone;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getExternalId() {
        return externalId;
    }

    public void setExternalId(long externalId) {
        this.externalId = externalId;
    }
}
