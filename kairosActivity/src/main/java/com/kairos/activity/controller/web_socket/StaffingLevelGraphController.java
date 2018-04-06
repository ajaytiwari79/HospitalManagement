package com.kairos.activity.controller.web_socket;

import com.kairos.activity.constants.ApiConstants;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelActivity;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelDuration;
import com.kairos.activity.persistence.model.staffing_level.StaffingLevelSetting;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelDto;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelTimeSlotDTO;
import com.kairos.activity.service.staffing_level.StaffingLevelService;
import com.kairos.activity.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.activity.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

@RestController
public class StaffingLevelGraphController {
    Logger logger = LoggerFactory.getLogger(StaffingLevelGraphController.class);

    @Autowired
    private StaffingLevelService staffingLevelService;

    @RequestMapping(value = API_ORGANIZATION_UNIT_URL+"/staffing_level/graph", method = RequestMethod.GET)
    public StaffingLevelDto dynamicStaffingLevelGraphSyncResponsetest(){

        StaffingLevelDuration duration=new StaffingLevelDuration(LocalTime.now(),LocalTime.now());
        StaffingLevelSetting staffingLevelSetting=new StaffingLevelSetting(15,duration);
        StaffingLevelDto dto=new StaffingLevelDto(1L, DateUtils.getDate(),20L,staffingLevelSetting);
        List<StaffingLevelTimeSlotDTO> staffingLevelTimeSlots=new ArrayList<>();

        for(int i=0;i<=95;i++){
            int Random = (int)(Math.random()*12);
            StaffingLevelTimeSlotDTO timeSlotDTO1=new StaffingLevelTimeSlotDTO(i,5,10,new StaffingLevelDuration(LocalTime.of(0,0),
                    LocalTime.of(0,15)) );
            timeSlotDTO1.setAvailableNoOfStaff(Random);
            StaffingLevelActivity activity=new StaffingLevelActivity(1L,6);
            timeSlotDTO1.getStaffingLevelActivities().add(activity);
            staffingLevelTimeSlots.add(timeSlotDTO1);
        }
        dto.setStaffingLevelInterval(staffingLevelTimeSlots);
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
