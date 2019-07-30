package com.kairos.service.counter;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.counter.CounterServiceMapping;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.counter.configuration.*;
import com.kairos.dto.activity.counter.data.*;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.tab.KPIPosition;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIDTO;
import com.kairos.dto.activity.counter.enums.*;
import com.kairos.dto.activity.kpi.*;
import com.kairos.dto.user.organization.OrganizationCommonDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.task_type.TaskService;
import com.kairos.utils.user_context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.enums.FilterType.STAFF_IDS;
import static java.util.stream.Collectors.toList;



@Service
public class CounterDataService extends MongoBaseService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CounterDataService.class);
    @Inject
    private TaskService taskService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private CounterServiceMapping counterServiceMapping;
    @Inject
    private ExecutorService executorService;
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private ActivityService activityService;
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private CounterDistService counterDistService;

    //FIXME: DO NOT REMOVE will be uncommented once representation model confirmed.
    public List<KPI> getCountersData(Long unitId, BigInteger solverConfigId) {
        ArrayList<KPI> kpiList = new ArrayList<>();
        return kpiList;
    }


    public Map generateKPIData(FilterCriteriaDTO filters, Long organizationId, Long staffId) {
        Map<BigInteger,ApplicableKPI> kpiIdAndApplicableKPIMap=new HashMap<>();
        List<KPI> kpis = counterRepository.getKPIsByIds(filters.getKpiIds());
        Map<BigInteger, KPI> kpiMap = kpis.stream().collect(Collectors.toMap(kpi -> kpi.getId(), kpi -> kpi));
        List<Future<CommonRepresentationData>> kpiResults = new ArrayList<>();
        Map<FilterType, List> filterBasedCriteria = new HashMap<>();
        Map<BigInteger, Map<FilterType, List>> staffKpiFilterCritera = new HashMap<>();
        if (filters.getFilters() != null && isCollectionNotEmpty(filters.getFilters())) {
            filters.getFilters().forEach(filter -> filterBasedCriteria.put(filter.getType(), filter.getValues()));
            kpiIdAndApplicableKPIMap.put(kpis.get(0).getId(),new ApplicableKPI(filters.getKpiRepresentation(),filters.getValue(),filters.getInterval(),filters.getFrequencyType()));
        } else {
            getStaffKPiFilterAndApplicableKpi(filters, staffId, kpiIdAndApplicableKPIMap, kpis, staffKpiFilterCritera);
        }
        for (BigInteger kpiId : filters.getKpiIds()) {
            if(kpiIdAndApplicableKPIMap.containsKey(kpiId)) {
                Callable<CommonRepresentationData> data = () -> counterServiceMapping.getService(kpiMap.get(kpiId).getType()).getCalculatedKPI(staffKpiFilterCritera.getOrDefault(kpiId, filterBasedCriteria), organizationId, kpiMap.get(kpiId), kpiIdAndApplicableKPIMap.get(kpiId));
                Future<CommonRepresentationData> responseData = executorService.submit(data);
                kpiResults.add(responseData);
            }
        }
        List<CommonRepresentationData> kpisData = new ArrayList();
        for (Future<CommonRepresentationData> data : kpiResults) {
            try {
                if(isNotNull(data))kpisData.add(data.get());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }

        return isNotNull(kpisData) ? kpisData.stream().collect(Collectors.toMap(CommonRepresentationData::getCounterId, kpiData -> kpiData)) : new HashMap<>();
    }

    private void getStaffKPiFilterAndApplicableKpi(FilterCriteriaDTO filters, Long staffId, Map<BigInteger, ApplicableKPI> kpiIdAndApplicableKPIMap, List<KPI> kpis, Map<BigInteger, Map<FilterType, List>> staffKpiFilterCritera) {
        List<ApplicableKPI> staffApplicableKPIS;
        if (filters.isCountryAdmin()) {
            staffApplicableKPIS = counterRepository.getApplicableKPI(kpis.stream().map(kpi -> kpi.getId()).collect(Collectors.toList()), ConfLevel.COUNTRY, filters.getCountryId());
        } else {
            staffApplicableKPIS = counterRepository.getApplicableKPI(kpis.stream().map(kpi -> kpi.getId()).collect(Collectors.toList()), ConfLevel.STAFF, staffId);
        }
        for (ApplicableKPI staffApplicableKPI : staffApplicableKPIS) {
            Map<FilterType, List> staffFilterBasedCriteria = new HashMap<>();
            if (isNotNull(staffApplicableKPI.getApplicableFilter())) {
                staffApplicableKPI.getApplicableFilter().getCriteriaList().forEach(filterCriteria -> staffFilterBasedCriteria.put(filterCriteria.getType(), filterCriteria.getValues()));
                if(KPIRepresentation.INDIVIDUAL_STAFF.equals(staffApplicableKPI.getKpiRepresentation())){
                    staffFilterBasedCriteria.put(STAFF_IDS, Arrays.asList(isNotNull(filters.getStaffId()) ?filters.getStaffId().intValue() : staffId.intValue()));
                   // staffApplicableKPI.setKpiRepresentation(KPIRepresentation.REPRESENT_PER_STAFF);
                }
                if(isNotNull(filters.getFrequencyType())){
                    staffApplicableKPI.setInterval(filters.getInterval());
                    staffApplicableKPI.setValue(filters.getValue());
                    staffApplicableKPI.setFrequencyType(filters.getFrequencyType());
                }
                if(isNotNull(filters.getStartDate()) && isNotNull( filters.getEndDate())) {
                    staffFilterBasedCriteria.put(FilterType.TIME_INTERVAL,Arrays.asList(filters.getStartDate(),filters.getEndDate()));
                    staffApplicableKPI.setFrequencyType(DurationType.MONTHS);
                }
                if(isCollectionNotEmpty(staffApplicableKPI.getFibonacciKPIConfigs())){
                    staffFilterBasedCriteria.put(FilterType.FIBONACCI,staffApplicableKPI.getFibonacciKPIConfigs());
                }
                staffKpiFilterCritera.put(staffApplicableKPI.getActiveKpiId(), staffFilterBasedCriteria);
            }
            kpiIdAndApplicableKPIMap.put(staffApplicableKPI.getActiveKpiId(),staffApplicableKPI);
        }
    }

    //kpi default data and copy and save filter
    public KPIDTO getDefaultFilterDataOfKpi(String tabId, BigInteger kpiId, Long refId, ConfLevel level) {
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(UserContext.getUserDetails().getLastSelectedOrganizationId());
        List<ApplicableKPI> applicableKPIS;
        if (!accessGroupPermissionCounterDTO.isManagement()) {
            exceptionService.actionNotPermittedException(MESSAGE_KPI_PERMISSION);
        }
        if (isNotNull(tabId) && !accessGroupPermissionCounterDTO.isCountryAdmin()) {
            level = ConfLevel.STAFF;
            applicableKPIS = counterRepository.getApplicableKPI(Arrays.asList(kpiId), level, accessGroupPermissionCounterDTO.getStaffId());
        } else {
            applicableKPIS = counterRepository.getApplicableKPI(Arrays.asList(kpiId), level, refId);
        }
        if (applicableKPIS.isEmpty()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTER_KPI_NOTFOUND);
        }
        List<FilterCriteria> criteriaList = new ArrayList<>();
        KPIDTO kpi = ObjectMapperUtils.copyPropertiesByMapper(counterRepository.getKPIByid(kpiId), KPIDTO.class);
        DefaultKpiDataDTO defaultKpiDataDTO = userIntegrationService.getKpiFilterDefaultData(ConfLevel.COUNTRY.equals(level) ? UserContext.getUserDetails().getLastSelectedOrganizationId() : refId);
        getSelectedFilterDefaultData(level, criteriaList, kpi, defaultKpiDataDTO);
        setKpiProperty(applicableKPIS.get(0), criteriaList, kpi);
        return kpi;
    }

    private void setKpiProperty(ApplicableKPI applicableKPI, List<FilterCriteria> criteriaList, KPIDTO kpiDTO) {
        kpiDTO.setDefaultFilters(criteriaList);
        kpiDTO.setTitle(applicableKPI.getTitle());
        kpiDTO.setValue(applicableKPI.getValue());
        kpiDTO.setFrequencyType(applicableKPI.getFrequencyType());
        kpiDTO.setInterval(applicableKPI.getInterval());
        kpiDTO.setKpiRepresentation(applicableKPI.getKpiRepresentation());
        if (isNotNull(applicableKPI.getApplicableFilter())) {
            kpiDTO.setSelectedFilters(applicableKPI.getApplicableFilter().getCriteriaList());
        }
    }

    private void getSelectedFilterDefaultData(ConfLevel level, List<FilterCriteria> criteriaList, KPIDTO kpi, DefaultKpiDataDTO defaultKpiDataDTO) {
        if (kpi.getFilterTypes().contains(FilterType.EMPLOYMENT_TYPE)) {
            getEmploymentTypeDefaultData(criteriaList, defaultKpiDataDTO);
        }
        if (kpi.getFilterTypes().contains(FilterType.TIME_SLOT)) {
            getTimeSlotDefaultData(criteriaList, defaultKpiDataDTO);
        }
        if (kpi.getFilterTypes().contains(FilterType.DAY_TYPE)) {
            getDayTypeDefaultData(criteriaList, defaultKpiDataDTO);
        }
        if (kpi.getFilterTypes().contains(FilterType.UNIT_IDS) && ConfLevel.UNIT.equals(level)) {
            getUnitIdsDefaultData(criteriaList, defaultKpiDataDTO);
        }
        if (kpi.getFilterTypes().contains(STAFF_IDS) && ConfLevel.UNIT.equals(level)) {
            getStaffDefaultData(criteriaList, defaultKpiDataDTO);
        }
        if (kpi.getFilterTypes().contains(FilterType.ACTIVITY_STATUS)) {
            getActivityStatusDefaultData(criteriaList);
        }
        if (kpi.getFilterTypes().contains(STAFF_IDS)) {
            getActivityStatusDefaultData(criteriaList);
        }
        if (kpi.getFilterTypes().contains(FilterType.UNIT_NAME)) {
            getActivityStatusDefaultData(criteriaList);
        }
        if (kpi.getFilterTypes().contains(FilterType.DAYS_OF_WEEK)) {
            getDayOfWeekDefaultData(criteriaList);
        }
        if (kpi.getFilterTypes().contains(FilterType.TIME_TYPE)) {
            getTimeTypesDefaultData(criteriaList, defaultKpiDataDTO);
        }
        List<Long> unitIds = defaultKpiDataDTO.getOrganizationCommonDTOS().stream().map(OrganizationCommonDTO::getId).collect(toList());
        if (kpi.getFilterTypes().contains(FilterType.PHASE)) {
            getPhaseDefaultData(criteriaList);
        }
        if (kpi.getFilterTypes().contains(FilterType.TIME_INTERVAL)) {
            criteriaList.add(new FilterCriteria(FilterType.TIME_INTERVAL.value, FilterType.TIME_INTERVAL, new ArrayList<>()));
        }
        if (kpi.getFilterTypes().contains(FilterType.ACTIVITY_IDS)) {
            getActivityDefaultData(criteriaList, unitIds);
        }
    }

    private void getActivityDefaultData(List<FilterCriteria> criteriaList, List<Long> unitIds) {
        List<ActivityDTO> activityDTOS = activityService.findAllActivityByDeletedFalseAndUnitId(unitIds);
        List<KPIFilterDefaultDataDTO> kpiFilterDefaultDataDTOS = new ArrayList<>();
        activityDTOS.forEach(activityDTO -> kpiFilterDefaultDataDTOS.add(new KPIFilterDefaultDataDTO(activityDTO.getId().longValue(), activityDTO.getName(), activityDTO.getUnitId())));
        criteriaList.add(new FilterCriteria(FilterType.ACTIVITY_IDS.value, FilterType.ACTIVITY_IDS, (List) kpiFilterDefaultDataDTOS));
    }

    private void getPhaseDefaultData(List<FilterCriteria> criteriaList) {
        List<PhaseDefaultName> phases = Arrays.asList(PhaseDefaultName.values());
        List<KPIFilterDefaultDataDTO> kpiFilterDefaultDataDTOS = new ArrayList<>();
        phases.forEach(phase -> kpiFilterDefaultDataDTOS.add(new KPIFilterDefaultDataDTO(phase.toString(), phase.toString())));
        criteriaList.add(new FilterCriteria(FilterType.PHASE.value, FilterType.PHASE, (List) kpiFilterDefaultDataDTOS));
    }

    private void getTimeTypesDefaultData(List<FilterCriteria> criteriaList, DefaultKpiDataDTO defaultKpiDataDTO) {
        List<TimeType> timeTypes = timeTypeService.getAllTimeTypesByCountryId(defaultKpiDataDTO.getCountryId());
        List<KPIFilterDefaultDataDTO> kpiFilterDefaultDataDTOS = new ArrayList<>();
        timeTypes.forEach(timeType -> kpiFilterDefaultDataDTOS.add(new KPIFilterDefaultDataDTO(timeType.getId().longValue(), timeType.getLabel())));
        criteriaList.add(new FilterCriteria(FilterType.TIME_TYPE.value, FilterType.TIME_TYPE, (List) kpiFilterDefaultDataDTOS));
    }

    private void getDayOfWeekDefaultData(List<FilterCriteria> criteriaList) {
        List<DayOfWeek> dayOfWeeks = Arrays.asList(DayOfWeek.values());
        List<KPIFilterDefaultDataDTO> kpiFilterDefaultDataDTOS = new ArrayList<>();
        dayOfWeeks.forEach(dayOfWeek -> kpiFilterDefaultDataDTOS.add(new KPIFilterDefaultDataDTO(dayOfWeek.toString(), dayOfWeek.toString())));
        criteriaList.add(new FilterCriteria(FilterType.DAYS_OF_WEEK.value, FilterType.DAYS_OF_WEEK, (List) kpiFilterDefaultDataDTOS));
    }

    private void getActivityStatusDefaultData(List<FilterCriteria> criteriaList) {
        List<ShiftStatus> activityStatus = Arrays.asList(ShiftStatus.values());
        List<KPIFilterDefaultDataDTO> kpiFilterDefaultDataDTOS = new ArrayList<>();
        activityStatus.forEach(shiftStatus -> kpiFilterDefaultDataDTOS.add(new KPIFilterDefaultDataDTO(shiftStatus.toString(), shiftStatus.toString())));
        criteriaList.add(new FilterCriteria(FilterType.ACTIVITY_STATUS.value, FilterType.ACTIVITY_STATUS, (List) kpiFilterDefaultDataDTOS));
    }

    private void getStaffDefaultData(List<FilterCriteria> criteriaList, DefaultKpiDataDTO defaultKpiDataDTO) {
        List<KPIFilterDefaultDataDTO> kpiFilterDefaultDataDTOS = new ArrayList<>();
        defaultKpiDataDTO.getStaffKpiFilterDTOs().forEach(staffKpiFilterDTO -> kpiFilterDefaultDataDTOS.add(new KPIFilterDefaultDataDTO(staffKpiFilterDTO.getId(), staffKpiFilterDTO.getFullName(), staffKpiFilterDTO.getUnitIds())));
        criteriaList.add(new FilterCriteria(STAFF_IDS.value, STAFF_IDS, (List) kpiFilterDefaultDataDTOS));
    }

    private void getUnitIdsDefaultData(List<FilterCriteria> criteriaList, DefaultKpiDataDTO defaultKpiDataDTO) {
        List<KPIFilterDefaultDataDTO> kpiFilterDefaultDataDTOS = new ArrayList<>();
        defaultKpiDataDTO.getOrganizationCommonDTOS().forEach(organizationCommonDTO -> kpiFilterDefaultDataDTOS.add(new KPIFilterDefaultDataDTO(organizationCommonDTO.getId(), organizationCommonDTO.getName())));
        criteriaList.add(new FilterCriteria(FilterType.UNIT_IDS.value, FilterType.UNIT_IDS, (List) kpiFilterDefaultDataDTOS));
    }

    private void getDayTypeDefaultData(List<FilterCriteria> criteriaList, DefaultKpiDataDTO defaultKpiDataDTO) {
        List<KPIFilterDefaultDataDTO> kpiFilterDefaultDataDTOS = new ArrayList<>();
        defaultKpiDataDTO.getDayTypeDTOS().forEach(dayTypeDTO -> kpiFilterDefaultDataDTOS.add(new KPIFilterDefaultDataDTO(dayTypeDTO.getId(), dayTypeDTO.getName())));
        criteriaList.add(new FilterCriteria(FilterType.DAY_TYPE.value, FilterType.DAY_TYPE, (List) kpiFilterDefaultDataDTOS));
    }

    private void getTimeSlotDefaultData(List<FilterCriteria> criteriaList, DefaultKpiDataDTO defaultKpiDataDTO) {
        List<KPIFilterDefaultDataDTO> kpiFilterDefaultDataDTOS = new ArrayList<>();
        defaultKpiDataDTO.getTimeSlotDTOS().forEach(timeSlotDTO -> kpiFilterDefaultDataDTOS.add(new KPIFilterDefaultDataDTO(timeSlotDTO.getId(), timeSlotDTO.getName())));
        criteriaList.add(new FilterCriteria(FilterType.TIME_SLOT.value, FilterType.TIME_SLOT, (List) kpiFilterDefaultDataDTOS));
    }

    private void getEmploymentTypeDefaultData(List<FilterCriteria> criteriaList, DefaultKpiDataDTO defaultKpiDataDTO) {
        List<KPIFilterDefaultDataDTO> kpiFilterDefaultDataDTOS = new ArrayList<>();
        defaultKpiDataDTO.getEmploymentTypeKpiDTOS().forEach(employmentTypeKpiDTO -> kpiFilterDefaultDataDTOS.add(new KPIFilterDefaultDataDTO(employmentTypeKpiDTO.getId(), employmentTypeKpiDTO.getName())));
        criteriaList.add(new FilterCriteria(FilterType.EMPLOYMENT_TYPE.value, FilterType.EMPLOYMENT_TYPE, (List) kpiFilterDefaultDataDTOS));
    }

    public TabKPIDTO saveKpiFilterData(String tabId, Long refId, BigInteger kpiId, CounterDTO counterDTO, ConfLevel level) {
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(UserContext.getUserDetails().getLastSelectedOrganizationId());
        if (!accessGroupPermissionCounterDTO.isManagement()) {
            exceptionService.actionNotPermittedException(MESSAGE_KPI_PERMISSION);
        }
        if (isNotNull(tabId) && !accessGroupPermissionCounterDTO.isCountryAdmin()) {
            level = ConfLevel.STAFF;
            refId = accessGroupPermissionCounterDTO.getStaffId();
        }
        List<ApplicableKPI> applicableKPIS = counterRepository.getApplicableKPI(Arrays.asList(kpiId), level, refId);
        if (applicableKPIS.isEmpty()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTER_KPI_NOTFOUND);
        }
        KPI kpi = counterRepository.getKPIByid(kpiId);
        if (isNotNull(kpi.getCalculationFormula()) && !kpi.getCalculationFormula().equals(counterDTO.getCalculationFormula()) && !accessGroupPermissionCounterDTO.isCountryAdmin()) {
            exceptionService.actionNotPermittedException(MESSAGE_KPI_PERMISSION);
        }
        if (!applicableKPIS.get(0).getTitle().equals(counterDTO.getTitle()) && Optional.ofNullable(counterRepository.getKpiByTitleAndUnitId(counterDTO.getTitle(), refId, level)).isPresent()) {
            exceptionService.duplicateDataException(ERROR_KPI_NAME_DUPLICATE);
        }
        kpi.setCalculationFormula(counterDTO.getCalculationFormula());
        applicableKPIS.get(0).setApplicableFilter(new ApplicableFilter(counterDTO.getSelectedFilters(), true));
        List<ApplicableKPI> updateApplicableKPI = counterRepository.getFilterBaseApplicableKPIByKpiIdsOrUnitId(Arrays.asList(kpiId), Arrays.asList(ConfLevel.UNIT, ConfLevel.STAFF), ConfLevel.COUNTRY.equals(level) ? null : refId);
        for (ApplicableKPI applicableKPI : updateApplicableKPI) {
            applicableKPI.setApplicableFilter(new ApplicableFilter(counterDTO.getSelectedFilters(), false));
            if (applicableKPI.getTitle().equals(applicableKPIS.get(0).getTitle())) {
                applicableKPI.setTitle(counterDTO.getTitle().trim());
            }
            setIntervalConfigurationOfKpi(counterDTO, applicableKPI);
        }
        applicableKPIS.get(0).setTitle(counterDTO.getTitle());
        setIntervalConfigurationOfKpi(counterDTO, applicableKPIS.get(0));
        applicableKPIS.addAll(updateApplicableKPI);
        save(applicableKPIS);
        save(kpi);
        kpi.setTitle(counterDTO.getTitle());
        return getTabKpiData(kpi, counterDTO, accessGroupPermissionCounterDTO);
    }

    private void setIntervalConfigurationOfKpi(CounterDTO counterDTO, ApplicableKPI applicableKPIS) {
        applicableKPIS.setKpiRepresentation(counterDTO.getKpiRepresentation());
        applicableKPIS.setValue(counterDTO.getValue());
        applicableKPIS.setFrequencyType(counterDTO.getFrequencyType());
        applicableKPIS.setInterval(counterDTO.getInterval());
    }

    public TabKPIDTO copyKpiFilterData(String tabId, Long refId, BigInteger kpiId, CounterDTO counterDTO, ConfLevel level) {
        boolean copy = (isNotNull(tabId) ? true : false);
        TabKPIConf tabKPIConf = null;
        List<ApplicableKPI> applicableKPIS;
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(UserContext.getUserDetails().getLastSelectedOrganizationId());
        if (!accessGroupPermissionCounterDTO.isManagement()) {
            exceptionService.actionNotPermittedException(MESSAGE_KPI_PERMISSION);
        }
        if (isNotNull(tabId) && !accessGroupPermissionCounterDTO.isCountryAdmin()) {
            level = ConfLevel.STAFF;
            applicableKPIS = counterRepository.getApplicableKPI(Arrays.asList(kpiId), level, accessGroupPermissionCounterDTO.getStaffId());
        } else {
            applicableKPIS = counterRepository.getApplicableKPI(Arrays.asList(kpiId), level, refId);
        }
        if (applicableKPIS.isEmpty()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTER_KPI_NOTFOUND);
        }
        KPI kpi = counterRepository.getKPIByid(kpiId);
        if (!kpi.getCalculationFormula().equals(counterDTO.getCalculationFormula()) && !accessGroupPermissionCounterDTO.isCountryAdmin()) {
            exceptionService.actionNotPermittedException(MESSAGE_KPI_PERMISSION);
        }
        if (Optional.ofNullable(counterRepository.getKpiByTitleAndUnitId(counterDTO.getTitle(), refId, level)).isPresent()) {
            exceptionService.duplicateDataException(ERROR_KPI_NAME_DUPLICATE);
        }
        KPI copyKpi = ObjectMapperUtils.copyPropertiesByMapper(kpi, KPI.class);
        copyKpi.setId(null);
        copyKpi.setTitle(counterDTO.getTitle());
        copyKpi.setCalculationFormula(counterDTO.getCalculationFormula());
        copyKpi.setFilterTypes(counterDTO.getSelectedFilters().stream().map(FilterCriteria::getType).collect(toList()));
        save(copyKpi);
        List<ApplicableKPI> applicableKPIs = new ArrayList<>();
        if (ConfLevel.COUNTRY.equals(level) || accessGroupPermissionCounterDTO.isCountryAdmin()) {
            applicableKPIs.add(new ApplicableKPI(copyKpi.getId(), kpi.getId(), refId, null, null, level, new ApplicableFilter(counterDTO.getSelectedFilters(), false), counterDTO.getTitle(), copy,counterDTO.getKpiRepresentation(),counterDTO.getInterval(),counterDTO.getValue(),counterDTO.getFrequencyType(),null));
        } else if (ConfLevel.UNIT.equals(level)) {
            applicableKPIs.add(new ApplicableKPI(copyKpi.getId(), applicableKPIS.get(0).getBaseKpiId(), null, refId, null, level, new ApplicableFilter(counterDTO.getSelectedFilters(), false), counterDTO.getTitle(), copy,counterDTO.getKpiRepresentation(),counterDTO.getInterval(),counterDTO.getValue(),counterDTO.getFrequencyType(),null));
            applicableKPIs.add(new ApplicableKPI(copyKpi.getId(), applicableKPIS.get(0).getBaseKpiId(), null, refId, accessGroupPermissionCounterDTO.getStaffId(), ConfLevel.STAFF, new ApplicableFilter(counterDTO.getSelectedFilters(), false), counterDTO.getTitle(), copy,counterDTO.getKpiRepresentation(),counterDTO.getInterval(),counterDTO.getValue(),counterDTO.getFrequencyType(),null));
        } else if (isNotNull(tabId) && ConfLevel.STAFF.equals(level)) {
            applicableKPIs.add(new ApplicableKPI(copyKpi.getId(), applicableKPIS.get(0).getBaseKpiId(), null, refId, accessGroupPermissionCounterDTO.getStaffId(), ConfLevel.STAFF, new ApplicableFilter(counterDTO.getSelectedFilters(), false), counterDTO.getTitle(), copy,counterDTO.getKpiRepresentation(),counterDTO.getInterval(),counterDTO.getValue(),counterDTO.getFrequencyType(),null));
            tabKPIConf = new TabKPIConf(tabId, copyKpi.getId(), null, refId, accessGroupPermissionCounterDTO.getStaffId(), level, new KPIPosition(0, 0), KPIValidity.BASIC, LocationType.FIX, counterDistService.calculatePriority(ConfLevel.UNIT, KPIValidity.BASIC, LocationType.FIX));
            save(tabKPIConf);
        }
        applicableKPIS.addAll(applicableKPIs);
        save(applicableKPIS);
        TabKPIDTO tabKPIDTO = getTabKpiData(copyKpi, counterDTO, accessGroupPermissionCounterDTO);
        tabKPIDTO.setId((isNotNull(tabKPIConf)) ? tabKPIConf.getId() : null);
        return tabKPIDTO;
    }

    public TabKPIDTO getKpiPreviewWithFilter(BigInteger kpiId, Long refId, FilterCriteriaDTO filterCriteria, ConfLevel level ) {
        AccessGroupPermissionCounterDTO  accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(UserContext.getUserDetails().getLastSelectedOrganizationId());
        TabKPIDTO tabKPIDTO = new TabKPIDTO();
        KPI kpi = counterRepository.getKPIByid(kpiId);
        tabKPIDTO.setKpi(ObjectMapperUtils.copyPropertiesByMapper(kpi, KPIDTO.class));
        filterCriteria.setKpiIds(Arrays.asList(kpiId));
        refId = ConfLevel.UNIT.equals(level) ? refId : UserContext.getUserDetails().getLastSelectedOrganizationId();
        Map<BigInteger, CommonRepresentationData> data = generateKPIData(filterCriteria, refId, accessGroupPermissionCounterDTO.getStaffId());
        tabKPIDTO.setData(data.get(kpiId));
        return tabKPIDTO;

    }

    public TabKPIDTO getKpiDataByInterval(BigInteger kpiId, Long refId, FilterCriteriaDTO filterCriteria, ConfLevel level ,Long staffId ) {
        filterCriteria.setStaffId(staffId);
        return getKpiPreviewWithFilter(kpiId,refId,filterCriteria,ConfLevel.UNIT);
    }

    private TabKPIDTO getTabKpiData(KPI copyKpi, CounterDTO counterDTO, AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO) {
        TabKPIDTO tabKPIDTO = new TabKPIDTO();
        tabKPIDTO.setKpi(ObjectMapperUtils.copyPropertiesByMapper(copyKpi, KPIDTO.class));
        tabKPIDTO.getKpi().setSelectedFilters(counterDTO.getSelectedFilters());
        Map<BigInteger, CommonRepresentationData> data = generateKPIData(new FilterCriteriaDTO(counterDTO.getSelectedFilters(), Arrays.asList(copyKpi.getId()), accessGroupPermissionCounterDTO.getCountryId(), accessGroupPermissionCounterDTO.isCountryAdmin(),counterDTO.getKpiRepresentation(),counterDTO.getInterval(),counterDTO.getValue(),counterDTO.getFrequencyType()), UserContext.getUserDetails().getLastSelectedOrganizationId(), accessGroupPermissionCounterDTO.getStaffId());
        if(isNotNull(data))
        tabKPIDTO.setData(data.get(copyKpi.getId()));
        return tabKPIDTO;
    }

    public  KPIResponseDTO generateKPICalculationData(FilterCriteriaDTO filters, Long organizationId, Long staffId) {
        Map<BigInteger,ApplicableKPI> kpiIdAndApplicableKPIMap=new HashMap<>();
        List<KPI> kpis = counterRepository.getKPIsByIds(filters.getKpiIds());
        Map<BigInteger, KPI> kpiMap = kpis.stream().collect(Collectors.toMap(kpi -> kpi.getId(), kpi -> kpi));
        List<Future<KPIResponseDTO>> kpiResults = new ArrayList<>();
        Map<FilterType, List> filterBasedCriteria = new HashMap<>();
        Map<BigInteger, Map<FilterType, List>> staffKpiFilterCritera = new HashMap<>();
        if (filters.getFilters() != null && isCollectionNotEmpty(filters.getFilters())) {
            filters.getFilters().forEach(filter -> filterBasedCriteria.put(filter.getType(), filter.getValues()));
            for(KPI kpi : kpis) {
                kpiIdAndApplicableKPIMap.put(kpi.getId(),new ApplicableKPI(filters.getKpiRepresentation(),filters.getValue(),filters.getInterval(),filters.getFrequencyType()));
            }
        } else {
            getStaffKPiFilterAndApplicableKpi(filters, staffId, kpiIdAndApplicableKPIMap, kpis, staffKpiFilterCritera);
        }
        for (BigInteger kpiId : filters.getKpiIds()) {
            if(!counterRepository.getKPIByid(kpiId).isMultiDimensional() && isNotNull(kpiIdAndApplicableKPIMap.get(kpiId))) {
                kpiIdAndApplicableKPIMap.get(kpiId).setKpiRepresentation(KPIRepresentation.REPRESENT_PER_STAFF);
                Callable<KPIResponseDTO> data = () -> counterServiceMapping.getService(kpiMap.get(kpiId).getType()).getCalculatedDataOfKPI(staffKpiFilterCritera.getOrDefault(kpiId, filterBasedCriteria), organizationId, kpiMap.get(kpiId), kpiIdAndApplicableKPIMap.get(kpiId));
                Future<KPIResponseDTO> responseData = executorService.submit(data);
                kpiResults.add(responseData);
            }
        }
        KPIResponseDTO kpiResponseDTO = new KPISetResponseDTO();
        for (Future<KPIResponseDTO> data : kpiResults) {
            try {
                if(isNotNull(data.get())) {
                    kpiResponseDTO.setKpiId(data.get().getKpiId());
                    kpiResponseDTO.setKpiName(data.get().getKpiName());
                    kpiResponseDTO.setStaffKPIValue(data.get().getStaffKPIValue());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return kpiResponseDTO;
    }
}
