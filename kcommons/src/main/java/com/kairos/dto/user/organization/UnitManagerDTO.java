package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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
    private String firstName;
    private String lastName;
    private String email;
    private String accessGroupName;
    private String userName;

}
