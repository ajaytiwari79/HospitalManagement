package com.kairos.service.counter;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.FilterCriteriaDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupKPIConfDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupMappingDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.access_group.StaffIdsDTO;
import com.kairos.dto.activity.counter.distribution.category.*;
import com.kairos.dto.activity.counter.distribution.dashboard.KPIDashboardDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeKPIConfDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeMappingDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIEntryConfDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.KPIValidity;
import com.kairos.dto.activity.counter.enums.LocationType;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.mongodb.client.result.DeleteResult;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class CounterDistService extends MongoBaseService {
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private CounterDataService counterDataService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private ActivityService activityService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private FibonacciKPIService fibonacciKPIService;

    private final static Logger LOGGER = LoggerFactory.getLogger(CounterDistService.class);

    //get access group page and dashboard tab
    public List<KPIAccessPageDTO> getKPIAccessPageListForUnit(Long refId, ConfLevel level) {
        List<KPIAccessPageDTO> kpiAccessPageDTOSOfDashboard = counterRepository.getKPIAcceccPage(refId, level);
        List<KPIAccessPageDTO> kpiAccessPageDTOS = userIntegrationService.getKPIEnabledTabsForModuleForUnit(refId);
        setKPIAccessPage(kpiAccessPageDTOSOfDashboard, kpiAccessPageDTOS);
        return kpiAccessPageDTOS;
    }

    public List<KPIAccessPageDTO> getKPIAccessPageListForCountry(Long countryId, Long unitId, ConfLevel level) {
        List<KPIAccessPageDTO> kpiAccessPageDTOSOfDashboard = counterRepository.getKPIAcceccPage(countryId, level);
        List<KPIAccessPageDTO> kpiAccessPageDTOS = userIntegrationService.getKPIEnabledTabsForModuleForUnit(unitId);
        setKPIAccessPage(kpiAccessPageDTOSOfDashboard, kpiAccessPageDTOS);
        return kpiAccessPageDTOS;
    }

    public void setKPIAccessPage(List<KPIAccessPageDTO> kpiAccessPages, List<KPIAccessPageDTO> kpiAccessPageDTOS) {
        if (kpiAccessPages.isEmpty() || kpiAccessPageDTOS.isEmpty()) return;
        Map<String, List<KPIAccessPageDTO>> accessPageMap = new HashMap<>();
        kpiAccessPages.stream().forEach(kpiAccessPageDTO -> {
            kpiAccessPageDTO.getChild().forEach(kpiAccessPageDto -> kpiAccessPageDto.setActive(true));
            accessPageMap.put(kpiAccessPageDTO.getModuleId(), kpiAccessPageDTO.getChild());
        });
        kpiAccessPageDTOS.stream().forEach(kpiAccessPageDTO -> {
            if (accessPageMap.get(kpiAccessPageDTO.getModuleId()) != null) {
                kpiAccessPageDTO.setChild(accessPageMap.get(kpiAccessPageDTO.getModuleId()));
            }
        });
    }

    public List<KPIDTO> getKPIsList(Long refId, ConfLevel level) {
        if (ConfLevel.STAFF.equals(level)) {
            refId = userIntegrationService.getStaffIdByUserId(refId);
        }
        List<KPIDTO> kpidtos = counterRepository.getCounterListForReferenceId(refId, level, false);
        if (kpidtos.isEmpty()) {
            LOGGER.info("KPI not found for {} id " + refId, level);
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTER_KPI_NOTFOUND);
        }
        return kpidtos;
    }

    public InitialKPICategoryDistDataDTO getInitialCategoryKPIDistData(Long refId, ConfLevel level) {
        List<KPICategoryDTO> categories = counterRepository.getKPICategory(null, level, refId);
        List<BigInteger> categoryIds = categories.stream().map(KPICategoryDTO::getId).collect(toList());
        List<CategoryKPIMappingDTO> categoryKPIMapping = counterRepository.getKPIsMappingForCategories(categoryIds);
        List<KPIDTO> fibonacciKPIDTOS = fibonacciKPIService.getAllFibonacciKPI(refId,level);
        Map<BigInteger,List<KPIDTO>> categoryIdAndFibonacciKPI = fibonacciKPIDTOS.stream().filter(fibonacciKPIDTO -> isNotNull(fibonacciKPIDTO.getCategoryId())).collect(Collectors.groupingBy(k->k.getCategoryId(),Collectors.toList()));
        for (CategoryKPIMappingDTO categoryKPIMappingDTO : categoryKPIMapping) {
            if(categoryIdAndFibonacciKPI.containsKey(categoryKPIMappingDTO.getCategoryId())){
                categoryKPIMappingDTO.getKpiId().addAll(categoryIdAndFibonacciKPI.get(categoryKPIMappingDTO.getCategoryId()).stream().map(KPIDTO::getId).collect(toList()));            }
        }
        return new InitialKPICategoryDistDataDTO(categories, categoryKPIMapping);
    }

    public StaffKPIGalleryDTO getInitialCategoryKPIDistDataForStaff(Long refId) {
        Set<BigInteger> kpiIds;
        List<KPIDTO> copyAndkpidtos = new ArrayList<>();
        List<KPIDTO> kpidtos;
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(refId);
        if (accessGroupPermissionCounterDTO.isCountryAdmin()) {
            kpidtos = counterRepository.getCounterListForReferenceId(refId, ConfLevel.UNIT, false);
        } else {
            if (isCollectionEmpty(accessGroupPermissionCounterDTO.getAccessGroupIds())) {
                exceptionService.dataNotFoundException(MESSAGE_STAFF_INVALID_UNIT);
            }
            kpidtos = counterRepository.getAccessGroupKPIDto(accessGroupPermissionCounterDTO.getAccessGroupIds(), ConfLevel.UNIT, refId, accessGroupPermissionCounterDTO.getStaffId());
            List<KPIDTO> copyKpidtos = counterRepository.getCopyKpiOfUnit(ConfLevel.STAFF, accessGroupPermissionCounterDTO.getStaffId(), true);
            if (isCollectionNotEmpty(copyKpidtos)) {
                copyAndkpidtos.addAll(copyKpidtos);
            }
        }
         copyAndkpidtos.addAll(kpidtos);
        kpiIds = copyAndkpidtos.stream().map(KPIDTO::getId).collect(Collectors.toSet());
        List<ApplicableKPI> applicableKPIS = counterRepository.getApplicableKPI(new ArrayList(kpiIds), ConfLevel.STAFF, accessGroupPermissionCounterDTO.getStaffId());
        Map<BigInteger, String> kpiIdAndTitleMap = applicableKPIS.stream().collect(Collectors.toMap(ApplicableKPI::getActiveKpiId, ApplicableKPI::getTitle));
        List<CategoryKPIMappingDTO> categoryKPIMapping = counterRepository.getKPIsMappingForCategoriesForStaff(kpiIds, refId, ConfLevel.UNIT);
        copyAndkpidtos.forEach(kpidto -> {
            if (kpiIdAndTitleMap.get(kpidto.getId()) != null) {
                kpidto.setTitle(kpiIdAndTitleMap.get(kpidto.getId()));
            }
        });
        return new StaffKPIGalleryDTO(categoryKPIMapping, copyAndkpidtos);
    }

    public void addCategoryKPIsDistribution(CategoryKPIsDTO categoryKPIsDetails, ConfLevel level, Long refId) {
        Long countryId = ConfLevel.COUNTRY.equals(level) ? refId : null;
        Long unitId = ConfLevel.UNIT.equals(level) ? refId : null;
        List<ApplicableKPI> applicableKPIS = counterRepository.getApplicableKPI(categoryKPIsDetails.getKpiIds(), level, refId);
        if (applicableKPIS.isEmpty()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTER_KPI_NOTFOUND);
        }
        List<KPICategoryDTO> kpiCategoryDTOS = counterRepository.getKPICategory(null, level, refId);
        List<BigInteger> categoryIds = kpiCategoryDTOS.stream().map(KPICategoryDTO::getId).collect(Collectors.toList());
        if (!categoryIds.contains(categoryKPIsDetails.getCategoryId())) {
            exceptionService.dataNotFoundByIdException("error.kpi_category.availability");
        }
        List<CategoryKPIConf> categoryKPIConfs = counterRepository.getCategoryKPIConfs(categoryKPIsDetails.getKpiIds(), categoryIds);
        List<BigInteger> availableCategoryIds = categoryKPIConfs.stream().map(CategoryKPIConf::getCategoryId).collect(toList());
        if (availableCategoryIds.contains(categoryKPIsDetails.getCategoryId())) {
            exceptionService.invalidOperationException("error.dist.category_kpi.invalid_operation");
        }
        List<CategoryKPIConf> newCategoryKPIConfs = new ArrayList<>();
        applicableKPIS.parallelStream().forEach(applicableKPI -> newCategoryKPIConfs.add(new CategoryKPIConf(applicableKPI.getActiveKpiId(), categoryKPIsDetails.getCategoryId(), countryId, unitId, level)));
        if (!newCategoryKPIConfs.isEmpty()) {
            save(newCategoryKPIConfs);
            counterRepository.removeCategoryKPIEntries(availableCategoryIds, categoryKPIsDetails.getKpiIds());
        }

    }

    public List<BigInteger> getInitialTabKPIDataConf(String moduleId, Long refId, ConfLevel level) {
        Long countryId = null;
        if (ConfLevel.UNIT.equals(level)) {
            countryId = userIntegrationService.getCountryId(refId);
        }
        List<TabKPIDTO> tabKPIDTOS = counterRepository.getTabKPIIdsByTabIds(moduleId, refId, countryId, level);
        if (tabKPIDTOS == null || tabKPIDTOS.isEmpty()) return new ArrayList<>();
        return tabKPIDTOS.stream().map(tabKPIDTO -> new BigInteger(tabKPIDTO.getKpi().getId().toString())).collect(Collectors.toList());
    }

    public TabKPIDTO updateInitialTabKPIDataConf(TabKPIDTO tabKPIDTO, Long unitId, ConfLevel level) {
        TabKPIConf tabKPIConf = counterRepository.findTabKPIConfigurationByTabId(tabKPIDTO.getTabId(), Arrays.asList(tabKPIDTO.getKpiId()), unitId, level);
        if (!Optional.ofNullable(tabKPIConf).isPresent()) {
            exceptionService.invalidRequestException(ERROR_KPI_INVALIDDATA);
        }
        if (tabKPIConf.getTabId().equals(tabKPIDTO.getTabId()) && tabKPIConf.getKpiId().equals(tabKPIDTO.getKpiId())) {
            tabKPIConf.setLocationType(tabKPIDTO.getLocationType());
            tabKPIConf.setKpiValidity(tabKPIDTO.getKpiValidity());
            tabKPIConf.setPriority(calculatePriority(level, tabKPIDTO.getKpiValidity(), tabKPIDTO.getLocationType()));
        }
        save(tabKPIConf);
        return ObjectMapperUtils.copyPropertiesByMapper(tabKPIConf, TabKPIDTO.class);

    }

    public int calculatePriority(ConfLevel level, KPIValidity validity, LocationType type) {
        return level.value + validity.value + type.value;
    }

    public List<TabKPIDTO> getInitialTabKPIDataConfForStaff(String moduleId, Long unitId, ConfLevel level, FilterCriteriaDTO filters, Long staffId) {
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(unitId);
        if (!accessGroupPermissionCounterDTO.isCountryAdmin() && CollectionUtils.isEmpty(accessGroupPermissionCounterDTO.getAccessGroupIds())) {
            exceptionService.actionNotPermittedException(MESSAGE_STAFF_INVALID_UNIT);
        }
        Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
        List<BigInteger> kpiIds = new ArrayList<>();
        if (!accessGroupPermissionCounterDTO.isCountryAdmin()) {
            kpiIds = counterRepository.getAccessGroupKPIIds(accessGroupPermissionCounterDTO.getAccessGroupIds(), ConfLevel.UNIT, unitId, accessGroupPermissionCounterDTO.getStaffId());
        }
        List<KPIDTO> copyKpiDtos = counterRepository.getCopyKpiOfUnit(ConfLevel.STAFF, accessGroupPermissionCounterDTO.getStaffId(), true);
        if (isCollectionNotEmpty(copyKpiDtos)) {
            kpiIds.addAll(copyKpiDtos.stream().map(KPIDTO::getId).collect(toList()));
        }
        List<ApplicableKPI> applicableKPIS;
        if (accessGroupPermissionCounterDTO.isCountryAdmin()) {
            applicableKPIS = counterRepository.getApplicableKPI(new ArrayList(), ConfLevel.COUNTRY, accessGroupPermissionCounterDTO.getCountryId());
        } else {
            applicableKPIS = counterRepository.getApplicableKPI(new ArrayList(kpiIds), ConfLevel.STAFF, accessGroupPermissionCounterDTO.getStaffId());
        }
        Map<BigInteger, String> kpiIdAndTitleMap = applicableKPIS.stream().collect(Collectors.toMap(ApplicableKPI::getActiveKpiId, ApplicableKPI::getTitle));
        List<TabKPIDTO> tabKPIDTOS = counterRepository.getTabKPIForStaffByTabAndStaffIdPriority(moduleId, kpiIds, accessGroupPermissionCounterDTO.getStaffId(), countryId, unitId, level);
        tabKPIDTOS = filterTabKpiDate(tabKPIDTOS);
        filters.setKpiIds(tabKPIDTOS.stream().map(tabKPIDTO -> tabKPIDTO.getKpi().getId()).collect(toList()));
        filters.setUnitId(unitId);
        filters.setCountryId(countryId);
        filters.setCountryAdmin(accessGroupPermissionCounterDTO.isCountryAdmin());
        filters.setManagement(accessGroupPermissionCounterDTO.isManagement());
        filters.setStaffId(accessGroupPermissionCounterDTO.isManagement() ? staffId : accessGroupPermissionCounterDTO.getStaffId());
        Map<BigInteger, CommonRepresentationData> data = counterDataService.generateKPIData(filters, unitId, accessGroupPermissionCounterDTO.getStaffId());
        tabKPIDTOS.forEach(tabKPIDTO -> {
            tabKPIDTO.setData(data.get(tabKPIDTO.getKpi().getId()));
            if (kpiIdAndTitleMap.get(tabKPIDTO.getKpi().getId()) != null) {
                tabKPIDTO.getKpi().setTitle(kpiIdAndTitleMap.get(tabKPIDTO.getKpi().getId()));
            }
        });
        return tabKPIDTOS;
    }

    public List<TabKPIDTO> filterTabKpiDate(List<TabKPIDTO> tabKPIDTOS) {
        Map<BigInteger, TabKPIDTO> filterResults = new LinkedHashMap<>();
        tabKPIDTOS = tabKPIDTOS.stream().filter(tabKPIDTO -> isNotNull(tabKPIDTO.getKpi())).collect(Collectors.toList());
        tabKPIDTOS.forEach(tabKPIDTO -> filterResults.put(tabKPIDTO.getKpi().getId(), tabKPIDTO));
        tabKPIDTOS.stream().forEach(tabKPIDTO -> {
            if (filterResults.get(tabKPIDTO.getKpi().getId()).getKpi().getId().equals(tabKPIDTO.getKpi().getId())) {
                if (filterResults.get(tabKPIDTO.getKpi().getId()).getPriority() > tabKPIDTO.getPriority()) {
                    filterResults.put(tabKPIDTO.getKpi().getId(), tabKPIDTO);
                }
            } else {
                filterResults.put(tabKPIDTO.getKpi().getId(), tabKPIDTO);
            }
        });
        return filterResults.entrySet().stream().map(Map.Entry::getValue).collect(toList());
    }

    public List<TabKPIDTO> getInitialTabKPIDataConfForStaffPriority(String moduleId, Long unitId, ConfLevel level) {
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(unitId);
        List<BigInteger> kpiIds = new ArrayList<>();
        if (!accessGroupPermissionCounterDTO.isCountryAdmin()) {
            kpiIds = counterRepository.getAccessGroupKPIIds(accessGroupPermissionCounterDTO.getAccessGroupIds(), ConfLevel.UNIT, unitId, accessGroupPermissionCounterDTO.getStaffId());
        }
        List<TabKPIDTO> tabKPIDTOS = counterRepository.getTabKPIForStaffByTabAndStaffIdPriority(moduleId, kpiIds, accessGroupPermissionCounterDTO.getStaffId(), accessGroupPermissionCounterDTO.getCountryId(), unitId, level);
        return tabKPIDTOS;
    }

    public List<TabKPIDTO> addTabKPIEntriesOfStaff(List<TabKPIMappingDTO> tabKPIMappingDTOS, Long unitId, ConfLevel level ,Long staffId) {
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(unitId);
        List<TabKPIConf> entriesToSave = new ArrayList<>();
        List<String> tabIds = tabKPIMappingDTOS.stream().map(TabKPIMappingDTO::getTabId).collect(toList());
        List<BigInteger> kpiIds = tabKPIMappingDTOS.stream().map(TabKPIMappingDTO::getKpiId).collect(toList());
        Map<String, Map<BigInteger, BigInteger>> tabKpiMap = setTabKPIEntries(tabIds, kpiIds, null, unitId, accessGroupPermissionCounterDTO.getStaffId(), level, accessGroupPermissionCounterDTO.isCountryAdmin());
        tabKPIMappingDTOS.stream().forEach(tabKPIMappingDTO -> {
            if (tabKpiMap.get(tabKPIMappingDTO.getTabId()).get(tabKPIMappingDTO.getKpiId()) == null) {
                entriesToSave.add(new TabKPIConf(tabKPIMappingDTO.getTabId(), tabKPIMappingDTO.getKpiId(), null, unitId, accessGroupPermissionCounterDTO.getStaffId(), level, tabKPIMappingDTO.getPosition(), KPIValidity.BASIC, LocationType.FIX, calculatePriority(ConfLevel.UNIT, KPIValidity.BASIC, LocationType.FIX)));
            }
        });
        if (entriesToSave.isEmpty()) {
            exceptionService.invalidRequestException(ERROR_KPI_INVALIDDATA);
        }
        save(entriesToSave);
        List<ApplicableKPI> applicableKPIS;
        if (accessGroupPermissionCounterDTO.isCountryAdmin()) {
            applicableKPIS = counterRepository.getApplicableKPI(new ArrayList(), ConfLevel.COUNTRY, accessGroupPermissionCounterDTO.getCountryId());
        } else {
            applicableKPIS = counterRepository.getApplicableKPI(new ArrayList(kpiIds), ConfLevel.STAFF, accessGroupPermissionCounterDTO.getStaffId());
        }
        Map<BigInteger, String> kpiIdAndTitleMap = applicableKPIS.stream().collect(Collectors.toMap(ApplicableKPI::getActiveKpiId, ApplicableKPI::getTitle));
        List<TabKPIDTO> tabKPIDTOS = counterRepository.getTabKPIForStaffByTabAndStaffId(tabIds, kpiIds, accessGroupPermissionCounterDTO.getStaffId(), unitId, level);
        Map<BigInteger, CommonRepresentationData> data = counterDataService.generateKPIData(new FilterCriteriaDTO(unitId, staffId ,kpiIds, accessGroupPermissionCounterDTO.getCountryId(), accessGroupPermissionCounterDTO.isCountryAdmin()), unitId,accessGroupPermissionCounterDTO.getStaffId());
        tabKPIDTOS.forEach(tabKPIDTO -> {
            tabKPIDTO.setData(data.get(tabKPIDTO.getKpi().getId()));
            if (kpiIdAndTitleMap.get(tabKPIDTO.getKpi().getId()) != null) {
                tabKPIDTO.getKpi().setTitle(kpiIdAndTitleMap.get(tabKPIDTO.getKpi().getId()));
            }
        });
        return tabKPIDTOS;
    }

    public void addTabKPIEntries(TabKPIEntryConfDTO tabKPIEntries, Long countryId, Long unitId, Long staffId, ConfLevel level) {
        List<TabKPIConf> entriesToSave = new ArrayList<>();
        Map<String, Map<BigInteger, BigInteger>> tabKpiMap = setTabKPIEntries(tabKPIEntries.getTabIds(), tabKPIEntries.getKpiIds(), countryId, unitId, staffId, level, false);
        tabKPIEntries.getTabIds().forEach(tabId -> tabKPIEntries.getKpiIds().forEach(kpiId -> {
            if (tabKpiMap.get(tabId).get(kpiId) == null) {
                entriesToSave.add(new TabKPIConf(tabId, kpiId, countryId, unitId, staffId, level, null, KPIValidity.BASIC, LocationType.FIX, calculatePriority(level, KPIValidity.BASIC, LocationType.FIX)));
            }
        }));
        if (entriesToSave.isEmpty()) {
            exceptionService.invalidRequestException(ERROR_KPI_INVALIDDATA);
        }
        save(entriesToSave);
    }

    public Map<String, Map<BigInteger, BigInteger>> setTabKPIEntries(List<String> tabIds, List<BigInteger> kpiIds, Long countryId, Long unitId, Long staffId, ConfLevel level, boolean isCountryAdmin) {
        Long refId = ConfLevel.COUNTRY.equals(level) ? countryId : unitId;
        if (ConfLevel.STAFF.equals(level)) {
            refId = staffId;
        }
        List<TabKPIMappingDTO> tabKPIMappingDTOS = counterRepository.getTabKPIConfigurationByTabIds(tabIds, kpiIds, refId, level);
        if (!isCountryAdmin) {
            List<ApplicableKPI> applicableKPIS = counterRepository.getKPIByKPIIds(kpiIds, refId, level);
            if (kpiIds.size() != applicableKPIS.size()) {
                exceptionService.actionNotPermittedException(MESSAGE_COUNTER_KPI_NOTFOUND);
            }
        }
        Map<String, Map<BigInteger, BigInteger>> tabKpiMap = new HashMap<>();
        tabIds.forEach(tabKpiId -> tabKpiMap.put(tabKpiId, new HashMap<BigInteger, BigInteger>()));
        tabKPIMappingDTOS.forEach(tabKPIMappingDTO -> tabKpiMap.get(tabKPIMappingDTO.getTabId()).put(tabKPIMappingDTO.getKpiId(), tabKPIMappingDTO.getKpiId()));
        return tabKpiMap;
    }

    public void updateTabKPIEntries(List<TabKPIMappingDTO> tabKPIMappingDTOS, String tabId, Long unitId, ConfLevel level) {
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(unitId);
        List<BigInteger> kpiIds = tabKPIMappingDTOS.stream().map(TabKPIMappingDTO::getKpiId).collect(Collectors.toList());
        List<TabKPIConf> tabKPIConfs = counterRepository.findTabKPIConfigurationByTabIds(Arrays.asList(tabId), kpiIds, accessGroupPermissionCounterDTO.getStaffId(), level);
        if (!Optional.ofNullable(tabKPIConfs).isPresent()) {
            exceptionService.invalidRequestException(ERROR_KPI_INVALIDDATA);
        }
        Map<BigInteger, TabKPIMappingDTO> tabKPIMappingDTOMap = new HashMap<>();
        tabKPIMappingDTOS.stream().forEach(tabKPIMappingDTO -> tabKPIMappingDTOMap.put(tabKPIMappingDTO.getId(), tabKPIMappingDTO));
        tabKPIConfs.stream().forEach(tabKPIConf -> {
            TabKPIMappingDTO tabKPIMappingDTO = tabKPIMappingDTOMap.get(tabKPIConf.getId());
            tabKPIConf.setPosition(tabKPIMappingDTO.getPosition());
        });
        save(tabKPIConfs);
    }

    public void removeTabKPIEntries(TabKPIMappingDTO tabKPIMappingDTO, Long refId, ConfLevel level) {
        if (ConfLevel.STAFF.equals(level)) {
            AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(refId);
            refId = accessGroupPermissionCounterDTO.getStaffId();
        }
        DeleteResult result = counterRepository.removeTabKPIConfiguration(tabKPIMappingDTO, refId, level);
        if (result.getDeletedCount() < 1) {
            exceptionService.invalidRequestException(ERROR_KPI_INVALIDDATA);
        }
    }
    //setting accessGroup-KPI configuration

    public List<BigInteger> getInitialAccessGroupKPIDataConf(Long accessGroupId, Long refId, ConfLevel level) {
        List<BigInteger> AccessGroupMappingIds = counterRepository.getAccessGroupKPIIdsAccessGroupIds(Arrays.asList(accessGroupId), new ArrayList<>(), level, refId);
        if (AccessGroupMappingIds == null || AccessGroupMappingIds.isEmpty()) return new ArrayList<>();
        return AccessGroupMappingIds;
    }

    public void addAccessGroupKPIEntries(AccessGroupKPIConfDTO accessGroupKPIConf, Long refId, ConfLevel level) {
        Long countryId = ConfLevel.COUNTRY.equals(level) ? refId : null;
        Long unitId = ConfLevel.UNIT.equals(level) ? refId : null;
        List<AccessGroupKPIEntry> entriesToSave = new ArrayList<>();
        List<ApplicableKPI> applicableKPIS = counterRepository.getKPIByKPIIds(accessGroupKPIConf.getKpiIds(), refId, level);
        Map<BigInteger, ApplicableKPI> kpiIdAndApplicableKpi = applicableKPIS.stream().collect(Collectors.toMap(ApplicableKPI::getActiveKpiId, v -> v));
        if (accessGroupKPIConf.getKpiIds().size() != applicableKPIS.size()) {
            exceptionService.actionNotPermittedException(MESSAGE_COUNTER_KPI_NOTFOUND);
        }
        List<AccessGroupMappingDTO> AccessGroupMappingDTOS = counterRepository.getAccessGroupKPIEntryAccessGroupIds(accessGroupKPIConf.getAccessGroupIds(), accessGroupKPIConf.getKpiIds(), level, refId);
        Map<Long, Map<BigInteger, BigInteger>> accessGroupKPIMap = new HashMap<>();
        accessGroupKPIConf.getAccessGroupIds().forEach(orgTypeId -> accessGroupKPIMap.put(orgTypeId, new HashMap<BigInteger, BigInteger>()));
        AccessGroupMappingDTOS.forEach(AccessGroupMappingDTO -> accessGroupKPIMap.get(AccessGroupMappingDTO.getAccessGroupId()).put(AccessGroupMappingDTO.getKpiId(), AccessGroupMappingDTO.getKpiId()));
        accessGroupKPIConf.getAccessGroupIds().forEach(accessGroupId -> accessGroupKPIConf.getKpiIds().forEach(kpiId -> {
            if (accessGroupKPIMap.get(accessGroupId).get(kpiId) == null) {
                entriesToSave.add(new AccessGroupKPIEntry(accessGroupId, kpiId, countryId, unitId, level));
            }
        }));
        if (entriesToSave.isEmpty()) {
            exceptionService.invalidRequestException(ERROR_KPI_INVALIDDATA);
        }
        save(entriesToSave);
        if (ConfLevel.UNIT.equals(level)) {
            assignKpiToStaffViaAccessGroup(accessGroupKPIConf, refId, unitId, kpiIdAndApplicableKpi, AccessGroupMappingDTOS);
        }
    }

    private void assignKpiToStaffViaAccessGroup(AccessGroupKPIConfDTO accessGroupKPIConf, Long refId, Long unitId, Map<BigInteger, ApplicableKPI> kpiIdAndApplicableKpi, List<AccessGroupMappingDTO> accessGroupMappingDTOS) {
        List<ApplicableKPI> applicableKPISToSave = new ArrayList<>();
        Map<Long, Map<BigInteger, BigInteger>> staffIdKpiMap = new HashMap<>();
        List<StaffIdsDTO> staffIdsDTOS = userIntegrationService.getStaffIdsByunitAndAccessGroupId(refId, accessGroupKPIConf.getAccessGroupIds());
        List<Long> staffids = staffIdsDTOS.stream().flatMap(staffIdsDTO -> staffIdsDTO.getStaffIds().stream()).collect(toList());
        staffids.forEach(staffid -> staffIdKpiMap.put(staffid, new HashMap<BigInteger, BigInteger>()));
        List<ApplicableKPI> applicableKPISForStaff = counterRepository.getApplicableKPIByReferenceId(accessGroupMappingDTOS.stream().map(AccessGroupMappingDTO::getKpiId).collect(toList()), staffids, ConfLevel.STAFF);
        applicableKPISForStaff.forEach(applicableKPI -> staffIdKpiMap.get(applicableKPI.getStaffId()).put(applicableKPI.getActiveKpiId(), applicableKPI.getActiveKpiId()));
        staffids.forEach(staffId -> accessGroupKPIConf.getKpiIds().forEach(kpiId -> {
            ApplicableFilter applicableFilter = null;
            if (staffIdKpiMap.get(staffId).get(kpiId) == null) {
                if (kpiIdAndApplicableKpi.containsKey(kpiId) && isNotNull(kpiIdAndApplicableKpi.get(kpiId).getApplicableFilter())) {
                    applicableFilter = new ApplicableFilter(kpiIdAndApplicableKpi.get(kpiId).getApplicableFilter().getCriteriaList(), false);
                }
                applicableKPISToSave.add(new ApplicableKPI(kpiId, kpiId, null, unitId, staffId, ConfLevel.STAFF, applicableFilter, kpiIdAndApplicableKpi.get(kpiId).getTitle(), false,kpiIdAndApplicableKpi.get(kpiId).getKpiRepresentation(),kpiIdAndApplicableKpi.get(kpiId).getInterval(),kpiIdAndApplicableKpi.get(kpiId).getValue(),kpiIdAndApplicableKpi.get(kpiId).getFrequencyType(),kpiIdAndApplicableKpi.get(kpiId).getFibonacciKPIConfigs()));
                staffIdKpiMap.get(staffId).put(kpiId, kpiId);
            }
        }));
        if (!applicableKPISToSave.isEmpty()) {
            save(applicableKPISToSave);
        }
    }

    public void removeAccessGroupKPIEntries(AccessGroupMappingDTO accessGroupMappingDTO, Long refId, ConfLevel level) {
        if (ConfLevel.UNIT.equals(level)) {
            AccessGroupKPIEntry accessGroupKPIEntry = counterRepository.getAccessGroupKPIEntry(accessGroupMappingDTO, refId, level);
            if (!Optional.ofNullable(accessGroupKPIEntry).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ACCESSGROUP_KPI_NOTFOUND);
            }
            List<AccessGroupPermissionCounterDTO> staffAndAccessGroups = userIntegrationService.getStaffAndAccessGroups(accessGroupKPIEntry);
            Set<Long> accessGroupsIds = staffAndAccessGroups.stream().flatMap(accessGroupDTO -> accessGroupDTO.getAccessGroupIds().stream().filter(accessGroup -> !(accessGroup.equals(accessGroupMappingDTO.getAccessGroupId())))).collect(toSet());
            List<AccessGroupMappingDTO> accessGroupMappingDTOS = counterRepository.getAccessGroupAndKpiId(accessGroupsIds, level, refId);
            Map<Long, List<BigInteger>> staffKpiMap = staffAndAccessGroups.stream().collect(Collectors.toMap(AccessGroupPermissionCounterDTO::getStaffId, v -> new ArrayList<>()));
            Map<Long, List<BigInteger>> accessGroupKpiMap = accessGroupMappingDTOS.stream().collect(Collectors.toMap(AccessGroupMappingDTO::getAccessGroupId, AccessGroupMappingDTO::getKpiIds));
            staffAndAccessGroups.forEach(accessGroupsDTO -> accessGroupsDTO.getAccessGroupIds().forEach(accessGroupId -> {
                if (accessGroupMappingDTO.getAccessGroupId() != accessGroupId) {
                    staffKpiMap.get(accessGroupsDTO.getStaffId()).addAll((accessGroupKpiMap.getOrDefault(accessGroupId, new ArrayList<>())));
                }
            }));
            List<Long> staffIds = new ArrayList<>();
            staffKpiMap.entrySet().forEach(kpis -> {
                if (!kpis.getValue().stream().filter(a -> a.equals(accessGroupMappingDTO.getKpiId())).findAny().isPresent()) {
                    staffIds.add(kpis.getKey());
                }
            });
            counterRepository.removeApplicableKPI(staffIds, Arrays.asList(accessGroupKPIEntry.getKpiId()), refId, ConfLevel.STAFF);
            counterRepository.removeTabKPIEntry(staffIds, Arrays.asList(accessGroupKPIEntry.getKpiId()), ConfLevel.STAFF);
            counterRepository.removeEntityById(accessGroupKPIEntry.getId(), AccessGroupKPIEntry.class);
        } else {
            counterRepository.removeAccessGroupKPIEntryForCountry(accessGroupMappingDTO, refId);
        }
    }

    public void addAndRemoveStaffAccessGroupKPISetting(Long unitId, Long accessGroupId, AccessGroupPermissionCounterDTO accessGroupAndStaffDTO, Boolean created) {
        List<BigInteger> kpiIds = counterRepository.getKPISOfAccessGroup(Arrays.asList(accessGroupId), unitId, ConfLevel.UNIT);
        if (created) {
            List<ApplicableKPI> applicableKPISToSave = new ArrayList<>();
            Map<Long, Map<BigInteger, BigInteger>> staffIdKpiMap = new HashMap<>();
            staffIdKpiMap.put(accessGroupAndStaffDTO.getStaffId(), new HashMap<BigInteger, BigInteger>());
            List<ApplicableKPI> applicableKPISForStaff = counterRepository.getApplicableKPIByReferenceId(kpiIds, Arrays.asList(accessGroupAndStaffDTO.getStaffId()), ConfLevel.STAFF);
            applicableKPISForStaff.forEach(applicableKPI -> staffIdKpiMap.get(applicableKPI.getStaffId()).put(applicableKPI.getBaseKpiId(), applicableKPI.getBaseKpiId()));
            List<ApplicableKPI> applicableKpis = counterRepository.getApplicableKPIByReferenceId(kpiIds, Arrays.asList(unitId), ConfLevel.UNIT);
            Map<BigInteger, ApplicableKPI> kpiIdAndApplicableKpi = applicableKpis.stream().collect(Collectors.toMap(ApplicableKPI::getActiveKpiId, v -> v));
            kpiIds.stream().forEach(kpiId -> {
                ApplicableFilter applicableFilter = null;
                if (staffIdKpiMap.get(accessGroupAndStaffDTO.getStaffId()).get(kpiId) == null) {
                    if (kpiIdAndApplicableKpi.containsKey(kpiId) && isNotNull(kpiIdAndApplicableKpi.get(kpiId).getApplicableFilter())) {
                        applicableFilter = new ApplicableFilter(kpiIdAndApplicableKpi.get(kpiId).getApplicableFilter().getCriteriaList(), false);
                    }
                    applicableKPISToSave.add(new ApplicableKPI(kpiId, kpiId, null, unitId, accessGroupAndStaffDTO.getStaffId(), ConfLevel.STAFF, applicableFilter, kpiIdAndApplicableKpi.get(kpiId).getTitle(), false,kpiIdAndApplicableKpi.get(kpiId).getKpiRepresentation(),kpiIdAndApplicableKpi.get(kpiId).getInterval(),kpiIdAndApplicableKpi.get(kpiId).getValue(),kpiIdAndApplicableKpi.get(kpiId).getFrequencyType(),kpiIdAndApplicableKpi.get(kpiId).getFibonacciKPIConfigs()));
                    staffIdKpiMap.get(accessGroupAndStaffDTO.getStaffId()).put(kpiId, kpiId);
                }
            });
            List<KPIDashboardDTO> kpiDashboardDTOS = counterRepository.getKPIDashboard(null, ConfLevel.UNIT, unitId);
            List<KPIDashboard> kpiDashboards = kpiDashboardDTOS.stream().map(dashboard -> new KPIDashboard(dashboard.getParentModuleId(), dashboard.getModuleId(), dashboard.getName(), null, unitId, accessGroupAndStaffDTO.getStaffId(), ConfLevel.STAFF, dashboard.isDefaultTab())).collect(Collectors.toList());
            if (!kpiDashboards.isEmpty()) {
                save(kpiDashboards);
            }
            if (!applicableKPISToSave.isEmpty()) {
                save(applicableKPISToSave);
            }
        } else {
            List<Long> accessGroupIds = accessGroupAndStaffDTO.getAccessGroupIds().stream().filter(accessGroupIdOne -> !accessGroupIdOne.equals(accessGroupId)).collect(toList());
            List<KPIDTO> kpidtos = counterRepository.getAccessGroupKPIDto(accessGroupIds, ConfLevel.UNIT, unitId, accessGroupAndStaffDTO.getStaffId());
            List<BigInteger> kpiDtoIds = kpidtos.stream().map(KPIDTO::getId).collect(toList());
            Map<BigInteger, BigInteger> availableKpi = new HashMap<>();
            List<BigInteger> removeAbleKPi = new ArrayList<>();
            kpiDtoIds.stream().forEach(kpi -> availableKpi.put(kpi, kpi));
            kpiIds.stream().forEach(kpiId -> {
                if (availableKpi.get(kpiId) == null) {
                    removeAbleKPi.add(kpiId);
                }
            });
            counterRepository.removeApplicableKPI(Arrays.asList(accessGroupAndStaffDTO.getStaffId()), removeAbleKPi, unitId, ConfLevel.STAFF);
            counterRepository.removeTabKPIEntry(Arrays.asList(accessGroupAndStaffDTO.getStaffId()), removeAbleKPi, ConfLevel.STAFF);
            counterRepository.removeDashboardTabOfStaff(accessGroupAndStaffDTO.getStaffId(), unitId, ConfLevel.STAFF);
        }
    }
    //setting orgType-KPI configuration

    public List<BigInteger> getInitialOrgTypeKPIDataConf(Long orgTypeId, Long countryId) {
        List<BigInteger> orgTypeKPIEntries = counterRepository.getOrgTypeKPIIdsOrgTypeIds(Arrays.asList(orgTypeId), new ArrayList<>());
        if (orgTypeKPIEntries == null || orgTypeKPIEntries.isEmpty()) return new ArrayList<>();
        return orgTypeKPIEntries;
    }

    public void addOrgTypeKPIEntries(OrgTypeKPIConfDTO orgTypeKPIConf, Long countryId) {
        List<ApplicableKPI> applicableKPIS = counterRepository.getKPIByKPIIds(orgTypeKPIConf.getKpiIds(), countryId, ConfLevel.COUNTRY);
        if (orgTypeKPIConf.getKpiIds().size() != applicableKPIS.size()) {
            exceptionService.actionNotPermittedException(MESSAGE_COUNTER_KPI_NOTFOUND);
        }
        Map<BigInteger, ApplicableKPI> kpiIdAndApplicableKpi = applicableKPIS.stream().collect(Collectors.toMap(ApplicableKPI::getActiveKpiId, v -> v));
        List<OrgTypeKPIEntry> entriesToSave = new ArrayList<>();
        Map<Long, Map<BigInteger, BigInteger>> orgTypeKPIsMap = new HashMap<>();
        orgTypeKPIConf.getOrgTypeIds().forEach(orgTypeId -> orgTypeKPIsMap.put(orgTypeId, new HashMap<BigInteger, BigInteger>()));
        List<OrgTypeMappingDTO> orgTypeMappingDTOS = counterRepository.getOrgTypeKPIEntryOrgTypeIds(orgTypeKPIConf.getOrgTypeIds(), orgTypeKPIConf.getKpiIds());
        orgTypeMappingDTOS.forEach(orgTypeMappingDTO -> orgTypeKPIsMap.get(orgTypeMappingDTO.getOrgTypeId()).put(orgTypeMappingDTO.getKpiId(), orgTypeMappingDTO.getKpiId()));
        orgTypeKPIConf.getOrgTypeIds().forEach(orgTypeId -> orgTypeKPIConf.getKpiIds().forEach(kpiId -> {
            if (orgTypeKPIsMap.get(orgTypeId).get(kpiId) == null) {
                entriesToSave.add(new OrgTypeKPIEntry(orgTypeId, kpiId, countryId));
            }
        }));
        if (entriesToSave.isEmpty()) {
            exceptionService.invalidRequestException(ERROR_KPI_INVALIDDATA);
        }
        save(entriesToSave);
        List<ApplicableKPI> applicableKPISToSave = new ArrayList<>();
        Map<Long, Map<BigInteger, BigInteger>> unitIdKpiMap = new HashMap<>();
        List<OrgTypeDTO> orgTypeDTOS = userIntegrationService.getOrganizationIdsBySubOrgId(orgTypeKPIConf.getOrgTypeIds());
        List<Long> unitIds = orgTypeDTOS.stream().map(OrgTypeDTO::getUnitId).collect(toList());
        unitIds.forEach(unitId -> unitIdKpiMap.put(unitId, new HashMap<BigInteger, BigInteger>()));
        List<ApplicableKPI> applicableKPISForUnit = counterRepository.getApplicableKPIByReferenceId(orgTypeMappingDTOS.stream().map(OrgTypeMappingDTO::getKpiId).collect(toList()), unitIds, ConfLevel.UNIT);
        applicableKPISForUnit.forEach(applicableKPI -> unitIdKpiMap.get(applicableKPI.getUnitId()).put(applicableKPI.getBaseKpiId(), applicableKPI.getBaseKpiId()));
        unitIds.forEach(unitId -> orgTypeKPIConf.getKpiIds().forEach(kpiId -> {
            if (unitIdKpiMap.get(unitId).get(kpiId) == null) {
                ApplicableFilter applicableFilter = null;
                if (kpiIdAndApplicableKpi.containsKey(kpiId) && isNotNull(kpiIdAndApplicableKpi.get(kpiId).getApplicableFilter())) {
                    applicableFilter = new ApplicableFilter(kpiIdAndApplicableKpi.get(kpiId).getApplicableFilter().getCriteriaList(), false);
                }
                applicableKPISToSave.add(new ApplicableKPI(kpiId, kpiId, null, unitId, null, ConfLevel.UNIT, applicableFilter, kpiIdAndApplicableKpi.get(kpiId).getTitle(), false,kpiIdAndApplicableKpi.get(kpiId).getKpiRepresentation(),kpiIdAndApplicableKpi.get(kpiId).getInterval(),kpiIdAndApplicableKpi.get(kpiId).getValue(),kpiIdAndApplicableKpi.get(kpiId).getFrequencyType(),kpiIdAndApplicableKpi.get(kpiId).getFibonacciKPIConfigs()));
                unitIdKpiMap.get(unitId).put(kpiId, kpiId);
            }
        }));
        if (!applicableKPISToSave.isEmpty()) {
            save(applicableKPISToSave);
        }
    }

    public void removeOrgTypeKPIEntries(OrgTypeMappingDTO orgTypeMappingDTO, Long countryId) {
        OrgTypeKPIEntry orgTypeKPIEntry = counterRepository.getOrgTypeKPIEntry(orgTypeMappingDTO, countryId);
        if (!Optional.ofNullable(orgTypeKPIEntry).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGTYPE_KPI_NOTFOUND);
        }
        List<OrgTypeDTO> orgTypeDTOS = userIntegrationService.getOrganizationIdsBySubOrgId(Arrays.asList(orgTypeKPIEntry.getOrgTypeId()));
        Set<Long> subOrgTypeIds = orgTypeDTOS.stream().flatMap(orgTypeDTO -> orgTypeDTO.getOrgTypeIds().stream().filter(orgTypeId -> !orgTypeId.equals(orgTypeMappingDTO.getOrgTypeId()))).collect(toSet());
        List<OrgTypeMappingDTO> orgTypeMappingDTOS = counterRepository.getOrgTypeKPIEntryOrgTypeIds(new ArrayList<>(subOrgTypeIds), new ArrayList<>());
        Map<Long, Set<BigInteger>> subOrgTypeOrKPIMap = orgTypeMappingDTOS.stream().collect(Collectors.groupingBy(OrgTypeMappingDTO::getOrgTypeId, Collectors.mapping(OrgTypeMappingDTO::getKpiId, Collectors.toSet())));
        Map<Long, Set<BigInteger>> unitIdOrKpiMap = new HashMap<>();
        List<Long> unitIds = orgTypeDTOS.stream().map(OrgTypeDTO::getUnitId).collect(toList());
        orgTypeDTOS.forEach(orgTypeDTO -> orgTypeDTO.getOrgTypeIds().forEach(subOrgType -> {
            if (subOrgTypeOrKPIMap.get(subOrgType) != null) {
                if (!unitIdOrKpiMap.containsKey(orgTypeDTO.getUnitId())) {
                    unitIdOrKpiMap.put(orgTypeDTO.getUnitId(), subOrgTypeOrKPIMap.get(subOrgType));
                } else {
                    unitIdOrKpiMap.get(orgTypeDTO.getUnitId()).addAll(subOrgTypeOrKPIMap.get(subOrgType));
                }
            }
        }));
        unitIdOrKpiMap.entrySet().forEach(k -> {
            if (unitIdOrKpiMap.get(k.getKey()).contains(orgTypeMappingDTO.getKpiId())) {
                unitIds.remove(k.getKey());
            }
        });
        if (!unitIds.isEmpty()) {
            counterRepository.removeCategoryKPIEntry(unitIds, orgTypeKPIEntry.getKpiId());
            counterRepository.removeAccessGroupKPIEntry(unitIds, orgTypeKPIEntry.getKpiId());
            counterRepository.removeTabKPIEntry(unitIds, Arrays.asList(orgTypeKPIEntry.getKpiId()), ConfLevel.UNIT);
            counterRepository.removeApplicableKPI(unitIds, Arrays.asList(orgTypeKPIEntry.getKpiId()), null, ConfLevel.UNIT);
        }
        counterRepository.removeEntityById(orgTypeKPIEntry.getId(), OrgTypeKPIEntry.class);
    }

    //dashboard setting for all level
    //default setting
    public void createDefaultStaffKPISetting(Long unitId, DefaultKPISettingDTO defaultKPISettingDTO) {
        List<ApplicableKPI> applicableKPIS = new ArrayList<>();
        List<ApplicableKPI> applicableKpis = counterRepository.getApplicableKPIByReferenceId(new ArrayList<>(), Arrays.asList(unitId), ConfLevel.UNIT);
        List<BigInteger> applicableKpiIds = applicableKpis.stream().map(ApplicableKPI::getActiveKpiId).collect(toList());
        Map<BigInteger, ApplicableKPI> kpiIdAndApplicableKpi = applicableKpis.stream().collect(Collectors.toMap(ApplicableKPI::getActiveKpiId, v -> v));
        applicableKpis.forEach(applicableKPI -> defaultKPISettingDTO.getStaffIds().forEach(staffId -> {
            ApplicableFilter applicableFilter = null;
            if (kpiIdAndApplicableKpi.containsKey(applicableKPI) && isNotNull(kpiIdAndApplicableKpi.get(applicableKPI).getApplicableFilter())) {
                applicableFilter = new ApplicableFilter(kpiIdAndApplicableKpi.get(applicableKPI).getApplicableFilter().getCriteriaList(), false);
            }
            applicableKPIS.add(new ApplicableKPI(applicableKPI.getActiveKpiId(), applicableKPI.getBaseKpiId(), null, unitId, staffId, ConfLevel.STAFF, applicableFilter, kpiIdAndApplicableKpi.get(applicableKPI.getActiveKpiId()).getTitle(), false,kpiIdAndApplicableKpi.get(applicableKPI.getActiveKpiId()).getKpiRepresentation(),kpiIdAndApplicableKpi.get(applicableKPI.getActiveKpiId()).getInterval(),kpiIdAndApplicableKpi.get(applicableKPI.getActiveKpiId()).getValue(),kpiIdAndApplicableKpi.get(applicableKPI.getActiveKpiId()).getFrequencyType(),kpiIdAndApplicableKpi.get(applicableKPI.getActiveKpiId()).getFibonacciKPIConfigs()));
        }));
        List<DashboardKPIConf> dashboardKPIConfToSave = new ArrayList<>();
        List<KPIDashboardDTO> kpiDashboardDTOS = counterRepository.getKPIDashboard(null, ConfLevel.UNIT, unitId);
        List<KPIDashboard> kpiDashboardsTosave = new ArrayList<>();
        defaultKPISettingDTO.getStaffIds().forEach(staffId -> {
            List<KPIDashboard> kpiDashboards = kpiDashboardDTOS.stream().map(dashboard -> new KPIDashboard(dashboard.getParentModuleId(), dashboard.getModuleId(), dashboard.getName(), null, unitId, staffId, ConfLevel.STAFF, dashboard.isDefaultTab())).collect(Collectors.toList());
            kpiDashboardsTosave.addAll(kpiDashboards);
        });
        if (!kpiDashboardsTosave.isEmpty()) {
            save(kpiDashboardsTosave);
        }
        List<String> oldDashboardsIds = kpiDashboardDTOS.stream().map(KPIDashboardDTO::getModuleId).collect(Collectors.toList());
        List<DashboardKPIConf> dashboardKPIConfList = counterRepository.getDashboardKPIConfs(applicableKpiIds, oldDashboardsIds, unitId, ConfLevel.UNIT);
        List<TabKPIConf> tabKPIConfKPIEntries = new ArrayList<>();
        List<TabKPIConf> tabKPIConf = counterRepository.findTabKPIIdsByKpiIdAndUnitOrCountry(applicableKpiIds, unitId, ConfLevel.UNIT);
        if (!tabKPIConf.isEmpty()) {
            defaultKPISettingDTO.getStaffIds().forEach(staffId -> {
                tabKPIConf.stream().forEach(tabKPIConfKPI -> tabKPIConfKPIEntries.add(new TabKPIConf(tabKPIConfKPI.getTabId(), tabKPIConfKPI.getKpiId(), null, unitId, staffId, ConfLevel.STAFF, tabKPIConfKPI.getPosition(), KPIValidity.BASIC, LocationType.FIX, calculatePriority(ConfLevel.STAFF, KPIValidity.BASIC, LocationType.FIX))));
                dashboardKPIConfList.stream().forEach(dashboardKPIConf -> dashboardKPIConfToSave.add(new DashboardKPIConf(dashboardKPIConf.getKpiId(), dashboardKPIConf.getModuleId(), null, unitId, staffId, ConfLevel.STAFF, dashboardKPIConf.getPosition())));
            });
        }
        if (!applicableKpiIds.isEmpty()) save(applicableKPIS);
        if (!dashboardKPIConfToSave.isEmpty()) save(dashboardKPIConfToSave);
        if (!tabKPIConfKPIEntries.isEmpty()) save(tabKPIConfKPIEntries);
    }

    public void createDefaultKpiSetting(Long unitId, DefaultKPISettingDTO defaultKPISettingDTO) {
        if (Optional.ofNullable(defaultKPISettingDTO.getParentUnitId()).isPresent()) {
            createTabs(defaultKPISettingDTO.getParentUnitId(), ConfLevel.UNIT, unitId);
        } else {
            createTabs(defaultKPISettingDTO.getCountryId(), ConfLevel.COUNTRY, unitId);
        }
        List<OrgTypeMappingDTO> orgTypeMappingDTOS = counterRepository.getOrgTypeKPIEntryOrgTypeIds(defaultKPISettingDTO.getOrgTypeIds(), new ArrayList<>());
        if (orgTypeMappingDTOS.isEmpty()) {
            return;
        }
        List<BigInteger> applicableKpiIds = orgTypeMappingDTOS.stream().map(OrgTypeMappingDTO::getKpiId).collect(Collectors.toList());
        if (Optional.ofNullable(defaultKPISettingDTO.getParentUnitId()).isPresent()) {
            setDefaultSettingUnit(defaultKPISettingDTO, applicableKpiIds, unitId, ConfLevel.UNIT);
        } else {
            setDefaultSettingUnit(defaultKPISettingDTO, applicableKpiIds, unitId, ConfLevel.COUNTRY);
        }
    }

    public void setDefaultSettingUnit(DefaultKPISettingDTO defalutKPISettingDTO, List<BigInteger> kpiIds, Long unitId, ConfLevel level) {
        List<AccessGroupMappingDTO> accessGroupMappingDTOS;
        Long refId = ConfLevel.COUNTRY.equals(level) ? defalutKPISettingDTO.getCountryId() : defalutKPISettingDTO.getParentUnitId();
        List<CategoryKPIConf> categoryKPIConfToSave = new ArrayList<>();
        List<DashboardKPIConf> dashboardKPIConfToSave = new ArrayList<>();
        List<AccessGroupKPIEntry> accessGroupKPIEntries = new ArrayList<>();
        List<TabKPIConf> tabKPIConfKPIEntries = new ArrayList<>();
        List<ApplicableKPI> applicableKpis = counterRepository.getApplicableKPIByReferenceId(kpiIds, Arrays.asList(refId), level);
        List<BigInteger> applicableKpiIds = applicableKpis.stream().map(ApplicableKPI::getActiveKpiId).collect(toList());
        Map<BigInteger, ApplicableKPI> kpiIdAndApplicableKpi = applicableKpis.stream().collect(Collectors.toMap(ApplicableKPI::getActiveKpiId, v -> v));
        //TODO code update for parent child access group fetching
        if (!Optional.ofNullable(defalutKPISettingDTO.getParentUnitId()).isPresent()) {
            List<Long> countryAccessGroupIds = defalutKPISettingDTO.getCountryAndOrgAccessGroupIdsMap().keySet().stream().collect(Collectors.toList());
            accessGroupMappingDTOS = counterRepository.getAccessGroupKPIEntryAccessGroupIds(countryAccessGroupIds, applicableKpiIds, level, refId);
            accessGroupMappingDTOS.forEach(accessGroupMappingDTO -> accessGroupKPIEntries.add(new AccessGroupKPIEntry(defalutKPISettingDTO.getCountryAndOrgAccessGroupIdsMap().get(accessGroupMappingDTO.getAccessGroupId()), accessGroupMappingDTO.getKpiId(), null, unitId, ConfLevel.UNIT)));
        } else {
            accessGroupMappingDTOS = counterRepository.getAccessGroupKPIEntryAccessGroupIds(new ArrayList<>(), applicableKpiIds, level, refId);
            accessGroupMappingDTOS.forEach(accessGroupMappingDTO -> accessGroupKPIEntries.add(new AccessGroupKPIEntry(accessGroupMappingDTO.getAccessGroupId(), accessGroupMappingDTO.getKpiId(), null, unitId, ConfLevel.UNIT)));
        }
        List<TabKPIConf> tabKPIConf = counterRepository.findTabKPIIdsByKpiIdAndUnitOrCountry(applicableKpiIds, refId, level);
        tabKPIConf.stream().forEach(tabKPIConfKPI -> tabKPIConfKPIEntries.add(new TabKPIConf(tabKPIConfKPI.getTabId(), tabKPIConfKPI.getKpiId(), null, unitId, null, ConfLevel.UNIT, null, KPIValidity.BASIC, LocationType.FIX, calculatePriority(ConfLevel.UNIT, KPIValidity.BASIC, LocationType.FIX))));
        List<KPICategoryDTO> kpiCategoryDTOS = counterRepository.getKPICategory(null, level, refId);
        Map<String, BigInteger> categoriesNameMap = new HashMap<>();
        Map<BigInteger, BigInteger> categoriesOldAndNewIds = new HashMap<>();
        kpiCategoryDTOS.stream().forEach(kpiCategoryDTO -> categoriesNameMap.put(kpiCategoryDTO.getName(), kpiCategoryDTO.getId()));
        List<KPICategory> kpiCategories = kpiCategoryDTOS.stream().map(category -> new KPICategory(category.getName(), null, unitId, ConfLevel.UNIT)).collect(Collectors.toList());
        if (!kpiCategories.isEmpty()) {
            save(kpiCategories);
        }
        kpiCategories.stream().forEach(kpiCategory -> categoriesOldAndNewIds.put(categoriesNameMap.get(kpiCategory.getName()), kpiCategory.getId()));
        List<BigInteger> oldCategoriesIds = kpiCategoryDTOS.stream().map(KPICategoryDTO::getId).collect(Collectors.toList());
        List<CategoryKPIConf> categoryKPIConfList = counterRepository.getCategoryKPIConfs(applicableKpiIds, oldCategoriesIds);
        categoryKPIConfList.stream().forEach(categoryKPIConf -> categoryKPIConfToSave.add(new CategoryKPIConf(categoryKPIConf.getKpiId(), categoriesOldAndNewIds.get(categoryKPIConf.getCategoryId()), null, unitId, ConfLevel.UNIT)));
        List<KPIDashboardDTO> kpiDashboardDTOS = counterRepository.getKPIDashboard(null, level, refId);
        List<String> oldDashboardsIds = kpiDashboardDTOS.stream().map(KPIDashboardDTO::getModuleId).collect(Collectors.toList());
        List<DashboardKPIConf> dashboardKPIConfList = counterRepository.getDashboardKPIConfs(applicableKpiIds, oldDashboardsIds, refId, level);
        dashboardKPIConfList.stream().forEach(dashboardKPIConf -> dashboardKPIConfToSave.add(new DashboardKPIConf(dashboardKPIConf.getKpiId(), dashboardKPIConf.getModuleId(), null, unitId, null, ConfLevel.UNIT, dashboardKPIConf.getPosition())));
        List<ApplicableKPI> applicableKPISToSave = new ArrayList<>();
        applicableKpiIds.forEach(kpiId -> {
            ApplicableFilter applicableFilter = null;
            if (kpiIdAndApplicableKpi.containsKey(kpiId) && isNotNull(kpiIdAndApplicableKpi.get(kpiId).getApplicableFilter())) {
                applicableFilter = new ApplicableFilter(kpiIdAndApplicableKpi.get(kpiId).getApplicableFilter().getCriteriaList(), false);
            }
            applicableKPISToSave.add(new ApplicableKPI(kpiId, kpiId, null, unitId, null, ConfLevel.UNIT, applicableFilter, kpiIdAndApplicableKpi.get(kpiId).getTitle(), false,kpiIdAndApplicableKpi.get(kpiId).getKpiRepresentation(),kpiIdAndApplicableKpi.get(kpiId).getInterval(),kpiIdAndApplicableKpi.get(kpiId).getValue(),kpiIdAndApplicableKpi.get(kpiId).getFrequencyType(),kpiIdAndApplicableKpi.get(kpiId).getFibonacciKPIConfigs()));
        });
        //due to avoid exception and entity may be blank here so I using multiple conditional statements harish
        if (!applicableKPISToSave.isEmpty()) {
            save(applicableKPISToSave);
        }
        if (!accessGroupKPIEntries.isEmpty()) {
            save(accessGroupKPIEntries);
        }
        if (!categoryKPIConfToSave.isEmpty()) {
            save(categoryKPIConfToSave);
        }
        if (!dashboardKPIConfToSave.isEmpty()) {
            save(dashboardKPIConfToSave);
        }
        if (!tabKPIConfKPIEntries.isEmpty()) {
            save(tabKPIConfKPIEntries);
        }
    }

    private String createModuleId(BigInteger id, String parentModuleId) {
        return parentModuleId + "_" + id;
    }

    private void createTabs(Long refId, ConfLevel level, Long unitId) {
        List<KPIDashboardDTO> kpiDashboardDTOS = counterRepository.getKPIDashboard(null, level, refId);
        List<KPIDashboard> kpiDashboards = kpiDashboardDTOS.stream().map(dashboard -> new KPIDashboard(dashboard.getParentModuleId(), dashboard.getModuleId(), dashboard.getName(), null, unitId, null, ConfLevel.UNIT, dashboard.isDefaultTab())).collect(Collectors.toList());
        if (!kpiDashboards.isEmpty()) {
            save(kpiDashboards);
        }
        kpiDashboards.stream().forEach(kpiDashboard -> kpiDashboard.setModuleId(createModuleId(kpiDashboard.getId(), kpiDashboard.getParentModuleId())));
        if (!kpiDashboards.isEmpty()) save(kpiDashboards);
    }


}