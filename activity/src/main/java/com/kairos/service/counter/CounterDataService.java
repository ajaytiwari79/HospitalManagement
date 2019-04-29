package com.kairos.service.counter;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.counter.CounterServiceMapping;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.configuration.KPIFilterDefaultDataDTO;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.data.FilterCriteriaDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.tab.KPIPosition;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.KPIValidity;
import com.kairos.dto.activity.counter.enums.LocationType;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.user.organization.OrganizationCommonDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.counter.ApplicableFilter;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.counter.TabKPIConf;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static java.util.stream.Collectors.toList;



@Service
public class CounterDataService extends MongoBaseService {
    private final static Logger logger = LoggerFactory.getLogger(CounterDataService.class);
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
        } else {
            getStaffKPiFilterAndApplicableKpi(filters, staffId, kpiIdAndApplicableKPIMap, kpis, staffKpiFilterCritera);
        }
        for (BigInteger kpiId : filters.getKpiIds()) {
            Callable<CommonRepresentationData> data = () -> counterServiceMapping.getService(kpiMap.get(kpiId).getType()).getCalculatedKPI(staffKpiFilterCritera.getOrDefault(kpiId, filterBasedCriteria), organizationId, kpiMap.get(kpiId),kpiIdAndApplicableKPIMap.get(kpiId));
            Future<CommonRepresentationData> responseData = executorService.submit(data);
            kpiResults.add(responseData);
        }
        List<CommonRepresentationData> kpisData = new ArrayList();
        for (Future<CommonRepresentationData> data : kpiResults) {
            try {
                kpisData.add(data.get());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }


        return kpisData.stream().collect(Collectors.toMap(CommonRepresentationData::getCounterId, kpiData -> kpiData));
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
                if(!filters.isManagement() && KPIRepresentation.INDIVIDUAL_STAFF.equals(staffApplicableKPI.getKpiRepresentation())){
                    staffFilterBasedCriteria.put(FilterType.STAFF_IDS, Arrays.asList(staffId.intValue()));
                }
                if(isNotNull(filters.getFrequencyType())){
                    staffApplicableKPI.setInterval(filters.getInterval());
                    staffApplicableKPI.setValue(filters.getValue());
                    staffApplicableKPI.setFrequencyType(filters.getFrequencyType());
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
            exceptionService.actionNotPermittedException("message.kpi.permission");
        }
        if (isNotNull(tabId) && !accessGroupPermissionCounterDTO.isCountryAdmin()) {
            level = ConfLevel.STAFF;
            applicableKPIS = counterRepository.getApplicableKPI(Arrays.asList(kpiId), level, accessGroupPermissionCounterDTO.getStaffId());
        } else {
            applicableKPIS = counterRepository.getApplicableKPI(Arrays.asList(kpiId), level, refId);
        }
        if (applicableKPIS.isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.counter.kpi.notfound");
        }
        List<FilterCriteria> criteriaList = new ArrayList<>();
        KPIDTO kpi = ObjectMapperUtils.copyPropertiesByMapper(counterRepository.getKPIByid(kpiId), KPIDTO.class);
        DefaultKpiDataDTO defaultKpiDataDTO = userIntegrationService.getKpiFilterDefaultData(ConfLevel.COUNTRY.equals(level) ? UserContext.getUserDetails().getLastSelectedOrganizationId() : refId);
        getSelectedFilterDefaultData(level, criteriaList, kpi, defaultKpiDataDTO);
        kpi.setDefaultFilters(criteriaList);
        kpi.setTitle(applicableKPIS.get(0).getTitle());
        if (isNotNull(applicableKPIS.get(0).getApplicableFilter())) {
            kpi.setSelectedFilters(applicableKPIS.get(0).getApplicableFilter().getCriteriaList());
        }
        return kpi;
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
        if (kpi.getFilterTypes().contains(FilterType.STAFF_IDS) && ConfLevel.UNIT.equals(level)) {
            getStaffDefaultData(criteriaList, defaultKpiDataDTO);
        }
        if (kpi.getFilterTypes().contains(FilterType.ACTIVITY_STATUS)) {
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
        criteriaList.add(new FilterCriteria(FilterType.STAFF_IDS.value, FilterType.STAFF_IDS, (List) kpiFilterDefaultDataDTOS));
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
            exceptionService.actionNotPermittedException("message.kpi.permission");
        }
        if (isNotNull(tabId) && !accessGroupPermissionCounterDTO.isCountryAdmin()) {
            level = ConfLevel.STAFF;
            refId = accessGroupPermissionCounterDTO.getStaffId();
        }
        List<ApplicableKPI> applicableKPIS = counterRepository.getApplicableKPI(Arrays.asList(kpiId), level, refId);
        if (applicableKPIS.isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.counter.kpi.notfound");
        }
        KPI kpi = counterRepository.getKPIByid(kpiId);
        if (!kpi.getCalculationFormula().equals(counterDTO.getCalculationFormula()) && !accessGroupPermissionCounterDTO.isCountryAdmin()) {
            exceptionService.actionNotPermittedException("message.kpi.permission");
        }
        if (!applicableKPIS.get(0).getTitle().equals(counterDTO.getTitle()) && Optional.ofNullable(counterRepository.getKpiByTitleAndUnitId(counterDTO.getTitle(), refId, level)).isPresent()) {
            exceptionService.duplicateDataException("error.kpi.name.duplicate");
        }
        kpi.setCalculationFormula(counterDTO.getCalculationFormula());
        applicableKPIS.get(0).setApplicableFilter(new ApplicableFilter(counterDTO.getSelectedFilters(), true));
        List<ApplicableKPI> updateApplicableKPI = counterRepository.getFilterBaseApplicableKPIByKpiIdsOrUnitId(Arrays.asList(kpiId), Arrays.asList(ConfLevel.UNIT, ConfLevel.STAFF), ConfLevel.COUNTRY.equals(level) ? null : refId);
        for (ApplicableKPI applicableKPI : updateApplicableKPI) {
            applicableKPI.setApplicableFilter(new ApplicableFilter(counterDTO.getSelectedFilters(), false));
            if (applicableKPI.getTitle().equals(applicableKPIS.get(0).getTitle())) {
                applicableKPI.setTitle(counterDTO.getTitle().trim());
            }
            applicableKPI.setKpiRepresentation(counterDTO.getKpiRepresentation());
            applicableKPI.setValue(counterDTO.getValue());
            applicableKPI.setFrequencyType(counterDTO.getFrequencyType());
            applicableKPI.setInterval(counterDTO.getInterval());
        }
        applicableKPIS.get(0).setTitle(counterDTO.getTitle());
        applicableKPIS.get(0).setKpiRepresentation(counterDTO.getKpiRepresentation());
        applicableKPIS.get(0).setValue(counterDTO.getValue());
        applicableKPIS.get(0).setFrequencyType(counterDTO.getFrequencyType());
        applicableKPIS.get(0).setInterval(counterDTO.getInterval());
        applicableKPIS.addAll(updateApplicableKPI);
        save(applicableKPIS);
        save(kpi);
        kpi.setTitle(counterDTO.getTitle());
        return getTabKpiData(kpi, counterDTO, accessGroupPermissionCounterDTO);
    }

    public TabKPIDTO copyKpiFilterData(String tabId, Long refId, BigInteger kpiId, CounterDTO counterDTO, ConfLevel level) {
        boolean copy = (isNotNull(tabId) ? true : false);
        TabKPIConf tabKPIConf = null;
        List<ApplicableKPI> applicableKPIS;
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(UserContext.getUserDetails().getLastSelectedOrganizationId());
        if (!accessGroupPermissionCounterDTO.isManagement()) {
            exceptionService.actionNotPermittedException("message.kpi.permission");
        }
        if (isNotNull(tabId) && !accessGroupPermissionCounterDTO.isCountryAdmin()) {
            level = ConfLevel.STAFF;
            applicableKPIS = counterRepository.getApplicableKPI(Arrays.asList(kpiId), level, accessGroupPermissionCounterDTO.getStaffId());
        } else {
            applicableKPIS = counterRepository.getApplicableKPI(Arrays.asList(kpiId), level, refId);
        }
        if (applicableKPIS.isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.counter.kpi.notfound");
        }
        KPI kpi = counterRepository.getKPIByid(kpiId);
        if (!kpi.getCalculationFormula().equals(counterDTO.getCalculationFormula()) && !accessGroupPermissionCounterDTO.isCountryAdmin()) {
            exceptionService.actionNotPermittedException("message.kpi.permission");
        }
        if (Optional.ofNullable(counterRepository.getKpiByTitleAndUnitId(counterDTO.getTitle(), refId, level)).isPresent()) {
            exceptionService.duplicateDataException("error.kpi.name.duplicate");
        }
        KPI copyKpi = ObjectMapperUtils.copyPropertiesByMapper(kpi, KPI.class);
        copyKpi.setId(null);
        copyKpi.setTitle(counterDTO.getTitle());
        copyKpi.setCalculationFormula(counterDTO.getCalculationFormula());
        copyKpi.setFilterTypes(counterDTO.getSelectedFilters().stream().map(FilterCriteria::getType).collect(toList()));
        save(copyKpi);
        List<ApplicableKPI> applicableKPIs = new ArrayList<>();
        if (ConfLevel.COUNTRY.equals(level) || accessGroupPermissionCounterDTO.isCountryAdmin()) {
            applicableKPIs.add(new ApplicableKPI(copyKpi.getId(), kpi.getId(), refId, null, null, level, new ApplicableFilter(counterDTO.getSelectedFilters(), false), counterDTO.getTitle(), copy,counterDTO.getKpiRepresentation(),counterDTO.getInterval(),counterDTO.getValue(),counterDTO.getFrequencyType()));
        } else if (ConfLevel.UNIT.equals(level)) {
            applicableKPIs.add(new ApplicableKPI(copyKpi.getId(), applicableKPIS.get(0).getBaseKpiId(), null, refId, null, level, new ApplicableFilter(counterDTO.getSelectedFilters(), false), counterDTO.getTitle(), copy,counterDTO.getKpiRepresentation(),counterDTO.getInterval(),counterDTO.getValue(),counterDTO.getFrequencyType()));
            applicableKPIs.add(new ApplicableKPI(copyKpi.getId(), applicableKPIS.get(0).getBaseKpiId(), null, refId, accessGroupPermissionCounterDTO.getStaffId(), ConfLevel.STAFF, new ApplicableFilter(counterDTO.getSelectedFilters(), false), counterDTO.getTitle(), copy,counterDTO.getKpiRepresentation(),counterDTO.getInterval(),counterDTO.getValue(),counterDTO.getFrequencyType()));
        } else if (isNotNull(tabId) && ConfLevel.STAFF.equals(level)) {
            applicableKPIs.add(new ApplicableKPI(copyKpi.getId(), applicableKPIS.get(0).getBaseKpiId(), null, refId, accessGroupPermissionCounterDTO.getStaffId(), ConfLevel.STAFF, new ApplicableFilter(counterDTO.getSelectedFilters(), false), counterDTO.getTitle(), copy,counterDTO.getKpiRepresentation(),counterDTO.getInterval(),counterDTO.getValue(),counterDTO.getFrequencyType()));
            tabKPIConf = new TabKPIConf(tabId, copyKpi.getId(), null, refId, accessGroupPermissionCounterDTO.getStaffId(), level, new KPIPosition(0, 0), KPIValidity.BASIC, LocationType.FIX, counterDistService.calculatePriority(ConfLevel.UNIT, KPIValidity.BASIC, LocationType.FIX));
            save(tabKPIConf);
        }
        applicableKPIS.addAll(applicableKPIs);
        save(applicableKPIS);
        TabKPIDTO tabKPIDTO = getTabKpiData(copyKpi, counterDTO, accessGroupPermissionCounterDTO);
        tabKPIDTO.setId((isNotNull(tabKPIConf)) ? tabKPIConf.getId() : null);
        return tabKPIDTO;
    }

    public TabKPIDTO getKpiPreviewWithFilter(BigInteger kpiId, Long refId, FilterCriteriaDTO filterCriteria, ConfLevel level) {
        TabKPIDTO tabKPIDTO = new TabKPIDTO();
        KPI kpi = counterRepository.getKPIByid(kpiId);
        tabKPIDTO.setKpi(ObjectMapperUtils.copyPropertiesByMapper(kpi, KPIDTO.class));
        filterCriteria.setKpiIds(Arrays.asList(kpiId));
        refId = ConfLevel.UNIT.equals(level) ? refId : UserContext.getUserDetails().getLastSelectedOrganizationId();
        Map<BigInteger, CommonRepresentationData> data = generateKPIData(filterCriteria, refId, null);
        tabKPIDTO.setData(data.get(kpiId));
        return tabKPIDTO;
    }

    public TabKPIDTO getKpiDataByInterval(BigInteger kpiId, Long refId, FilterCriteriaDTO filterCriteria, ConfLevel level) {
        return getKpiPreviewWithFilter(kpiId,refId,filterCriteria,level);
    }

    private TabKPIDTO getTabKpiData(KPI copyKpi, CounterDTO counterDTO, AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO) {
        TabKPIDTO tabKPIDTO = new TabKPIDTO();
        tabKPIDTO.setKpi(ObjectMapperUtils.copyPropertiesByMapper(copyKpi, KPIDTO.class));
        tabKPIDTO.getKpi().setSelectedFilters(counterDTO.getSelectedFilters());
        Map<BigInteger, CommonRepresentationData> data = generateKPIData(new FilterCriteriaDTO(counterDTO.getSelectedFilters(), Arrays.asList(copyKpi.getId()), accessGroupPermissionCounterDTO.getCountryId(), accessGroupPermissionCounterDTO.isCountryAdmin()), UserContext.getUserDetails().getLastSelectedOrganizationId(), accessGroupPermissionCounterDTO.getStaffId());
        tabKPIDTO.setData(data.get(copyKpi.getId()));
        return tabKPIDTO;
    }


}
