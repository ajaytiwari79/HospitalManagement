package com.kairos.service.dashboard;

import com.kairos.activity.activity.ActivityDTO;
import com.kairos.activity.cta.CTABasicDetailsDTO;
import com.kairos.activity.dashboard.UserSickDataWrapper;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.organization.OrganizationCommonDTO;
import com.kairos.util.user_context.UserContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public UserSickDataWrapper markUserAsSick(Long unitId) {
        UserSickDataWrapper userSickDataWrapper = new UserSickDataWrapper();
        if (unitId == null) {
            Long userId = UserContext.getUserDetails().getId();
            List<StaffResultDTO> staffAndOrganizationDetails =
                    genericRestClient.publishRequest(null, null, false, IntegrationOperation.GET, "/user/{userId}/unit_sick_settings", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffResultDTO>>>() {
            }, userId);
            if (!Optional.ofNullable(staffAndOrganizationDetails).isPresent() && staffAndOrganizationDetails.isEmpty()) {
                exceptionService.actionNotPermittedException("message.staff.notfound");
            }
            if (!staffAndOrganizationDetails.isEmpty() && staffAndOrganizationDetails.size() > 1) {
                userSickDataWrapper.setStaffOrganizations(staffAndOrganizationDetails);
            } else {
                List<ActivityDTO> activities = activityMongoRepository.findAllByTimeTypeIdAndUnitId(staffAndOrganizationDetails.get(0).getAllowedTimeTypesForSick(), staffAndOrganizationDetails.get(0).getUnitId());
                userSickDataWrapper.setActivities(activities);
            }
        } else {


            Set<BigInteger> sickTimeTypeIds = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/sick_settings/default", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<BigInteger>>>() {
            }, unitId);
            List<ActivityDTO> activities = activityMongoRepository.findAllByTimeTypeIdAndUnitId(sickTimeTypeIds, unitId);
            userSickDataWrapper.setActivities(activities);
        }
        return userSickDataWrapper;
    }
}
