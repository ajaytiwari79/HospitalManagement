package com.kairos.service.time_bank;


import com.kairos.activity.shift.StaffUnitPositionDetails;
import com.kairos.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.time_bank.CalculatedTimeBankByDateDTO;
import com.kairos.activity.time_bank.TimeBankDTO;
import com.kairos.activity.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.time_type.TimeTypeDTO;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.rest_client.TimeBankRestClient;
import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.util.DateUtils;
import com.kairos.util.time_bank.TimeBankCalculationService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

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

    public void saveTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift) {
        //UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        staffAdditionalInfoDTO.getUnitPosition().setStaffId(shift.getStaffId());
        List<DailyTimeBankEntry> dailyTimeBanks = renewDailyTimeBank(staffAdditionalInfoDTO.getUnitPosition(),shift);
        if (!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }
    }

    public void saveTimeBanks(StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<Shift> shifts) {
        //UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        staffAdditionalInfoDTO.getUnitPosition().setStaffId(shifts.get(0).getStaffId());
        List<DailyTimeBankEntry> updatedDailyTimeBankEntries = new ArrayList<>();
        for (Shift shift : shifts) {
            List<DailyTimeBankEntry> dailyTimeBankEntries = renewDailyTimeBank(staffAdditionalInfoDTO.getUnitPosition(),shift);
            updatedDailyTimeBankEntries.addAll(dailyTimeBankEntries);
        }
        if(!updatedDailyTimeBankEntries.isEmpty()) {
            save(updatedDailyTimeBankEntries);
        }
    }

    private List<DailyTimeBankEntry> renewDailyTimeBank(StaffUnitPositionDetails unitPositionWithCtaDetailsDTO, Shift shift){
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = new DateTime(shift.getEndDate()).plusDays(1).withTimeAtStartOfDay();
        List<DailyTimeBankEntry> dailyTimeBanks = timeBankMongoRepository.findAllByUnitPositionAndDate(unitPositionWithCtaDetailsDTO.getId(), startDate.toDate(),endDate.toDate());
        timeBankMongoRepository.deleteAll(dailyTimeBanks);
        dailyTimeBanks = new ArrayList<>();
        //Interval interval = new Interval(startDate, startDate.plusDays(1).withTimeAtStartOfDay());
        /*List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());*/

        while (startDate.isBefore(endDate)) {
            Interval interval = new Interval(startDate, startDate.plusDays(1).withTimeAtStartOfDay());
            List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionWithCtaDetailsDTO.getId(), startDate.toDate(), startDate.plusDays(1).toDate());
                DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(unitPositionWithCtaDetailsDTO, interval, shifts);
                if(dailyTimeBank!=null) {
                    dailyTimeBanks.add(dailyTimeBank);
                }
                startDate = startDate.plusDays(1);
        }
        /*if (!startDate.toLocalDate().equals(endDate.toLocalDate())) {
            interval = new Interval(endDate, endDate.plusDays(1).withTimeAtStartOfDay());
            shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
            shifts = filterSubshifts(shifts);
            if (shifts != null && !shifts.isEmpty()) {
                DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(unitPositionWithCtaDetailsDTO, interval, shifts);
                dailyTimeBanks.add(dailyTimeBank);
            }
        }*/
        return dailyTimeBanks;
    }





    @Deprecated
    public Boolean createBlankTimeBank(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO) {
        DateTime startDate = DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()).withTimeAtStartOfDay();
        startDate = startDate.plusDays(1);//Todo this should be removed
        DateTime endDate = startDate.plusYears(3);
        List<DailyTimeBankEntry> newDailyTimeBankEntries = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            DailyTimeBankEntry dailyTimeBankEntry = new DailyTimeBankEntry(unitPositionWithCtaDetailsDTO.getId(), unitPositionWithCtaDetailsDTO.getStaffId(), unitPositionWithCtaDetailsDTO.getWorkingDaysInWeek(), DateUtils.toLocalDate(startDate));
            int contractualMin = startDate.getDayOfWeek() <= unitPositionWithCtaDetailsDTO.getWorkingDaysInWeek() ? unitPositionWithCtaDetailsDTO.getTotalWeeklyMinutes() / unitPositionWithCtaDetailsDTO.getWorkingDaysInWeek() : 0;
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
            DailyTimeBankEntry dailyTimeBankEntry = new DailyTimeBankEntry(unitPositionWithCtaDetailsDTO.getId(), unitPositionWithCtaDetailsDTO.getStaffId(), unitPositionWithCtaDetailsDTO.getWorkingDaysInWeek(), DateUtils.toLocalDate(startDate));
            int contractualMin = startDate.getDayOfWeek() <= unitPositionWithCtaDetailsDTO.getWorkingDaysInWeek() ? unitPositionWithCtaDetailsDTO.getTotalWeeklyMinutes() / unitPositionWithCtaDetailsDTO.getWorkingDaysInWeek() : 0;
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


    public Map<Long, List<ShiftWithActivityDTO>> getShiftsMapByUEPs(List<Long> unitPositionIds, List<ShiftWithActivityDTO> shiftQueryResultWithActivities) {
        Map<Long, List<ShiftWithActivityDTO>> shiftsMap = new HashMap<>(unitPositionIds.size());
        unitPositionIds.forEach(uEPId -> {
            shiftsMap.put(uEPId, getShiftsByUEP(uEPId, shiftQueryResultWithActivities));
        });
        return shiftsMap;
    }


    public List<ShiftWithActivityDTO> getShiftsByUEP(Long unitPositionId, List<ShiftWithActivityDTO> shiftQueryResultWithActivities) {
        List<ShiftWithActivityDTO> shifts = new ArrayList<>();
        shiftQueryResultWithActivities.forEach(s -> {
            if (s.getUnitPositionId().equals(unitPositionId)) {
                shifts.add(s);
            }
        });
        return shifts;
    }


    public List<CalculatedTimeBankByDateDTO> getTimeBankFromCurrentDateByUEP(Long unitPositonId) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(145l);//timeBankRestClient.getCTAbyUnitEmployementPosition(unitEmploymentPositonId);
        List<ShiftWithActivityDTO> shifts = null;//shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitEmploymentPositonId,new Date(),new Date());
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
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, startDate, endDate);
        shiftQueryResultWithActivities = timeBankCalculationService.filterSubshifts(shiftQueryResultWithActivities);
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


   /* public boolean saveTimeBank() {
        Shift shift = new Shift();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        DateTime dt = formatter.parseDateTime("20/01/2018 03:41");
        shift.setStartDate(dt.toDate());
        shift.setEndDate(dt.plusHours(10).toDate());
        saveTimeBank(145l, shift);
        return true;

    }
*/
}
