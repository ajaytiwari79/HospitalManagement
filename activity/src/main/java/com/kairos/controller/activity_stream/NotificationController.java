package com.kairos.controller.activity_stream;
import com.kairos.persistence.model.activity_stream.Notification;
import com.kairos.service.activity_stream.NotificationService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_NOTIFICATION_URL;

/**
 * Created by oodles on 2/2/17.
 */
@RestController
@RequestMapping(API_NOTIFICATION_URL)
@Api(value = API_NOTIFICATION_URL)
public class NotificationController {

    @Inject
    private NotificationService notificationService;


    @RequestMapping(value = "{notificationId}/module/{module}", method = RequestMethod.GET)
    @ApiOperation("fetch notifications")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications(@PathVariable long notificationId, @PathVariable String module) {

        List<Notification> response = notificationService.fetchUnreadNotifications(notificationId, module);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/{notificationId}", method = RequestMethod.POST)
    @ApiOperation("mark notification read")
    public ResponseEntity<Map<String, Object>> markNotificationRead(@PathVariable Long notificationId) {

        Boolean status = notificationService.markNotificationRead(notificationId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, status);
    }


}
