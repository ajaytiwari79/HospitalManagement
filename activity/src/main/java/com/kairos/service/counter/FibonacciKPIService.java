package com.kairos.service.counter;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.counter.CounterServiceMapping;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.KPIAxisData;
import com.kairos.dto.activity.counter.data.KPIRepresentationData;
import com.kairos.dto.activity.counter.distribution.tab.KPIPosition;
import com.kairos.dto.activity.counter.enums.*;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.counter.FibonacciKPIRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
public class FibonacciKPIService implements CounterService{

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
        if(confLevel.equals(ConfLevel.UNIT) && !userIntegrationService.isExistOrganization(referenceId)){
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
        } else if (ConfLevel.UNIT.equals(confLevel)) {
            applicableKPIs.add(new ApplicableKPI(fibonacciKPI.getId(), fibonacciKPI.getId(), null, referenceId, null, confLevel, new ApplicableFilter(new ArrayList<>(), false), fibonacciKPI.getTitle(), false));
        }
        fibonacciKPIRepository.saveEntities(applicableKPIs);
        return fibonacciKPIDTO;
    }

    public FibonacciKPIDTO updateFibonacciKPI(Long referenceId,FibonacciKPIDTO fibonacciKPIDTO,ConfLevel confLevel){
        boolean existByName = fibonacciKPIRepository.existByName(fibonacciKPIDTO.getId(),fibonacciKPIDTO.getTitle(),confLevel,referenceId);
        if(existByName){
            exceptionService.duplicateDataException("error.kpi.name.duplicate");
        }
        if(confLevel.equals(ConfLevel.COUNTRY) && !userIntegrationService.isCountryExists(referenceId)) {
            exceptionService.dataNotFoundByIdException("message.country.id");
        }
        if(confLevel.equals(ConfLevel.UNIT) && !userIntegrationService.isExistOrganization(referenceId)){
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

    public List<FibonacciKPIDTO> getAllFibonacciKPI(Long referenceId,ConfLevel confLevel){
        List<FibonacciKPIDTO> fibonacciKPIDTOS = fibonacciKPIRepository.findAllFibonacciKPIByCountryId(referenceId,confLevel);
        for (FibonacciKPIDTO fibonacciKPIDTO : fibonacciKPIDTOS) {
            fibonacciKPIDTO.setFibonacciKPI(true);
        }
        return fibonacciKPIDTOS;
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

    public void getFibonacciCalculation(){
        FibonacciKPIDTO fibonacciKPIDTO = fibonacciKPIRepository.getOneByfibonacciId(new BigInteger(""));
        for (KPIDTO kpiCounter : fibonacciKPIDTO.getKpiCounters()) {
            counterServiceMapping.getService(kpiCounter.getType()).getFibonacciCalculatedCounter(null, UserContext.getUserDetails().getLastSelectedOrganizationId());
        }
    }

    @Override
    public CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        return null;
    }

    @Override
    public CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        Map<BigInteger,FibonacciKPIConfig> fibonacciKPIConfigMap = ((FibonacciKPI)kpi).getFibonacciKPIConfigs().stream().collect(Collectors.toMap(fibonacciKPIConfig -> fibonacciKPIConfig.getKpiId(),v->v));
        List<KPI> counters = counterRepository.getKPIsByIds(new ArrayList<>(fibonacciKPIConfigMap.keySet()));
        Map<BigInteger,Map<Long, Number>> countersCalculationMap = new TreeMap<>(Com);
        for (KPI counter : counters) {
            Map<Long, Double> kpiCalculation = counterServiceMapping.getService(counter.getType()).getFibonacciCalculatedCounter(filterBasedCriteria,organizationId);
            countersCalculationMap.put(counter.getId(),kpiCalculation);
        }
        return new KPIRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList, new KPIAxisData(AppConstants.DATE,AppConstants.LABEL),new KPIAxisData(AppConstants.HOURS,AppConstants.VALUE_FIELD));
    }

    @Override
    public Map<Long, Double> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId) {
        return null;
    }
}
