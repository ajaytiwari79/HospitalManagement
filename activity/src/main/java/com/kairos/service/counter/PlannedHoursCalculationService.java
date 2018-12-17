package com.kairos.service.counter;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.chart.KpiDataUnit;
import com.kairos.dto.activity.counter.data.RawRepresentationData;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlannedHoursCalculationService implements CounterService {
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
//currently not use
//    private Map<Long,Long> calculatePlannedHours(Set<Long> staffIds, LocalDate startDate, LocalDate endDate ){
//        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByStaffIdsAndDate(staffIds, DateUtils.asDate(startDate),DateUtils.asDate(endDate));
//        return dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getStaffId,Collectors.summingLong(d->d.getTotalTimeBankMin()+d.getContractualMin())));
//    }

    private List<KpiDataUnit> getPlannedHoursKpiData(Long organizationId, Map<FilterType, List> filterBasedCriteria, boolean kpi){
        List<Long> staffIds=new ArrayList<>();
        Set<BigInteger> timeTypeIds=new HashSet<>();
        if(kpi && filterBasedCriteria.get(FilterType.SELECTED_STAFF_IDS)!= null){
            staffIds = getLongValue(filterBasedCriteria.get(FilterType.SELECTED_STAFF_IDS));
        }else if(filterBasedCriteria.get(FilterType.STAFF_IDS) != null){
            staffIds = getLongValue(filterBasedCriteria.get(FilterType.STAFF_IDS));
        }
        List<LocalDate> filterDates = (filterBasedCriteria.get(FilterType.TIME_INTERVAL) !=null) ? filterBasedCriteria.get(FilterType.TIME_INTERVAL): Arrays.asList(DateUtils.getStartDateOfWeek(),DateUtils.getEndDateOfWeek());
        List<Long> unitIds = (filterBasedCriteria.get(FilterType.UNIT_IDS)!=null) ? getLongValue(filterBasedCriteria.get(FilterType.UNIT_IDS)):new ArrayList();
        List<String> shiftActivityStatus=(filterBasedCriteria.get(FilterType.APPROVAL_STATUS)!=null)?filterBasedCriteria.get(FilterType.APPROVAL_STATUS):new ArrayList<>();
        List<Long> employmentType = (filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)!=null) ?getLongValue(filterBasedCriteria.get(FilterType.EMPLOYMENT_TYPE)): new ArrayList();
        if(filterBasedCriteria.get(FilterType.TIME_TYPE)!=null) {
              if(filterBasedCriteria.get(FilterType.TIME_TYPE).get(0) instanceof String){
                  timeTypeIds=timeTypeMongoRepository.findActivityIdssByTimeTypeEnum(filterBasedCriteria.get(FilterType.TIME_TYPE));
              }else{
                  timeTypeIds=timeTypeMongoRepository.findActivityIdsByTimeTypeIds(getBigIntegerValue(filterBasedCriteria.get(FilterType.TIME_TYPE)));
                  timeTypeIds.addAll(getBigIntegerValue(filterBasedCriteria.get(FilterType.TIME_TYPE)));
              }
        }
        StaffEmploymentTypeDTO staffEmploymentTypeDTO=new StaffEmploymentTypeDTO(staffIds,unitIds,employmentType,organizationId,filterDates.get(0).toString(),filterDates.get(1).toString());
        List<StaffDTO> staffDTOS=genericIntegrationService.getStaffsByFilter(staffEmploymentTypeDTO);
        Map<Long, String> staffIdAndNameMap = staffDTOS.stream().collect(Collectors.toMap(StaffDTO::getId, StaffDTO::getFullName));
        List<KpiDataUnit> kpiDataUnits=shiftMongoRepository.findShiftsByKpiFilters(staffDTOS.stream().map(staffDTO -> staffDTO.getId()).collect(Collectors.toList()), shiftActivityStatus,timeTypeIds,DateUtils.asDate(filterDates.get(0)),DateUtils.asDate(filterDates.get(1)));
        kpiDataUnits.forEach(kpiData->{
            kpiData.setLabel(staffIdAndNameMap.get(kpiData.getRefId()));
            kpiData.setValue(DateUtils.getHoursFromTotalMinutes(kpiData.getValue()));
        });
        return kpiDataUnits;
    }

    private List<Long> getLongValue(List<Object> objects){
        return objects.stream().map(o -> ((Integer)o).longValue()).collect(Collectors.toList());
    }

    private List<BigInteger> getBigIntegerValue(List<Object> objects){
        return objects.stream().map(o->new BigInteger(((Integer) o).toString())).collect(Collectors.toList());
    }

    @Override
    public RawRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<KpiDataUnit> dataList= getPlannedHoursKpiData(organizationId,filterBasedCriteria,true);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList);
    }

    @Override
    public RawRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi) {
        List<KpiDataUnit> dataList= getPlannedHoursKpiData(organizationId,filterBasedCriteria,false);
        return new RawRepresentationData(kpi.getId(), kpi.getTitle(), kpi.getChart(), DisplayUnit.HOURS, RepresentationUnit.DECIMAL, dataList);
    }
}
