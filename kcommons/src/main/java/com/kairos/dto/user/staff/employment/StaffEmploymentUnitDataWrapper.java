package com.kairos.dto.user.staff.employment;

import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.expertise.SeniorAndChildCareDaysDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * CreatedBy vipulpandey on 28/11/18
 **/
public class StaffEmploymentUnitDataWrapper {
    private List<StaffEmploymentDetails> staffEmploymentDetails;
    private List<TimeSlotWrapper> timeSlotWrappers; // these Timeslot is shiftPlanning unit TimeSlot which tells us Day,Evening,Night from to
    private List<Long> skills;
    private String profilePic;
    private Long unitId;
    private Date organizationNightStartTimeFrom;
    private Date organizationNightEndTimeTo;
    private Map<Long, List<LocalDate>> publicHoliday;
    //These are the all day type of country
    private List<DayTypeDTO> dayTypes;
    private UserAccessRoleDTO user;
    //This is unit TimeZone
    private ZoneId unitTimeZone;
    private int staffAge;
    private Long staffUserId;
    private String cprNumber;
    private SeniorAndChildCareDaysDTO seniorAndChildCareDays;
    private List<ReasonCodeDTO> reasonCodes;



    public StaffEmploymentUnitDataWrapper() {
        // DC
    }

    public StaffEmploymentUnitDataWrapper(List<StaffEmploymentDetails> staffEmploymentDetails) {
        this.staffEmploymentDetails = staffEmploymentDetails;
    }

    public List<StaffEmploymentDetails> getStaffEmploymentDetails() {
        return staffEmploymentDetails;
    }

    public void setStaffEmploymentDetails(List<StaffEmploymentDetails> staffEmploymentDetails) {
        this.staffEmploymentDetails = staffEmploymentDetails;
    }

    public List<TimeSlotWrapper> getTimeSlotWrappers() {
        return timeSlotWrappers;
    }

    public void setTimeSlotWrappers(List<TimeSlotWrapper> timeSlotWrappers) {
        this.timeSlotWrappers = timeSlotWrappers;
    }

    public List<Long> getSkills() {
        return skills;
    }

    public void setSkills(List<Long> skills) {
        this.skills = skills;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Date getOrganizationNightStartTimeFrom() {
        return organizationNightStartTimeFrom;
    }

    public void setOrganizationNightStartTimeFrom(Date organizationNightStartTimeFrom) {
        this.organizationNightStartTimeFrom = organizationNightStartTimeFrom;
    }

    public Date getOrganizationNightEndTimeTo() {
        return organizationNightEndTimeTo;
    }

    public void setOrganizationNightEndTimeTo(Date organizationNightEndTimeTo) {
        this.organizationNightEndTimeTo = organizationNightEndTimeTo;
    }

    public Map<Long, List<LocalDate>> getPublicHoliday() {
        return publicHoliday;
    }

    public void setPublicHoliday(Map<Long, List<LocalDate>> publicHoliday) {
        this.publicHoliday = publicHoliday;
    }

    public List<DayTypeDTO> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayTypeDTO> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public UserAccessRoleDTO getUser() {
        return user;
    }

    public void setUser(UserAccessRoleDTO user) {
        this.user = user;
    }

    public ZoneId getUnitTimeZone() {
        return unitTimeZone;
    }

    public void setUnitTimeZone(ZoneId unitTimeZone) {
        this.unitTimeZone = unitTimeZone;
    }

    public int getStaffAge() {
        return staffAge;
    }

    public void setStaffAge(int staffAge) {
        this.staffAge = staffAge;
    }

    public Long getStaffUserId() {
        return staffUserId;
    }

    public void setStaffUserId(Long staffUserId) {
        this.staffUserId = staffUserId;
    }

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }

    public SeniorAndChildCareDaysDTO getSeniorAndChildCareDays() {
        return seniorAndChildCareDays;
    }

    public void setSeniorAndChildCareDays(SeniorAndChildCareDaysDTO seniorAndChildCareDays) {
        this.seniorAndChildCareDays = seniorAndChildCareDays;
    }

    public List<ReasonCodeDTO> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCodeDTO> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }
}
