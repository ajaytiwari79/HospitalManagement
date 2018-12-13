package com.kairos.service.counter;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.chart.DataUnit;
import com.kairos.dto.activity.counter.data.RawRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.rest_client.RestClientUrlType;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.ApiConstants.STAFF_BY_EMPLOYMENT_TYPE;

@Service
public class PlannedHoursCalculationService implements CounterService {
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private GenericIntegrationService genericIntegrationService;

    private Map<Long,Long> calculatePlannedHour(Set<Long> staffIds, LocalDate startDate, LocalDate endDate ){
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByStaffIdsAndDate(staffIds, DateUtils.asDate(startDate),DateUtils.asDate(endDate));
        return dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getStaffId,Collectors.summingLong(d->d.getTotalTimeBankMin()+d.getContractualMin())));
    }

    private List<DataUnit> getPlannedHours(Long organizationId,Map<FilterType, List> filterBasedCriteria, boolean kpi){
        List<StaffDTO> staffDTOS;
        List<Long> staffIds=new ArrayList<>();
//        List<String> timeType=new ArrayList();
//        List<String> approvalStatus=new ArrayList();
        if(kpi && filterBasedCriteria.get(FilterType.SELECTED_STAFF_IDS)!= null){
            staffIds = getLongValue(filterBasedCriteria.get(FilterType.SELECTED_STAFF_IDS));
        }else if(filterBasedCriteria.get(FilterType.STAFF_IDS) != null){
            staffIds = getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS));
        }
        List<LocalDate> dates = (filterBasedCriteria.get(FilterType.TIME_INTERVAL) !=null) ? filterBasedCriteria.get(FilterType.TIME_INTERVAL): Arrays.asList(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS)!=null) ? getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)):new ArrayList();
         // if(filterBasedCriteria.get(FilterType.TIME_TYPE)!=null) {
//            timeType = filterBasedCriteria.get(FilterType.TIME_TYPE);
//        }
//        if(filterBasedCriteria.get(FilterType.APPROVAL_STATUS)!=null) {
//            approvalStatus = filterBasedCriteria.get(FilterType.APPROVAL_STATUS);
//        }
        List<Long> employmentType = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)!=null) ?getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)): new ArrayList();
        StaffEmploymentTypeDTO staffEmploymentTypeDTO=new StaffEmploymentTypeDTO(staffIds,unitIds,employmentType,organizationId,dates.get(0).toString(),dates.get(1).toString());
        staffDTOS=genericIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        List<DataUnit> dataList = new ArrayList<>();
        if(Optional.ofNullable(staffDTOS).isPresent()) {
            Map<Long, String> staffIdAndNameMap = staffDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getFullName()));
            Map<Long, Long> plannedHoursMap = calculatePlannedHour(staffIdAndNameMap.keySet(), dates.get(0), dates.get(1));
            dataList = plannedHoursMap.entrySet().stream().map(entry->new DataUnit(staffIdAndNameMap.get(entry.getKey()), entry.getKey(), entry.getValue())).collect(Collectors.toList());
        }
        return dataList;
    }

    private List<Long> getLongValue(List<Object> objects){
        return objects.stream().map(o -> ((Integer)o).longValue()).collect(Collectors.toList());
    }

    @Override
    public RawRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<DataUnit> dataList=getPlannedHours(organizationId,filterBasedCriteria,true);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList);
    }

    @Override
    public RawRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<DataUnit> dataList=getPlannedHours(organizationId,filterBasedCriteria,false);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList);
    }
}
