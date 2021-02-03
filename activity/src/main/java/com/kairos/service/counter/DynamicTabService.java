package com.kairos.service.counter;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.category.KPIDashboardUpdationDTO;
import com.kairos.dto.activity.counter.distribution.dashboard.KPIDashboardDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.counter.KPIDashboard;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.constants.ActivityMessagesConstants.*;

@Service
public class DynamicTabService {
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private CounterDistService counterDistService;
    static final String PARENT_MODULE_ID = "module_1";
    static final String MODULE_ID = "module_1_786";
    static final String DEFAULT_TAB = "Default";
    static final long COUNTRY_ID = 18712L;


    /**
     * @param refId it can be either countryId or unitId based on level
     *              if level is STAFF or UNIT then refId is unitId
     *              if level is COUNTRY then refId is countryId
     * @param level
     * @return
     */
    public boolean addDefaultTab(Long refId, ConfLevel level) {

        List<KPIDashboard> kpiDashboards = new ArrayList<>();
        if (ConfLevel.STAFF.equals(level)) {
            List<StaffDTO> staffDTOS = userIntegrationService.getStaffListByUnit();
            List<KPIDashboardDTO> kpiDashboardDTOS = counterRepository.getKPIDashboardsOfStaffs(refId, level, staffDTOS.stream().map(k -> k.getId()).collect(Collectors.toList()));
            Map<Long, List<KPIDashboardDTO>> staffDefaultMap = kpiDashboardDTOS.stream().collect(Collectors.groupingBy(k -> k.getStaffId()));
            for (StaffDTO staff : staffDTOS) {
                if (staffDefaultMap.getOrDefault(staff.getId(),new ArrayList<>()).stream().noneMatch(k -> DEFAULT_TAB.equals(k.getName()))) {
                    kpiDashboards.add(new KPIDashboard(PARENT_MODULE_ID, MODULE_ID, DEFAULT_TAB, COUNTRY_ID, refId, staff.getId(), level, true));
                }
            }
            if (isCollectionNotEmpty(kpiDashboards)) {
                counterRepository.saveEntities(kpiDashboards);
            }
        }
        return true;
    }


    public List<KPIDashboardDTO> getDashboardTabOfRef(Long refId, ConfLevel level) {
        List<KPIDashboardDTO> kpiDashboardDTOS;
        if (ConfLevel.STAFF.equals(level)) {
            AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(refId);
            kpiDashboardDTOS = counterRepository.getKPIDashboard(refId, level, accessGroupPermissionCounterDTO.getStaffId());
        } else {
            kpiDashboardDTOS = counterRepository.getKPIDashboard(null, level, refId);
        }
        return kpiDashboardDTOS;
    }


    public List<KPIDashboardDTO> addDashboardTabToRef(Long unitId, Long countryId, List<KPIDashboardDTO> kpiDashboardDTOS, ConfLevel level) {
        Long staffId;
        if (ConfLevel.STAFF.equals(level)) {
            AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(unitId);
            staffId = accessGroupPermissionCounterDTO.getStaffId();
        } else {
            staffId = null;
        }
        List<String> names = getTrimmedNames(kpiDashboardDTOS);
        verifyForDashboardTabAvailability(names, unitId, staffId, countryId, level);
        List<KPIDashboard> kpiDashboards = new ArrayList<>();
        kpiDashboardDTOS.stream().forEach(kpiDashboardDTO -> {
            if (!kpiDashboardDTO.getName().trim().isEmpty()) {
                kpiDashboards.add(new KPIDashboard(kpiDashboardDTO.getParentModuleId(), kpiDashboardDTO.getModuleId(), kpiDashboardDTO.getName(), countryId, unitId, staffId, level, kpiDashboardDTO.isDefaultTab()));
            }
        });
        if (!kpiDashboards.isEmpty()) {
            counterRepository.saveEntities(kpiDashboards);
            kpiDashboards.stream().forEach(kpiDashboard -> kpiDashboard.setModuleId(createModuleId(kpiDashboard.getId(), kpiDashboard.getParentModuleId())));
            counterRepository.saveEntities(kpiDashboards);
        }
        List<StaffDTO> staffDTOS = userIntegrationService.getStaffListByUnit();
        if (ConfLevel.UNIT.equals(level)) {
            createTabsForStaff(unitId, kpiDashboards, staffDTOS.stream().map(StaffDTO::getId).collect(Collectors.toList()));
        }
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(kpiDashboards, KPIDashboardDTO.class);
    }

    public List<KPIDashboardDTO> addDashboardDefaultTabToRef(KPIDashboardDTO kpiDashboardDTO, ConfLevel level) {
        List<KPIDashboard> dashboardKPIDTOS = counterRepository.getKPIDashboardsOfStaffAndUnits(kpiDashboardDTO.getUnitIds(), ConfLevel.STAFF, Arrays.asList(kpiDashboardDTO.getStaffId()));
        Map<Long, List<KPIDashboard>> kpiDashboardMap = dashboardKPIDTOS.stream().collect(Collectors.groupingBy(KPIDashboard::getUnitId, Collectors.toList()));
        List<KPIDashboard> kpiDashboards = new ArrayList<>();
        kpiDashboardDTO.getUnitIds().forEach(unit -> {
            if (kpiDashboardMap.get(unit).stream().noneMatch(k -> k.getName().equalsIgnoreCase(kpiDashboardDTO.getName()))) {
                kpiDashboards.add(new KPIDashboard(kpiDashboardDTO.getParentModuleId(), kpiDashboardDTO.getModuleId(), kpiDashboardDTO.getName(), kpiDashboardDTO.getCountryId(), unit, kpiDashboardDTO.getStaffId(), level, kpiDashboardDTO.isDefaultTab()));
            }

        });

        if (!kpiDashboards.isEmpty()) {
            counterRepository.saveEntities(kpiDashboards);
            kpiDashboards.stream().forEach(kpiDashboard -> kpiDashboard.setModuleId(createModuleId(kpiDashboard.getId(), kpiDashboard.getParentModuleId())));
            counterRepository.saveEntities(kpiDashboards);
        }
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(kpiDashboards, KPIDashboardDTO.class);
    }

    private String createModuleId(BigInteger id, String parentModuleId) {
        return parentModuleId + "_" + id;
    }

    private void verifyForDashboardTabAvailability(List<String> dashboardTabs, Long unitId, Long staffId, Long countryId, ConfLevel level) {
        // confLevel, name
        Long refId = null;
        Long referenceId = ConfLevel.STAFF.equals(level) ? staffId : countryId;
        refId = ConfLevel.UNIT.equals(level) ? unitId : referenceId;
        List<String> formattedNames = new ArrayList<>();
        dashboardTabs.forEach(dashboardTab -> formattedNames.add(dashboardTab.trim().toLowerCase()));
        List<KPIDashboardDTO> kpiDashboardDTOS = ConfLevel.STAFF.equals(level) ? counterRepository.getKPIDashboardsOfStaffs(unitId, ConfLevel.STAFF, Arrays.asList(staffId)) : counterRepository.getKPIDashboard(null, level, refId);
        List<KPIDashboardDTO> duplicateEntries = new ArrayList<>();
        kpiDashboardDTOS.forEach(kpiDashboardDTO -> {
            if (formattedNames.contains(kpiDashboardDTO.getName().trim().toLowerCase())) {
                duplicateEntries.add(kpiDashboardDTO);
            }
        });
        if (ObjectUtils.isCollectionNotEmpty(duplicateEntries)) {
            exceptionService.duplicateDataException(ERROR_DASHBOARD_NAME_DUPLICATE);
        }
    }

    private List<String> getTrimmedNames(List<KPIDashboardDTO> dashboardTabs) {
        List<String> dashboardTabsName = new ArrayList<>();
        try {
            dashboardTabs.forEach(kpiDashboardDTO -> {
                kpiDashboardDTO.setName(kpiDashboardDTO.getName().trim());
                dashboardTabsName.add(kpiDashboardDTO.getName());
            });
        } catch (NullPointerException e) {
            exceptionService.dataNotFoundException(MESSAGE_DASHBOARDTAB_NOTFOUND);
        }
        return dashboardTabsName;
    }

    public List<KPIDashboardDTO> updateDashboardTabs(Long refId, KPIDashboardUpdationDTO dashboardTabs, ConfLevel level) {
        if (ConfLevel.STAFF.equals(level)) {
            AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(refId);
            refId = accessGroupPermissionCounterDTO.getStaffId();
        }
        Set<String> dashboardTabNames = dashboardTabs.getUpdateDashboardTab().stream().map(category -> category.getName().trim().toLowerCase()).collect(Collectors.toSet());
        if (dashboardTabNames.size() != dashboardTabs.getUpdateDashboardTab().size())
            exceptionService.duplicateDataException(ERROR_DASHBOARD_NAME_DUPLICATE);
        List<KPIDashboardDTO> deletableDashboardTab = getExistingDashboardTab(dashboardTabs.getDeleteDashboardTab(), level, refId);
        List<KPIDashboardDTO> existingDashboardTab = getExistingDashboardTab(dashboardTabs.getUpdateDashboardTab(), level, refId);
        List<KPIDashboard> kpiDashboards = modifyCategories(dashboardTabs.getUpdateDashboardTab(), existingDashboardTab, level, refId);
        List<String> deletableCategoryIds = deletableDashboardTab.stream().filter(k -> !k.isDefaultTab()).map(KPIDashboardDTO::getModuleId).collect(Collectors.toList());
        counterRepository.removeAll("moduleId", deletableCategoryIds, KPIDashboard.class, level);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(kpiDashboards, KPIDashboardDTO.class);
    }

    private List<KPIDashboardDTO> getExistingDashboardTab(List<KPIDashboardDTO> dashboardTabs, ConfLevel level, Long refId) {
        if (dashboardTabs.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> dashboardIds = dashboardTabs.stream().map(KPIDashboardDTO::getModuleId).collect(Collectors.toList());
        List<KPIDashboardDTO> dashboardDTOs = counterRepository.getKPIDashboard(dashboardIds, level, refId);
        if (dashboardTabs.size() != dashboardDTOs.size()) {
            exceptionService.invalidOperationException(ERROR_KPI_INVALIDDATA);
        }
        return dashboardDTOs;
    }

    private List<KPIDashboard> modifyCategories(List<KPIDashboardDTO> changedDashboardTabs, List<KPIDashboardDTO> existingAssignmentDTOs, ConfLevel level, Long refId) {
        if (existingAssignmentDTOs.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, KPIDashboardDTO> dashboardDTOMapById = changedDashboardTabs.parallelStream().collect(Collectors.toMap(KPIDashboardDTO::getModuleId, kPICategoryDTO -> kPICategoryDTO));
        List<String> categoryIds = changedDashboardTabs.stream().map(KPIDashboardDTO::getModuleId).collect(Collectors.toList());
        List<KPIDashboard> kpiDashboards = counterRepository.getKPIDashboardByIds(categoryIds, level, refId);
        for (KPIDashboard kpiDashboard : kpiDashboards) {
            KPIDashboardDTO kpiDashboardDTO = dashboardDTOMapById.get(kpiDashboard.getModuleId());
            if (!kpiDashboardDTO.getName().equals(kpiDashboard.getName()) && !kpiDashboard.isDefaultTab()) {
                kpiDashboard.setName(kpiDashboardDTO.getName());
            }
            kpiDashboard.setEnable(kpiDashboardDTO.isEnable());
        }
        return counterRepository.saveEntities(kpiDashboards);
    }

    private void createTabsForStaff(Long unitId, List<KPIDashboard> kpiDashboards, List<Long> staffIds) {
        List<KPIDashboard> dashboards = new ArrayList<>();
        List<KPIDashboardDTO> dashboardDTOList = counterRepository.getKPIDashboardsOfStaffs(unitId, ConfLevel.STAFF, staffIds);
        Map<String, KPIDashboardDTO> nameAndKPIDashBoardMap = dashboardDTOList.stream().collect(Collectors.toMap(k -> k.getName().trim().toLowerCase() + k.getStaffId(), v -> v, (first, second) -> second));
        kpiDashboards.forEach(kpiDashboard -> {
            List<KPIDashboard> kpiDashboardList = new ArrayList<>();
            staffIds.forEach(staff -> {
                if (!nameAndKPIDashBoardMap.containsKey(kpiDashboard.getName().trim().toLowerCase() + staff)) {
                    kpiDashboardList.add(new KPIDashboard(kpiDashboard.getParentModuleId(), kpiDashboard.getModuleId(), kpiDashboard.getName(), kpiDashboard.getCountryId(), kpiDashboard.getUnitId(), staff, ConfLevel.STAFF, kpiDashboard.isDefaultTab()));
                }
            });
            dashboards.addAll(kpiDashboardList);
        });
        if (CollectionUtils.isNotEmpty(dashboards)) {
            counterRepository.saveEntities(dashboards);
            dashboards.stream().forEach(kpiDashboard -> kpiDashboard.setModuleId(createModuleId(kpiDashboard.getId(), kpiDashboard.getParentModuleId())));
            counterRepository.saveEntities(dashboards);
        }
    }


}