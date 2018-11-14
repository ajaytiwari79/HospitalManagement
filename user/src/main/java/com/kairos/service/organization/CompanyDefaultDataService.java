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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
    @Inject
    private ReasonCodeService reasonCodeService;
    @Inject
    private GdprIntegrationService gdprIntegrationService;


    public CompletableFuture<Boolean> createDefaultDataInUnit(Long parentId, List<Organization> units, Long countryId, List<TimeSlot> timeSlots,Map<Long,Map<Long, Long>> orgAndUnitAccessGroupIdsMap,Map<Long,Long> unitAndStaffId) throws InterruptedException, ExecutionException {
        OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO = new OrgTypeAndSubTypeDTO(countryId,parentId);
        units.forEach(unit -> {
            asynchronousService.executeInBackGround(() -> {
                activityIntegrationService.createDefaultKPISetting(
                                new DefaultKPISettingDTO(unit.getOrganizationSubTypes().stream().map(organizationType -> organizationType.getId()).collect(Collectors.toList()),
                                        null, parentId, orgAndUnitAccessGroupIdsMap.get(unit.getId())), unit.getId());
                activityIntegrationService.createDefaultKPISettingForStaff(new DefaultKPISettingDTO(Arrays.asList(unitAndStaffId.get(unit.getId()))), unit.getId());
                    });
                asynchronousService.executeInBackGround(()->timeSlotService.createDefaultTimeSlots(unit, timeSlots));
                asynchronousService.executeInBackGround(() -> activityIntegrationService.crateDefaultDataForOrganization(unit.getId(), parentId, orgTypeAndSubTypeDTO));
                asynchronousService.executeInBackGround(() -> vrpClientService.createDefaultPreferredTimeWindow(unit));
                asynchronousService.executeInBackGround(() -> activityIntegrationService.createDefaultPriorityGroupsFromCountry(countryId, unit.getId()));
                asynchronousService.executeInBackGround(() -> reasonCodeService.createDefalutDateForSubUnit(unit,parentId));
                asynchronousService.executeInBackGround(()-> gdprIntegrationService.createDefaultDataForOrganization(countryId,unit.getId()));
        });
        System.out.print("--------------------------------------");
        return CompletableFuture.completedFuture(true);
    }

    public List<Future<Object>> createDefaultDataForParentOrganization(Organization organization, Map<Long,Map<Long, Long>> countryAndOrgAccessGroupIdsMap,
                                                                             List<TimeSlot> timeSlots, OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO,Long countryId,Map<Long,Long> unitAndStaffId) throws InterruptedException, ExecutionException {
        List<Future<Object>> futureList = new ArrayList<>();
        futureList.add(asynchronousService.executeAsynchronously(() -> {
            activityIntegrationService.crateDefaultDataForOrganization(organization.getId(), organization.getId(), orgTypeAndSubTypeDTO);
            return true;
        }));
        futureList.add(asynchronousService.executeAsynchronously(() -> {
            vrpClientService.createDefaultPreferredTimeWindow(organization);
            return true;
        }));
        futureList.add(asynchronousService.executeAsynchronously(() -> {
            organizationGraphRepository.linkWithRegionLevelOrganization(organization.getId());
            return true;
        }));
        futureList.add(asynchronousService.executeAsynchronously(() -> {
            activityIntegrationService.createDefaultKPISetting(
                    new DefaultKPISettingDTO(orgTypeAndSubTypeDTO.getSubTypeId(),
                            organization.getCountry().getId(), null, countryAndOrgAccessGroupIdsMap.get(organization.getId())), organization.getId());
            activityIntegrationService.createDefaultKPISettingForStaff(new DefaultKPISettingDTO(Arrays.asList(unitAndStaffId.get(organization.getId()))), organization.getId());
            return true;
        }));
        asynchronousService.executeInBackGround(() -> timeSlotService.createDefaultTimeSlots(organization, timeSlots));
        asynchronousService.executeInBackGround(() -> organizationGraphRepository.assignDefaultSkillsToOrg(organization.getId(), DateUtils.getCurrentDayStartMillis(), DateUtils.getCurrentDayStartMillis()));
        asynchronousService.executeInBackGround(() -> organizationGraphRepository.assignDefaultServicesToOrg(organization.getId(), DateUtils.getCurrentDayStartMillis(), DateUtils.getCurrentDayStartMillis()));
        orgTypeAndSubTypeDTO.setOrganizationSubTypeId(organization.getOrganizationSubTypes().get(0).getId());
        asynchronousService.executeInBackGround(() -> activityIntegrationService.createDefaultOpenShiftRuleTemplate(orgTypeAndSubTypeDTO, organization.getId()));
        asynchronousService.executeInBackGround(() -> reasonCodeService.createDefalutDateForUnit(organization,countryId));
        //asynchronousService.executeInBackGround(()-> gdprIntegrationService.createDefaultDataForOrganization(countryId,organization.getId()));
        return futureList;
    }
}
