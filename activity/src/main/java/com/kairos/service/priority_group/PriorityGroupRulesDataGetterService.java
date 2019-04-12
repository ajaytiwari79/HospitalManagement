package com.kairos.service.priority_group;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilter;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import com.kairos.dto.activity.shift.ShiftCountDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentQueryResult;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.open_shift.OpenShiftNotification;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.persistence.repository.priority_group.PriorityGroupRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.wrapper.priority_group.PriorityGroupRuleDataDTO;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class PriorityGroupRulesDataGetterService {
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
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
        LocalDate maxDate = DateUtils.asLocalDate(openShifts.stream().map(OpenShift::getEndDate).max(Date::compareTo).get());
        LocalDate minDate = DateUtils.asLocalDate(openShifts.stream().map(OpenShift::getStartDate).min(Date::compareTo).get());
        Long maxDateLong = DateUtils.getLongFromLocalDate(maxDate);

        List<StaffEmploymentQueryResult> staffsEmployments = getStaffListByStaffIncludefilter(priorityGroupDTO,maxDateLong);
        List<Long> employmentIds = staffsEmployments.stream().map(s -> s.getEmploymentId()).collect(Collectors.toList());
        List<Long> staffIds = staffsEmployments.stream().map(s -> s.getStaffId()).collect(Collectors.toList());

        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentIdsAndBeforDate(employmentIds, DateUtils.getISOEndOfWeekDate(maxDate));
        Map<Long, List<DailyTimeBankEntry>> employmentDailyTimeBankEntryMap= dailyTimeBankEntries.stream().collect(groupingBy(DailyTimeBankEntry::getEmploymentId));

        Map<BigInteger, List<StaffEmploymentQueryResult>> openShiftStaffMap =  new HashMap<BigInteger, List<StaffEmploymentQueryResult>>();
        Map<BigInteger, OpenShift> openShiftMap = new HashMap<BigInteger, OpenShift>();
        for(OpenShift openShift:openShifts) {

            List<StaffEmploymentQueryResult> staffEmploymentQueryResults = new ArrayList<>(staffsEmployments);
            staffEmploymentQueryResults.stream().filter(staffEmploymentQueryResult -> staffEmploymentQueryResult.getStartDate()<openShift.getStartDate().getTime());
            openShiftStaffMap.put(openShift.getId(), staffEmploymentQueryResults);
            openShiftMap.put(openShift.getId(),openShift);
        }
        calculateTimeBankAndPlannedHours(priorityGroupDTO.getUnitId(),employmentDailyTimeBankEntryMap,openShiftStaffMap,openShiftMap);
        List<ShiftCountDTO> shiftCountDTOS = shiftMongoRepository.getAssignedShiftsCountByEmploymentId(employmentIds, new Date() );
        Map<Long,Integer> assignedOpenShiftMap = shiftCountDTOS.stream().collect(Collectors.toMap(ShiftCountDTO::getEmploymentId,ShiftCountDTO::getCount));
        List<Shift> shifts = getShifts(priorityGroupDTO,maxDate,minDate,employmentIds);

        Map<Long, List<Shift>> employmentAndShiftsMap = shifts.stream().collect(groupingBy(Shift::getEmploymentId));

        List<OpenShiftNotification> openShiftNotifications = openShiftNotificationMongoRepository.findByOpenShiftIds(staffIds);
        Set<BigInteger> unavailableActivitySet = activityMongoRepository.findAllActivitiesByUnitIdAndUnavailableTimeType(priorityGroupDTO.getUnitId());
        return new PriorityGroupRuleDataDTO(employmentAndShiftsMap,openShiftMap,openShiftStaffMap,shifts,
                openShiftNotifications,assignedOpenShiftMap,unavailableActivitySet);
    }


    public List<StaffEmploymentQueryResult> getStaffListByStaffIncludefilter(PriorityGroupDTO priorityGroupDTO, Long maxDateLong ) {

        StaffIncludeFilter staffIncludeFilter = priorityGroupDTO.getStaffIncludeFilter();
        StaffIncludeFilterDTO staffIncludeFilterDTO = new StaffIncludeFilterDTO();
        ObjectMapperUtils.copyProperties(staffIncludeFilter,staffIncludeFilterDTO);
        staffIncludeFilterDTO.setMaxOpenShiftDate(maxDateLong);
        staffIncludeFilterDTO.setExpertiseIds(priorityGroupDTO.getExpertiseIds());
        staffIncludeFilterDTO.setEmploymentTypeIds(priorityGroupDTO.getEmploymentTypeIds());
        return userIntegrationService.getStaffIdsByPriorityGroupIncludeFilter(staffIncludeFilterDTO,priorityGroupDTO.getUnitId());

    }
    public List<Shift> getShifts(PriorityGroupDTO priorityGroupDTO, LocalDate maxDate, LocalDate minDate,List<Long> commonEmploymentIds) {

        LocalDate filterShiftStartLocalDate;
        Date filterShiftStartDate;
        Date filterShiftEndDate;
        Integer lastWorkingDaysWithActivity = priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysWithActivity();
        Integer lastWorkingDaysWithUnit = priorityGroupDTO.getStaffExcludeFilter().getLastWorkingDaysInUnit();
        if (Optional.ofNullable(lastWorkingDaysWithActivity).isPresent() && Optional.ofNullable(lastWorkingDaysWithUnit).isPresent()) {
            filterShiftStartLocalDate = lastWorkingDaysWithActivity > lastWorkingDaysWithUnit ? minDate.minusDays(lastWorkingDaysWithActivity) : minDate.minusDays(lastWorkingDaysWithUnit);
        } else {
            if (Optional.ofNullable(lastWorkingDaysWithActivity).isPresent()) {
                filterShiftStartLocalDate = minDate.minusDays(lastWorkingDaysWithActivity);
            } else if (Optional.ofNullable(lastWorkingDaysWithUnit).isPresent()) {
                filterShiftStartLocalDate = minDate.minusDays(lastWorkingDaysWithUnit);
            } else {
                filterShiftStartLocalDate = minDate;
            }
        }
        filterShiftStartDate = DateUtils.getDateFromLocalDate(filterShiftStartLocalDate);
        filterShiftEndDate = DateUtils.getDateFromLocalDate(maxDate.plusDays(1));
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByEmploymentIds(commonEmploymentIds, filterShiftStartDate, filterShiftEndDate);

        return shifts;
    }
//flagged to be changed after accumulatedtimebank has been
    public void calculateTimeBankAndPlannedHours(Long unitId, Map<Long, List<DailyTimeBankEntry>> employmentDailyTimeBankEntryMap, Map<BigInteger, List<StaffEmploymentQueryResult>> openShiftStaffMap, Map<BigInteger,OpenShift> openShiftMap) {



        for(Map.Entry<BigInteger,List<StaffEmploymentQueryResult>> entry: openShiftStaffMap.entrySet()) {
            Iterator<StaffEmploymentQueryResult> staffEmploymentIterator = entry.getValue().iterator();
            int timeBank;
            int deltaTimeBank;
            int plannedHoursWeekly;
            while(staffEmploymentIterator.hasNext()) {

                StaffEmploymentQueryResult staffEmploymentQueryResult = staffEmploymentIterator.next();
                LocalDate openShiftDate = DateUtils.asLocalDate(openShiftMap.get(entry.getKey()).getStartDate());
                Long endDate = DateUtils.getLongFromLocalDate(openShiftDate);
                Long endDateDeltaWeek = DateUtils.getISOEndOfWeekDate(openShiftDate).getTime();
                Long startDateDeltaWeek = DateUtils.getISOStartOfWeek(openShiftDate);
                //Todo Yatharth please check it and change for totalWeekly hour
                EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = new EmploymentWithCtaDetailsDTO(staffEmploymentQueryResult.getEmploymentId(),
                        Optional.ofNullable(staffEmploymentQueryResult.getContractedMinByWeek()).isPresent()? staffEmploymentQueryResult.getContractedMinByWeek():0,
                        Optional.ofNullable(staffEmploymentQueryResult.getWorkingDaysPerWeek()).isPresent()? staffEmploymentQueryResult.getWorkingDaysPerWeek():0,
                        DateUtils.getDateFromEpoch(staffEmploymentQueryResult.getStartDate()), DateUtils.getDateFromEpoch(staffEmploymentQueryResult.getEndDate()), staffEmploymentQueryResult.getTotalWeeklyHours());
                LocalDate startDatePlanned = DateUtils.getDateFromEpoch(DateUtils.getISOStartOfWeek(openShiftDate));
                LocalDate endDatePlanned = DateUtils.asLocalDate(DateUtils.getISOEndOfWeekDate(openShiftDate));
                List<DailyTimeBankEntry> dailyTimeBankEntries = employmentDailyTimeBankEntryMap.get(staffEmploymentQueryResult.getEmploymentId());

                dailyTimeBankEntries = Optional.ofNullable(dailyTimeBankEntries).orElse(new ArrayList<>());

                plannedHoursWeekly = dailyTimeBankEntries.stream().filter(dailyTimeBankEntry -> dailyTimeBankEntry.getDate().isAfter(startDatePlanned)||
                        dailyTimeBankEntry.getDate().isEqual(startDatePlanned)&&dailyTimeBankEntry.getDate().isBefore(endDatePlanned)||
               dailyTimeBankEntry.getDate().isEqual(endDatePlanned)).mapToInt(d->d.getScheduledMinutesOfTimeBank() + d.getCtaBonusMinutesOfTimeBank()).sum();
                Set<DateTimeInterval> planningPeriodIntervals = timeBankCalculationService.getPlanningPeriodIntervals(unitId,DateUtils.getDateFromLocalDate(DateUtils.getDateFromEpoch(staffEmploymentQueryResult.getStartDate())),DateUtils.getDate(endDate));
                timeBank = -1* timeBankCalculationService.calculateTimeBankForInterval(planningPeriodIntervals,new Interval(DateUtils.getDateFromLocalDate(DateUtils.getDateFromEpoch(staffEmploymentQueryResult.getStartDate())).getTime(),endDate),
                        employmentWithCtaDetailsDTO,false,dailyTimeBankEntries,false);
                planningPeriodIntervals = timeBankCalculationService.getPlanningPeriodIntervals(unitId,DateUtils.getDate(startDateDeltaWeek),DateUtils.getDate(endDateDeltaWeek));
                deltaTimeBank =  -1 * timeBankCalculationService.calculateTimeBankForInterval(planningPeriodIntervals,new Interval(startDateDeltaWeek,endDateDeltaWeek),
                        employmentWithCtaDetailsDTO,false,dailyTimeBankEntries, false);
                staffEmploymentQueryResult.setAccumulatedTimeBank(timeBank);
                staffEmploymentQueryResult.setDeltaWeeklytimeBank(deltaTimeBank);
                staffEmploymentQueryResult.setPlannedHoursWeek(plannedHoursWeekly);

            }

        }
    }



}
