package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class UnitManagerDTO {
    @NotNull(message = "error.Organization.unitmanager.accessgroupid.notnull")
    private Long accessGroupId;
    @NotNull(message = "error.cprnumber.notnull")
    private String cprNumber;
    @NotNull(message = "error.firstname.notnull")
    private String firstName;
    @NotNull(message = "error.lastname.notnull")
    private String lastName;
    @NotNull(message = "error.email.notnull")
    private String email;
    private String accessGroupName;
    @NotNull(message = "error.Staff.userName.notnull")
    private String userName;
    private Long id;

}
