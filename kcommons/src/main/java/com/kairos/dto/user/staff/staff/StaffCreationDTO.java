package com.kairos.dto.user.staff.staff;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by pankaj on 7/3/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffCreationDTO {
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
    private String employedSince;
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

    public StaffCreationDTO() {
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

    public String getEmployedSince() {

        return employedSince;
    }

    public void setEmployedSince(String employedSince) {
        this.employedSince = employedSince;
    }

    public Long getInactiveFrom() {
        if(inactiveFrom == null)
            return null;
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

    public StaffCreationDTO(@NotNull(message = "error.staff.firstname.notnull") String firstName, @NotNull(message = "error.staff.lastname.notnull") String lastName, @NotNull(message = "error.staff.cprNumber.notnull") String cprNumber, String familyName, @Email(message = "error.email.valid") String privateEmail, Gender gender, String userName, @NotNull(message = "error.staff.externalid.notnull") Long externalId, @NotNull(message = "error.staff.accessGroup.id.notnull") Long accessGroupId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cprNumber = cprNumber;
        this.familyName = familyName;
        this.privateEmail = privateEmail;
        this.gender = gender;
        this.userName = userName;
        this.externalId = externalId;
        this.accessGroupId = accessGroupId;
    }
}
