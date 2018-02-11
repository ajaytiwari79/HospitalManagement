package com.kairos.response.dto.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.web.skill.SkillDTO;

import java.util.Set;

/**
 * Created by oodles on 20/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffDTO {
    private String middleName;

    private String mobileTelephone;

    private String autosignatureId;

    private String lastName;

    private String secondaryEmailAddress;

    private String organizationName;

    private String primaryEmailAddress;

    private String departmentName;

    private String version;

    private Long id;

    private String homeTelephone;

    private String primaryIdentifier;

    private CurrentAddress secondaryAddress;

    private String initials;

    private String unitName;

    private boolean active;

    private String fullName;

    private String firstName;

    private String workTelephone;

    private CurrentAddress primaryAddress;
    private Set<SkillDTO> skillSet;

    public StaffDTO(Long id, String firstName, Set<SkillDTO> skillSet) {
        this.id = id;
        this.firstName = firstName;
        this.skillSet = skillSet;
    }

    public Set<SkillDTO> getSkillSet() {
        return skillSet;
    }

    public void setSkillSet(Set<SkillDTO> skillSet) {
        this.skillSet = skillSet;
    }


    public String getMiddleName ()
    {
        return middleName;
    }

    public void setMiddleName (String middleName)
    {
        this.middleName = middleName;
    }


    public String getLastName ()
    {
        return lastName;
    }

    public void setLastName (String lastName)
    {
        this.lastName = lastName;
    }


    public String getOrganizationName ()
    {
        return organizationName;
    }

    public void setOrganizationName (String organizationName)
    {
        this.organizationName = organizationName;
    }



    public String getDepartmentName ()
    {
        return departmentName;
    }

    public void setDepartmentName (String departmentName)
    {
        this.departmentName = departmentName;
    }

    public String getVersion ()
    {
        return version;
    }

    public void setVersion (String version)
    {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrimaryIdentifier ()
    {
        return primaryIdentifier;
    }

    public void setPrimaryIdentifier (String primaryIdentifier)
    {
        this.primaryIdentifier = primaryIdentifier;
    }



    public String getInitials ()
    {
        return initials;
    }

    public void setInitials (String initials)
    {
        this.initials = initials;
    }

    public String getUnitName ()
    {
        return unitName;
    }

    public void setUnitName (String unitName)
    {
        this.unitName = unitName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFullName ()
    {
        return fullName;
    }

    public void setFullName (String fullName)
    {
        this.fullName = fullName;
    }

    public String getFirstName ()
    {
        return firstName;
    }

    public void setFirstName (String firstName)
    {
        this.firstName = firstName;
    }

    public String getWorkTelephone ()
    {
        return workTelephone;
    }

    public void setWorkTelephone (String workTelephone)
    {
        this.workTelephone = workTelephone;
    }

    public String getMobileTelephone() {
        return mobileTelephone;
    }

    public void setMobileTelephone(String mobileTelephone) {
        this.mobileTelephone = mobileTelephone;
    }

    public String getAutosignatureId() {
        return autosignatureId;
    }

    public void setAutosignatureId(String autosignatureId) {
        this.autosignatureId = autosignatureId;
    }

    public String getSecondaryEmailAddress() {
        return secondaryEmailAddress;
    }

    public void setSecondaryEmailAddress(String secondaryEmailAddress) {
        this.secondaryEmailAddress = secondaryEmailAddress;
    }

    public String getPrimaryEmailAddress() {
        return primaryEmailAddress;
    }

    public void setPrimaryEmailAddress(String primaryEmailAddress) {
        this.primaryEmailAddress = primaryEmailAddress;
    }

    public String getHomeTelephone() {
        return homeTelephone;
    }

    public void setHomeTelephone(String homeTelephone) {
        this.homeTelephone = homeTelephone;
    }

    public CurrentAddress getSecondaryAddress() {
        return secondaryAddress;
    }

    public void setSecondaryAddress(CurrentAddress secondaryAddress) {
        this.secondaryAddress = secondaryAddress;
    }

    public CurrentAddress getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(CurrentAddress primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [middleName = "+middleName+", mobileTelephone = "+mobileTelephone+", autosignatureId = "+autosignatureId+", lastName = "+lastName+", secondaryEmailAddress = "+secondaryEmailAddress+", organizationName = "+organizationName+", primaryEmailAddress = "+primaryEmailAddress+", departmentName = "+departmentName+", version = "+version+", id = "+id+", homeTelephone = "+homeTelephone+", primaryIdentifier = "+primaryIdentifier+", secondaryAddress = "+secondaryAddress+", initials = "+initials+", unitName = "+unitName+", active = "+active+", fullName = "+fullName+", firstName = "+firstName+", workTelephone = "+workTelephone+", primaryAddress = "+primaryAddress+"]";
    }
}
