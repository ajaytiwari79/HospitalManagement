package com.kairos.service.dashboard;

import com.kairos.activity.cta.CTABasicDetailsDTO;
import com.kairos.enums.IntegrationOperation;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CreatedBy vipulpandey on 30/8/18
 **/
@Service
@Transactional
public class SickService {
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject private GenericRestClient genericRestClient;
    @Inject
    private ExceptionService exceptionService;

    public Long markUserAsSick(Long unitId) {
        if (unitId!=null){
            Long userId = UserContext.getUserDetails().getId();

            List<StaffResultDTO> staffAndOrganizationIds = genericRestClient.publishRequest(null,null,false,IntegrationOperation.GET,"/user/{userId}/unit_sick_settings",null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffResultDTO>>>() {},null);
            if (!Optional.ofNullable(staffAndOrganizationIds).isPresent() && staffAndOrganizationIds.isEmpty()) {
                exceptionService.actionNotPermittedException("message.staff.notfound");
            }
            if (!staffAndOrganizationIds.isEmpty() && staffAndOrganizationIds.size() > 1) {
                List<OrganizationCommonDTO> unitIdAndNames = staffAndOrganizationIds.stream().map(s -> new OrganizationCommonDTO(s.getUnitId(), s.getUnitName())).collect(Collectors.toList());
            } else
            {

            }
        }else {

        }

        return  unitId;
    }
}
