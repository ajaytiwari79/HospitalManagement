package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.client.ContactDetail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.kairos.constants.UserMessagesConstants.*;

/**
 * Created by prabjot on 10/2/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitManagerDTO {

    @NotBlank(message = ERROR_FIRSTNAME_NOTNULL)
    String firstName;
    @NotBlank(message = ERROR_LASTNAME_NOTNULL)
    String lastName;
    @Email
    String email;
    @NotNull(message = ERROR_ORGANIZATION_UNITMANAGER_ACCESSGROUPID_NOTNULL)
    Long accessGroupId;
    ContactDetail contactDetail;
    Long staffId;

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

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getFullName(){
        return this.firstName+" "+this.getLastName();
    }
}
