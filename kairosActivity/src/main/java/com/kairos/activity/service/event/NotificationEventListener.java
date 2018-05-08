package com.kairos.activity.service.event;

import com.kairos.activity.service.staffing_level.StaffingLevelService;
import com.kairos.activity.util.event.ShiftNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class NotificationEventListener {
    Logger logger= LoggerFactory.getLogger(NotificationEventListener.class);
    @Autowired
    StaffingLevelService staffingLevelService;

    @Async
    @EventListener
    public void shiftNotificationEvent(ShiftNotificationEvent shiftNotificationEvent) throws UnsupportedEncodingException {
         logger.info("shift created details {}",shiftNotificationEvent);

         staffingLevelService.updateStaffingLevelAvailableStaffCount(shiftNotificationEvent);
    }
}
