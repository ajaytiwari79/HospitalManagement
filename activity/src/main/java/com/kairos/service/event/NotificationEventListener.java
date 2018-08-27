package com.kairos.service.event;

import com.kairos.constants.AppConstants;
import com.kairos.service.mail.MailService;
import com.kairos.service.priority_group.PriorityGroupService;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.user.staff.unit_position.StaffUnitPositionQueryResult;
import com.kairos.util.event.ShiftNotificationEvent;
import com.kairos.wrapper.priority_group.PriorityGroupRuleDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Component
public class NotificationEventListener {
    Logger logger= LoggerFactory.getLogger(NotificationEventListener.class);
    @Inject
    StaffingLevelService staffingLevelService;
    @Inject
    MailService mailService;
    @Inject
    PriorityGroupService priorityGroupService;


    @Async
    @EventListener
    public void shiftNotificationEvent(ShiftNotificationEvent shiftNotificationEvent) throws UnsupportedEncodingException {
         logger.info("shift created details {}",shiftNotificationEvent);

         staffingLevelService.updateStaffingLevelAvailableStaffCount(shiftNotificationEvent);
    }
    @Async
    @EventListener
    public void shiftNotificationEvent(Map<BigInteger,List<StaffUnitPositionQueryResult>> openShiftStaffMap) throws UnsupportedEncodingException {
        logger.info("send emails to staff filtered for openshifts {send Emails}");

        priorityGroupService.sendNotificationsToStaff(openShiftStaffMap);
       }
}
