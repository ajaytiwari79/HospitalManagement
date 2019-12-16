package com.kairos.dto.user.staff.staff;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by pankaj on 7/3/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class StaffCreationDTO {
    @NotBlank(message = "error.staff.firstname.notnull")
    private String firstName;
    @NotBlank(message = "error.staff.lastname.notnull")
    private String lastName;
    //@NotBlank(message = "error.staff.cprNumber.notnull")
    private String cprNumber;
    private String familyName;
    private String workPhone;
    //@Email(message = "error.email.valid")
    private String privateEmail;
    private Gender gender;
    private Long engineerTypeId;
    private String employedSince;
    private Date inactiveFrom;
    private String privatePhone;
    private String workEmail;
    //@NotBlank(message = "error.staff.userName.notnull")
    private String userName;
    private Long externalId;

    //@NotNull(message = "error.staff.accessGroup.id.notnull")
    private Long accessGroupId;
    private StaffStatusEnum currentStatus;

    private List<TagDTO> tags;


    public Long getInactiveFrom() {
        if(inactiveFrom == null)
            return null;
        return inactiveFrom.getTime();
    }


    public StaffCreationDTO(@NotNull(message = "error.staff.firstname.notnull") String firstName, @NotNull(message = "error.staff.lastname.notnull") String lastName, @NotNull(message = "error.staff.cprNumber.notnull") String cprNumber, String familyName, @Email(message = "error.email.valid") String privateEmail, Gender gender, String userName,  Long externalId, @NotNull(message = "error.staff.accessGroup.id.notnull") Long accessGroupId) {
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
