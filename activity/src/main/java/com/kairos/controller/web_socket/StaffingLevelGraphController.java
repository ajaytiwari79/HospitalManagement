package com.kairos.controller.web_socket;

import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.ApiConstants;
import com.kairos.dto.activity.staffing_level.*;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.service.staffing_level.StaffingLevelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.ApiConstants.API_UNIT_URL;

@RestController
public class StaffingLevelGraphController {
    private static final Logger logger = LoggerFactory.getLogger(StaffingLevelGraphController.class);

    @Autowired
    private StaffingLevelService staffingLevelService;

    @RequestMapping(value = API_UNIT_URL +"/staffing_level/graph", method = RequestMethod.GET)
    public PresenceStaffingLevelDto dynamicStaffingLevelGraphSyncResponsetest(){

        Duration duration=new Duration(LocalTime.now(),LocalTime.now());
        StaffingLevelSetting staffingLevelSetting=new StaffingLevelSetting(15,duration);
        PresenceStaffingLevelDto dto=new PresenceStaffingLevelDto(new BigInteger("1"), DateUtils.getDate(),20,staffingLevelSetting);
        List<StaffingLevelTimeSlotDTO> staffingLevelTimeSlots=new ArrayList<>();

        for(int i=0;i<=95;i++){
            int Random = (int)(Math.random()*12);
            StaffingLevelTimeSlotDTO timeSlotDTO1=new StaffingLevelTimeSlotDTO(i,5,10,new Duration(LocalTime.of(0,0),
                    LocalTime.of(0,15)) );
            timeSlotDTO1.setAvailableNoOfStaff(Random);
            StaffingLevelActivity activity=new StaffingLevelActivity(new BigInteger("1"),6,6);
            timeSlotDTO1.getStaffingLevelActivities().add(activity);
            staffingLevelTimeSlots.add(timeSlotDTO1);
        }
        dto.setPresenceStaffingLevelInterval(staffingLevelTimeSlots);
        return dto;
    }


    /**
     * @auther anil maurya
     * this method is used for in
     * @param clientId testing only
     * @return
     */
    @MessageMapping("/test/{clientId}")
    @SendTo(ApiConstants.API_V1+"/ws/dynamic-push/test/{clientId}")
    public String testWebSocket(@DestinationVariable Long clientId,String message){
        logger.info(" web socket responding");
        return "web socket responding"+message;
    }
}
