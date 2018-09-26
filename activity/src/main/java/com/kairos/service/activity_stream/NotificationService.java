package com.kairos.service.activity_stream;

import com.kairos.dto.activity.response.RequestComponent;
import com.kairos.rest_client.StaffRestClient;
import com.kairos.dto.user.staff.ClientStaffInfoDTO;
import com.kairos.persistence.model.activity_stream.Notification;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.repository.activity_stream.NotificationMongoRepository;
import com.kairos.service.mail.MailService;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.constants.AppConstants.ABSENCE_PLANNING;
import static com.kairos.constants.AppConstants.REQUEST_TO_CREATE_NEW_UTILITY;

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
