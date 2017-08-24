package com.kairos.service.requestComponent;
import com.kairos.constants.AppConstants;
import com.kairos.persistence.model.enums.RequestType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.request_component.RequestComponent;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.request_component.RequestComponentGraphRepository;
import com.kairos.service.staff.StaffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 22/8/17.
 */
@Service
@Transactional
public class RequestComponentService {

    @Inject
    private RequestComponentGraphRepository requestComponentGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    //@Inject
   // private NotificationService notificationService;
    @Inject
    private StaffService staffService;

    public RequestComponent createRequest(Long organizationId, RequestComponent requestComponent) {
        try {
            Organization organization;

            switch (requestComponent.getRequestSentType()) {
                case AppConstants.UNIT:
                    organization = organizationGraphRepository.getParentOfOrganization(requestComponent.getRequestSentId());
                    break;
                case AppConstants.ORGANIZATION:
                    organization = organizationGraphRepository.findOne(organizationId);
                    break;
                default:
                    organization = null;
                    break;

            }
            if (organization != null) {

                Map<String, Object> unitManagerMap = staffService.getUnitManager(organization.getId());
                List<Map<String, Object>> unitManagerList = (List<Map<String, Object>>) unitManagerMap.get("unitManager");
                if (unitManagerList.size() != 0) {
                  //  notificationService.addNewRequestNotification(organization, request_component, unitManagerList);
                }
            }
            return requestComponent;
        }catch (Exception exception){
            return null;
        }
    }

    public RequestType[] fetchRequestTypes(){
        return RequestType.values();
    }


}
