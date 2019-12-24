package com.kairos.dto.user.user.staff;

import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.expertise.SeniorAndChildCareDaysDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.staff.StaffChildDetailDTO;
import com.kairos.dto.user.skill.SkillLevelDTO;
import com.kairos.dto.user_context.UserContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by oodles on 28/11/17.
 */
// TODO incorrect Its a jumbled file We need to fix impact StaffRetrivalService getStaffsEmploymentData  this id name is already present in employment
@Getter
@Setter
@NoArgsConstructor
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
    //This is unit TimeZone
    private ZoneId unitTimeZone;
    private int staffAge;
    //these Timeslot is shiftPlanning unit TimeSlot which tells us Day,Evening,Night from to
    private List<TimeSlotWrapper> timeSlotSets;
    private Long staffUserId;
    private String cprNumber;
    private SeniorAndChildCareDaysDTO seniorAndChildCareDays;
    private List<ReasonCodeDTO> reasonCodes=new ArrayList<>();
    private UserAccessRoleDTO userAccessRoleDTO;
    private List<StaffChildDetailDTO> staffChildDetails;
    private List<SkillLevelDTO> skillLevelDTOS;
    private boolean countryAdmin;
    private List<TagDTO> tags;
    private Map<String, String> unitWiseAccessRole=new HashMap<>();


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
}
