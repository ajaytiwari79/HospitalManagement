package com.kairos.service.counter;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.counter.CounterServiceMapping;
import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIConfigDTO;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIDTO;
import com.kairos.dto.activity.kpi.*;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.counter.FibonacciKPIRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.counter.KPIUtils;
import com.kairos.utils.user_context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.dto.activity.counter.enums.ConfLevel.STAFF;
import static com.kairos.dto.activity.counter.enums.ConfLevel.UNIT;
import static com.kairos.utils.counter.KPIUtils.getLongValue;

@Service
public class FibonacciKPIService implements CounterService{

    private final static Logger LOGGER = LoggerFactory.getLogger(FibonacciKPIService.class);

    @Inject private FibonacciKPIRepository fibonacciKPIRepository;
    @Inject private UserIntegrationService userIntegrationService;
    @Inject private ExceptionService exceptionService;
    @Inject private CounterServiceMapping counterServiceMapping;
    @Inject private CounterRepository counterRepository;
    @Inject private CounterHelperService counterHelperService;

    public FibonacciKPIDTO createFibonacciKPI(Long referenceId, FibonacciKPIDTO fibonacciKPIDTO, ConfLevel confLevel) {
        boolean existByName = fibonacciKPIRepository.existByName(null,fibonacciKPIDTO.getTitle(),confLevel,referenceId);
        if(existByName){
            exceptionService.duplicateDataException(ERROR_KPI_NAME_DUPLICATE);
        }
        if(confLevel.equals(ConfLevel.COUNTRY) && !userIntegrationService.isCountryExists(referenceId)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID);
        }
        if(confLevel.equals(ConfLevel.UNIT) && !userIntegrationService.isExistOrganization(referenceId)){
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID);
        }
        fibonacciKPIDTO.setReferenceId(referenceId);
        fibonacciKPIDTO.setConfLevel(confLevel);
        FibonacciKPI fibonacciKPI = ObjectMapperUtils.copyPropertiesByMapper(fibonacciKPIDTO, FibonacciKPI.class);
        fibonacciKPIRepository.save(fibonacciKPI);
        fibonacciKPIDTO.setId(fibonacciKPI.getId());
        List<ApplicableKPI> applicableKPIs = new ArrayList<>();
        if (ConfLevel.COUNTRY.equals(confLevel) ) {
            applicableKPIs.add(new ApplicableKPI(fibonacciKPI.getId(), fibonacciKPI.getId(), referenceId, null, null, confLevel, new ApplicableFilter(new ArrayList<>(), false), fibonacciKPI.getTitle(), false,ObjectMapperUtils.copyPropertiesOfListByMapper(fibonacciKPIDTO.getFibonacciKPIConfigs(),FibonacciKPIConfig.class),KPIRepresentation.INDIVIDUAL_STAFF));
        } else if (UNIT.equals(confLevel)) {
            applicableKPIs.add(new ApplicableKPI(fibonacciKPI.getId(), fibonacciKPI.getId(), null, referenceId, null, confLevel, new ApplicableFilter(new ArrayList<>(), false), fibonacciKPI.getTitle(), false,ObjectMapperUtils.copyPropertiesOfListByMapper(fibonacciKPIDTO.getFibonacciKPIConfigs(),FibonacciKPIConfig.class),KPIRepresentation.INDIVIDUAL_STAFF));
        }
        fibonacciKPIRepository.saveEntities(applicableKPIs);
        return fibonacciKPIDTO;
    }

    public FibonacciKPIDTO updateFibonacciKPI(Long referenceId,FibonacciKPIDTO fibonacciKPIDTO,ConfLevel confLevel){
        boolean existByName = fibonacciKPIRepository.existByName(fibonacciKPIDTO.getId(),fibonacciKPIDTO.getTitle(),confLevel,referenceId);
        if(existByName){
            exceptionService.duplicateDataException(ERROR_KPI_NAME_DUPLICATE);
        }
        if(confLevel.equals(ConfLevel.COUNTRY) && !userIntegrationService.isCountryExists(referenceId)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID);
        }
        if(confLevel.equals(ConfLevel.UNIT) && !userIntegrationService.isExistOrganization(referenceId)){
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID);
        }
        ApplicableKPI applicableKPI = counterRepository.getKPIByKPIId(fibonacciKPIDTO.getId(),referenceId,confLevel);
        if(isNull(applicableKPI)){
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,"FibonacciKPI",fibonacciKPIDTO.getId());
        }
        applicableKPI.setFibonacciKPIConfigs(ObjectMapperUtils.copyPropertiesOfListByMapper(fibonacciKPIDTO.getFibonacciKPIConfigs(),FibonacciKPIConfig.class));
        applicableKPI.setTitle(fibonacciKPIDTO.getTitle());
        counterRepository.save(applicableKPI);
        return fibonacciKPIDTO;
    }

    public List<KPIDTO> getAllFibonacciKPI(Long referenceId,ConfLevel confLevel){
        List<KPIDTO> kpidtos = counterRepository.getFibonacciKpiForReferenceId(referenceId, confLevel, false);
        return kpidtos;
    }

    public KPIDTO getOneFibonacciKPI(BigInteger fibonacciKPIId,Long referenceId,ConfLevel confLevel){
        KPIDTO kpidto = fibonacciKPIRepository.getOneByfibonacciId(fibonacciKPIId,referenceId,confLevel);
        if(confLevel.equals(UNIT)){
            List<FibonacciKPIConfig> fibonacciKPIConfigs = getFibonacciKPIsByOrganizationConfig(ObjectMapperUtils.copyPropertiesOfListByMapper(kpidto.getFibonacciKPIConfigs(),FibonacciKPIConfig.class),referenceId);
            kpidto.setFibonacciKPIConfigs(ObjectMapperUtils.copyPropertiesOfListByMapper(fibonacciKPIConfigs, FibonacciKPIConfigDTO.class));
        }
        kpidto.setFibonacciKPI(true);
        return kpidto;
    }

    public boolean deleteFibonacciKPI(BigInteger fibonacciKPIId){
        FibonacciKPI fibonacciKPI = fibonacciKPIRepository.findFibonacciKPIById(fibonacciKPIId);
        if(isNull(fibonacciKPI)){
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND,"FibonacciKPI",fibonacciKPIId);
            return false;
        }
        fibonacciKPI.setDeleted(true);
        fibonacciKPIRepository.save(fibonacciKPI);
        return true;
    }

    @Override
    public Map<FilterType, List> getApplicableFilters(List<FilterCriteria> availableFilters, Map<FilterType, List> providedFiltersMap) {
        return null;
    }

    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        return null;
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        /*Map[] calculateFibonacciKPImaps = calculateFibonacciKPI(filterBasedCriteria,organizationId,applicableKPI);
        Map<Long,List<ClusteredBarChartKpiDataUnit>> kpiAndstaffDataMap = (Map<Long,List<ClusteredBarChartKpiDataUnit>>)calculateFibonacciKPImaps[0];
        Map<Long,CommonKpiDataUnit> staffAndfibonacciKpiDataMap = (Map<Long,CommonKpiDataUnit>)calculateFibonacciKPImaps[1];
        for (Map.Entry<Long, CommonKpiDataUnit> staffAndFibonacciEntry : staffAndfibonacciKpiDataMap.entrySet()) {
            ClusteredBarChartKpiDataUnit clusteredBarChartKpiDataUnit = (ClusteredBarChartKpiDataUnit)staffAndFibonacciEntry.getValue();
            clusteredBarChartKpiDataUnit.setSubValues(new ArrayList(kpiAndstaffDataMap.get(staffAndFibonacciEntry.getKey())){{
                ClusteredBarChartKpiDataUnit subValue = ObjectMapperUtils.copyPropertiesByMapper(clusteredBarChartKpiDataUnit,ClusteredBarChartKpiDataUnit.class);
                //subValue.setValue(getHoursByMinutes(subValue.getValue()));
                add(subValue);
            }});
        }*/
        return null;//new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, new ArrayList<>(staffAndfibonacciKpiDataMap.values()), new KPIAxisData(AppConstants.DATE,AppConstants.LABEL),new KPIAxisData(AppConstants.HOURS,AppConstants.VALUE_FIELD));
    }

    @Override
    public KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        return calculateFibonacciKPI(filterBasedCriteria,organizationId,applicableKPI);
    }

    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder,List<StaffKpiFilterDTO> staffKpiFilterDTOS,ApplicableKPI applicableKPI) {
        return new TreeSet<>();
    }

    private KPIResponseDTO calculateFibonacciKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
        List<LocalDate> filterDates = (filterBasedCriteria.get(FilterType.TIME_INTERVAL) != null) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL)) ? KPIUtils.getLocalDate(filterBasedCriteria.get(FilterType.TIME_INTERVAL)) : Arrays.asList(DateUtils.getStartDateOfWeek(), DateUtils.getEndDateOfWeek());
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(new ArrayList<>(), newArrayList(organizationId), new ArrayList<>(), organizationId, filterDates.get(0).toString(), filterDates.get(1).toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        List<FibonacciKPIConfig> fibonacciKPIConfigs = getFibonacciKPIsByOrganizationConfig(applicableKPI.getFibonacciKPIConfigs(), organizationId);
        Map<BigInteger, FibonacciKPIConfig> fibonacciKPIConfigMap = fibonacciKPIConfigs.stream().collect(Collectors.toMap(fibonacciKPIConfig -> fibonacciKPIConfig.getKpiId(), v -> v));
        List<KPI> counters = counterRepository.getKPIsByIds(new ArrayList<>(fibonacciKPIConfigMap.keySet()));
        Map<Long, FibonacciKPICalculation> kpiAndFibonacciDataMap = new HashMap<>();
        applicableKPI.setKpiRepresentation(KPIRepresentation.REPRESENT_PER_STAFF);
        for (KPI counter : counters) {
            FibonacciKPIConfig fibonacciKPIConfig = fibonacciKPIConfigMap.get(counter.getId());
            TreeSet<FibonacciKPICalculation> kpiCalculation = counterServiceMapping.getService(counter.getType()).getFibonacciCalculatedCounter(filterBasedCriteria, organizationId, fibonacciKPIConfig.getSortingOrder(), staffKpiFilterDTOS, applicableKPI);
            for (FibonacciKPICalculation fibonacciKPICalculation : kpiCalculation) {
                FibonacciKPICalculation fibonacciKPIValueCalulation = kpiAndFibonacciDataMap.getOrDefault(fibonacciKPICalculation.getStaffId(), new FibonacciKPICalculation(fibonacciKPICalculation.getOrderValueByFiboncci(),fibonacciKPICalculation.getStaffId()));
                fibonacciKPIValueCalulation.setFibonacciKpiCount(fibonacciKPIValueCalulation.getFibonacciKpiCount().add(fibonacciKPICalculation.getFibonacciKpiCount()));
                kpiAndFibonacciDataMap.put(fibonacciKPICalculation.getStaffId(),fibonacciKPIValueCalulation);
            }
        }
        Map<Long,Double> staffIdAndOrderMap = new HashMap<>();
        double order = 0;
        for (FibonacciKPICalculation fibonacciKPICalculation : kpiAndFibonacciDataMap.values().stream().sorted().collect(Collectors.toList())) {
            staffIdAndOrderMap.put(fibonacciKPICalculation.getStaffId(),order++);
        }
        return new KPIResponseDTO(applicableKPI.getActiveKpiId(),applicableKPI.getTitle(),staffIdAndOrderMap);
    }

    private List<FibonacciKPIConfig> getFibonacciKPIsByOrganizationConfig(List<FibonacciKPIConfig> fibonacciKPIConfigs, Long organizationId){
        AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(UserContext.getUserDetails().getLastSelectedOrganizationId());
        List<FibonacciKPIConfig> updatedFibonacciKPIConfigs = new ArrayList<>(fibonacciKPIConfigs.size());
        Map<BigInteger,FibonacciKPIConfig> fibonacciKPIConfigMap = fibonacciKPIConfigs.stream().collect(Collectors.toMap(k -> k.getKpiId(),v->v));
        List<ApplicableKPI> applicableKPIS = counterRepository.getApplicableKPI(new ArrayList(fibonacciKPIConfigMap.keySet()),STAFF,accessGroupPermissionCounterDTO.getStaffId());
        for (ApplicableKPI applicableKPI : applicableKPIS) {
            updatedFibonacciKPIConfigs.add(fibonacciKPIConfigMap.get(applicableKPI.getActiveKpiId()));
        }
        return updatedFibonacciKPIConfigs;
    }

}
