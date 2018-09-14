package com.kairos.persistence.model.staff.personal_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.Gender;
import com.kairos.utils.CPRUtil;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.time.Period;

/**
 * Created by vipul on 8/9/17.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffPersonalDetailDTO {
    private Long id;
    private String lastName;
    private Long employedSince;
    private String badgeNumber;
    private String userName;
    private Long externalId;
    private String firstName;
    private Long organizationId;
    private Long visitourId;
    private String cprNumber;
    private String visitourTeamId;
    private Long roasteringTime;
    private Long freeDay;
    private Long mostOverStaffingHours;
    private Long mostUnderStaffingHours;
    private Long accumulatedTimeBank;
    private Long accumulatedPoints;
    private String name;
    private String profilePic;
    protected Gender gender;
    private String city;
    private String province;
    private Boolean unitPosition;
    private Integer age;
    private String privatePhone;
    private LocalDate dateOfBirth;
    private Boolean pregnant;
    // used for staff validation
    private Long accessGroupId;
    private String accessGroupName;
    private String email;
    public StaffPersonalDetailDTO() {
        // default constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getEmployedSince() {
        return employedSince;
    }

    public void setEmployedSince(Long employedSince) {
        this.employedSince = employedSince;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(String badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(Long visitourId) {
        this.visitourId = visitourId;
    }

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }

    public String getVisitourTeamId() {
        return visitourTeamId;
    }

    public void setVisitourTeamId(String visitourTeamId) {
        this.visitourTeamId = visitourTeamId;
    }

    public Long getRoasteringTime() {
        return roasteringTime;
    }

    public void setRoasteringTime(Long roasteringTime) {
        this.roasteringTime = roasteringTime;
    }

    public Long getFreeDay() {
        return freeDay;
    }

    public void setFreeDay(Long freeDay) {
        this.freeDay = freeDay;
    }

    public Long getMostOverStaffingHours() {
        return mostOverStaffingHours;
    }

    public void setMostOverStaffingHours(Long mostOverStaffingHours) {
        this.mostOverStaffingHours = mostOverStaffingHours;
    }

    public Long getMostUnderStaffingHours() {
        return mostUnderStaffingHours;
    }

    public void setMostUnderStaffingHours(Long mostUnderStaffingHours) {
        this.mostUnderStaffingHours = mostUnderStaffingHours;
    }

    public Long getAccumulatedTimeBank() {
        return accumulatedTimeBank;
    }

    public void setAccumulatedTimeBank(Long accumulatedTimeBank) {
        this.accumulatedTimeBank = accumulatedTimeBank;
    }

    public Long getAccumulatedPoints() {
        return accumulatedPoints;
    }

    public void setAccumulatedPoints(Long accumulatedPoints) {
        this.accumulatedPoints = accumulatedPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Boolean getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(Boolean unitPosition) {
        this.unitPosition = unitPosition;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPrivatePhone() {
        return privatePhone;
    }

    public void setPrivatePhone(String privatePhone) {
        this.privatePhone = privatePhone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getPregnant() {
        return pregnant;
    }

    public void setPregnant(Boolean pregnant) {
        this.pregnant = pregnant;
    }

    public Integer getAge() {
        this.age=this.cprNumber!=null?Period.between(CPRUtil.getDateOfBirthFromCPR(this.cprNumber), LocalDate.now()).getYears():null;
        return age;
    }

    public Long getAccessGroupId() {
        return accessGroupId;
    }

    public void setAccessGroupId(Long accessGroupId) {
        this.accessGroupId = accessGroupId;
    }

    public String getAccessGroupName() {
        return accessGroupName;
    }

    public void setAccessGroupName(String accessGroupName) {
        this.accessGroupName = accessGroupName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
