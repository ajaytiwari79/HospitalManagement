package com.kairos.persistence.model.organization;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.kairos.persistence.model.user.client.ContactDetail;

/**
 * Created by prabjot on 10/2/17.
 */
public class UnitManagerDTO {

    @NotEmpty(message = "error.firstname.notnull") @NotNull(message = "error.firstname.notnull")
    String firstName;
    @NotEmpty(message = "error.lastname.notnull") @NotNull(message = "error.lastname.notnull")
    String lastName;
    @Email
    String email;
    @NotNull(message = "error.Organization.unitmanager.accessgroupid.notnull")
    Long accessGroupId;
    ContactDetail contactDetail;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Long getAccessGroupId() {
        return accessGroupId;
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccessGroupId(Long accessGroupId) {
        this.accessGroupId = accessGroupId;
    }

    public void setContactDetail(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
    }
}
