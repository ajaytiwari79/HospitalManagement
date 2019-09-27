package com.kairos.dto.user.user.staff;

import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.expertise.SeniorAndChildCareDaysDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by oodles on 28/11/17.
 */
// TODO incorrect Its a jumbled file We need to fix impact StaffRetrivalService getStaffsEmploymentData  this id name is already present in employment
public class StaffAdditionalInfoDTO {

    private String name;
    private long id;
    private List<Long> teams;
    private List<Long> skills;
    private String profilePic;
    private Long unitId;
    private StaffEmploymentDetails employment;
    private Date organizationNightStartTimeFrom;
    private Date organizationNightEndTimeTo;
    private Map<Long, List<LocalDate>> publicHoliday;
    //These are the all day type of country
    private List<DayTypeDTO> dayTypes;
    private UserAccessRoleDTO user;
    //This is unit TimeZone
    private ZoneId unitTimeZone;
    private int staffAge;
    //these Timeslot is shiftPlanning unit TimeSlot which tells us Day,Evening,Night from to
    private List<TimeSlotWrapper> timeSlotSets;
    private Long staffUserId;
    private String cprNumber;
    private SeniorAndChildCareDaysDTO seniorAndChildCareDays;
    private List<ReasonCodeDTO> reasonCodes;
    private UserAccessRoleDTO userAccessRoleDTO;


    public StaffAdditionalInfoDTO() {
    }

    public StaffAdditionalInfoDTO(StaffEmploymentDetails employment) {
        this.employment = employment;
    }

    public StaffAdditionalInfoDTO(StaffEmploymentDetails employment, List<DayTypeDTO> dayTypes) {
        this.employment = employment;
        this.dayTypes = dayTypes;
    }

    public StaffAdditionalInfoDTO( List<ReasonCodeDTO> reasonCodes,StaffEmploymentDetails employment) {
        this.employment = employment;
        this.reasonCodes = reasonCodes;
    }

    public SeniorAndChildCareDaysDTO getSeniorAndChildCareDays() {
        return seniorAndChildCareDays;
    }

    public void setSeniorAndChildCareDays(SeniorAndChildCareDaysDTO seniorAndChildCareDays) {
        this.seniorAndChildCareDays = seniorAndChildCareDays;
    }

    public int getStaffAge() {
        return staffAge;
    }

    public void setStaffAge(int staffAge) {
        this.staffAge = staffAge;
    }

    public String getCprNumber() {
        return cprNumber;
    }

    public void setCprNumber(String cprNumber) {
        this.cprNumber = cprNumber;
    }

    public Long getStaffUserId() {
        return staffUserId;
    }

    public void setStaffUserId(Long staffUserId) {
        this.staffUserId = staffUserId;
    }

    public Map<Long, List<LocalDate>> getPublicHoliday() {
        return publicHoliday;
    }

    public void setPublicHoliday(Map<Long, List<LocalDate>> publicHoliday) {
        this.publicHoliday = publicHoliday;
    }

    public List<TimeSlotWrapper> getTimeSlotSets() {
        return timeSlotSets;
    }

    public void setTimeSlotSets(List<TimeSlotWrapper> timeSlotSets) {
        this.timeSlotSets = timeSlotSets;
    }

    public ZoneId getUnitTimeZone() {
        return unitTimeZone;
    }

    public void setUnitTimeZone(ZoneId unitTimeZone) {
        this.unitTimeZone = unitTimeZone;
    }

    public List<DayTypeDTO> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayTypeDTO> dayTypes) {
        this.dayTypes = dayTypes;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Long> getTeams() {
        return teams;
    }

    public void setTeams(List<Long> teams) {
        this.teams = teams;
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

    public StaffEmploymentDetails getEmployment() {
        return employment;
    }

    public void setEmployment(StaffEmploymentDetails employment) {
        this.employment = employment;
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


    public UserAccessRoleDTO getUserAccessRoleDTO() {
        return userAccessRoleDTO;
    }

    public void setUserAccessRoleDTO(UserAccessRoleDTO userAccessRoleDTO) {
        this.userAccessRoleDTO = userAccessRoleDTO;
    }

    public Set<AccessGroupRole> getRoles() {
            Set<AccessGroupRole> roles = new HashSet<>();
            if(userAccessRoleDTO!=null) {
                if (Optional.ofNullable(userAccessRoleDTO.getManagement()).isPresent() && userAccessRoleDTO.getManagement()) {
                    roles.add(AccessGroupRole.MANAGEMENT);
                }
                if (Optional.ofNullable(userAccessRoleDTO.getStaff()).isPresent() && userAccessRoleDTO.getStaff()) {
                    roles.add(AccessGroupRole.STAFF);
                }
            }
        return roles;
    }


    public List<ReasonCodeDTO> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCodeDTO> reasonCodes) {
        this.reasonCodes = Optional.ofNullable(reasonCodes).orElse(new ArrayList<>());
    }
}
