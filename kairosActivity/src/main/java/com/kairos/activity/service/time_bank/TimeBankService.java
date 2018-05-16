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
import com.kairos.activity.response.dto.time_bank.CTARuleTemplateCalulatedTimeBankDTO;
import com.kairos.activity.response.dto.time_bank.CalculatedTimeBankByDateDTO;
import com.kairos.activity.response.dto.time_bank.TimeBankDTO;
import com.kairos.activity.response.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.activity.TimeTypeService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
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
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        List<DailyTimeBankEntry> dailyTimeBanks = calculateDailyTimeBank(unitPositionWithCtaDetailsDTO,shift,unitPositionId);
        if (!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }
    }

    public void saveTimeBanks(Long unitPositionId, List<Shift> shifts) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        List<DailyTimeBankEntry> updatedDailyTimeBankEntries = new ArrayList<>();
        for (Shift shift : shifts) {
            List<DailyTimeBankEntry> dailyTimeBankEntries = calculateDailyTimeBank(unitPositionWithCtaDetailsDTO,shift,unitPositionId);
            updatedDailyTimeBankEntries.addAll(dailyTimeBankEntries);
        }
        if(!updatedDailyTimeBankEntries.isEmpty()) {
            save(updatedDailyTimeBankEntries);
        }
    }

    private List<DailyTimeBankEntry> calculateDailyTimeBank(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, Shift shift, Long unitPositionId){
        unitPositionWithCtaDetailsDTO.setStaffId(shift.getStaffId());
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = new DateTime(shift.getEndDate()).withTimeAtStartOfDay();
        if(startDate.equals(endDate)){
            endDate = endDate.plusDays(1);
        }
        List<DailyTimeBankEntry> dailyTimeBanks = timeBankMongoRepository.findAllByUnitPositionAndDate(unitPositionId, startDate.toDate(), endDate.toDate());
        timeBankMongoRepository.deleteAll(dailyTimeBanks);
        dailyTimeBanks = new ArrayList<>();
        Interval interval = new Interval(startDate, startDate.plusDays(1).withTimeAtStartOfDay());
        List<ShiftQueryResultWithActivity> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        DailyTimeBankEntry dailyTimeBank = new DailyTimeBankEntry(unitPositionWithCtaDetailsDTO.getUnitPositionId(), unitPositionWithCtaDetailsDTO.getStaffId(), unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek(), DateUtils.asLocalDate(interval.getStart().toDate()));
        shifts = filterSubshifts(shifts);
        if (shifts != null && !shifts.isEmpty()) {
            timeBankCalculationService.getTimeBankByInterval(unitPositionWithCtaDetailsDTO, interval, shifts, dailyTimeBank);
            dailyTimeBanks.add(dailyTimeBank);
        }
        if (!startDate.toLocalDate().equals(endDate.toLocalDate())) {
            interval = new Interval(endDate, endDate.plusDays(1).withTimeAtStartOfDay());
            shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
            shifts = filterSubshifts(shifts);
            if (shifts != null && !shifts.isEmpty()) {
                timeBankCalculationService.getTimeBankByInterval(unitPositionWithCtaDetailsDTO, interval, shifts, dailyTimeBank);
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
    public Boolean createBlankTimeBank(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        DateTime startDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).withTimeAtStartOfDay();
        startDate = startDate.plusDays(1);//Todo this should be removed
        DateTime endDate = startDate.plusYears(3);
        List<DailyTimeBankEntry> newDailyTimeBankEntries = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            DailyTimeBankEntry dailyTimeBankEntry = new DailyTimeBankEntry(unitPositionWithCtaDetailsDTO.getUnitPositionId(), unitPositionWithCtaDetailsDTO.getStaffId(), unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek(), DateUtils.toLocalDate(startDate));
            int contractualMin = startDate.getDayOfWeek() <= unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek() ? unitPositionWithCtaDetailsDTO.getContractedMinByWeek() / unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek() : 0;
            dailyTimeBankEntry.setTotalTimeBankMin(-contractualMin);
            dailyTimeBankEntry.setContractualMin(contractualMin);
            dailyTimeBankEntry.setTimeBankCTADistributionList(timeBankCalculationService.getDistribution(unitPositionWithCtaDetailsDTO));
            newDailyTimeBankEntries.add(dailyTimeBankEntry);
            startDate = startDate.plusDays(1);
        }
        if (!newDailyTimeBankEntries.isEmpty()) {
            save(newDailyTimeBankEntries);
        }
        return null;
    }

    public Boolean updateBlankTimebank(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        DateTime startDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).withTimeAtStartOfDay();
        DateTime endDate = startDate.plusYears(3);
        List<DailyTimeBankEntry> newDailyTimeBankEntries = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            DailyTimeBankEntry dailyTimeBankEntry = new DailyTimeBankEntry(unitPositionWithCtaDetailsDTO.getUnitPositionId(), unitPositionWithCtaDetailsDTO.getStaffId(), unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek(), DateUtils.toLocalDate(startDate));
            int contractualMin = startDate.getDayOfWeek() <= unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek() ? unitPositionWithCtaDetailsDTO.getContractedMinByWeek() / unitPositionWithCtaDetailsDTO.getWorkingDaysPerWeek() : 0;
            dailyTimeBankEntry.setTotalTimeBankMin(-contractualMin);
            dailyTimeBankEntry.setContractualMin(contractualMin);
            dailyTimeBankEntry.setTimeBankCTADistributionList(timeBankCalculationService.getDistribution(unitPositionWithCtaDetailsDTO));
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
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(145l);//timeBankRestClient.getCTAbyUnitEmployementPosition(unitEmploymentPositonId);
        List<ShiftQueryResultWithActivity> shifts = null;//shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitEmploymentPositonId,new Date(),new Date());
        return timeBankCalculationService.getTimeBankByDates(unitPositionWithCtaDetailsDTO, shifts, 365);
    }

    public UnitPositionWithCtaDetailsDTO getCostTimeAgreement(Long unitPositionId) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = timeBankRestClient.getCTAbyUnitEmployementPosition(unitPositionId);
       /* for (CTARuleTemplateCalulatedTimeBankDTO ctaRuleTemplateCalulatedTimeBankDTO : unitPositionWithCtaDetailsDTO.getCtaRuleTemplates()) {
            if (ctaRuleTemplateCalulatedTimeBankDTO.getTimeTypeIds() != null && !ctaRuleTemplateCalulatedTimeBankDTO.getTimeTypeIds().isEmpty()) {
                List<BigInteger> timeTypeIds = timeTypeService.getAllParentTimeTypeByTimeTypeId(ctaRuleTemplateCalulatedTimeBankDTO.getTimeTypeIds(), unitPositionWithCtaDetailsDTO.getCountryId());
                ctaRuleTemplateCalulatedTimeBankDTO.setTimeTypeIdsWithParentTimeType(ctaRuleTemplateCalulatedTimeBankDTO.getTimeTypeIds());
            }
        }*/
        return unitPositionWithCtaDetailsDTO;
    }


    public TimeBankDTO getAdvanceViewTimeBank(Long unitId, Long unitPositionId, String query, Date startDate, Date endDate) {
        TimeBankDTO timeBankDTO = null;
        List<DailyTimeBankEntry> dailyTimeBanks = timeBankMongoRepository.findAllByUnitPositionAndDate(unitPositionId, startDate, endDate);//Arrays.asList(getMockTimeBank());
        List<ShiftQueryResultWithActivity> shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, startDate, endDate);
        shiftQueryResultWithActivities = filterSubshifts(shiftQueryResultWithActivities);
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        int totalTimeBankBeforeStartDate = 0;
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeTypeByCountryId(unitPositionWithCtaDetailsDTO.getCountryId());
        if(new DateTime(startDate).isAfter(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()))){
            Interval interval = new Interval(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()),new DateTime(startDate));
            //totaltimebank is timebank without daily timebank entries
            List<DailyTimeBankEntry> dailyTimeBanksBeforeStartDate = timeBankMongoRepository.findAllByUnitPositionAndBeforeDate(unitPositionId, new DateTime(startDate).toDate());

            int totalTimeBank = timeBankCalculationService.calculateTimeBankForInterval(interval, unitPositionWithCtaDetailsDTO,false,dailyTimeBanksBeforeStartDate,false);

            totalTimeBankBeforeStartDate = dailyTimeBanksBeforeStartDate != null && !dailyTimeBanksBeforeStartDate.isEmpty()
                    ? dailyTimeBanksBeforeStartDate.stream().mapToInt(dailyTimeBank -> dailyTimeBank.getTotalTimeBankMin()).sum() : 0;
            totalTimeBankBeforeStartDate = totalTimeBankBeforeStartDate - totalTimeBank;
        }
        timeBankDTO = timeBankCalculationService.getAdvanceViewTimeBank(totalTimeBankBeforeStartDate, startDate, endDate, query, shiftQueryResultWithActivities, dailyTimeBanks, unitPositionWithCtaDetailsDTO, timeTypeDTOS);
        //timeBankDTO1.setCostTimeAgreement(getCostTimeAgreement(145l));
        return timeBankDTO;
    }


    public TimeBankDTO getOverviewTimeBank(Long unitPositionId, Integer year) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        Interval interval = getIntervalByDateForOverviewTimeBank(unitPositionWithCtaDetailsDTO, year);
        List<DailyTimeBankEntry> dailyTimeBankEntries = new ArrayList<>();
        if (interval.getStart().getYear() <= new DateTime().getYear()) {
            dailyTimeBankEntries = timeBankMongoRepository.findAllByUnitPositionAndDate(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        }
        return timeBankCalculationService.getOverviewTimeBank(unitPositionId, interval.getStart().dayOfYear().withMinimumValue(), interval.getEnd().dayOfYear().withMaximumValue(), dailyTimeBankEntries, unitPositionWithCtaDetailsDTO);
    }

    private Interval getIntervalByDateForOverviewTimeBank(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, Integer year) {

        DateTime startDate = new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
        DateTime endDate = new DateTime().withYear(year).dayOfYear().withMaximumValue().withTimeAtStartOfDay();
        if (startDate.getYear() == DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).getYear() && startDate.isBefore(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).withTimeAtStartOfDay())) {
            startDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).withTimeAtStartOfDay();
        }
        if (startDate.getYear() != DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).getYear() && startDate.isAfter(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()))) {
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
