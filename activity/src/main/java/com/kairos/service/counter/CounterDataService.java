package com.kairos.service.counter;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

import com.kairos.counter.CounterServiceMapping;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.FilterCriteriaDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.task_type.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;


@Service
public class CounterDataService {
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

    //FIXME: DO NOT REMOVE will be uncommented once representation model confirmed.
    public List<KPI> getCountersData(Long unitId, BigInteger solverConfigId) {
        ArrayList<KPI> kpiList = new ArrayList<>();
        return kpiList;
    }


    public Map generateKPIData(FilterCriteriaDTO filters, Long organizationId, Long staffId) {
        List<KPI> kpis = counterRepository.getKPIsByIds(filters.getKpiIds());
        Map<BigInteger, KPI> kpiMap = kpis.stream().collect(Collectors.toMap(kpi -> kpi.getId(), kpi -> kpi));
        List<Future<CommonRepresentationData>> kpiResults = new ArrayList<>();
        Map<FilterType, List> filterBasedCriteria = new HashMap<>();
        Map<BigInteger, Map<FilterType, List>> staffKpiFilterCritera = new HashMap<>();
        if (filters.getFilters() != null && isCollectionNotEmpty(filters.getFilters())) {
            filters.getFilters().forEach(filter -> {
                filterBasedCriteria.put(filter.getType(), filter.getValues());
            });
        } else {
            List<ApplicableKPI> staffApplicableKPIS = new ArrayList<>();
            if (filters.isCountryAdmin()) {
                staffApplicableKPIS = counterRepository.getApplicableKPI(kpis.stream().map(kpi -> kpi.getId()).collect(Collectors.toList()), ConfLevel.COUNTRY, filters.getCountryId());
            } else {
                staffApplicableKPIS = counterRepository.getApplicableKPI(kpis.stream().map(kpi -> kpi.getId()).collect(Collectors.toList()), ConfLevel.STAFF, staffId);
            }
            for (ApplicableKPI staffApplicableKPI : staffApplicableKPIS) {
                Map<FilterType, List> staffFilterBasedCriteria = new HashMap<>();
                if (isNotNull(staffApplicableKPI.getApplicableFilter())) {
                    staffApplicableKPI.getApplicableFilter().getCriteriaList().forEach(filterCriteria -> {
                        staffFilterBasedCriteria.put(filterCriteria.getType(), filterCriteria.getValues());
                    });
                    staffKpiFilterCritera.put(staffApplicableKPI.getActiveKpiId(), staffFilterBasedCriteria);
                }
            }
        }
        for (BigInteger kpiId : filters.getKpiIds()) {
            Callable<CommonRepresentationData> data = () -> counterServiceMapping.getService(kpiMap.get(kpiId).getType()).getCalculatedKPI(staffKpiFilterCritera.getOrDefault(kpiId, filterBasedCriteria), organizationId, kpiMap.get(kpiId));
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


        return kpisData.stream().collect(Collectors.toMap(kpiData -> kpiData.getCounterId(), kpiData -> kpiData));
    }

}
