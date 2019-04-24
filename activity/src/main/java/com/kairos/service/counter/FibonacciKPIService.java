package com.kairos.service.counter;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.KPIUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.counter.CounterServiceMapping;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.persistence.model.activity.ActivityPriority;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.counter.FibonacciKPIRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getHoursByMinutes;
import static com.kairos.commons.utils.KPIUtils.getLongValue;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.dto.activity.counter.enums.ConfLevel.UNIT;

@Service
public class FibonacciKPIService implements CounterService{

    private final static Logger LOGGER = LoggerFactory.getLogger(FibonacciKPIService.class);

    @Inject private FibonacciKPIRepository fibonacciKPIRepository;
    @Inject private UserIntegrationService userIntegrationService;
    @Inject private ExceptionService exceptionService;
    @Inject private CounterServiceMapping counterServiceMapping;
    @Inject private CounterRepository counterRepository;

    public FibonacciKPIDTO createFibonacciKPI(Long referenceId, FibonacciKPIDTO fibonacciKPIDTO, ConfLevel confLevel) {
        boolean existByName = fibonacciKPIRepository.existByName(null,fibonacciKPIDTO.getTitle(),confLevel,referenceId);
        if(existByName){
            exceptionService.duplicateDataException("error.kpi.name.duplicate");
        }
        if(confLevel.equals(ConfLevel.COUNTRY) && !userIntegrationService.isCountryExists(referenceId)) {
            exceptionService.dataNotFoundByIdException("message.country.id");
        }
        if(confLevel.equals(UNIT) && !userIntegrationService.isExistOrganization(referenceId)){
            exceptionService.dataNotFoundByIdException("message.organization.id");
        }
        fibonacciKPIDTO.setReferenceId(referenceId);
        fibonacciKPIDTO.setConfLevel(confLevel);
        FibonacciKPI fibonacciKPI = ObjectMapperUtils.copyPropertiesByMapper(fibonacciKPIDTO, FibonacciKPI.class);
        fibonacciKPIRepository.save(fibonacciKPI);
        fibonacciKPIDTO.setId(fibonacciKPI.getId());
        List<ApplicableKPI> applicableKPIs = new ArrayList<>();
        if (ConfLevel.COUNTRY.equals(confLevel) ) {
            applicableKPIs.add(new ApplicableKPI(fibonacciKPI.getId(), fibonacciKPI.getId(), referenceId, null, null, confLevel, new ApplicableFilter(new ArrayList<>(), false), fibonacciKPI.getTitle(), false));
        } else if (UNIT.equals(confLevel)) {
            applicableKPIs.add(new ApplicableKPI(fibonacciKPI.getId(), fibonacciKPI.getId(), null, referenceId, null, confLevel, new ApplicableFilter(new ArrayList<>(), false), fibonacciKPI.getTitle(), false));
        }
        createFibonacciKPIAtOrganizations(fibonacciKPI,UNIT.equals(confLevel) ? referenceId : null);
        fibonacciKPIRepository.saveEntities(applicableKPIs);
        return fibonacciKPIDTO;
    }

    private boolean createFibonacciKPIAtOrganizations(FibonacciKPI fibonacciKPI, Long unitId){
        List<Long> organizationIds = userIntegrationService.getAllOrganizationIds(unitId);
        List<FibonacciKPI> fibonacciKPIS = new ArrayList<>(organizationIds.size());
        fibonacciKPI.setId(null);
        List<ApplicableKPI> applicableKPIs = new ArrayList<>();
        for (Long organizationId : organizationIds) {
            FibonacciKPI organizationfibonacciKPI = ObjectMapperUtils.copyPropertiesByMapper(fibonacciKPI,FibonacciKPI.class);
            organizationfibonacciKPI.setConfLevel(UNIT);
            organizationfibonacciKPI.setReferenceId(organizationId);
            fibonacciKPIS.add(organizationfibonacciKPI);
            applicableKPIs.add(new ApplicableKPI(fibonacciKPI.getId(), fibonacciKPI.getId(), null, organizationId, null, UNIT, new ApplicableFilter(new ArrayList<>(), false), fibonacciKPI.getTitle(), false));
        }
        if(isCollectionNotEmpty(fibonacciKPIS)) {
            fibonacciKPIRepository.saveEntities(fibonacciKPIS);
            fibonacciKPIRepository.saveEntities(applicableKPIs);
        }
        return true;
    }

    public FibonacciKPIDTO updateFibonacciKPI(Long referenceId,FibonacciKPIDTO fibonacciKPIDTO,ConfLevel confLevel){
        boolean existByName = fibonacciKPIRepository.existByName(fibonacciKPIDTO.getId(),fibonacciKPIDTO.getTitle(),confLevel,referenceId);
        if(existByName){
            exceptionService.duplicateDataException("error.kpi.name.duplicate");
        }
        if(confLevel.equals(ConfLevel.COUNTRY) && !userIntegrationService.isCountryExists(referenceId)) {
            exceptionService.dataNotFoundByIdException("message.country.id");
        }
        if(confLevel.equals(UNIT) && !userIntegrationService.isExistOrganization(referenceId)){
            exceptionService.dataNotFoundByIdException("message.organization.id");
        }
        fibonacciKPIDTO.setReferenceId(referenceId);
        fibonacciKPIDTO.setConfLevel(confLevel);
        FibonacciKPI fibonacciKPI = fibonacciKPIRepository.findFibonacciKPIById(fibonacciKPIDTO.getId());
        if(isNull(fibonacciKPI)){
            exceptionService.dataNotFoundByIdException("message.dataNotFound","FibonacciKPI",fibonacciKPIDTO.getId());
        }
        fibonacciKPI = ObjectMapperUtils.copyPropertiesByMapper(fibonacciKPIDTO,FibonacciKPI.class);
        fibonacciKPIRepository.save(fibonacciKPI);
        fibonacciKPIDTO.setId(fibonacciKPI.getId());
        return fibonacciKPIDTO;
    }

    public List<KPIDTO> getAllFibonacciKPI(Long referenceId,ConfLevel confLevel){
        List<KPIDTO> kpidtos = counterRepository.getFibonacciKpiForReferenceId(referenceId, confLevel, false);
        List<KPIDTO> fibonacciKPIDTOS = fibonacciKPIRepository.findAllFibonacciKPIByReferenceId(referenceId,confLevel);
        if(isCollectionNotEmpty(fibonacciKPIDTOS)){
            kpidtos = new ArrayList<>(kpidtos);
            kpidtos.addAll(fibonacciKPIDTOS);
        }/*
        if (kpidtos.isEmpty()) {
            LOGGER.info("Fibonacci KPI not found for {} id " + referenceId,confLevel);
            exceptionService.dataNotFoundByIdException("message.counter.kpi.notfound");
        }*/
        return kpidtos;
    }

    public FibonacciKPIDTO getOneFibonacciKPI(BigInteger fibonacciKPIId){
        FibonacciKPI fibonacciKPI = fibonacciKPIRepository.findFibonacciKPIById(fibonacciKPIId);
        FibonacciKPIDTO fibonacciKPIDTO = ObjectMapperUtils.copyPropertiesByMapper(fibonacciKPI,FibonacciKPIDTO.class);
        fibonacciKPIDTO.setFibonacciKPI(true);
        return fibonacciKPIDTO;
    }

    public boolean deleteFibonacciKPI(BigInteger fibonacciKPIId){
        FibonacciKPI fibonacciKPI = fibonacciKPIRepository.findFibonacciKPIById(fibonacciKPIId);
        if(isNull(fibonacciKPI)){
            exceptionService.dataNotFoundByIdException("message.dataNotFound","FibonacciKPI",fibonacciKPIId);
            return false;
        }
        fibonacciKPI.setDeleted(true);
        fibonacciKPIRepository.save(fibonacciKPI);
        return true;
    }

    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        return null;
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        Map[] calculateFibonacciKPImaps = calculateFibonacciKPI(filterBasedCriteria,organizationId,kpi);
        Map<Long,List<ClusteredBarChartKpiDataUnit>> kpiAndstaffDataMap = (Map<Long,List<ClusteredBarChartKpiDataUnit>>)calculateFibonacciKPImaps[0];
        Map<Long,CommonKpiDataUnit> staffAndfibonacciKpiDataMap = (Map<Long,CommonKpiDataUnit>)calculateFibonacciKPImaps[1];
        for (Map.Entry<Long, CommonKpiDataUnit> staffAndFibonacciEntry : staffAndfibonacciKpiDataMap.entrySet()) {
            ClusteredBarChartKpiDataUnit clusteredBarChartKpiDataUnit = (ClusteredBarChartKpiDataUnit)staffAndFibonacciEntry.getValue();
            clusteredBarChartKpiDataUnit.setSubValues(new ArrayList(kpiAndstaffDataMap.get(staffAndFibonacciEntry.getKey())){{
                ClusteredBarChartKpiDataUnit subValue = ObjectMapperUtils.copyPropertiesByMapper(clusteredBarChartKpiDataUnit,ClusteredBarChartKpiDataUnit.class);
                //subValue.setValue(getHoursByMinutes(subValue.getValue()));
                add(subValue);
            }});
        }
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, new ArrayList<>(staffAndfibonacciKpiDataMap.values()), new KPIAxisData(AppConstants.DATE,AppConstants.LABEL),new KPIAxisData(AppConstants.HOURS,AppConstants.VALUE_FIELD));
    }

    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder,List<StaffKpiFilterDTO> staffKpiFilterDTOS,List<LocalDate> filterDates) {
        return new TreeSet<>();
    }

    private Map[] calculateFibonacciKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi){
        List<Long> staffIds = getLongValue(filterBasedCriteria.getOrDefault(FilterType.STAFF_IDS, new ArrayList<>()));
        List<LocalDate> filterDates = (filterBasedCriteria.get(FilterType.TIME_INTERVAL) !=null)&& isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL))? KPIUtils.getLocalDate(filterBasedCriteria.get(FilterType.TIME_INTERVAL)): Arrays.asList(DateUtils.getStartDateOfWeek(),DateUtils.getEndDateOfWeek());
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(staffIds, newArrayList(organizationId), new ArrayList<>(), organizationId, filterDates.get(0).toString(), filterDates.get(1).toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        Map<BigInteger,FibonacciKPIConfig> fibonacciKPIConfigMap = ((FibonacciKPI)kpi).getFibonacciKPIConfigs().stream().collect(Collectors.toMap(fibonacciKPIConfig -> fibonacciKPIConfig.getKpiId(),v->v));
        List<KPI> counters = counterRepository.getKPIsByIds(new ArrayList<>(fibonacciKPIConfigMap.keySet()));
        Map<Long,CommonKpiDataUnit> staffAndfibonacciKpiDataMap = new HashMap<>();
        Map<Long,List<ClusteredBarChartKpiDataUnit>> kpiAndstaffDataMap = new HashMap<>();
        Map<Long, String> staffIdAndNameMap = staffKpiFilterDTOS.stream().collect(Collectors.toMap(StaffKpiFilterDTO::getId, StaffKpiFilterDTO::getFullName));
        for (KPI counter : counters) {
            FibonacciKPIConfig fibonacciKPIConfig = fibonacciKPIConfigMap.get(counter.getId());
            TreeSet<FibonacciKPICalculation> kpiCalculation = counterServiceMapping.getService(counter.getType()).getFibonacciCalculatedCounter(filterBasedCriteria,organizationId,fibonacciKPIConfig.getSortingOrder(),staffKpiFilterDTOS,filterDates);
            for (FibonacciKPICalculation fibonacciKPICalculation : kpiCalculation) {
                List<ClusteredBarChartKpiDataUnit> staffKpis = kpiAndstaffDataMap.getOrDefault(fibonacciKPICalculation.getStaffId(),new ArrayList<>());
                staffKpis.add(new ClusteredBarChartKpiDataUnit(counter.getTitle(),getHoursByMinutes(fibonacciKPICalculation.getValue().doubleValue())));
                ClusteredBarChartKpiDataUnit clusteredBarChartKpiDataUnit;
                if(staffAndfibonacciKpiDataMap.containsKey(fibonacciKPICalculation.getStaffId())){
                    clusteredBarChartKpiDataUnit = (ClusteredBarChartKpiDataUnit)staffAndfibonacciKpiDataMap.get(fibonacciKPICalculation.getStaffId());
                    clusteredBarChartKpiDataUnit.setValue(clusteredBarChartKpiDataUnit.getValue() + (fibonacciKPICalculation.getValue() * fibonacciKPICalculation.getFibonacciKpiCount()));
                }else {
                    clusteredBarChartKpiDataUnit = new ClusteredBarChartKpiDataUnit(staffIdAndNameMap.get(fibonacciKPICalculation.getStaffId()),fibonacciKPICalculation.getValue() * fibonacciKPICalculation.getFibonacciKpiCount());
                    staffAndfibonacciKpiDataMap.put(fibonacciKPICalculation.getStaffId(),clusteredBarChartKpiDataUnit);
                }
                kpiAndstaffDataMap.putIfAbsent(fibonacciKPICalculation.getStaffId(),staffKpis);
            }
        }
        return new Map[]{kpiAndstaffDataMap,staffAndfibonacciKpiDataMap};
    }

}
