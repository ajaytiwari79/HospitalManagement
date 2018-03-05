package com.kairos.persistence.model.user.staff;

import com.kairos.persistence.model.enums.Gender;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 8/9/17.
 */
@QueryResult
public class StaffPersonalDetailDTO {
    private long id;
    private String lastName;
    private long employedSince;
    private String badgeNumber;
    private String userName;
    private Long externalId;
    private String firstName;
    private long organizationId;
    private long visitourId;
    private String cprNumber;
    private String visitourTeamId;
    private long roasteringTime;
    private long freeDay;
    private long mostOverStaffingHours;
    private long mostUnderStaffingHours;
    private long accumulatedTimeBank;
    private long accumulatedPoints;
    private String name;
    private String profilePic;
    protected Gender gender;
    private String city;
    private String province;
    private Boolean unitPosition;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getEmployedSince() {
        return employedSince;
    }

    public void setEmployedSince(long employedSince) {
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

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public long getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(long visitourId) {
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

    public long getRoasteringTime() {
        return roasteringTime;
    }

    public void setRoasteringTime(long roasteringTime) {
        this.roasteringTime = roasteringTime;
    }

    public long getFreeDay() {
        return freeDay;
    }

    public void setFreeDay(long freeDay) {
        this.freeDay = freeDay;
    }

    public long getMostOverStaffingHours() {
        return mostOverStaffingHours;
    }

    public void setMostOverStaffingHours(long mostOverStaffingHours) {
        this.mostOverStaffingHours = mostOverStaffingHours;
    }

    public long getMostUnderStaffingHours() {
        return mostUnderStaffingHours;
    }

    public void setMostUnderStaffingHours(long mostUnderStaffingHours) {
        this.mostUnderStaffingHours = mostUnderStaffingHours;
    }

    public long getAccumulatedTimeBank() {
        return accumulatedTimeBank;
    }

    public void setAccumulatedTimeBank(long accumulatedTimeBank) {
        this.accumulatedTimeBank = accumulatedTimeBank;
    }

    public long getAccumulatedPoints() {
        return accumulatedPoints;
    }

    public void setAccumulatedPoints(long accumulatedPoints) {
        this.accumulatedPoints = accumulatedPoints;
    }

    public String getName() {
        return this.firstName + " " + this.lastName;
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
}
