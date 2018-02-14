package com.kairos.persistence.model.user.staff;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.enums.Gender;
import com.kairos.persistence.model.enums.StaffStatusEnum;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by pankaj on 7/3/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffCreationPOJOData {
    @NotNull(message = "error.staff.firstname.notnull")
    private String firstName;
    @NotNull(message = "error.staff.lastname.notnull")
    private String lastName;
    @NotNull(message = "error.staff.cprNumber.notnull")
    private String cprNumber;
    private String familyName;
    private String workPhone;
    @Email(message = "error.email.valid")
    private String privateEmail;
    private Gender gender;
    private Long engineerTypeId;
    private Date employedSince;
    private Date inactiveFrom;
    private String privatePhone;
    private String workEmail;
    private String userName;
    @NotNull(message = "error.staff.externalid.notnull")
    private Long externalId;

    @NotNull(message = "error.staff.accessGroup.id.notnull")
    private Long accessGroupId;
    private StaffStatusEnum currentStatus;

    public Long getAccessGroupId() {
        return accessGroupId;
    }

    public void setAccessGroupId(Long accessGroupId) {
        this.accessGroupId = accessGroupId;
    }

    public StaffCreationPOJOData() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.trim();
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
        this.familyName = familyName.trim();
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone.trim();
    }

    public String getPrivateEmail() {
        return privateEmail;
    }

    public void setPrivateEmail(String privateEmail) {
        this.privateEmail = privateEmail.trim().toLowerCase();
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


    public void setExternalId(Long externalId) {
        this.externalId = externalId;
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
        this.workEmail = workEmail.trim().toLowerCase();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getExternalId() {
        return externalId;
    }

    public StaffStatusEnum getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(StaffStatusEnum currentStatus) {
        this.currentStatus = currentStatus;
    }
}
