package com.kairos.dto.activity.wta.basic_details;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 26/4/18
 */

@Getter
@Setter
@NoArgsConstructor
public class WTADefaultDataInfoDTO {
    private List<ActivityDTO> activityList = new ArrayList<>();
    private List<TimeTypeDTO> timeTypes = new ArrayList<>();
    private List<DayTypeDTO> dayTypes = new ArrayList<>();
    private List<PresenceTypeDTO> presenceTypes = new ArrayList<>();
    private List<TimeSlotDTO> timeSlots = new ArrayList<>();
    private Long countryID;



    public WTADefaultDataInfoDTO(List<DayTypeDTO> dayTypes, List<PresenceTypeDTO> presenceTypes, List<TimeSlotDTO> timeSlots, Long countryID) {
        this.dayTypes = dayTypes;
        this.presenceTypes = presenceTypes;
        this.timeSlots = timeSlots;
        this.countryID = countryID;
    }


}
