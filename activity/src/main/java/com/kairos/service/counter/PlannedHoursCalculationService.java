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
import com.kairos.rest_client.GenericRestClient;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlannedHoursCalculationService implements CounterService {
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private GenericRestClient genericRestClient;

    private Map<Long,Long> calculatePlannedHour(List<Long> staffIds, LocalDate startDate, LocalDate endDate ){
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByStaffIdsAndDate(staffIds, DateUtils.asDate(startDate),DateUtils.asDate(endDate));
        Map<Long,Long> staffPlannedHours = dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getStaffId,Collectors.summingLong(d->d.getTotalTimeBankMin()+d.getContractualMin())));
        return staffPlannedHours;
    }

    private List<DataUnit> getPlannedHours(Map<FilterType, List> filterBasedCriteria, boolean kpi){
        List<StaffDTO> staffDTOS=new ArrayList<>();
        List<Long> staffIds=new ArrayList<>();
        List<Date> dates=new ArrayList<>();
        List<Long> unit;
        List<String> timeType;
        List<String> approvalStatus;
        List<String> employmentType;
        if(kpi && filterBasedCriteria.get(FilterType.SELECTED_STAFF_IDS)!= null){
            staffIds = filterBasedCriteria.get(FilterType.SELECTED_STAFF_IDS);
        }else if(filterBasedCriteria.get(FilterType.STAFF_IDS) != null){
            staffIds = filterBasedCriteria.get(FilterType.STAFF_IDS);
        }
        if(filterBasedCriteria.get(FilterType.TIME_INTERVAL) !=null) {
            dates = filterBasedCriteria.get(FilterType.TIME_INTERVAL);
        }
        if(filterBasedCriteria.get(FilterType.UNIT_IDS)!=null) {
            unit = filterBasedCriteria.get(FilterType.UNIT_IDS);
        }
        if(filterBasedCriteria.get(FilterType.TIME_TYPE)!=null) {
            timeType = filterBasedCriteria.get(FilterType.TIME_TYPE);
        }
        if(filterBasedCriteria.get(FilterType.APPROVAL_STATUS)!=null) {
            approvalStatus = filterBasedCriteria.get(FilterType.APPROVAL_STATUS);
        }
        if(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)!=null) {
            employmentType = filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE);
            StaffEmploymentTypeDTO staffEmploymentTypeDTO=new StaffEmploymentTypeDTO(filterBasedCriteria.get(FilterType.UNIT_IDS),filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE),filterBasedCriteria.get(FilterType.TIME_INTERVAL).get(0).toString());
            staffDTOS=genericRestClient.publishRequest(staffEmploymentTypeDTO, null, RestClientUrlType.COUNTRY, HttpMethod.GET, "/staff_by_employment_type", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>>(){});
        }
        return new ArrayList<>();
    }

    @Override
    public RawRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long countryId, KPI kpi) {
        List<DataUnit> dataList=getPlannedHours(filterBasedCriteria,true);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList);
    }

    @Override
    public RawRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long countryId, KPI kpi) {
        List<DataUnit> dataList=getPlannedHours(filterBasedCriteria,false);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList);
    }
}
