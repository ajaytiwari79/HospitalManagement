package com.kairos.activity.service.time_bank;


import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.TimeBankRestClient;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.activity.persistence.repository.time_bank.TimeBankMongoRepository;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.response.dto.time_bank.CTARuleTemplateDTO;
import com.kairos.activity.response.dto.time_bank.CalculatedTimeBankByDateDTO;
import com.kairos.activity.response.dto.time_bank.TimeBankDTO;
import com.kairos.activity.response.dto.time_bank.TimebankWrapper;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.activity.TimeTypeService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
@Transactional
@Service
public class TimeBankService extends MongoBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TimeBankService.class);

    @Inject
    private TimeBankMongoRepository timeBankMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private TimeBankRestClient timeBankRestClient;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private OrganizationRestClient organizationRestClient;
    @Inject
    private TimeTypeService timeTypeService;

    public void saveTimeBank(Long unitPositionId, Shift shift) {
       /* TimebankWrapper timebankWrapper = getCostTimeAgreement(unitPositionId);
        List<DailyTimeBankEntry> dailyTimeBanks = calculateDailyTimeBank(timebankWrapper,shift,unitPositionId);
        if (!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }*/
    }

    public void saveTimeBanks(Long unitPositionId, List<Shift> shifts) {
        /*TimebankWrapper timebankWrapper = getCostTimeAgreement(unitPositionId);
        List<DailyTimeBankEntry> updatedDailyTimeBankEntries = new ArrayList<>();
        for (Shift shift : shifts) {
            List<DailyTimeBankEntry> dailyTimeBankEntries = calculateDailyTimeBank(timebankWrapper,shift,unitPositionId);
            updatedDailyTimeBankEntries.addAll(dailyTimeBankEntries);
        }
        if(!updatedDailyTimeBankEntries.isEmpty()) {
            save(updatedDailyTimeBankEntries);
        }*/
    }

    private List<DailyTimeBankEntry> calculateDailyTimeBank(TimebankWrapper timebankWrapper,Shift shift,Long unitPositionId){
        timebankWrapper.setStaffId(shift.getStaffId());
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = new DateTime(shift.getEndDate()).withTimeAtStartOfDay();
        List<DailyTimeBankEntry> dailyTimeBanks = timeBankMongoRepository.findAllByUnitPositionAndDate(unitPositionId, startDate.toDate(), endDate.toDate());
        timeBankMongoRepository.deleteAll(dailyTimeBanks);
        dailyTimeBanks = new ArrayList<>();
        Interval interval = new Interval(startDate, startDate.plusDays(1).withTimeAtStartOfDay());
        List<ShiftQueryResultWithActivity> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        DailyTimeBankEntry dailyTimeBank = new DailyTimeBankEntry(timebankWrapper.getUnitPositionId(), timebankWrapper.getStaffId(), timebankWrapper.getWorkingDaysPerWeek(), DateUtils.asLocalDate(interval.getStart().toDate()));
        shifts = filterSubshifts(shifts);
        if (shifts != null && !shifts.isEmpty()) {
            timeBankCalculationService.getTimeBankByInterval(timebankWrapper, interval, shifts, dailyTimeBank);
            dailyTimeBanks.add(dailyTimeBank);
        }
        if (!startDate.toLocalDate().equals(endDate.toLocalDate())) {
            interval = new Interval(endDate, endDate.plusDays(1).withTimeAtStartOfDay());
            shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
            shifts = filterSubshifts(shifts);
            if (shifts != null && !shifts.isEmpty()) {
                timeBankCalculationService.getTimeBankByInterval(timebankWrapper, interval, shifts, dailyTimeBank);
                dailyTimeBanks.add(dailyTimeBank);
            }
        }
        return dailyTimeBanks;
    }


    public List<ShiftQueryResultWithActivity> filterSubshifts(List<ShiftQueryResultWithActivity> shifts){
        List<ShiftQueryResultWithActivity> shiftQueryResultWithActivities = new ArrayList<>(shifts.size());
        for (ShiftQueryResultWithActivity shift : shifts) {
            if (shift.getSubShift() != null && shift.getSubShift().getStartDate() != null) {
                ShiftQueryResultWithActivity shiftQueryResultWithActivity = new ShiftQueryResultWithActivity(shift.getStartDate(), shift.getEndDate(), shift.getActivity());
                shiftQueryResultWithActivities.add(shiftQueryResultWithActivity);
            } else {
                shiftQueryResultWithActivities.add(shift);
            }
        }
        return shiftQueryResultWithActivities;
    }


    @Deprecated
    public Boolean createBlankTimeBank(TimebankWrapper timebankWrapper) {
        DateTime startDate = DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionStartDate()).withTimeAtStartOfDay();
        startDate = startDate.plusDays(1);//Todo this should be removed
        DateTime endDate = startDate.plusYears(3);
        List<DailyTimeBankEntry> newDailyTimeBankEntries = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            DailyTimeBankEntry dailyTimeBankEntry = new DailyTimeBankEntry(timebankWrapper.getUnitPositionId(), timebankWrapper.getStaffId(), timebankWrapper.getWorkingDaysPerWeek(), DateUtils.toLocalDate(startDate));
            int contractualMin = startDate.getDayOfWeek() <= timebankWrapper.getWorkingDaysPerWeek() ? timebankWrapper.getContractedMinByWeek() / timebankWrapper.getWorkingDaysPerWeek() : 0;
            dailyTimeBankEntry.setTotalTimeBankMin(-contractualMin);
            dailyTimeBankEntry.setContractualMin(contractualMin);
            dailyTimeBankEntry.setTimeBankCTADistributionList(timeBankCalculationService.getDistribution(timebankWrapper));
            newDailyTimeBankEntries.add(dailyTimeBankEntry);
            startDate = startDate.plusDays(1);
        }
        if (!newDailyTimeBankEntries.isEmpty()) {
            save(newDailyTimeBankEntries);
        }
        return null;
    }

    public Boolean updateBlankTimebank(TimebankWrapper timebankWrapper) {
        DateTime startDate = DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionStartDate()).withTimeAtStartOfDay();
        DateTime endDate = startDate.plusYears(3);
        List<DailyTimeBankEntry> newDailyTimeBankEntries = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            DailyTimeBankEntry dailyTimeBankEntry = new DailyTimeBankEntry(timebankWrapper.getUnitPositionId(), timebankWrapper.getStaffId(), timebankWrapper.getWorkingDaysPerWeek(), DateUtils.toLocalDate(startDate));
            int contractualMin = startDate.getDayOfWeek() <= timebankWrapper.getWorkingDaysPerWeek() ? timebankWrapper.getContractedMinByWeek() / timebankWrapper.getWorkingDaysPerWeek() : 0;
            dailyTimeBankEntry.setTotalTimeBankMin(-contractualMin);
            dailyTimeBankEntry.setContractualMin(contractualMin);
            dailyTimeBankEntry.setTimeBankCTADistributionList(timeBankCalculationService.getDistribution(timebankWrapper));
            newDailyTimeBankEntries.add(dailyTimeBankEntry);
            startDate = startDate.plusDays(1);
        }
        if (!newDailyTimeBankEntries.isEmpty()) {
            save(newDailyTimeBankEntries);
        }
        return null;
    }


    public Map<Long, List<ShiftQueryResultWithActivity>> getShiftsMapByUEPs(List<Long> unitPositionIds, List<ShiftQueryResultWithActivity> shiftQueryResultWithActivities) {
        Map<Long, List<ShiftQueryResultWithActivity>> shiftsMap = new HashMap<>(unitPositionIds.size());
        unitPositionIds.forEach(uEPId -> {
            shiftsMap.put(uEPId, getShiftsByUEP(uEPId, shiftQueryResultWithActivities));
        });
        return shiftsMap;
    }


    public List<ShiftQueryResultWithActivity> getShiftsByUEP(Long unitPositionId, List<ShiftQueryResultWithActivity> shiftQueryResultWithActivities) {
        List<ShiftQueryResultWithActivity> shifts = new ArrayList<>();
        shiftQueryResultWithActivities.forEach(s -> {
            if (s.getUnitEmploymentPositionId().equals(unitPositionId)) {
                shifts.add(s);
            }
        });
        return shifts;
    }


    public List<CalculatedTimeBankByDateDTO> getTimeBankFromCurrentDateByUEP(Long unitPositonId) {
        TimebankWrapper timebankWrapper = getCostTimeAgreement(145l);//timeBankRestClient.getCTAbyUnitEmployementPosition(unitEmploymentPositonId);
        List<ShiftQueryResultWithActivity> shifts = null;//shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitEmploymentPositonId,new Date(),new Date());
        return timeBankCalculationService.getTimeBankByDates(timebankWrapper, shifts, 365);
    }

    public TimebankWrapper getCostTimeAgreement(Long unitPositionId) {
        TimebankWrapper timebankWrapper = timeBankRestClient.getCTAbyUnitEmployementPosition(unitPositionId);
        for (CTARuleTemplateDTO ctaRuleTemplateDTO : timebankWrapper.getCtaRuleTemplates()) {
            if (ctaRuleTemplateDTO.getTimeTypeId() != null) {
                List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllParentTimeTypeByTimeTypeId(ctaRuleTemplateDTO.getTimeTypeId(), timebankWrapper.getCountryId());
                ctaRuleTemplateDTO.setTimeTypeIdsWithParentTimeType(timeTypeDTOS.stream().map(tt -> tt.getId()).collect(Collectors.toList()));
            }
            ctaRuleTemplateDTO.setDays(Arrays.asList(1, 2, 3, 4, 5, 6, 7));//Todo it should be removed when cta daytype implemented
        }
        return timebankWrapper;
    }


    public TimeBankDTO getAdvanceViewTimeBank(Long unitId, Long unitPositionId, String query, Date startDate, Date endDate) {
        TimeBankDTO timeBankDTO = null;
        List<DailyTimeBankEntry> dailyTimeBanks = timeBankMongoRepository.findAllByUnitPositionAndDate(unitPositionId, startDate, endDate);//Arrays.asList(getMockTimeBank());
        List<ShiftQueryResultWithActivity> shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, startDate, endDate);
        shiftQueryResultWithActivities = filterSubshifts(shiftQueryResultWithActivities);
        TimebankWrapper timebankWrapper = getCostTimeAgreement(unitPositionId);
        int totalTimeBankBeforeStartDate = 0;
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeTypeByCountryId(timebankWrapper.getCountryId());
        if(new DateTime(startDate).isAfter(DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionStartDate()))){
            Interval interval = new Interval(DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionStartDate()),new DateTime(startDate));
            //totaltimebank is timebank without daily timebank entries
            int totalTimeBank = timeBankCalculationService.calculateTimeBankForInterval(interval,timebankWrapper);
            List<DailyTimeBankEntry> dailyTimeBanksBeforeStartDate = timeBankMongoRepository.findAllByUnitPositionAndBeforeDate(unitPositionId, new DateTime(startDate).toDate());
            totalTimeBankBeforeStartDate = dailyTimeBanksBeforeStartDate != null && !dailyTimeBanksBeforeStartDate.isEmpty()
                    ? dailyTimeBanksBeforeStartDate.stream().mapToInt(dailyTimeBank -> dailyTimeBank.getTotalTimeBankMin()).sum() : 0;
            totalTimeBankBeforeStartDate = totalTimeBankBeforeStartDate - totalTimeBank;
        }
        timeBankDTO = timeBankCalculationService.getAdvanceViewTimeBank(totalTimeBankBeforeStartDate, startDate, endDate, query, shiftQueryResultWithActivities, dailyTimeBanks, timebankWrapper, timeTypeDTOS);
        //timeBankDTO1.setCostTimeAgreement(getCostTimeAgreement(145l));
        return timeBankDTO;
    }


    public TimeBankDTO getOverviewTimeBank(Long unitPositionId, Integer year) {
        TimebankWrapper timebankWrapper = getCostTimeAgreement(unitPositionId);
        Interval interval = getIntervalByDateForOverviewTimeBank(timebankWrapper, year);
        List<DailyTimeBankEntry> dailyTimeBankEntries = new ArrayList<>();
        if (interval.getStart().getYear() <= new DateTime().getYear()) {
            dailyTimeBankEntries = timeBankMongoRepository.findAllByUnitPositionAndDate(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        }
        return timeBankCalculationService.getOverviewTimeBank(unitPositionId, interval.getStart().dayOfYear().withMinimumValue(), interval.getEnd().dayOfYear().withMaximumValue(), dailyTimeBankEntries,timebankWrapper);
    }

    private Interval getIntervalByDateForOverviewTimeBank(TimebankWrapper timebankWrapper, Integer year) {

        DateTime startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDate = new DateTime().withYear(year).dayOfYear().withMaximumValue().withTimeAtStartOfDay();
        if (startDate.getYear() == DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionStartDate()).getYear() && startDate.isBefore(DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionStartDate()).withTimeAtStartOfDay())) {
            startDate = DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionStartDate()).withTimeAtStartOfDay();
        }
        if (startDate.getYear() != DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionStartDate()).getYear() && startDate.isAfter(DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionStartDate()))) {
            startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        }
        if (endDate.isAfter(new DateTime().plusDays(1).withTimeAtStartOfDay()) && endDate.getYear() == new DateTime().getYear()) {
            endDate = new DateTime().withTimeAtStartOfDay();
        }
        //endDate = endDate.plusMonths(5);//todo this should be removed
        return new Interval(startDate, endDate);
    }


    public boolean saveTimeBank() {
        Shift shift = new Shift();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        DateTime dt = formatter.parseDateTime("20/01/2018 03:41");
        shift.setStartDate(dt.toDate());
        shift.setEndDate(dt.plusHours(10).toDate());
        saveTimeBank(145l, shift);
        return true;

    }

}
