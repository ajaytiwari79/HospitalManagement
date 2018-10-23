package com.kairos.service.time_bank;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;

import com.kairos.dto.activity.time_bank.TimeBankAndPayoutDTO;
import com.kairos.dto.activity.time_bank.TimeBankDTO;
import com.kairos.dto.activity.time_bank.TimeBankVisualViewDTO;
import com.kairos.dto.activity.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.constants.AppConstants;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.pay_out.PayOut;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.persistence.repository.pay_out.PayOutTransactionMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.rest_client.TimeBankRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.pay_out.PayOutCalculationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutTransaction;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.service.shift.ShiftService;
import com.kairos.utils.time_bank.TimeBankCalculationService;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.ONE_DAY_MINUTES;

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
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private OrganizationRestClient organizationRestClient;
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private PayOutRepository payOutRepository;
    @Inject
    private PayOutTransactionMongoRepository payOutTransactionMongoRepository;
    @Inject private PayOutCalculationService payOutCalculationService;
    @Inject private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ExceptionService exceptionService;


    /**
     * @param staffAdditionalInfoDTO
     * @param shift
     */
    public void saveTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift) {
        staffAdditionalInfoDTO.getUnitPosition().setStaffId(shift.getStaffId());
        List<DailyTimeBankEntry> dailyTimeBanks = renewDailyTimeBank(staffAdditionalInfoDTO, shift);
        if (!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }
    }

    /**
     * @param staffAdditionalInfoDTO
     * @param shifts
     */
    public void saveTimeBanksAndPayOut(StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<Shift> shifts) {
        staffAdditionalInfoDTO.getUnitPosition().setStaffId(shifts.get(0).getStaffId());
        List<DailyTimeBankEntry> updatedDailyTimeBankEntries = new ArrayList<>();
        for (Shift shift : shifts) {
            List<DailyTimeBankEntry> dailyTimeBankEntries = renewDailyTimeBank(staffAdditionalInfoDTO, shift);
            updatedDailyTimeBankEntries.addAll(dailyTimeBankEntries);
        }
        if (!updatedDailyTimeBankEntries.isEmpty()) {
            save(updatedDailyTimeBankEntries);
        }
    }

    public void saveTimeBanksAndPayOut(List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, List<Shift> shifts, Map<BigInteger, ActivityWrapper> activityWrapperMap,Date startDate,Date endDate) {
        DateTime startDateTime = new DateTime(startDate).withTimeAtStartOfDay();
        DateTime endDateTime = new DateTime(endDate).plusDays(1).withTimeAtStartOfDay();
        DateTime shiftEndTime=new DateTime(shifts.get(shifts.size()-1).getEndDate()).plusDays(1).withTimeAtStartOfDay();
        List<Long> unitPositionIds = new ArrayList<>(staffAdditionalInfoDTOS.stream().map(staffAdditionalInfoDTO  -> staffAdditionalInfoDTO.getUnitPosition().getId()).collect(Collectors.toSet()));
      //  List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByIdsAndBetweenDates(unitPositionIds, startDate.toDate(), endDate.toDate());
       // Map<String,DailyTimeBankEntry> dailyTimeBankEntryAndUnitPositionMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(k->k.getUnitPositionId()+""+k.getDate(),v->v));
        timeBankRepository.deleteDailyTimeBank(unitPositionIds, startDateTime.toDate(), endDateTime.toDate());
        List<ShiftWithActivityDTO> shiftsList = shiftMongoRepository.findAllShiftsBetweenDurationByUEPS(unitPositionIds, startDateTime.toDate(), endDateTime.toDate());
        Map<String, List<ShiftWithActivityDTO>> shiftDateMap = shiftsList.stream().collect(Collectors.groupingBy(k -> k.getUnitPositionId() + "" + DateUtils.asLocalDate(k.getStartDate())));
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>();
//        List<DailyTimeBankEntry> dailyTimeBankEntrys = timeBankRepository.findAllDailyTimeBankByIdsAndBetweenDates(unitPositionIds, startDate.toDate(),endDate.toDate());
//        Map<Long, DailyTimeBankEntry> dailyTimeBankEntryMap = dailyTimeBankEntrys.stream().collect(Collectors.toMap(k -> k.getUnitPositionId(), v -> v));
        List<PayOut> payOutList = payOutRepository.findAllByUnitPositionsAndDate(unitPositionIds,startDateTime.toDate(),endDateTime.toDate());
        Map<BigInteger,PayOut> shiftAndPayOutMap=payOutList.stream().collect(Collectors.toMap(k -> k.getShiftId(), v->v));
        List<PayOut> payOuts = new ArrayList<>();
        while (startDateTime.isBefore(shiftEndTime)) {
            int totalTimeBank = 0;
            for (StaffAdditionalInfoDTO unitPositionWithCtaDetailsDTO : staffAdditionalInfoDTOS) {
                List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftDateMap.getOrDefault(unitPositionWithCtaDetailsDTO.getUnitPosition().getId() + "" + DateUtils.asLocalDate(startDateTime.toDate()), new ArrayList<>());
               // DailyTimeBankEntry dailyTimeBankEntry = dailyTimeBankEntryMap.get(unitPositionWithCtaDetailsDTO.getUnitPosition().getId());
             //   long accumulatedTimeBank = dailyTimeBankEntry != null ? dailyTimeBankEntry.getAccumultedTimeBankMin() : 0;
                Interval interval = new Interval(startDateTime, startDateTime.plusDays(1).withTimeAtStartOfDay());
                DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(unitPositionWithCtaDetailsDTO, interval, shiftWithActivityDTOS,new HashMap<>());
                if (dailyTimeBank != null) {
                   // accumulatedTimeBank = accumulatedTimeBank + dailyTimeBank.getTotalTimeBankMin();
                   // dailyTimeBank.setAccumultedTimeBankMin(accumulatedTimeBank);
                    totalTimeBank += dailyTimeBank.getTotalTimeBankMin();
                    dailyTimeBanks.add(dailyTimeBank);
                }
                //Todo Pradeep should reafctor this method so that we can calculate accumulated timebank
                /*if (totalTimeBank != 0) {
                    unitPositionAndTimeBank.put(unitPositionWithCtaDetailsDTO.getUnitPosition().getId(), totalTimeBank);
                }*/
                DateTimeInterval dateTimeInterval = new DateTimeInterval(startDateTime.getMillis(), startDateTime.plusDays(1).getMillis());
                List<Shift> shiftList = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftWithActivityDTOS,Shift.class);
                for (Shift shift : shiftList) {
                    PayOut payOut = shiftAndPayOutMap.getOrDefault(shift.getId(),new PayOut(shift.getId(), shift.getUnitPositionId(), shift.getStaffId(), dateTimeInterval.getStartLocalDate(),shift.getUnitId()));
                    payOut = payOutCalculationService.calculateAndUpdatePayOut(dateTimeInterval, unitPositionWithCtaDetailsDTO, shift, activityWrapperMap, payOut);
                    if(payOut.getTotalPayOutMin()>0) {
                        //Todo Pradeep should reafctor this method so that we can calculate accumulated payout
                        //payOutRepository.updatePayOut(payOut.getUnitPositionId(),(int) payOut.getTotalPayOutMin());
                        payOuts.add(payOut);
                    }

                }

            }
            startDateTime = startDateTime.plusDays(1);
        }
        Map<BigInteger,Shift> shiftIdAndShiftMap=shifts.stream().collect(Collectors.toMap(k->k.getId(),v->v));
        shiftAndPayOutMap.entrySet().forEach(k->{
            if(shiftIdAndShiftMap.get(k.getKey())==null){
                PayOut  deletePayOut=shiftAndPayOutMap.get(k.getKey());
                deletePayOut.setDeleted(true);
                payOuts.add(deletePayOut);
            }
        });
        if(!payOuts.isEmpty()){
            payOutRepository.saveEntities(payOuts);
        }
        if (!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }
    }


    /**
     * @param staffAdditionalInfoDTO
     * @param shift
     * @return List<DailyTimeBankEntry>
     */
    private List<DailyTimeBankEntry> renewDailyTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift) {
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = new DateTime(shift.getEndDate()).plusDays(1).withTimeAtStartOfDay();
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByUnitPositionIdAndBetweenDates(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate.toDate(), endDate.toDate());
        Map<String,DailyTimeBankEntry> dailyTimeBankEntryAndUnitPositionMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(k->k.getUnitPositionId()+""+k.getDate(),v->v));
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>();
        DailyTimeBankEntry dailyTimeBankEntry = timeBankRepository.findLastTimeBankByUnitPositionId(shift.getUnitPositionId(), shift.getStartDate());
        long accumulatedTimeBank = dailyTimeBankEntry != null ? dailyTimeBankEntry.getAccumultedTimeBankMin() : 0;
        int totalTimeBank = 0;
        while (startDate.isBefore(endDate)) {
            Interval interval = new Interval(startDate, startDate.plusDays(1).withTimeAtStartOfDay());
            List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate.toDate(), startDate.plusDays(1).toDate());
            DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(staffAdditionalInfoDTO, interval, shifts,dailyTimeBankEntryAndUnitPositionMap);
            if (dailyTimeBank != null) {
                accumulatedTimeBank = accumulatedTimeBank + dailyTimeBank.getTotalTimeBankMin();
                dailyTimeBank.setAccumultedTimeBankMin(accumulatedTimeBank);
                totalTimeBank += dailyTimeBank.getTotalTimeBankMin();
                dailyTimeBanks.add(dailyTimeBank);
            }
            startDate = startDate.plusDays(1);
        }
        //Todo Pradeep should reafctor this method so that we can calculate accumulated timebank
        /*if (totalTimeBank != 0) {
            timeBankRepository.updateAccumulatedTimeBank(shift.getUnitPositionId(), totalTimeBank);
        }*/
        return dailyTimeBanks;
    }


    /**
     * @param unitPositionId
     * @return UnitPositionWithCtaDetailsDTO
     */
    public UnitPositionWithCtaDetailsDTO getCostTimeAgreement(Long unitPositionId,Date startDate,Date endDate) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = genericIntegrationService.getCTAbyUnitEmployementPosition(unitPositionId);
        if (!Optional.ofNullable(unitPositionWithCtaDetailsDTO).isPresent()){
            exceptionService.dataNotFoundException("message.staffUnitPosition.notFound");
        }
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByUnitPositionIdBetweenDate(unitPositionId,startDate,endDate);
        List<CTARuleTemplateDTO> ruleTemplates = ctaResponseDTOS.stream().flatMap(ctaResponseDTO -> ctaResponseDTO.getRuleTemplates().stream()).collect(Collectors.toList());
        ruleTemplates = ruleTemplates.stream().filter(ObjectUtils.distinctByKey(CTARuleTemplateDTO ::getName)).collect(Collectors.toList());
        unitPositionWithCtaDetailsDTO.setCtaRuleTemplates(ruleTemplates);
        return unitPositionWithCtaDetailsDTO;
    }

    /**
     * @param unitId
     * @param unitPositionId
     * @param query
     * @param startDate
     * @param endDate
     * @return TimeBankAndPayoutDTO
     */
    public TimeBankAndPayoutDTO getAdvanceViewTimeBank(Long unitId, Long unitPositionId, String query, Date startDate, Date endDate) {
        List<DailyTimeBankEntry> dailyTimeBanks = timeBankRepository.findAllByUnitPositionAndDate(unitPositionId, startDate, endDate);
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, startDate, endDate);
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId, startDate, endDate);
        long totalTimeBankBeforeStartDate = 0;
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeTypeByCountryId(unitPositionWithCtaDetailsDTO.getCountryId());
        if (new DateTime(startDate).isAfter(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getStartDate()))) {
            Interval interval = new Interval(DateUtils.toJodaDateTime(unitPositionWithCtaDetailsDTO.getStartDate()), new DateTime(startDate));
            //totaltimebank is timebank without daily timebank entries
            List<DailyTimeBankEntry> dailyTimeBanksBeforeStartDate = timeBankRepository.findAllByUnitPositionAndBeforeDate(unitPositionId, new DateTime(startDate).toDate());
            int totalTimeBank = timeBankCalculationService.calculateTimeBankForInterval(interval, unitPositionWithCtaDetailsDTO, false, dailyTimeBanksBeforeStartDate, false);
            totalTimeBankBeforeStartDate = dailyTimeBanksBeforeStartDate != null && !dailyTimeBanksBeforeStartDate.isEmpty()
                    ? dailyTimeBanksBeforeStartDate.stream().mapToInt(dailyTimeBank -> dailyTimeBank.getTotalTimeBankMin()).sum() : 0;
            totalTimeBankBeforeStartDate = totalTimeBankBeforeStartDate - totalTimeBank;
        }
        List<PayOutTransaction> payOutTransactions = payOutTransactionMongoRepository.findAllByUnitPositionIdAndDate(unitPositionId, startDate, endDate);
        List<PayOut> payOuts = payOutRepository.findAllByUnitPositionAndDate(unitPositionId, startDate, endDate);
        TimeBankAndPayoutDTO timeBankAndPayoutDTO = timeBankCalculationService.getAdvanceViewTimeBank(totalTimeBankBeforeStartDate, startDate, endDate, query, shiftQueryResultWithActivities, dailyTimeBanks, unitPositionWithCtaDetailsDTO, timeTypeDTOS, payOuts, payOutTransactions);
        //timeBankDTO1.setCostTimeAgreement(getCostTimeAgreement(145l));
        return timeBankAndPayoutDTO;
    }

    /**
     * @param unitPositionId
     * @param year
     * @return TimeBankDTO
     */
    public TimeBankDTO getOverviewTimeBank(Long unitPositionId, Integer year) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = genericIntegrationService.getCTAbyUnitEmployementPosition(unitPositionId);
        Interval interval = getIntervalByDateTimeBank(unitPositionWithCtaDetailsDTO, year);
        List<DailyTimeBankEntry> dailyTimeBankEntries = new ArrayList<>();
        if (interval.getStart().getYear() <= new DateTime().getYear()) {
            dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndDate(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        }
        return timeBankCalculationService.getOverviewTimeBank(unitPositionId, interval.getStart().dayOfYear().withMinimumValue(), interval.getEnd().dayOfYear().withMaximumValue(), dailyTimeBankEntries, unitPositionWithCtaDetailsDTO);
    }


    public TimeBankVisualViewDTO getTimeBankForVisualView(Long unitId, Long unitPositionId, String query, Integer value, Date startDate, Date endDate) {
        ZonedDateTime endZonedDate = null;
        ZonedDateTime startZonedDate = null;
        if (StringUtils.isNotEmpty(query)) {
            if (query.equals(AppConstants.WEEK)) {
                startZonedDate = ZonedDateTime.now().with(ChronoField.ALIGNED_WEEK_OF_YEAR, value).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS);
                endZonedDate = startZonedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            } else if (query.equals(AppConstants.MONTH)) {
                startZonedDate = ZonedDateTime.now().with(ChronoField.MONTH_OF_YEAR, value).with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
                endZonedDate = startZonedDate.with(TemporalAdjusters.lastDayOfMonth());

            }
            startDate = DateUtils.getDateByZoneDateTime(startZonedDate);
            endDate = DateUtils.getDateByZoneDateTime(endZonedDate);
        }
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId, startDate, endDate);
        DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
        DailyTimeBankEntry dailyTimeBankEntry = timeBankRepository.findLastTimeBankByUnitPositionId(unitPositionId, startDate);
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(unitPositionId, startDate, endDate);
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndDate(unitPositionId, startDate, endDate);
        Long countryId = genericIntegrationService.getCountryIdOfOrganization(unitId);
        Map<String, List<TimeType>> presenceAbsenceTimeTypeMap = timeTypeService.getPresenceAbsenceTimeType(countryId);
        return timeBankCalculationService.getVisualViewTimeBank(interval, dailyTimeBankEntry, shifts, dailyTimeBankEntries, presenceAbsenceTimeTypeMap, unitPositionWithCtaDetailsDTO);

    }


    /**
     * @param unitPositionWithCtaDetailsDTO
     * @param year
     * @return Interval
     */
    private Interval getIntervalByDateTimeBank(UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO, Integer year) {
        ZonedDateTime startDate = ZonedDateTime.now().withYear(year).with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = startDate.with(TemporalAdjusters.lastDayOfYear());
        Date unitPositionStartDate = unitPositionWithCtaDetailsDTO.getStartDate().isAfter(LocalDate.now()) ? new DateTime().withTimeAtStartOfDay().toDate() : DateUtils.asDate(unitPositionWithCtaDetailsDTO.getStartDate());
        Date unitPositionEndDate = unitPositionWithCtaDetailsDTO.getEndDate() == null || unitPositionWithCtaDetailsDTO.getEndDate().isAfter(LocalDate.now()) ? new DateTime().withTimeAtStartOfDay().toDate() : DateUtils.asDate(unitPositionWithCtaDetailsDTO.getEndDate());
        Interval unitPositionInterval = new Interval(unitPositionStartDate.getTime(), unitPositionEndDate.getTime());
        Interval selectedInterval = new Interval(startDate.toInstant().toEpochMilli(), endDate.toInstant().toEpochMilli());
        Interval interval = selectedInterval.overlap(unitPositionInterval);
        if (interval == null) {
            interval = new Interval(new DateTime().withTimeAtStartOfDay(), new DateTime().withTimeAtStartOfDay());
        }
        return interval;
    }
    /**
     *
     * @param unitPositionId
     * @param startDate
     * @param staffAdditionalInfoDTO
     * @Desc to update Time Bank after applying function in Unit position
     * @return
     */
    public boolean updateTimeBank(Long unitPositionId,Date startDate,StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        Date endDate = DateUtils.asDate(DateUtils.asZoneDateTime(startDate).plusMinutes(ONE_DAY_MINUTES));
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionId( staffAdditionalInfoDTO.getUnitPosition().getId(),startDate);
        if(ctaResponseDTO==null){
            exceptionService.dataNotFoundException("message.cta.notFound");
        }
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        shiftService.setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        Shift shift = new Shift(startDate,endDate,unitPositionId);
        saveTimeBank(staffAdditionalInfoDTO,shift);
        return true;
    }




}
