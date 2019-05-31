package com.kairos.service.organization;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.service.client.VRPClientService;
import com.kairos.service.country.ReasonCodeService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.integration.GdprIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CreatedBy vipulpandey on 22/8/18
 **/
@Service
@Transactional
public class CompanyDefaultDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDefaultDataService.class);
    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private VRPClientService vrpClientService;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private ReasonCodeService reasonCodeService;
    @Inject
    private GdprIntegrationService gdprIntegrationService;


    public void createDefaultDataInUnit(Long parentId, List<Unit> units, Long countryId, List<TimeSlot> timeSlots) {
        OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO = new OrgTypeAndSubTypeDTO(countryId, parentId);
        units.forEach(unit -> {
            orgTypeAndSubTypeDTO.setOrganizationTypeId(unit.getOrganizationType().getId());
            orgTypeAndSubTypeDTO.setSubTypeId(unit.getOrganizationSubTypes().stream().map(organizationType -> organizationType.getId()).collect(Collectors.toList()));
            orgTypeAndSubTypeDTO.setOrganizationSubTypeId(unit.getOrganizationSubTypes().get(0).getId());
            orgTypeAndSubTypeDTO.setWorkcentre(unit.isWorkcentre());
            orgTypeAndSubTypeDTO.setSubTypeId(unit.getOrganizationSubTypes().stream().map(k->k.getId()).collect(Collectors.toList()));
            orgTypeAndSubTypeDTO.setParentOrganization(unit.isParentOrganization());
            activityIntegrationService.crateDefaultDataForOrganization(unit.getId(), parentId, orgTypeAndSubTypeDTO);
            activityIntegrationService.createDefaultKPISetting(
                    new DefaultKPISettingDTO(unit.getOrganizationSubTypes().stream().map(organizationType -> organizationType.getId()).collect(Collectors.toList()),
                            null, parentId, null), unit.getId());
            timeSlotService.createDefaultTimeSlots(unit, timeSlots);
            vrpClientService.createDefaultPreferredTimeWindow(unit);
            activityIntegrationService.createDefaultPriorityGroupsFromCountry(countryId, unit.getId());
            reasonCodeService.createReasonCodeForUnit(unit, parentId);
            //gdprIntegrationService.createDefaultDataForOrganization(countryId, unit.getId());

        });
    }

    public void createDefaultDataForParentOrganization(Organization organization, Map<Long, Long> countryAndOrgAccessGroupIdsMap,

                                                       List<TimeSlot> timeSlots, OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO, Long countryId) {
            orgTypeAndSubTypeDTO.setSubTypeId(organization.getOrganizationSubTypes().stream().map(k->k.getId()).collect(Collectors.toList()));
            orgTypeAndSubTypeDTO.setOrganizationSubTypeId(organization.getOrganizationSubTypes().get(0).getId());
            activityIntegrationService.crateDefaultDataForOrganization(organization.getId(), organization.getId(), orgTypeAndSubTypeDTO);
            unitGraphRepository.linkWithRegionLevelOrganization(organization.getId());
            activityIntegrationService.createDefaultKPISetting(new DefaultKPISettingDTO(orgTypeAndSubTypeDTO.getSubTypeId(), organization.getCountry().getId(), null, countryAndOrgAccessGroupIdsMap), organization.getId());
            timeSlotService.createDefaultTimeSlots(organization, timeSlots);
            unitGraphRepository.assignDefaultSkillsToOrg(organization.getId(), DateUtils.getCurrentDayStartMillis(), DateUtils.getCurrentDayStartMillis());
            unitGraphRepository.assignDefaultServicesToOrg(organization.getId(), DateUtils.getCurrentDayStartMillis(), DateUtils.getCurrentDayStartMillis());
            //gdprIntegrationService.createDefaultDataForOrganization(countryId, organization.getId());
    }
}
