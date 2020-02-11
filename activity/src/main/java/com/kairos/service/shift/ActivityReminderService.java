package com.kairos.service.shift;

import com.kairos.commons.service.mail.SendGridMailService;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.CommonConstants.DEFAULT_EMAIL_TEMPLATE;

/**
 * Created By G.P.Ranjan on 7/2/20
 **/
@Service
public class ActivityReminderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftReminderService.class);

    @Inject
    private SendGridMailService sendGridMailService;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private StaffActivitySettingRepository staffActivitySettingRepository;

    public void sendReminderViaEmail(KairosSchedulerExecutorDTO jobDetails) {
        Activity activity = activityMongoRepository.findOne(jobDetails.getEntityId());
        if (isNull(activity)) {
            LOGGER.info("Unable to find activity by id {}", jobDetails.getEntityId());
        }
        List<StaffActivitySetting> staffActivitySettings = staffActivitySettingRepository.findByActivityIdAndDeletedFalse(activity.getId());
        Set<Long> staffId = staffActivitySettings.stream().map(StaffActivitySetting::getStaffId).collect(Collectors.toSet());
        List<StaffPersonalDetail> staffPersonalDetails = userIntegrationService.getStaffDetailByIds(jobDetails.getUnitId(), staffId);
        LocalDate expiryDate = activity.getRulesActivityTab().getCutOffIntervals().get(0).getEndDate();
        for (StaffPersonalDetail staffPersonalDetail : staffPersonalDetails) {
            int cutoffCount = 0;
            sendEmail(staffPersonalDetail, activity, expiryDate, cutoffCount);
        }
    }

    public void sendEmail(StaffPersonalDetail staffDTO, Activity activity, LocalDate expiryDate, int cutoffCount) {
        String description = String.format(ABSENCE_ACTIVITY_REMINDER_EMAIL_BODY, activity.getName(), getLocalDateStringByPattern(expiryDate ,COMMON_DATE_FORMAT), cutoffCount);
        Map<String,Object> templateParam = new HashMap<>();
        templateParam.put("receiverName",staffDTO.getFullName());
        templateParam.put("description", description);
        if(isNotNull(staffDTO.getProfilePic())) {
            templateParam.put("receiverImage",envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath()+staffDTO.getProfilePic());
        }
        sendGridMailService.sendMailWithSendGrid(DEFAULT_EMAIL_TEMPLATE,templateParam, null, SHIFT_NOTIFICATION,staffDTO.getContactDetail().getPrivateEmail());
    }
}
