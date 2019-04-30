package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
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

    public UnitManagerDTO() {
        // default constructor
    }

    public Long getAccessGroupId() {
        return accessGroupId;
    }

    public void setAccessGroupId(Long accessGroupId) {
        this.accessGroupId = accessGroupId;
    }

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = StringUtils.trim(userName);
    }

    public String getAccessGroupName() {
        return accessGroupName;
    }

    public void setAccessGroupName(String accessGroupName) {
        this.accessGroupName = accessGroupName;
    }
}
