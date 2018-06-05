package com.kairos.activity.service.priority_group;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.persistence.model.open_shift.OpenShiftNotification;
import com.kairos.activity.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.activity.persistence.repository.priority_group.PriorityGroupRepository;
import com.kairos.activity.persistence.repository.time_bank.TimeBankMongoRepository;
import com.kairos.activity.response.dto.priority_group.PriorityGroupRuleDataDTO;
import com.kairos.activity.response.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
import com.kairos.response.dto.web.ShiftCountDTO;
import com.kairos.response.dto.web.StaffUnitPositionQueryResult;
import com.kairos.response.dto.web.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.response.dto.web.open_shift.priority_group.StaffIncludeFilter;
import com.kairos.response.dto.web.open_shift.priority_group.StaffIncludeFilterDTO;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class PriorityGroupRulesDataGetterService {
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private TimeBankMongoRepository timeBankMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private PriorityGroupIntegrationService priorityGroupIntegrationService;
    @Inject
    private PriorityGroupRepository priorityGroupRepository;
    @Inject
    private OpenShiftMongoRepository openShiftMongoRepository;
    @Inject
    private OpenShiftNotificationMongoRepository openShiftNotificationMongoRepository;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;




    public PriorityGroupRuleDataDTO getData(PriorityGroupDTO priorityGroupDTO) {

        List<OpenShift> openShifts = openShiftMongoRepository.findOpenShiftsByUnitIdAndOrderId(priorityGroupDTO.getUnitId(),priorityGroupDTO.getOrderId());
        List<BigInteger> openShiftIds = openShifts.stream().map(OpenShift:: getId).collect(Collectors.toList());
        LocalDate maxDate = openShifts.stream().map(OpenShift::getStartDate).max(LocalDate::compareTo).get();
        LocalDate minDate = openShifts.stream().map(OpenShift::getStartDate).min(LocalDate::compareTo).get();

        List<StaffUnitPositionQueryResult> staffsUnitPositions = getStaffListByStaffIncludefilter(priorityGroupDTO,maxDate);
        List<Long> unitPositionIds = staffsUnitPositions.stream().map(StaffUnitPositionQueryResult::getUnitPositionId).collect(Collectors.toList());

        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankMongoRepository.findAllByUnitPositionsAndBeforDate(unitPositionIds, DateUtils.getISOEndOfWeekDate(maxDate));
        Map<Long, List<DailyTimeBankEntry>> unitPositionDailyTimeBankEntryMap= dailyTimeBankEntries.stream().collect(groupingBy(DailyTimeBankEntry::getUnitPositionId));

        Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap =  new HashMap<BigInteger, List<StaffUnitPositionQueryResult>>();
        Map<BigInteger, OpenShift> openShiftMap = new HashMap<BigInteger, OpenShift>();
        for(OpenShift openShift:openShifts) {
            openShiftStaffMap.put(openShift.getId(),new ArrayList<>(staffsUnitPositions));
            openShiftMap.put(openShift.getId(),openShift);
        }
        calculateTimeBankAndPlannedHours(unitPositionDailyTimeBankEntryMap,openShiftStaffMap,openShiftMap);
        List<ShiftCountDTO> shiftCountDTOS = shiftMongoRepository.getAssignedShiftsCountByUnitPositionId(unitPositionIds, new Date() );
        Map<Long,Integer> assignedOpenShiftMap = shiftCountDTOS.stream().collect(Collectors.toMap(ShiftCountDTO::getUnitPositionId,ShiftCountDTO::getCount));
        List<Shift> shifts = getShifts(priorityGroupDTO,maxDate,minDate,unitPositionIds);

        Map<Long, List<Shift>> shiftUnitPositionMap = shifts.stream().collect(groupingBy(Shift::getUnitPositionId));

        List<OpenShiftNotification> openShiftNotifications = openShiftNotificationMongoRepository.findByOpenShiftIds(openShiftIds);

        /*PriorityGroupRuleDataDTO priorityGroupRuleDataDTO = new PriorityGroupRuleDataDTO(shiftUnitPositionMap,openShiftMap,openShiftStaffMap,shifts,
                openShiftNotifications,assignedOpenShiftMap);*/

        Set<Long> unavailableActivitySet = activityMongoRepository.findAllActivitiesByUnitIdAndUnavailableTimeType(priorityGroupDTO.getUnitId());
        return new PriorityGroupRuleDataDTO(shiftUnitPositionMap,openShiftMap,openShiftStaffMap,shifts,
                openShiftNotifications,assignedOpenShiftMap,unavailableActivitySet);
    }


    /*public Object getAllDataForValidation(PriorityGroupDTO priorityGroupDTO) {


        List<OpenShift> openShifts = openShiftMongoRepository.findOpenShiftsByUnitIdAndOrderId(priorityGroupDTO.getUnitId(),priorityGroupDTO.getOrderId());
        List<BigInteger> openShiftIds = openShifts.stream().map(OpenShift:: getId).collect(Collectors.toList());
        LocalDate maxDate = openShifts.stream().map(OpenShift::getStartDate).max(LocalDate::compareTo).get();
        LocalDate minDate = openShifts.stream().map(OpenShift::getStartDate).min(LocalDate::compareTo).get();

        StaffIncludeFilter staffIncludeFilter = priorityGroupDTO.getStaffIncludeFilter();
        StaffIncludeFilterDTO staffIncludeFilterDTO = new StaffIncludeFilterDTO();
        ObjectMapperUtils.copyProperties(staffIncludeFilter,staffIncludeFilterDTO);
        staffIncludeFilterDTO.setOpenShiftDate(maxDate);
        Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap =  new HashMap<BigInteger, List<StaffUnitPositionQueryResult>>();
        Map<BigInteger, OpenShift> openShiftMap = new HashMap<BigInteger, OpenShift>();
        LocalDate filterShiftStartLocalDate;
        Date filterShiftStartDate;
        Date filterShiftEndDate;



        List<StaffUnitPositionQueryResult> staffsUnitPositions = priorityGroupIntegrationService.getStaffIdsByPriorityGroupIncludeFilter(staffIncludeFilterDTO,priorityGroupDTO.getUnitId());
        List<Long> unitPositionIds = staffsUnitPositions.stream().map(StaffUnitPositionQueryResult::getUnitPositionId).collect(Collectors.toList());

        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankMongoRepository.findAllByUnitPositionsAndBeforDate(unitPositionIds, DateUtils.getISOEndOfWeekDate(maxDate));

        Map<Long, List<DailyTimeBankEntry>> unitPositionDailyTimeBankEntryMap= dailyTimeBankEntries.stream().collect(groupingBy(DailyTimeBankEntry::getUnitPositionId));

        for(OpenShift openShift:openShifts) {
            openShiftStaffMap.put(openShift.getId(),new ArrayList<>(staffsUnitPositions));
            openShiftMap.put(openShift.getId(),openShift);
        }

       List<Long> commonUnitPositionIds =  new ArrayList<Long>(openShiftStaffMap.values().stream().flatMap(
               staffUnitPositionQueryResults->staffUnitPositionQueryResults.stream().map(staffUnitPositionQueryResult->staffUnitPositionQueryResult.getUnitPositionId())).collect(Collectors.toSet()));

        List<ShiftCountDTO> shiftCountDTOS = shiftMongoRepository.getAssignedShiftsCountByUnitPositionId(commonUnitPositionIds, new Date() );
        Map<Long,Integer> assignedOpenShiftMap = shiftCountDTOS.stream().collect(Collectors.toMap(ShiftCountDTO::getUnitPositionId,ShiftCountDTO::getCount));
                Integer lastWorkingDaysWithActivity = priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysWithActivity();
                Integer lastWorkingDaysWithUnit = priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysInUnit();
                if(Optional.ofNullable(lastWorkingDaysWithActivity).isPresent()&&Optional.ofNullable(lastWorkingDaysWithUnit).isPresent()) {
                        filterShiftStartLocalDate = lastWorkingDaysWithActivity>lastWorkingDaysWithUnit?minDate.minusDays(lastWorkingDaysWithActivity):minDate.minusDays(lastWorkingDaysWithUnit);
                }
                else {
                    if(Optional.ofNullable(lastWorkingDaysWithActivity).isPresent()) {
                        filterShiftStartLocalDate = minDate.minusDays(lastWorkingDaysWithActivity);
                    }
                    else if(Optional.ofNullable(lastWorkingDaysWithUnit).isPresent()) {
                        filterShiftStartLocalDate = minDate.minusDays(lastWorkingDaysWithUnit);
                    }
                    else{
                        filterShiftStartLocalDate = minDate;
                    }
                }
        filterShiftStartDate = DateUtils.asDate(filterShiftStartLocalDate);
        filterShiftEndDate =    DateUtils.asDate(maxDate);
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByUnitPositions(commonUnitPositionIds,filterShiftStartDate,filterShiftEndDate);

        Map<Long, List<Shift>> shiftUnitPositionMap = shifts.stream().collect(groupingBy(Shift::getUnitPositionId));

        List<OpenShiftNotification> openShiftNotifications = openShiftNotificationMongoRepository.findByOpenShiftIds(openShiftIds);

        calculateTimeBankAndPlannedHours(unitPositionDailyTimeBankEntryMap,openShiftStaffMap,openShiftMap);



        return null;
    }*/

    public List<StaffUnitPositionQueryResult> getStaffListByStaffIncludefilter(PriorityGroupDTO priorityGroupDTO, LocalDate maxDate ) {

        StaffIncludeFilter staffIncludeFilter = priorityGroupDTO.getStaffIncludeFilter();
        StaffIncludeFilterDTO staffIncludeFilterDTO = new StaffIncludeFilterDTO();
        ObjectMapperUtils.copyProperties(staffIncludeFilter,staffIncludeFilterDTO);
        staffIncludeFilterDTO.setOpenShiftDate(maxDate);
        return priorityGroupIntegrationService.getStaffIdsByPriorityGroupIncludeFilter(staffIncludeFilterDTO,priorityGroupDTO.getUnitId());

    }
    public List<Shift> getShifts(PriorityGroupDTO priorityGroupDTO, LocalDate maxDate, LocalDate minDate,List<Long> commonUnitPositionIds) {

        LocalDate filterShiftStartLocalDate;
        Date filterShiftStartDate;
        Date filterShiftEndDate;
        Integer lastWorkingDaysWithActivity = priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysWithActivity();
        Integer lastWorkingDaysWithUnit = priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysInUnit();
        if(Optional.ofNullable(lastWorkingDaysWithActivity).isPresent()&&Optional.ofNullable(lastWorkingDaysWithUnit).isPresent()) {
            filterShiftStartLocalDate = lastWorkingDaysWithActivity>lastWorkingDaysWithUnit?minDate.minusDays(lastWorkingDaysWithActivity):minDate.minusDays(lastWorkingDaysWithUnit);
        }
        else {
            if(Optional.ofNullable(lastWorkingDaysWithActivity).isPresent()) {
                filterShiftStartLocalDate = minDate.minusDays(lastWorkingDaysWithActivity);
            }
            else if(Optional.ofNullable(lastWorkingDaysWithUnit).isPresent()) {
                filterShiftStartLocalDate = minDate.minusDays(lastWorkingDaysWithUnit);
            }
            else{
                filterShiftStartLocalDate = minDate;
            }
        }
        filterShiftStartDate = DateUtils.asDate(filterShiftStartLocalDate);
        filterShiftEndDate =    DateUtils.asDate(maxDate.plusDays(1));
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByUnitPositions(commonUnitPositionIds,filterShiftStartDate,filterShiftEndDate);

        return shifts;
    }

    public void calculateTimeBankAndPlannedHours(Map<Long, List<DailyTimeBankEntry>> unitPositionDailyTimeBankEntryMap,Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap, Map<BigInteger,OpenShift> openShiftMap) {

        //Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = staffsUnitPositions.iterator();

        int timeBank;
        int deltaTimeBank;
        int plannedHoursWeekly;
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()) {
            Iterator<StaffUnitPositionQueryResult> staffUnitPositionIterator = entry.getValue().iterator();

            while(staffUnitPositionIterator.hasNext()) {

                StaffUnitPositionQueryResult staffUnitPositionQueryResult = staffUnitPositionIterator.next();
                Long endDate = DateUtils.getLongFromLocalDate(openShiftMap.get(entry.getKey()).getStartDate());
                Long endDateDeltaWeek = DateUtils.getISOEndOfWeekDate(openShiftMap.get(entry.getKey()).getStartDate()).getTime();
                Long startDateDeltaWeek = DateUtils.getISOStartOfWeek(openShiftMap.get(entry.getKey()).getStartDate());
                UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = new UnitPositionWithCtaDetailsDTO(staffUnitPositionQueryResult.getUnitPositionId(),
                        staffUnitPositionQueryResult.getContractedMinByWeek(),staffUnitPositionQueryResult.getWorkingDaysPerWeek(),
                        DateUtils.getDateFromEpoch(staffUnitPositionQueryResult.getStartDate()), DateUtils.getDateFromEpoch(staffUnitPositionQueryResult.getEndDate()));
                LocalDate startDatePlanned = DateUtils.getDateFromEpoch(DateUtils.getISOStartOfWeek(openShiftMap.get(entry.getKey()).getStartDate()));
                LocalDate endDatePlanned = DateUtils.asLocalDate(DateUtils.getISOEndOfWeekDate(openShiftMap.get(entry.getKey()).getStartDate()));
                List<DailyTimeBankEntry> dailyTimeBankEntries = unitPositionDailyTimeBankEntryMap.get(staffUnitPositionQueryResult.getUnitPositionId());


                plannedHoursWeekly = dailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> dailyTimeBankEntry.getDate().isAfter(startDatePlanned)||
                        dailyTimeBankEntry.getDate().isEqual(startDatePlanned)&&dailyTimeBankEntry.getDate().isBefore(endDatePlanned)||
                        dailyTimeBankEntry.getDate().isEqual(endDatePlanned)).mapToInt(d->d.getScheduledMin() + d.getTimeBankMinWithCta()).sum();
                timeBank = timeBankCalculationService.calculateTimeBankForInterval(new Interval(staffUnitPositionQueryResult.getStartDate(),endDate),
                        unitPositionWithCtaDetailsDTO,false,unitPositionDailyTimeBankEntryMap.get(staffUnitPositionQueryResult.getUnitPositionId()),false);

                deltaTimeBank =  timeBankCalculationService.calculateTimeBankForInterval(new Interval(startDateDeltaWeek,endDateDeltaWeek),
                        unitPositionWithCtaDetailsDTO,false,unitPositionDailyTimeBankEntryMap.get(staffUnitPositionQueryResult.getUnitPositionId()), false);

                staffUnitPositionQueryResult.setAccumulatedTimeBank(timeBank);
                staffUnitPositionQueryResult.setDeltaWeeklytimeBank(deltaTimeBank);
                staffUnitPositionQueryResult.setPlannedHoursWeek(plannedHoursWeekly);

            }
        }
    }


    /*public static void main(String[] args) {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        LocalDate localdate = LocalDate.now();
        Date date1 = Date.from(localdate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        //DateUtils..with(DayOfWeek.SATURDAY);
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        int i = c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DATE,-i);
        System.out.println(ZonedDateTime.ofInstant(date1.toInstant(),
                ZoneId.systemDefault()).with(DayOfWeek.SATURDAY));
    }*/
}
