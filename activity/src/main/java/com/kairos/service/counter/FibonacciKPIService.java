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
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.ApplicableKPIRepository;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.counter.FibonacciKPIRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.counter.KPIUtils;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.UNCATEGORIZED;
import static com.kairos.dto.activity.counter.enums.ConfLevel.*;

@Service
public class FibonacciKPIService implements CounterService{
    @Inject private FibonacciKPIRepository fibonacciKPIRepository;
    @Inject private UserIntegrationService userIntegrationService;
    @Inject private ExceptionService exceptionService;
    @Inject private CounterServiceMapping counterServiceMapping;
    @Inject private CounterRepository counterRepository;

    @Inject private CounterHelperService counterHelperService;
    @Inject private ApplicableKPIRepository applicableKPIRepository;

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
        ApplicableKPI applicableKPI=new ApplicableKPI(fibonacciKPI.getId(), fibonacciKPI.getId(), COUNTRY.equals(confLevel)?referenceId:null, UNIT.equals(confLevel)?referenceId:null, null, confLevel, new ApplicableFilter(new ArrayList<>(), false), fibonacciKPI.getTitle(), false,ObjectMapperUtils.copyPropertiesOfListByMapper(fibonacciKPIDTO.getFibonacciKPIConfigs(),FibonacciKPIConfig.class),KPIRepresentation.INDIVIDUAL_STAFF);
        applicableKPIRepository.save(applicableKPI);
        KPICategory kpiCategory=counterRepository.getKPICategoryByName(UNCATEGORIZED,confLevel,referenceId);
        CategoryKPIConf categoryKPIConf=new CategoryKPIConf(applicableKPI.getActiveKpiId(), kpiCategory.getId(), COUNTRY.equals(confLevel)?referenceId:null, UNIT.equals(confLevel)?referenceId:null, confLevel);
        counterRepository.save(categoryKPIConf);
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
        return counterRepository.getFibonacciKpiForReferenceId(referenceId, confLevel, false);
    }

    public KPIDTO getOneFibonacciKPI(BigInteger fibonacciKPIId,Long referenceId,ConfLevel confLevel){
        KPIDTO kpidto = fibonacciKPIRepository.getOneByfibonacciId(fibonacciKPIId,referenceId,confLevel);
        if(confLevel.equals(UNIT)){
            List<FibonacciKPIConfig> fibonacciKPIConfigs = getFibonacciKPIsByOrganizationConfig(ObjectMapperUtils.copyPropertiesOfListByMapper(kpidto.getFibonacciKPIConfigs(),FibonacciKPIConfig.class),referenceId,confLevel);
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
        return new CommonRepresentationData();
    }

    @Override
    public KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI) {
        return calculateFibonacciKPI(filterBasedCriteria,organizationId,applicableKPI);
    }

    @Override
    public TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder,List<StaffKpiFilterDTO> staffKpiFilterDTOS,KPI kpi,ApplicableKPI applicableKPI) {
        return new TreeSet<>();
    }

    private KPIResponseDTO calculateFibonacciKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, ApplicableKPI applicableKPI) {
        List<LocalDate> filterDates = (filterBasedCriteria.get(FilterType.TIME_INTERVAL) != null) && isCollectionNotEmpty(filterBasedCriteria.get(FilterType.TIME_INTERVAL)) ? KPIUtils.getLocalDate(filterBasedCriteria.get(FilterType.TIME_INTERVAL)) : Arrays.asList(DateUtils.getStartDateOfWeek(), DateUtils.getEndDateOfWeek());
        StaffEmploymentTypeDTO staffEmploymentTypeDTO = new StaffEmploymentTypeDTO(new ArrayList<>(), newArrayList(organizationId), new ArrayList<>(), organizationId, filterDates.get(0).toString(), filterDates.get(1).toString());
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = userIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        List<FibonacciKPIConfig> fibonacciKPIConfigs = getFibonacciKPIsByOrganizationConfig(applicableKPI.getFibonacciKPIConfigs(), organizationId,ConfLevel.STAFF);
        Map<BigInteger, FibonacciKPIConfig> fibonacciKPIConfigMap = fibonacciKPIConfigs.stream().collect(Collectors.toMap(FibonacciKPIConfig::getKpiId, v -> v));
        List<KPI> counters = counterRepository.getKPIsByIds(new ArrayList<>(fibonacciKPIConfigMap.keySet()));
        Map<Long, FibonacciKPICalculation> kpiAndFibonacciDataMap = new HashMap<>();
        applicableKPI.setKpiRepresentation(KPIRepresentation.REPRESENT_PER_STAFF);
        for (KPI counter : counters) {
            FibonacciKPIConfig fibonacciKPIConfig = fibonacciKPIConfigMap.get(counter.getId());
            TreeSet<FibonacciKPICalculation> kpiCalculations = counterServiceMapping.getService(counter.getType()).getFibonacciCalculatedCounter(filterBasedCriteria, organizationId, fibonacciKPIConfig.getSortingOrder(), staffKpiFilterDTOS,counter, applicableKPI);
            for (FibonacciKPICalculation fibonacciKPICalculation : kpiCalculations) {
                FibonacciKPICalculation fibonacciKPIValueCalulation = kpiAndFibonacciDataMap.getOrDefault(fibonacciKPICalculation.getStaffId(), new FibonacciKPICalculation(fibonacciKPICalculation.getOrderValueByFiboncci(),fibonacciKPICalculation.getStaffId()));
                fibonacciKPIValueCalulation.setFibonacciKpiCount(fibonacciKPIValueCalulation.getFibonacciKpiCount().add(fibonacciKPICalculation.getFibonacciKpiCount()));
                kpiAndFibonacciDataMap.put(fibonacciKPICalculation.getStaffId(),fibonacciKPIValueCalulation);
            }
        }
        Map<Long,Double> staffIdAndOrderMap = new HashMap<>();
        double order = 1;
        for (FibonacciKPICalculation fibonacciKPICalculation : kpiAndFibonacciDataMap.values().stream().sorted(Comparator.comparing(FibonacciKPICalculation::getFibonacciKpiCount)).collect(Collectors.toList())) {
            staffIdAndOrderMap.put(fibonacciKPICalculation.getStaffId(),order++);
        }
        return new KPIResponseDTO(applicableKPI.getActiveKpiId(),applicableKPI.getTitle(),staffIdAndOrderMap);
    }

    private List<FibonacciKPIConfig> getFibonacciKPIsByOrganizationConfig(List<FibonacciKPIConfig> fibonacciKPIConfigs, Long organizationId,ConfLevel level){
        List<FibonacciKPIConfig> updatedFibonacciKPIConfigs = new ArrayList<>(fibonacciKPIConfigs.size());
        Map<BigInteger,FibonacciKPIConfig> fibonacciKPIConfigMap = fibonacciKPIConfigs.stream().collect(Collectors.toMap(FibonacciKPIConfig::getKpiId, v->v));
        List<ApplicableKPI> applicableKPIS = new ArrayList<>();
                if(UNIT.equals(level)){
                    applicableKPIS = counterRepository.getApplicableKPI(new ArrayList(fibonacciKPIConfigMap.keySet()),UNIT,organizationId);
                }else if(STAFF.equals(level)){
                    AccessGroupPermissionCounterDTO accessGroupPermissionCounterDTO = userIntegrationService.getAccessGroupIdsAndCountryAdmin(UserContext.getUserDetails().getLastSelectedOrganizationId());
                    if (accessGroupPermissionCounterDTO.isCountryAdmin()) {
                        applicableKPIS = counterRepository.getApplicableKPI(new ArrayList(fibonacciKPIConfigMap.keySet()), ConfLevel.COUNTRY, accessGroupPermissionCounterDTO.getCountryId());
                    } else {
                        applicableKPIS = counterRepository.getApplicableKPI(new ArrayList(fibonacciKPIConfigMap.keySet()), ConfLevel.STAFF, accessGroupPermissionCounterDTO.getStaffId());
                    }
                }
        for (ApplicableKPI applicableKPI : applicableKPIS) {
            updatedFibonacciKPIConfigs.add(fibonacciKPIConfigMap.get(applicableKPI.getActiveKpiId()));
        }
        return updatedFibonacciKPIConfigs;
    }

}
