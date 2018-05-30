package com.kairos.activity.service.priority_group;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjuster;
import java.util.*;
import java.util.stream.Collectors;

import com.kairos.activity.client.UserRestClient;
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
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.ShiftCountDTO;
import com.kairos.response.dto.web.StaffDTO;
import com.kairos.response.dto.web.StaffUnitPositionQueryResult;
import com.kairos.response.dto.web.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.response.dto.web.open_shift.priority_group.StaffIncludeFilter;
import com.kairos.response.dto.web.open_shift.priority_group.StaffIncludeFilterDTO;

import javax.inject.Inject;

import static java.util.stream.Collectors.groupingBy;

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

    public Object getAllDataForValidation(PriorityGroupDTO priorityGroupDTO) {


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
        return null;
    }

    public static void main(String[] args) {
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
    }
}
