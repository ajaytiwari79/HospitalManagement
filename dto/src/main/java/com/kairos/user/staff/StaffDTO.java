package com.kairos.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.client.dto.Skill;
import com.kairos.enums.Gender;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by oodles on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    private List<Long> skills;

    private  Long unit;
    private Long anonymousStaffId;
    private ContactAddress contactAddress;
    private ContactDetail contactDetail;

    private Long visitourId;
    private String email;
    private String profilePic;
    private Set<Skill> skillSet;
    private Long unitEmploymentPositionId;
    private Gender gender;
    private boolean pregnant;
    private LocalDate dateOfBirth;
    private Set<Long> expertiseIds;
    private Long employmentTypeId;

    public Long getUnitEmploymentPositionId() {
        return unitEmploymentPositionId;
    }

    public void setUnitEmploymentPositionId(Long unitEmploymentPositionId) {
        this.unitEmploymentPositionId = unitEmploymentPositionId;
    }

    public Set<Skill> getSkillSet() {
        return skillSet;
    }

    public void setSkillSet(Set<Skill> skillSet) {
        this.skillSet = skillSet;
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

    public List<Long> getSkills() {
        return skills;
    }

    public void setSkills(List<Long> skills) {
        this.skills = skills;
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

    public ContactAddress getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(ContactAddress contactAddress) {
        this.contactAddress = contactAddress;
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public void setContactDetail(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
    }

    public void setInactiveFrom(Long inactiveFrom) {
        this.inactiveFrom = inactiveFrom;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isPregnant() {
        return pregnant;
    }

    public void setPregnant(boolean pregnant) {
        this.pregnant = pregnant;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Set<Long> getExpertiseIds() {
        return expertiseIds;
    }

    public void setExpertiseIds(Set<Long> expertiseIds) {
        this.expertiseIds = expertiseIds;
    }

    public Long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(Long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }
}
