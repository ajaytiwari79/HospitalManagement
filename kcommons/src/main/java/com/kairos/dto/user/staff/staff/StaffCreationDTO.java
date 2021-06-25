package com.kairos.dto.user.staff.staff;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.enums.Gender;
import com.kairos.enums.StaffStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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
    @NotNull(message = "error.staff.accessGroup.id.notnull")
    private Long accessGroupId;
    private StaffStatusEnum currentStatus;
    private String email;

    private List<TagDTO> tags;


    public Long getInactiveFrom() {
        if(inactiveFrom == null)
            return null;
        return inactiveFrom.getTime();
    }

    public String getPrivateEmail() {
        return privateEmail=privateEmail==null?email:privateEmail;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = StringUtils.isEmpty(cprNumber)?null:cprNumber;
    }
}
