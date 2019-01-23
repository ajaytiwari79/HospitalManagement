package com.kairos.service.counter;

import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.category.KPICategoryDTO;
import com.kairos.dto.activity.counter.distribution.dashboard.KPIDashboardDTO;
import com.kairos.dto.activity.counter.distribution.category.KPIDashboardUpdationDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.persistence.model.counter.KPIDashboard;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.isNotNull;

@Service
public class DynamicTabService extends MongoBaseService {
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private CounterDistService counterDistService;

    public List<KPIDashboardDTO> getDashboardTabOfRef(Long refId, ConfLevel level) {
        List<KPIDashboardDTO> kpiDashboardDTOS;
        if (ConfLevel.STAFF.equals(level)) {
            Long unitId = refId;
            AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = genericIntegrationService.getAccessGroupIdsAndCountryAdmin(refId);
            refId = accessGroupPermissionCounterDTO.getStaffId();
            kpiDashboardDTOS = counterRepository.getKPIDashboard(unitId, level, refId);
        } else {
            kpiDashboardDTOS = counterRepository.getKPIDashboard(null, level, refId);
        }

        return kpiDashboardDTOS;
    }

    public List<KPIDashboardDTO> addDashboardTabToRef(Long unitId, Long countryId, List<KPIDashboardDTO> kpiDashboardDTOS, ConfLevel level) {
        Long staffId;
        if (ConfLevel.STAFF.equals(level)) {
            AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = genericIntegrationService.getAccessGroupIdsAndCountryAdmin(unitId);
            staffId = accessGroupPermissionCounterDTO.getStaffId();
        } else {
            staffId = null;
        }
        List<String> names = getTrimmedNames(kpiDashboardDTOS);
        verifyForDashboardTabAvailability(names, unitId, staffId, countryId, level);
        List<KPIDashboard> kpiDashboards = new ArrayList<>();
        kpiDashboardDTOS.stream().forEach(kpiDashboardDTO -> {
            kpiDashboards.add(new KPIDashboard(kpiDashboardDTO.getParentModuleId(), kpiDashboardDTO.getModuleId(), kpiDashboardDTO.getName(), countryId, unitId, staffId, level));
        });
        save(kpiDashboards);
        kpiDashboards.stream().forEach(kpiDashboard -> {
            kpiDashboard.setModuleId(createModuleId(kpiDashboard.getId(), kpiDashboard.getParentModuleId()));
        });
        if (!kpiDashboards.isEmpty()) {
            save(kpiDashboards);
        } else {
            exceptionService.actionNotPermittedException("error.kpi.invalidData");
        }
        List<StaffDTO> staffDTOS = genericIntegrationService.getStaffListByUnit();

//        if (CollectionUtils.isNotEmpty(staffDTOS)) {
//            counterDistService.createDefaultStaffKPISetting(unitId, new DefaultKPISettingDTO(staffDTOS.stream().map(StaffDTO::getId).collect(Collectors.toList())));
//        }
        if(ConfLevel.UNIT.equals(level)){
            createTabsForStaff(kpiDashboards,staffDTOS.stream().map(StaffDTO::getId).collect(Collectors.toList()));
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(kpiDashboards, KPIDashboardDTO.class);
    }

    private String createModuleId(BigInteger id, String parentModuleId) {
        return parentModuleId + "_" + id;
    }

    private void verifyForDashboardTabAvailability(List<String> dashboardTabs, Long unitId, Long staffId, Long countryId, ConfLevel level) {
        // confLevel, name
        Long refId = null;
        refId = ConfLevel.UNIT.equals(level) ? unitId : (ConfLevel.STAFF.equals(staffId) ? staffId : countryId);
        List<String> formattedNames = new ArrayList<>();
        dashboardTabs.forEach(dashboardTab -> formattedNames.add(dashboardTab.trim().toLowerCase()));
        List<KPIDashboardDTO> kpiDashboardDTOS = counterRepository.getKPIDashboard(null, level, refId);
        List<KPIDashboardDTO> duplicateEntries = new ArrayList<>();
        kpiDashboardDTOS.forEach(kpiDashboardDTO -> {
            if (formattedNames.contains(kpiDashboardDTO.getName().trim().toLowerCase())) {
                duplicateEntries.add(kpiDashboardDTO);
            }
        });
        if (duplicateEntries.size() > 0) exceptionService.duplicateDataException("error.dashboard.name.duplicate");
    }

    private List<String> getTrimmedNames(List<KPIDashboardDTO> dashboardTabs) {
        List<String> dashboardTabsName = new ArrayList<>();
        try {
            dashboardTabs.forEach(kpiDashboardDTO -> {
                kpiDashboardDTO.setName(kpiDashboardDTO.getName().trim());
                dashboardTabsName.add(kpiDashboardDTO.getName());
            });
        } catch (NullPointerException e) {
            exceptionService.dataNotFoundException("message.dashboardtab.notfound");
        }
        return dashboardTabsName;
    }

    public List<KPIDashboardDTO> updateDashboardTabs(Long refId, KPIDashboardUpdationDTO dashboardTabs, ConfLevel level) {
        if (ConfLevel.STAFF.equals(level)) {
            AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = genericIntegrationService.getAccessGroupIdsAndCountryAdmin(refId);
            refId = accessGroupPermissionCounterDTO.getStaffId();
        }
        Set<String> dashboardTabNames = dashboardTabs.getUpdateDashboardTab().stream().map(category -> category.getName().trim().toLowerCase()).collect(Collectors.toSet());
        if (dashboardTabNames.size() != dashboardTabs.getUpdateDashboardTab().size())
            exceptionService.duplicateDataException("error.kpi_category.duplicate");
        List<KPIDashboardDTO> deletableDashboardTab = getExistingDashboardTab(dashboardTabs.getDeleteDashboardTab(), level, refId);
        List<KPIDashboardDTO> existingDashboardTab = getExistingDashboardTab(dashboardTabs.getUpdateDashboardTab(), level, refId);
        List<KPIDashboard> kpiDashboards = modifyCategories(dashboardTabs.getUpdateDashboardTab(), existingDashboardTab, level, refId);
        List<String> deletableCategoryIds = deletableDashboardTab.stream().map(kpiCategoryDTO -> kpiCategoryDTO.getModuleId()).collect(Collectors.toList());
        // counterRepository.removeAll("categoryId", deletableCategoryIds, CategoryKPIConf.class);
        counterRepository.removeAll("moduleId", deletableCategoryIds, KPIDashboard.class);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(kpiDashboards, KPIDashboardDTO.class);
    }

    private List<KPIDashboardDTO> getExistingDashboardTab(List<KPIDashboardDTO> dashboardTabs, ConfLevel level, Long refId) {
        if (dashboardTabs.isEmpty()) return new ArrayList<>();
        List<String> dashboardIds = dashboardTabs.stream().map(KPIDashboardDTO::getModuleId).collect(Collectors.toList());
        List<KPIDashboardDTO> dashboardDTOs = counterRepository.getKPIDashboard(dashboardIds, level, refId);
        if (dashboardTabs.size() != dashboardDTOs.size()) {
            exceptionService.invalidOperationException("error.kpi.invalidData");
        }
        return dashboardDTOs;
    }

    private List<KPIDashboard> modifyCategories(List<KPIDashboardDTO> changedDashboardTabs, List<KPIDashboardDTO> existingAssignmentDTOs, ConfLevel level, Long refId) {
        if (existingAssignmentDTOs.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, KPIDashboardDTO> dashboardDTOMapById = changedDashboardTabs.parallelStream().collect(Collectors.toMap(KPIDashboardDTO::getModuleId, kPICategoryDTO -> kPICategoryDTO));
        List<String> categoriesIds = changedDashboardTabs.stream().map(KPIDashboardDTO::getModuleId).collect(Collectors.toList());
        List<KPIDashboard> kpiDashboards = counterRepository.getKPIDashboardByIds(categoriesIds, level, refId);
        for (KPIDashboard kpiDashboard : kpiDashboards) {
            KPIDashboardDTO kpiDashboardDTO = dashboardDTOMapById.get(kpiDashboard.getModuleId());
            if (!kpiDashboardDTO.getName().equals(kpiDashboard.getName())) {
                kpiDashboard.setName(kpiDashboardDTO.getName());
            }
            kpiDashboard.setEnable(kpiDashboardDTO.isEnable());
        }
        save(kpiDashboards);
        return kpiDashboards;
    }

    private void createTabsForStaff(List<KPIDashboard> kpiDashboards,List<Long> staffIds){
        List<KPIDashboard> dashboards=new ArrayList<>();
        kpiDashboards.forEach(kpiDashboard -> {
            List<KPIDashboard> kpiDashboardList=new ArrayList<>();
            staffIds.forEach(staff->{
                kpiDashboardList.add(new KPIDashboard(kpiDashboard.getParentModuleId(),kpiDashboard.getModuleId(),kpiDashboard.getName(),kpiDashboard.getCountryId(),kpiDashboard.getUnitId(),staff,ConfLevel.STAFF));
            });
            dashboards.addAll(kpiDashboardList);
        });
        if(CollectionUtils.isNotEmpty(dashboards)){
            save(dashboards);
        }
    }


}