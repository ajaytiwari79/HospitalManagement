package com.kairos.service.dashboard;

import com.kairos.activity.activity.ActivityDTO;
import com.kairos.activity.dashboard.UserSickDataWrapper;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.SickSettingsRepository;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftSickService;
import com.kairos.util.user_context.UserContext;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

/**
 * CreatedBy vipulpandey on 30/8/18
 **/
@Service
@Transactional
public class SickService {
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private GenericRestClient genericRestClient;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private SickSettingsRepository sickSettingsRepository;
    @Inject
    private ShiftSickService shiftSickService;

    public UserSickDataWrapper markUserAsSick(Long unitId) {
        UserSickDataWrapper userSickDataWrapper = new UserSickDataWrapper();
        if (unitId == null) {
            Long userId = UserContext.getUserDetails().getId();
            BasicNameValuePair sickSettingsRequired = new BasicNameValuePair("sickSettingsRequired", "YES");
            List<StaffResultDTO> staffAndOrganizationDetails =
                    genericRestClient.publishRequest(null, null, false, IntegrationOperation.GET, "/user/{userId}/unit_sick_settings", Collections.singletonList(sickSettingsRequired), new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffResultDTO>>>() {
                    }, userId);
            if (!Optional.ofNullable(staffAndOrganizationDetails).isPresent() && staffAndOrganizationDetails.isEmpty()) {
                exceptionService.actionNotPermittedException("message.staff.notfound");
            }
            if (!staffAndOrganizationDetails.isEmpty() && staffAndOrganizationDetails.size() <= 1) {
                List<ActivityDTO> activities = activityMongoRepository.findAllByTimeTypeIdAndUnitId(staffAndOrganizationDetails.get(0).getAllowedTimeTypesForSick(), staffAndOrganizationDetails.get(0).getUnitId());
                userSickDataWrapper.setActivities(activities);
            }
            userSickDataWrapper.setStaffOrganizations(staffAndOrganizationDetails);

        } else {
            Set<BigInteger> sickTimeTypeIds = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/sick_settings/default", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<BigInteger>>>() {
            }, unitId);
            List<ActivityDTO> activities = activityMongoRepository.findAllByTimeTypeIdAndUnitId(sickTimeTypeIds, unitId);
            userSickDataWrapper.setActivities(activities);
        }
        return userSickDataWrapper;
    }

    public Map<String,Long> markUserAsFine(Long staffId, Long unitId) {
        Map<String,Long> response= new HashMap<>();
        UserSickDataWrapper userSickDataWrapper = new UserSickDataWrapper();
        if (unitId == null) {
            Long userId = UserContext.getUserDetails().getId();
            BasicNameValuePair sickSettingsRequired = new BasicNameValuePair("sickSettingsRequired", "NO");
            List<StaffResultDTO> staffAndOrganizationDetails =
                    genericRestClient.publishRequest(null, null, false, IntegrationOperation.GET, "/user/{userId}/unit_sick_settings", Collections.singletonList(sickSettingsRequired), new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffResultDTO>>>() {
                    }, userId);
            if (!Optional.ofNullable(staffAndOrganizationDetails).isPresent() && staffAndOrganizationDetails.isEmpty()) {
                exceptionService.actionNotPermittedException("message.staff.notfound");
            }
            if (!staffAndOrganizationDetails.isEmpty() && staffAndOrganizationDetails.size() > 1) {
                userSickDataWrapper.setStaffOrganizations(staffAndOrganizationDetails);
            }
        } else {
            if (unitId == null || staffId == null) {
                exceptionService.actionNotPermittedException("error.empty.staff.or.unit.setting");
            }
            shiftSickService.disableSicknessShiftsOfStaff(staffId, unitId);
            sickSettingsRepository.markUserAsFine(staffId, unitId);  //set end date of user sick table.
        }
        response.put("unitId",unitId);
        response.put("staffId",staffId);
        return response;
    }
}
