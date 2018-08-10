package com.kairos.service.event;

import com.kairos.constants.AppConstants;
import com.kairos.service.mail.MailService;
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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Component
public class NotificationEventListener {
    Logger logger= LoggerFactory.getLogger(NotificationEventListener.class);
    @Autowired
    StaffingLevelService staffingLevelService;
    @Autowired
    MailService mailService;

    @Async
    @EventListener
    public void shiftNotificationEvent(ShiftNotificationEvent shiftNotificationEvent) throws UnsupportedEncodingException {
         logger.info("shift created details {}",shiftNotificationEvent);

         staffingLevelService.updateStaffingLevelAvailableStaffCount(shiftNotificationEvent);
    }
    @Async
    @EventListener
    public void shiftNotificationEvent(PriorityGroupRuleDataDTO priorityGroupRuleDataDTO) throws UnsupportedEncodingException {
        logger.info("shift created details {send Emails}");

        Map<BigInteger,List<StaffUnitPositionQueryResult>> openShiftStaffMap = priorityGroupRuleDataDTO.getOpenShiftStaffMap();
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry:openShiftStaffMap.entrySet()) {

            int fibonacciCounter = 0;//Using it to put fibonacci order in email for testing.
            for(StaffUnitPositionQueryResult staffUnitPositionQueryResult:entry.getValue()) {

                mailService.sendPlainMail(staffUnitPositionQueryResult.getStaffEmail(), String.format(AppConstants.OPENSHIFT_EMAIL_BODY,fibonacciCounter++),AppConstants.OPENSHIFT_SUBJECT);

            }
        }
    }
}
