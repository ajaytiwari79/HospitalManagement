package com.kairos.service.time_bank;


import com.kairos.activity.shift.StaffUnitPositionDetails;
import com.kairos.activity.time_bank.*;
import com.kairos.activity.time_bank.time_bank_basic.time_bank.ScheduledActivitiesDTO;
import com.kairos.activity.time_type.TimeTypeDTO;
import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.pay_out.PayOut;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.persistence.repository.pay_out.PayOutTransactionMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.rest_client.TimeBankRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.pay_out.PayOutTransaction;
import com.kairos.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.util.DateUtils;
import com.kairos.util.time_bank.TimeBankCalculationService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.ZonedDateTime;
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
    private TimeBankRepository timeBankRepository;
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
    @Inject private PayOutRepository payOutRepository;
    @Inject private PayOutTransactionMongoRepository payOutTransactionMongoRepository;


    /**
     *
     * @param staffAdditionalInfoDTO
     * @param shift
     */
    public void saveTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift) {
        staffAdditionalInfoDTO.getUnitPosition().setStaffId(shift.getStaffId());
        List<DailyTimeBankEntry> dailyTimeBanks = renewDailyTimeBank(staffAdditionalInfoDTO.getUnitPosition(),shift);
        if (!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }
    }

    /**
     *
     * @param staffAdditionalInfoDTO
     * @param shifts
     */
    public void saveTimeBanks(StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<Shift> shifts) {
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

    /**
     *
     * @param unitPositionWithCtaDetailsDTO
     * @param shift
     * @return List<DailyTimeBankEntry>
     */
    private List<DailyTimeBankEntry> renewDailyTimeBank(StaffUnitPositionDetails unitPositionWithCtaDetailsDTO, Shift shift){
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = new DateTime(shift.getEndDate()).plusDays(1).withTimeAtStartOfDay();
        List<DailyTimeBankEntry> dailyTimeBanks = timeBankRepository.findAllByUnitPositionAndDate(unitPositionWithCtaDetailsDTO.getId(), startDate.toDate(),endDate.toDate());
        timeBankRepository.deleteAll(dailyTimeBanks);
        dailyTimeBanks = new ArrayList<>();
        while (startDate.isBefore(endDate)) {
            Interval interval = new Interval(startDate, startDate.plusDays(1).withTimeAtStartOfDay());
            List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionWithCtaDetailsDTO.getId(), startDate.toDate(), startDate.plusDays(1).toDate());
                DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(unitPositionWithCtaDetailsDTO, interval, shifts);
                if(dailyTimeBank!=null) {
                    dailyTimeBanks.add(dailyTimeBank);
                }
                startDate = startDate.plusDays(1);
        }
        return dailyTimeBanks;
    }


    /**
     *
     * @param unitPositionIds
     * @param shiftQueryResultWithActivities
     * @return Map<Long, List<ShiftWithActivityDTO>>
     */
    public Map<Long, List<ShiftWithActivityDTO>> getShiftsMapByUEPs(List<Long> unitPositionIds, List<ShiftWithActivityDTO> shiftQueryResultWithActivities) {
        Map<Long, List<ShiftWithActivityDTO>> shiftsMap = new HashMap<>(unitPositionIds.size());
        unitPositionIds.forEach(uEPId -> {
            shiftsMap.put(uEPId, getShiftsByUEP(uEPId, shiftQueryResultWithActivities));
        });
        return shiftsMap;
    }

    /**
     *
     * @param unitPositionId
     * @param shiftQueryResultWithActivities
     * @return List<ShiftWithActivityDTO>
     */
    public List<ShiftWithActivityDTO> getShiftsByUEP(Long unitPositionId, List<ShiftWithActivityDTO> shiftQueryResultWithActivities) {
        List<ShiftWithActivityDTO> shifts = new ArrayList<>();
        shiftQueryResultWithActivities.forEach(s -> {
            if (s.getUnitPositionId().equals(unitPositionId)) {
                shifts.add(s);
            }
        });
        return shifts;
    }

    /**
     *
     * @param unitPositonId
     * @return List<CalculatedTimeBankByDateDTO>
     */
    public List<CalculatedTimeBankByDateDTO> getTimeBankFromCurrentDateByUEP(Long unitPositonId) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(145l);
        List<ShiftWithActivityDTO> shifts = null;//shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitEmploymentPositonId,new Date(),new Date());
        return timeBankCalculationService.getTimeBankByDates(unitPositionWithCtaDetailsDTO, shifts, 365);
    }

    /**
     *
     * @param unitPositionId
     * @return UnitPositionWithCtaDetailsDTO
     */
    public UnitPositionWithCtaDetailsDTO getCostTimeAgreement(Long unitPositionId) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = timeBankRestClient.getCTAbyUnitEmployementPosition(unitPositionId);
        return unitPositionWithCtaDetailsDTO;
    }

    /**
     *
     * @param unitId
     * @param unitPositionId
     * @param query
     * @param startDate
     * @param endDate
     * @return TimeBankAndPayoutDTO
     */
    public TimeBankAndPayoutDTO getAdvanceViewTimeBank(Long unitId, Long unitPositionId, String query, Date startDate, Date endDate) {
        List<DailyTimeBankEntry> dailyTimeBanks = timeBankRepository.findAllByUnitPositionAndDate(unitPositionId, startDate, endDate);//Arrays.asList(getMockTimeBank());
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, startDate, endDate);
        shiftQueryResultWithActivities = timeBankCalculationService.filterSubshifts(shiftQueryResultWithActivities);
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        int totalTimeBankBeforeStartDate = 0;
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeTypeByCountryId(unitPositionWithCtaDetailsDTO.getCountryId());
        if(new DateTime(startDate).isAfter(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()))){
            Interval interval = new Interval(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getUnitPositionStartDate()),new DateTime(startDate));
            //totaltimebank is timebank without daily timebank entries
            List<DailyTimeBankEntry> dailyTimeBanksBeforeStartDate = timeBankRepository.findAllByUnitPositionAndBeforeDate(unitPositionId, new DateTime(startDate).toDate());

            int totalTimeBank = timeBankCalculationService.calculateTimeBankForInterval(interval, unitPositionWithCtaDetailsDTO,false,dailyTimeBanksBeforeStartDate,false);

            totalTimeBankBeforeStartDate = dailyTimeBanksBeforeStartDate != null && !dailyTimeBanksBeforeStartDate.isEmpty()
                    ? dailyTimeBanksBeforeStartDate.stream().mapToInt(dailyTimeBank -> dailyTimeBank.getTotalTimeBankMin()).sum() : 0;
            totalTimeBankBeforeStartDate = totalTimeBankBeforeStartDate - totalTimeBank;
        }
        List<PayOutTransaction> payOutTransactions = payOutTransactionMongoRepository.findAllByUnitPositionAndDate(unitPositionId,startDate,endDate);
        List<PayOut> payOuts = payOutRepository.findAllByUnitPositionAndDate(unitPositionId, startDate, endDate);
        TimeBankAndPayoutDTO timeBankAndPayoutDTO = timeBankCalculationService.getAdvanceViewTimeBank(totalTimeBankBeforeStartDate, startDate, endDate, query, shiftQueryResultWithActivities, dailyTimeBanks, unitPositionWithCtaDetailsDTO, timeTypeDTOS,payOuts,payOutTransactions);
        //timeBankDTO1.setCostTimeAgreement(getCostTimeAgreement(145l));
        return timeBankAndPayoutDTO;
    }

    /**
     *
     * @param unitPositionId
     * @param year
     * @return TimeBankDTO
     */
    public TimeBankDTO getOverviewTimeBank(Long unitPositionId, Integer year) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId);
        Interval interval = getIntervalByDateForOverviewTimeBank(unitPositionWithCtaDetailsDTO, year);
        List<DailyTimeBankEntry> dailyTimeBankEntries = new ArrayList<>();
        if (interval.getStart().getYear() <= new DateTime().getYear()) {
            dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndDate(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        }
        return timeBankCalculationService.getOverviewTimeBank(unitPositionId, interval.getStart().dayOfYear().withMinimumValue(), interval.getEnd().dayOfYear().withMaximumValue(), dailyTimeBankEntries, unitPositionWithCtaDetailsDTO);
    }



    public TimeBankVisualViewDTO getTimeBankForVisualView(Long unitId,Long unitPositionId,String query,Integer value,Date startDate,Date endDate){
        if(StringUtils.isNotEmpty(query)){

        }
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId,startDate,endDate);
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndDate(unitPositionId,new Date(),new Date());
        Long countryId = organizationRestClient.getCountryIdOfOrganization(unitId);
        Map<String,List<TimeType>> presenceAbsenceTimeTypeMap = timeTypeService.getPresenceAbsenceTimeType(countryId);
        List<ScheduledActivitiesDTO> scheduledActivitiesDTOS = getScheduledActivities(shifts);
        return timeBankCalculationService.getVisualViewTimeBank();

    }




    private List<ScheduledActivitiesDTO> getScheduledActivities(List<ShiftWithActivityDTO> shifts){
        Map<String,Long> activityScheduledMin  = shifts.stream().collect(Collectors.groupingBy(s->s.getActivityId()+"-"+s.getActivity().getName(),Collectors.summingLong(s->s.getScheduledMinutes())));
        List<ScheduledActivitiesDTO> scheduledActivitiesDTOS = new ArrayList<>(activityScheduledMin.size());
        activityScheduledMin.forEach((activity, mintues) -> {
            String[] idNameArray = activity.split("-");
             scheduledActivitiesDTOS.add(new ScheduledActivitiesDTO(new BigInteger(idNameArray[0]),idNameArray[1],mintues));
        });
        return scheduledActivitiesDTOS;
    }

    /**
     *
     * @param unitPositionWithCtaDetailsDTO
     * @param year
     * @return Interval
     */
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


}
