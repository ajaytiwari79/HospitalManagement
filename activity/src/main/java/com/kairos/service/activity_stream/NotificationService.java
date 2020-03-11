package com.kairos.service.activity_stream;

import com.kairos.commons.service.mail.SendGridMailService;
import com.kairos.dto.activity.response.RequestComponent;
import com.kairos.dto.user.staff.ClientStaffInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.activity_stream.Notification;
import com.kairos.persistence.repository.activity_stream.NotificationMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.AppConstants.REQUEST_TO_CREATE_NEW_UTILITY;

/**
 * Created by oodles on 2/2/17.
 */
@Service
public class NotificationService {

    @Inject
    NotificationMongoRepository notificationMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;

    @Inject
    SendGridMailService sendGridMailService;

    public List<Notification> fetchUnreadNotifications(Long unitId, String module){
        UserContext.setUnitId(unitId);
        ClientStaffInfoDTO clientStaffInfoDTO = userIntegrationService.getStaffInfo();
        return notificationMongoRepository.findNotificationByOrganizationIdAndUserIdAndSource(unitId, clientStaffInfoDTO.getStaffId(), module);
    }


    public void markNotificationRead(BigInteger notificationId){
        Optional<Notification> notification = notificationMongoRepository.findById(notificationId);
        if(notification.isPresent()) {
            notification.get().setRead(true);
            notificationMongoRepository.save(notification.get());
        }

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
