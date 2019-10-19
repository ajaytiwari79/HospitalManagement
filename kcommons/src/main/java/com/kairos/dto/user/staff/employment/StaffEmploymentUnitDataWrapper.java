package com.kairos.dto.user.staff.employment;

import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.expertise.SeniorAndChildCareDaysDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * CreatedBy vipulpandey on 28/11/18
 **/
@Getter
@Setter
@NoArgsConstructor
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


    public StaffEmploymentUnitDataWrapper(List<StaffEmploymentDetails> staffEmploymentDetails) {
        this.staffEmploymentDetails = staffEmploymentDetails;
    }


}
