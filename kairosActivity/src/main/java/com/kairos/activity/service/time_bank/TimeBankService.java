package com.kairos.activity.service.time_bank;


import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.TimeBankRestClient;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.time_bank.DailyTimeBank;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepositoryImpl;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.activity.response.dto.time_bank.*;
import com.kairos.activity.response.dto.time_bank.TimebankWrapper;
import com.kairos.activity.persistence.repository.time_bank.TimeBankMongoRepository;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
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

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */

@Service
public class TimeBankService extends MongoBaseService {

    private static final Logger logger = LoggerFactory.getLogger(TimeBankService.class);

    @Inject
    private TimeBankMongoRepository timeBankMongoRepository;
    @Inject
    private ShiftMongoRepositoryImpl shiftMongoRepository;
    @Inject
    private TimeBankRestClient timeBankRestClient;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private OrganizationRestClient organizationRestClient;
    @Inject private TimeTypeService timeTypeService;

    public void saveTimeBank(Long unitPositionId, Shift shift) {
        TimebankWrapper timebankWrapper = getCostTimeAgreement(unitPositionId, shift.getUnitId());
        timebankWrapper.setStaffId(shift.getStaffId());
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = new DateTime(shift.getEndDate()).withTimeAtStartOfDay();
        List<DailyTimeBank> dailyTimeBanks = timeBankMongoRepository.findAllByUnitPositionAndDate(unitPositionId, startDate.toDate(), endDate.toDate());
        if (dailyTimeBanks != null && !dailyTimeBanks.isEmpty()) {
            for (DailyTimeBank dailyTimeBank : dailyTimeBanks) {
                Interval interval = new Interval(DateUtils.toJodaDateTime(dailyTimeBank.getDate()).withTimeAtStartOfDay(), DateUtils.toJodaDateTime(dailyTimeBank.getDate()).plusDays(1).withTimeAtStartOfDay());
                List<ShiftQueryResultWithActivity> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
                shifts = filterSubshifts(shifts);
                DailyTimeBank newtimeBankDaily = timeBankCalculationService.getTimeBankByInterval(timebankWrapper, interval, shifts, dailyTimeBank);
                save(newtimeBankDaily);
            }
        }/* else {
            dailyTimeBanks = new ArrayList<>();
            Interval interval = new Interval(startDate, startDate.plusDays(1).withTimeAtStartOfDay());
            List<ShiftQueryResultWithActivity> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitEmploymentPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
            DailyTimeBank dailyTimeBank = new DailyTimeBank(timebankWrapper.getUnitPositionId(), timebankWrapper.getStaffId(), timebankWrapper.getWorkingDaysPerWeek(), DateUtils.asLocalDate(interval.getStart().toDate()));
            dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(timebankWrapper, interval, shifts, dailyTimeBank);
            dailyTimeBanks.add(dailyTimeBank);
            if (!startDate.toLocalDate().equals(endDate.toLocalDate())) {
                interval = new Interval(endDate, endDate.plusDays(1).withTimeAtStartOfDay());
                shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitEmploymentPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
                DailyTimeBank newtimeBankDaily = new DailyTimeBank(timebankWrapper.getUnitPositionId(), timebankWrapper.getStaffId(), timebankWrapper.getWorkingDaysPerWeek(), DateUtils.asLocalDate(interval.getStart().toDate()));
                newtimeBankDaily = timeBankCalculationService.getTimeBankByInterval(timebankWrapper, interval, shifts, newtimeBankDaily);
                dailyTimeBanks.add(newtimeBankDaily);
            }
        }*/
        /*if(!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }*/
        // createBlankTimeBank(timebankWrapper);
    }


    public void saveTimeBank(Long unitPositionId, List<Shift> shifts,Long unitId) {
        TimebankWrapper timebankWrapper = getCostTimeAgreement(unitPositionId, unitId);
        List<DailyTimeBank> updatedDailyTimeBanks = new ArrayList<>();
        for (Shift shift : shifts) {
            timebankWrapper.setStaffId(shift.getStaffId());
            DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
            DateTime endDate = new DateTime(shift.getEndDate()).withTimeAtStartOfDay();
            List<DailyTimeBank> dailyTimeBanks = timeBankMongoRepository.findAllByUnitPositionAndDate(unitPositionId, startDate.toDate(), endDate.toDate());
            if (dailyTimeBanks != null && !dailyTimeBanks.isEmpty()) {
                for (DailyTimeBank dailyTimeBank : dailyTimeBanks) {
                    Interval interval = new Interval(DateUtils.toJodaDateTime(dailyTimeBank.getDate()).withTimeAtStartOfDay(), DateUtils.toJodaDateTime(dailyTimeBank.getDate()).plusDays(1).withTimeAtStartOfDay());
                    List<ShiftQueryResultWithActivity> shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
                    shiftQueryResultWithActivities = filterSubshifts(shiftQueryResultWithActivities);
                    DailyTimeBank newtimeBankDaily = timeBankCalculationService.getTimeBankByInterval(timebankWrapper, interval, shiftQueryResultWithActivities, dailyTimeBank);
                    updatedDailyTimeBanks.add(newtimeBankDaily);
                }
            }
        }
        save(updatedDailyTimeBanks);
    }


    public List<ShiftQueryResultWithActivity> filterSubshifts(List<ShiftQueryResultWithActivity> shifts){
        List<ShiftQueryResultWithActivity> shiftQueryResultWithActivities = new ArrayList<>(shifts.size());
        for (ShiftQueryResultWithActivity shift : shifts) {
            if(shift.getSubShift()!=null && shift.getSubShift().getStartDate()!=null){
                ShiftQueryResultWithActivity shiftQueryResultWithActivity = new ShiftQueryResultWithActivity(shift.getStartDate(),shift.getEndDate(),shift.getActivity());
                shiftQueryResultWithActivities.add(shiftQueryResultWithActivity);
            }
            else {
                shiftQueryResultWithActivities.add(shift);
            }
        }
        return shiftQueryResultWithActivities;
    }

    public Boolean createBlankTimeBank(TimebankWrapper timebankWrapper) {
        DateTime startDate = DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionDate()).withTimeAtStartOfDay();
        startDate = startDate.plusDays(1);//Todo this should be removed
        DateTime endDate = startDate.plusYears(3);
        List<DailyTimeBank> newDailyTimeBanks = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            DailyTimeBank dailyTimeBank = new DailyTimeBank(timebankWrapper.getUnitPositionId(), timebankWrapper.getStaffId(), timebankWrapper.getWorkingDaysPerWeek(), DateUtils.toLocalDate(startDate));
            int contractualMin = startDate.getDayOfWeek() <= timebankWrapper.getWorkingDaysPerWeek() ? timebankWrapper.getContractedMinByWeek() / timebankWrapper.getWorkingDaysPerWeek() : 0;
            dailyTimeBank.setTotalTimeBankMin(-contractualMin);
            dailyTimeBank.setContractualMin(contractualMin);
            dailyTimeBank.setTimeBankDistributionList(timeBankCalculationService.getDistribution(timebankWrapper));
            newDailyTimeBanks.add(dailyTimeBank);
            startDate = startDate.plusDays(1);
        }
        if (!newDailyTimeBanks.isEmpty()) {
            save(newDailyTimeBanks);
        }
        return null;
    }

    public Boolean updateBlankTimebank(TimebankWrapper timebankWrapper) {
        DateTime startDate = DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionDate()).withTimeAtStartOfDay();
        DateTime endDate = startDate.plusYears(3);
        List<DailyTimeBank> newDailyTimeBanks = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            DailyTimeBank dailyTimeBank = new DailyTimeBank(timebankWrapper.getUnitPositionId(), timebankWrapper.getStaffId(), timebankWrapper.getWorkingDaysPerWeek(), DateUtils.toLocalDate(startDate));
            int contractualMin = startDate.getDayOfWeek() <= timebankWrapper.getWorkingDaysPerWeek() ? timebankWrapper.getContractedMinByWeek() / timebankWrapper.getWorkingDaysPerWeek() : 0;
            dailyTimeBank.setTotalTimeBankMin(-contractualMin);
            dailyTimeBank.setContractualMin(contractualMin);
            dailyTimeBank.setTimeBankDistributionList(timeBankCalculationService.getDistribution(timebankWrapper));
            newDailyTimeBanks.add(dailyTimeBank);
            startDate = startDate.plusDays(1);
        }
        if (!newDailyTimeBanks.isEmpty()) {
            save(newDailyTimeBanks);
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
        TimebankWrapper timebankWrapper = getCostTimeAgreement(145l, null);//timeBankRestClient.getCTAbyUnitEmployementPosition(unitEmploymentPositonId);
        List<ShiftQueryResultWithActivity> shifts = null;//shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitEmploymentPositonId,new Date(),new Date());
        return timeBankCalculationService.getTimeBankByDates(timebankWrapper, shifts, 365);
    }

    public TimebankWrapper getCostTimeAgreement(Long unitPositionId, Long unitId) {
        //todo please uncomment this
         TimebankWrapper timebankWrapper = timeBankRestClient.getCTAbyUnitEmployementPosition(unitPositionId);
        for (CTARuleTemplateDTO ctaRuleTemplateDTO : timebankWrapper.getCtaRuleTemplates()) {
            if(ctaRuleTemplateDTO.getTimeTypeId()!=null){
                List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllParentTimeTypeByTimeTypeId(ctaRuleTemplateDTO.getTimeTypeId(), timebankWrapper.getCountryId());
                ctaRuleTemplateDTO.setTimeTypeIdsWithParentTimeType(timeTypeDTOS.stream().map(tt->tt.getId()).collect(Collectors.toList()));
            }
            ctaRuleTemplateDTO.setDays(Arrays.asList(1, 2, 3, 4, 5, 6, 7));//Todo it should be removed when cta daytype implemented
        }
        /*TimebankWrapper timebankWrapper = new TimebankWrapper(unitPositionId);
        timebankWrapper.setStaffId(134l);
        timebankWrapper.setUnitPositionDate(new DateTime().toDate());
        timebankWrapper.setContractedMinByWeek(2100);
        timebankWrapper.setWorkingDaysPerWeek(7);
        timebankWrapper.setCtaRuleTemplates(getCta(unitId));
        timebankWrapper.setCountryId(4l);*/
        return timebankWrapper;
    }


    private List<CTARuleTemplateDTO> getCta(Long unitId) {
        List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = new ArrayList<>(6);
        List<ActivityDTO> activity = activityMongoRepository.findAllActivityByUnitId(unitId);
        List<BigInteger> activityIds = activity.stream().map(a -> a.getId()).collect(Collectors.toList());
//        List<BigInteger> activityIds = Arrays.asList(new BigInteger("1927"), new BigInteger("1928"),new BigInteger("1929"),new BigInteger("1930"));
        ctaRuleTemplateDTOS.add(getWorkingEveningShift(activityIds));
        ctaRuleTemplateDTOS.add(getWorkingNightShift(activityIds));
        ctaRuleTemplateDTOS.add(getWorkingOnHalfPublicHoliday(activityIds));
        ctaRuleTemplateDTOS.add(getWorkingOnPublicHoliday(activityIds));
        ctaRuleTemplateDTOS.add(getWorkingOnSaturday(activityIds));
        ctaRuleTemplateDTOS.add(getWorkingOnSunday(activityIds));
        return ctaRuleTemplateDTOS;
    }

    private CTARuleTemplateDTO getWorkingEveningShift(List<BigInteger> activities) {
        CTARuleTemplateDTO workingEveningShift = new CTARuleTemplateDTO(12l, "Working Evening", 10, new BigInteger("12"));
        workingEveningShift.setCtaIntervalDTOS(Arrays.asList(new CTAIntervalDTO("MINUTES", 10, 1020, 1380)));
        workingEveningShift.setActivityIds(activities);
        workingEveningShift.setDays(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        return workingEveningShift;
    }

    private CTARuleTemplateDTO getWorkingNightShift(List<BigInteger> activities) {
        CTARuleTemplateDTO workingNightShift = new CTARuleTemplateDTO(13l, "working night", 60, new BigInteger("12"));
        workingNightShift.setCtaIntervalDTOS(Arrays.asList(new CTAIntervalDTO("MINUTES", 10, 1380, 420)));
        workingNightShift.setActivityIds(activities);
        workingNightShift.setDays(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        return workingNightShift;
    }

    private CTARuleTemplateDTO getWorkingOnPublicHoliday(List<BigInteger> activities) {
        CTARuleTemplateDTO workingOnPublicHoliday = new CTARuleTemplateDTO(14l, "public holiday", 60, new BigInteger("12"));
        workingOnPublicHoliday.setActivityIds(activities);
        workingOnPublicHoliday.setCtaIntervalDTOS(Arrays.asList(new CTAIntervalDTO("MINUTES", 2, 0, 1440)));
        workingOnPublicHoliday.setPublicHolidays(getPublicHolidayDates());
        return workingOnPublicHoliday;
    }


    private List<LocalDate> getPublicHolidayDates() {
        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(LocalDate.of(2018, 2, 17));
        localDates.add(LocalDate.of(2018, 2, 16));
        localDates.add(LocalDate.of(2018, 3, 5));
        localDates.add(LocalDate.of(2018, 3, 2));
        return localDates;
    }

    private CTARuleTemplateDTO getWorkingOnSaturday(List<BigInteger> activities) {
        CTARuleTemplateDTO workingOnSaturday = new CTARuleTemplateDTO(17l, "working on sat", 60, new BigInteger("12"));
        workingOnSaturday.setCtaIntervalDTOS(Arrays.asList(new CTAIntervalDTO("FIXED", 20, 0, 1440)));
        workingOnSaturday.setActivityIds(activities);
        workingOnSaturday.setDays(Arrays.asList(6));
        return workingOnSaturday;
    }

    private CTARuleTemplateDTO getWorkingOnSunday(List<BigInteger> activities) {
        CTARuleTemplateDTO workingOnSunday = new CTARuleTemplateDTO(15l, "working on sun", 60, new BigInteger("12"));
        // workingOnSaturday.setStartFrom(1380);
        // workingOnSaturday.setEndTo(420);
        workingOnSunday.setCtaIntervalDTOS(Arrays.asList(new CTAIntervalDTO("PERCENTAGE", 20, 0, 1440)));
        workingOnSunday.setActivityIds(activities);
        // workingOnSunday.setMinutesFromCta(30);
        workingOnSunday.setDays(Arrays.asList(7));
        return workingOnSunday;
    }


    private CTARuleTemplateDTO getWorkingOnHalfPublicHoliday(List<BigInteger> activities) {
        CTARuleTemplateDTO workingOnHalfPublicHoliday = new CTARuleTemplateDTO(16l, "working on half public", 60, new BigInteger("12"));
        workingOnHalfPublicHoliday.setCtaIntervalDTOS(Arrays.asList(new CTAIntervalDTO("PERCENTAGE", 20, 720, 1440)));
        workingOnHalfPublicHoliday.setActivityIds(activities);
        //workingOnHalfPublicHoliday.setDays(Arrays.asList(7));
        workingOnHalfPublicHoliday.setPublicHolidays(getPublicHolidayDates());
        // workingOnHalfPublicHoliday.setMinutesFromCta(30);
        return workingOnHalfPublicHoliday;
    }


    public TimeBankDTO getAdvanceViewTimeBank(Long unitId,Long unitPositionId, String query, Date startDate, Date endDate) {
        TimeBankDTO timeBankDTO = null;
            List<DailyTimeBank> dailyTimeBanks = timeBankMongoRepository.findAllByUnitPositionAndDate(unitPositionId, startDate, endDate);//Arrays.asList(getMockTimeBank());
            List<ShiftQueryResultWithActivity> shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, startDate, endDate);
            shiftQueryResultWithActivities = filterSubshifts(shiftQueryResultWithActivities);
            TimebankWrapper timebankWrapper = getCostTimeAgreement(unitPositionId, unitId);
            List<DailyTimeBank> dailyTimeBanksBeforeStartDate = timeBankMongoRepository.findAllByUnitPositionAndBeforeDate(unitPositionId, new DateTime(startDate).toDate());
            List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeTypeByCountryId(timebankWrapper.getCountryId());
            int totalTimeBankBeforeStartDate = dailyTimeBanksBeforeStartDate != null && !dailyTimeBanksBeforeStartDate.isEmpty()
                    ? dailyTimeBanksBeforeStartDate.stream().mapToInt(dailyTimeBank -> dailyTimeBank.getTotalTimeBankMin()).sum() : 0;
            timeBankDTO = timeBankCalculationService.getAdvanceViewTimeBank(totalTimeBankBeforeStartDate, startDate, endDate, query, shiftQueryResultWithActivities, dailyTimeBanks, timebankWrapper, timeTypeDTOS );
            //timeBankDTO1.setCostTimeAgreement(getCostTimeAgreement(145l));
        return timeBankDTO;
    }


    public TimeBankDTO getOverviewTimeBank(Long unitId, Long unitPositionId, Integer year) {
        TimebankWrapper timebankWrapper = getCostTimeAgreement(unitPositionId, unitId);
        Interval interval = getIntervalByDateForOverviewTimeBank(timebankWrapper,year);
        List<DailyTimeBank> dailyTimeBanks = new ArrayList<>();
        if(interval.getStart().getYear()<=new DateTime().getYear()){
            dailyTimeBanks = timeBankMongoRepository.findAllByUnitPositionAndDate(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        }
        return timeBankCalculationService.getOverviewTimeBank(unitPositionId, interval.getStart().dayOfYear().withMinimumValue(), interval.getEnd().dayOfYear().withMaximumValue(), dailyTimeBanks);
    }

    private Interval getIntervalByDateForOverviewTimeBank(TimebankWrapper timebankWrapper, Integer year){

        DateTime startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDate = new DateTime().withYear(year).dayOfYear().withMaximumValue().withTimeAtStartOfDay();
        if(startDate.getYear()==DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionDate()).getYear() && startDate.isBefore(DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionDate()).withTimeAtStartOfDay())){
            startDate = DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionDate()).withTimeAtStartOfDay();
        }
        if(startDate.getYear()!=DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionDate()).getYear() && startDate.isAfter(DateUtils.toJodaDateTime(timebankWrapper.getUnitPositionDate()))){
            startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        }
        if(endDate.isAfter(new DateTime().plusDays(1).withTimeAtStartOfDay()) && endDate.getYear()==new DateTime().getYear()){
            endDate = new DateTime().withTimeAtStartOfDay();
        }
        //endDate = endDate.plusMonths(5);//todo this should be removed
        return new Interval(startDate,endDate);
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
