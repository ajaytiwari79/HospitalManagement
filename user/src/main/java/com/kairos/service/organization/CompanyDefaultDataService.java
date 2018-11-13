package com.kairos.service.organization;

import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.AccessGroupQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.service.AsynchronousService;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.client.VRPClientService;
import com.kairos.service.country.ReasonCodeService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.commons.utils.DateUtils;
import com.kairos.service.integration.GdprIntegrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    @Inject
    private ReasonCodeService reasonCodeService;
    @Inject
    private GdprIntegrationService gdprIntegrationService;


    public CompletableFuture<Boolean> createDefaultDataInUnit(Long parentId, List<Organization> units, Long countryId, List<TimeSlot> timeSlots) throws InterruptedException, ExecutionException {
        OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO = new OrgTypeAndSubTypeDTO(countryId,parentId);
        units.forEach(unit -> {
               /* asynchronousService.executeInBackGround(() -> timeSlotService.createDefaultTimeSlots(unit, timeSlots));
                asynchronousService.executeInBackGround(() -> activityIntegrationService.crateDefaultDataForOrganization(unit.getId(), parentId, orgTypeAndSubTypeDTO));
                asynchronousService.executeInBackGround(() -> vrpClientService.createDefaultPreferredTimeWindow(unit));
                asynchronousService.executeInBackGround(() -> activityIntegrationService.createDefaultPriorityGroupsFromCountry(countryId, unit.getId()));
                asynchronousService.executeInBackGround(() -> reasonCodeService.createDefalutDateForSubUnit(unit,parentId));*/
                asynchronousService.executeInBackGround(()-> gdprIntegrationService.createDefaultDataForOrganization(countryId,unit.getId()));
        });
        return CompletableFuture.completedFuture(true);
    }

    public CompletableFuture<Boolean> createDefaultDataForParentOrganization(Organization organization, Map<Long, Long> countryAndOrgAccessGroupIdsMap,
                                                                             List<TimeSlot> timeSlots, OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO,Long countryId) throws InterruptedException, ExecutionException {
     /*   asynchronousService.executeInBackGround(() -> activityIntegrationService.crateDefaultDataForOrganization(organization.getId(), organization.getId(), orgTypeAndSubTypeDTO));
        asynchronousService.executeInBackGround(() -> vrpClientService.createDefaultPreferredTimeWindow(organization));
        asynchronousService.executeInBackGround(() -> organizationGraphRepository.linkWithRegionLevelOrganization(organization.getId()));
        asynchronousService.executeInBackGround(() -> activityIntegrationService.createDefaultKPISetting(
                new DefaultKPISettingDTO(orgTypeAndSubTypeDTO.getSubTypeId(),
                        organization.getCountry().getId(), null, countryAndOrgAccessGroupIdsMap), organization.getId()));
        asynchronousService.executeInBackGround(() -> timeSlotService.createDefaultTimeSlots(organization, timeSlots));
        asynchronousService.executeInBackGround(() -> organizationGraphRepository.assignDefaultSkillsToOrg(organization.getId(), DateUtils.getCurrentDayStartMillis(), DateUtils.getCurrentDayStartMillis()));
        asynchronousService.executeInBackGround(() -> organizationGraphRepository.assignDefaultServicesToOrg(organization.getId(), DateUtils.getCurrentDayStartMillis(), DateUtils.getCurrentDayStartMillis()));
        orgTypeAndSubTypeDTO.setOrganizationSubTypeId(organization.getOrganizationSubTypes().get(0).getId());
        asynchronousService.executeInBackGround(() -> activityIntegrationService.createDefaultOpenShiftRuleTemplate(orgTypeAndSubTypeDTO, organization.getId()));
        asynchronousService.executeInBackGround(() -> reasonCodeService.createDefalutDateForUnit(organization,countryId));*/
        asynchronousService.executeInBackGround(()-> gdprIntegrationService.createDefaultDataForOrganization(countryId,organization.getId()));

        return CompletableFuture.completedFuture(true);
    }
}
