package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.client.ContactDetail;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.kairos.constants.UserMessagesConstants.*;

/**
 * Created by prabjot on 10/2/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class UnitManagerDTO {

    @NotBlank(message = ERROR_FIRSTNAME_NOTNULL)
    private String firstName;
    @NotBlank(message = ERROR_LASTNAME_NOTNULL)
    private String lastName;
    @Email
    private String email;
    @NotNull(message = ERROR_ORGANIZATION_UNITMANAGER_ACCESSGROUPID_NOTNULL)
    private Long accessGroupId;
    private ContactDetail contactDetail;
    private Long staffId;
    private Long id;

    public String getFullName(){
        return this.firstName+" "+this.getLastName();
    }
}
