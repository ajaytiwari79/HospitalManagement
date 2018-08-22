package com.kairos.service.organization;

import com.kairos.activity.counter.DefaultKPISettingDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.service.AsynchronousService;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.client.VRPClientService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * CreatedBy vipulpandey on 22/8/18
 **/
@Service
@Transactional
public class CompanyDefaultDataService {

    @Inject
    private AsynchronousService asynchronousService;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private VRPClientService vrpClientService;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;


    public CompletableFuture<Boolean> createDefaultDataInUnit(Long parentId, List<Organization> units, Long countryId,List<TimeSlot>timeSlots) throws InterruptedException, ExecutionException {
        units.forEach(unit -> {
            try {
                asynchronousService.executeInBackGround(() -> accessGroupService.createDefaultAccessGroups(unit));
                asynchronousService.executeInBackGround(() -> timeSlotService.createDefaultTimeSlots(unit,timeSlots));
                asynchronousService.executeInBackGround(() -> activityIntegrationService.crateDefaultDataForOrganization(unit.getId(), parentId, countryId,null,null));
                asynchronousService.executeInBackGround(() -> vrpClientService.createDefaultPreferredTimeWindow(unit));
                asynchronousService.executeInBackGround(() -> activityIntegrationService.createDefaultPriorityGroupsFromCountry(countryId, unit.getId()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        return CompletableFuture.completedFuture(true);
    }


    public CompletableFuture<Boolean> createDefaultDataForParentOrganization(Organization organization,Map<Long, Long> countryAndOrgAccessGroupIdsMap ,
                                                                             List<TimeSlot>timeSlots,Long orgTypeId,List<Long> orgSubTypeId) throws InterruptedException, ExecutionException {
        asynchronousService.executeInBackGround(() -> vrpClientService.createDefaultPreferredTimeWindow(organization));
        asynchronousService.executeInBackGround(() -> organizationGraphRepository.linkWithRegionLevelOrganization(organization.getId()));

        asynchronousService.executeInBackGround(() -> activityIntegrationService.createDefaultKPISetting(
                new DefaultKPISettingDTO(orgSubTypeId,
                        organization.getCountry().getId(), null, countryAndOrgAccessGroupIdsMap), organization.getId()));
        asynchronousService.executeInBackGround(() -> activityIntegrationService.crateDefaultDataForOrganization(organization.getId(), organization.getId(), organization.getCountry().getId(),orgTypeId,orgSubTypeId));
        asynchronousService.executeInBackGround(() -> timeSlotService.createDefaultTimeSlots(organization,timeSlots));
        asynchronousService.executeInBackGround(() -> organizationGraphRepository.assignDefaultSkillsToOrg(organization.getId(), DateUtils.getCurrentDayStartMillis(), DateUtils.getCurrentDayStartMillis()));
        asynchronousService.executeInBackGround(() -> organizationGraphRepository.assignDefaultServicesToOrg(organization.getId(), DateUtils.getCurrentDayStartMillis(), DateUtils.getCurrentDayStartMillis()));
        OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO = new OrgTypeAndSubTypeDTO(organization.getOrganizationType().getId(), organization.getOrganizationSubTypes().get(0).getId(), organization.getCountry().getId());
        asynchronousService.executeInBackGround(() -> activityIntegrationService.createDefaultOpenShiftRuleTemplate(orgTypeAndSubTypeDTO, organization.getId()));
        return CompletableFuture.completedFuture(true);
    }
}
