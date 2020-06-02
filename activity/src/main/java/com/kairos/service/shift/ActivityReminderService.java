package com.kairos.service.shift;

import com.kairos.commons.service.mail.KMailService;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.wta.IntervalBalance;
import com.kairos.dto.activity.wta.WorkTimeAgreementBalance;
import com.kairos.dto.user.staff.employment.EmploymentDTO;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.wta.WorkTimeAgreementBalancesCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getCurrentLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.CommonConstants.DEFAULT_EMAIL_TEMPLATE;
import static com.kairos.enums.wta.WTATemplateType.CHILD_CARE_DAYS_CHECK;
import static com.kairos.enums.wta.WTATemplateType.WTA_FOR_CARE_DAYS;

/**
 * Created By G.P.Ranjan on 7/2/20
 **/

@Service
public class ActivityReminderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityReminderService.class);

    @Inject
    private EnvConfig envConfig;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private StaffActivitySettingRepository staffActivitySettingRepository;
    @Inject
    private WorkTimeAgreementBalancesCalculationService workTimeAgreementBalancesCalculationService;
    @Inject
    private KMailService kMailService;

    public void sendActivityReminderViaEmail(Long unitId, BigInteger entityId) {
        Activity activity = activityMongoRepository.findOne(entityId);
        if (isNull(activity)) {
            LOGGER.info("Unable to find activity by id {}", entityId);
        }
        List<StaffActivitySetting> staffActivitySettings = staffActivitySettingRepository.findByActivityIdAndDeletedFalse(activity.getId());
        Set<Long> staffIds = staffActivitySettings.stream().map(StaffActivitySetting::getStaffId).collect(Collectors.toSet());
        List<StaffDTO> staffDTOS = userIntegrationService.getStaffDetailByIds(unitId, staffIds);
        for (StaffDTO staffDTO : staffDTOS) {
            if(isCollectionNotEmpty(staffDTO.getEmployments()) && isNotNull(staffDTO.getPrivateEmail())) {
                try {
                    for (EmploymentDTO employment : staffDTO.getEmployments()) {
                        WorkTimeAgreementBalance workTimeAgreementBalance = workTimeAgreementBalancesCalculationService.getWorkTimeAgreementBalance(unitId, employment.getId(), getCurrentLocalDate(), getCurrentLocalDate(), newHashSet(WTATemplateType.SENIOR_DAYS_PER_YEAR,CHILD_CARE_DAYS_CHECK,WTA_FOR_CARE_DAYS), activity.getId());
                        List<IntervalBalance> intervalBalances = workTimeAgreementBalance.getWorkTimeAgreementRuleTemplateBalances().stream().flatMap(workTimeAgreementRuleTemplateBalancesDTO -> workTimeAgreementRuleTemplateBalancesDTO.getIntervalBalances().stream()).filter(intervalBalance -> (int) intervalBalance.getAvailable()>0).collect(Collectors.toList());
                        for (IntervalBalance intervalBalance : intervalBalances) {
                            sendEmail(staffDTO, activity, intervalBalance);
                        }
                    }
                }catch (Exception ex){
                    LOGGER.info("Exception {}", ex.getMessage());
                }
            }
        }
    }

    public void sendEmail(StaffDTO staffDTO, Activity activity, IntervalBalance intervalBalance) {
        String description = String.format(ABSENCE_ACTIVITY_REMINDER_EMAIL_BODY, activity.getName(), intervalBalance.getEndDate(), intervalBalance.getAvailable());
        Map<String,Object> templateParam = new HashMap<>();
        templateParam.put("receiverName", staffDTO.getFullName());
        templateParam.put(DESCRIPTION, description);
        if(isNotNull(staffDTO.getProfilePic())) {
            templateParam.put("receiverImage",envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath()+ staffDTO.getProfilePic());
        }
        //sendGridMailService.sendMailWithSendGrid(DEFAULT_EMAIL_TEMPLATE,templateParam, null, ACTIVITY_REMINDER,staffPersonalDetail.getPrivateEmail());
        kMailService.sendMail(null, ACTIVITY_REMINDER,templateParam.get(DESCRIPTION).toString(),templateParam.get(DESCRIPTION).toString(),templateParam,DEFAULT_EMAIL_TEMPLATE, staffDTO.getPrivateEmail());
        LOGGER.info("Mail Send {}", staffDTO.getPrivateEmail());
    }
}
