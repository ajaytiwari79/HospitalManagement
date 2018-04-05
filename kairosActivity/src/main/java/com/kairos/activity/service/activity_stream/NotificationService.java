package com.kairos.activity.service.activity_stream;

import com.kairos.activity.client.StaffRestClient;
import com.kairos.activity.client.dto.ClientStaffInfoDTO;
import com.kairos.activity.persistence.model.activity_stream.Notification;
import com.kairos.activity.persistence.model.task.Task;
import com.kairos.activity.persistence.repository.activity_stream.NotificationMongoRepository;
import com.kairos.activity.response.dto.RequestComponent;
import com.kairos.activity.service.mail.MailService;
import com.kairos.activity.util.userContext.UserContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.activity.constants.AppConstants.ABSENCE_PLANNING;
import static com.kairos.activity.constants.AppConstants.REQUEST_TO_CREATE_NEW_UTILITY;

/**
 * Created by oodles on 2/2/17.
 */
@Service
public class NotificationService {

    @Inject
    NotificationMongoRepository notificationMongoRepository;
    @Inject
    private StaffRestClient staffRestClient;

    @Inject
    MailService mailService;

    public List<Notification> fetchUnreadNotifications(Long unitId, String module){
        UserContext.setUnitId(unitId);
        ClientStaffInfoDTO clientStaffInfoDTO = staffRestClient.getStaffInfo();
        return notificationMongoRepository.findNotificationByOrganizationIdAndUserIdAndSource(unitId, clientStaffInfoDTO.getStaffId(), module);
    }


    public Boolean markNotificationRead(Long notificationId){
        Notification notification =null;// notificationGraphRepository.findById(notificationId);
        if(notification != null) {
            notification.setRead(true);
            notificationMongoRepository.save(notification);
            return true;
        }else return false;

    }


    public void addStaffMissingNotification(Task task, String message, String name){
        Notification notification = new Notification();
        notification.setOrganizationId(task.getUnitId());
        notification.setSource(ABSENCE_PLANNING);
        notification.setName(name);
        notification.setMessage(message);
        notificationMongoRepository.save(notification);
        //mailService.sendPlainMail("jasgeet.eden@oodlestechnologies.com", message, message);

    }

    /**
     * This method is used to add notification when request come to create new skills/service/...etc.
     * @param requestComponent
     * @param organizationId
     */
    public void addNewRequestNotification(Long organizationId, RequestComponent requestComponent, Long userId, String source){

        Notification notification = new Notification();
        notification.setOrganizationId(organizationId);
        notification.setSource(source);
        notification.setName(REQUEST_TO_CREATE_NEW_UTILITY +" "+ requestComponent.getRequestType().value);
        notification.setMessage(requestComponent.getDescription());
        notification.setUserId(userId);
        notificationMongoRepository.save(notification);

    }
}
