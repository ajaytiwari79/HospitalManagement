package com.planner.responseDto.PlanningDto.shiftPlanningDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

/**
 * Created by oodles on 3/2/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffDTO {
   private Long id;
    private String firstName;
    private String lastName;
    private BigInteger cprNumber;
    private String familyName;

    private String privateEmail;
    private Integer privatePhone;
    private String workEmail;
    private Integer workPhone;

    private Date employedSince;
    private Boolean active;
    private Long inactiveFrom;

    private Long teamId;

    private Set<SkillDTO> skillSet;

    private  Long unit;
    private Long anonymousStaffId;

    private Long visitourId;
    private String email;
    private String profilePic;
    private Long unitEmploymentPositionId;

    public Long getUnitEmploymentPositionId() {
        return unitEmploymentPositionId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Long getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(Long visitourId) {
        this.visitourId = visitourId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setInactiveFrom(Long inactiveFrom) {
        this.inactiveFrom = inactiveFrom;
    }

    public Set<SkillDTO> getSkillSet() {
        return skillSet;
    }

    public void setSkillSet(Set<SkillDTO> skillSet) {
        this.skillSet = skillSet;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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



    public String getPrivateEmail() {
        return privateEmail;
    }

    public void setPrivateEmail(String privateEmail) {
        this.privateEmail = privateEmail;
    }



    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }


    public BigInteger getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(BigInteger cprNumber) {
        this.cprNumber = cprNumber;
    }

    public Integer getPrivatePhone() {
        return privatePhone;
    }

    public void setPrivatePhone(Integer privatePhone) {
        this.privatePhone = privatePhone;
    }

    public Integer getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(Integer workPhone) {
        this.workPhone = workPhone;
    }



    public Date getEmployedSince() {
        return employedSince;
    }

    public void setEmployedSince(Date employedSince) {
        this.employedSince = employedSince;
    }


    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public long getInactiveFrom() {
        return inactiveFrom;
    }

    public void setInactiveFrom(long inactiveFrom) {
        this.inactiveFrom = inactiveFrom;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }


    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public Long getAnonymousStaffId() {
        return anonymousStaffId;
    }

    public void setAnonymousStaffId(Long anonymousStaffId) {
        this.anonymousStaffId = anonymousStaffId;
    }

}
