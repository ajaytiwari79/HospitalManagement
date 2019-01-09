package com.kairos.service.time_bank;


import com.google.common.collect.Sets;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.CopyShiftDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.activity.time_bank.TimeBankAndPayoutDTO;
import com.kairos.dto.activity.time_bank.TimeBankDTO;
import com.kairos.dto.activity.time_bank.TimeBankVisualViewDTO;
import com.kairos.dto.activity.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.pay_out.PayOut;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.persistence.repository.pay_out.PayOutTransactionMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutCalculationService;
import com.kairos.service.pay_out.PayOutTransaction;
import com.kairos.service.shift.ShiftService;
import com.kairos.utils.time_bank.TimeBankCalculationService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.commons.collections.CollectionUtils;
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

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.ONE_DAY_MINUTES;
import static com.kairos.constants.AppConstants.ORGANIZATION;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;


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
    private TimeTypeService timeTypeService;
    @Inject
    private PayOutRepository payOutRepository;
    @Inject
    private PayOutTransactionMongoRepository payOutTransactionMongoRepository;
    @Inject
    private PayOutCalculationService payOutCalculationService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ExceptionService exceptionService;
    @Inject private PlanningPeriodMongoRepository planningPeriodMongoRepository;


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
    public void saveTimeBanks(StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<Shift> shifts) {
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

    public void saveTimeBanksAndPayOut(List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, List<Shift> shifts, Map<BigInteger, ActivityWrapper> activityWrapperMap, Date startDate, Date endDate) {
        Date startDateTime = new DateTime(startDate).withTimeAtStartOfDay().toDate();
        Date endDateTime = new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate();
        Date shiftEndTime = new DateTime(shifts.get(shifts.size() - 1).getEndDate()).plusDays(1).withTimeAtStartOfDay().toDate();
        List<Long> unitPositionIds = new ArrayList<>(staffAdditionalInfoDTOS.stream().map(staffAdditionalInfoDTO -> staffAdditionalInfoDTO.getUnitPosition().getId()).collect(Collectors.toSet()));
        timeBankRepository.deleteDailyTimeBank(unitPositionIds, startDateTime, endDateTime);
        List<ShiftWithActivityDTO> shiftsList = shiftMongoRepository.findAllShiftsBetweenDurationByUnitPositions(unitPositionIds, startDateTime, endDateTime);
        Map<String, List<ShiftWithActivityDTO>> shiftDateMap = shiftsList.stream().collect(Collectors.groupingBy(k -> k.getUnitPositionId() + "" + DateUtils.asLocalDate(k.getStartDate())));
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>();
        List<PayOut> payOutList = payOutRepository.findAllByUnitPositionsAndDate(unitPositionIds, startDateTime, endDateTime);
        Map<BigInteger, PayOut> shiftAndPayOutMap = payOutList.stream().collect(Collectors.toMap(k -> k.getShiftId(), v -> v));
        List<PayOut> payOuts = new ArrayList<>();
        Set<DateTimeInterval> dateTimeIntervals = timeBankCalculationService.getPlanningPeriodIntervals(shifts.get(0).getUnitId(), startDateTime, endDateTime);
        while (startDateTime.before(shiftEndTime)) {
            for (StaffAdditionalInfoDTO staffAdditionalInfoDTO : staffAdditionalInfoDTOS) {
                List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftDateMap.getOrDefault(staffAdditionalInfoDTO.getUnitPosition().getId() + "" + DateUtils.asLocalDate(startDateTime), new ArrayList<>());
                Interval interval = new Interval(startDateTime.getTime(), DateUtils.asDate(DateUtils.asZoneDateTime(startDateTime).plusDays(1)).getTime());
                staffAdditionalInfoDTO.getUnitPosition().setStaffId(staffAdditionalInfoDTO.getId());
                DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(staffAdditionalInfoDTO.getUnitPosition(), interval, shiftWithActivityDTOS, new HashMap<>(), dateTimeIntervals, staffAdditionalInfoDTO.getDayTypes());
                if (dailyTimeBank != null) {
                    dailyTimeBanks.add(dailyTimeBank);
                }
                DateTimeInterval dateTimeInterval = new DateTimeInterval(startDateTime.getTime(), plusDays(startDateTime,1).getTime());
                List<Shift> shiftList = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftWithActivityDTOS, Shift.class);
                for (Shift shift : shiftList) {
                    PayOut payOut = shiftAndPayOutMap.getOrDefault(shift.getId(), new PayOut(shift.getId(), shift.getUnitPositionId(), shift.getStaffId(), dateTimeInterval.getStartLocalDate(), shift.getUnitId()));
                    payOut = payOutCalculationService.calculateAndUpdatePayOut(dateTimeInterval, staffAdditionalInfoDTO.getUnitPosition(), shift, activityWrapperMap, payOut,staffAdditionalInfoDTO.getDayTypes());
                    if (payOut.getTotalPayOutMin() > 0) {
                        payOuts.add(payOut);
                    }
                }
            }
            startDateTime = plusDays(startDateTime,1);
        }
        Map<BigInteger, Shift> shiftIdAndShiftMap = shifts.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        shiftAndPayOutMap.entrySet().forEach(k -> {
            if (shiftIdAndShiftMap.get(k.getKey()) == null) {
                PayOut deletePayOut = shiftAndPayOutMap.get(k.getKey());
                deletePayOut.setDeleted(true);
                payOuts.add(deletePayOut);
            }
        });
        if (!payOuts.isEmpty()) {
            payOutRepository.saveEntities(payOuts);
        }
        if (!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }
    }



    private List<DailyTimeBankEntry> renewDailyTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift) {
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = new DateTime(shift.getEndDate()).plusDays(1).withTimeAtStartOfDay();
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByUnitPositionIdAndBetweenDates(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate.toDate(), endDate.toDate());
        Map<String,DailyTimeBankEntry> dailyTimeBankEntryAndUnitPositionMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(k->k.getUnitPositionId()+""+k.getDate(),v->v));
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>();
        Set<DateTimeInterval> dateTimeIntervals = timeBankCalculationService.getPlanningPeriodIntervals(shift.getUnitId(),startDate.toDate(),endDate.toDate());
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByUnitPosition(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate.toDate(),endDate.toDate());
        while (startDate.isBefore(endDate)) {
            Interval interval = new Interval(startDate, startDate.plusDays(1).withTimeAtStartOfDay());
            List<ShiftWithActivityDTO> shiftWithActivityDTOList = getShiftsByInterval(shiftWithActivityDTOS, interval);
            staffAdditionalInfoDTO.getUnitPosition().setStaffId(staffAdditionalInfoDTO.getId());
            DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(staffAdditionalInfoDTO.getUnitPosition(), interval, shiftWithActivityDTOList,dailyTimeBankEntryAndUnitPositionMap,dateTimeIntervals,staffAdditionalInfoDTO.getDayTypes());
            if (dailyTimeBank != null) {
                dailyTimeBanks.add(dailyTimeBank);
            }
            startDate = startDate.plusDays(1);
        }
        if (CollectionUtils.isNotEmpty(dailyTimeBanks)) {
            updateBonusHoursOfTimeBankInShift(shiftWithActivityDTOS, Arrays.asList(shift));
        }
        return dailyTimeBanks;
    }


    /**
     * @param unitPositionId
     * @return UnitPositionWithCtaDetailsDTO
     */
    public UnitPositionWithCtaDetailsDTO getCostTimeAgreement(Long unitPositionId, Date startDate, Date endDate) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = genericIntegrationService.getCTAbyUnitEmployementPosition(unitPositionId);
        if (!Optional.ofNullable(unitPositionWithCtaDetailsDTO).isPresent()) {
            exceptionService.dataNotFoundException("message.staffUnitPosition.notFound");
        }
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByUnitPositionIdBetweenDate(unitPositionId, startDate, endDate);
        List<CTARuleTemplateDTO> ruleTemplates = ctaResponseDTOS.stream().flatMap(ctaResponseDTO -> ctaResponseDTO.getRuleTemplates().stream()).collect(toList());
        ruleTemplates = ruleTemplates.stream().filter(ObjectUtils.distinctByKey(CTARuleTemplateDTO::getName)).collect(toList());
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
        endDate = asDate(DateUtils.asLocalDate(endDate).plusDays(1));
        List<DailyTimeBankEntry> dailyTimeBanks = timeBankRepository.findAllByUnitPositionAndDate(unitPositionId, startDate, endDate);
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByUnitPosition(unitPositionId, startDate, endDate);
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getCostTimeAgreement(unitPositionId, startDate, endDate);
        long totalTimeBankBeforeStartDate = 0;
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeTypeByCountryId(unitPositionWithCtaDetailsDTO.getCountryId());
        if (new DateTime(startDate).isAfter(toJodaDateTime(unitPositionWithCtaDetailsDTO.getStartDate()))) {
            Interval interval = new Interval(toJodaDateTime(unitPositionWithCtaDetailsDTO.getStartDate()), new DateTime(startDate));
            //totaltimebank is timebank without daily timebank entries
            List<DailyTimeBankEntry> dailyTimeBanksBeforeStartDate = timeBankRepository.findAllByUnitPositionAndBeforeDate(unitPositionId, new DateTime(startDate).toDate());
            Set<DateTimeInterval> planningPeriodIntervals = timeBankCalculationService.getPlanningPeriodIntervals(unitId, interval.getStart().toDate(), interval.getEnd().toDate());
            int totalTimeBank = timeBankCalculationService.calculateTimeBankForInterval(planningPeriodIntervals, interval, unitPositionWithCtaDetailsDTO, false, dailyTimeBanksBeforeStartDate, false);
            totalTimeBankBeforeStartDate = isCollectionNotEmpty(dailyTimeBanksBeforeStartDate)
                    ? dailyTimeBanksBeforeStartDate.stream().mapToInt(dailyTimeBank -> dailyTimeBank.getTotalTimeBankMin()).sum() : 0;
            totalTimeBankBeforeStartDate = totalTimeBankBeforeStartDate - totalTimeBank;
        }
        List<PayOutTransaction> payOutTransactions = payOutTransactionMongoRepository.findAllByUnitPositionIdAndDate(unitPositionId, startDate, endDate);
        List<PayOut> payOuts = payOutRepository.findAllByUnitPositionAndDate(unitPositionId, startDate, endDate);
        TimeBankAndPayoutDTO timeBankAndPayoutDTO = timeBankCalculationService.getTimeBankAdvanceView(unitId, totalTimeBankBeforeStartDate, startDate, endDate, query, shiftQueryResultWithActivities, dailyTimeBanks, unitPositionWithCtaDetailsDTO, timeTypeDTOS, payOuts, payOutTransactions);
        //timeBankDTO1.setCostTimeAgreement(getCostTimeAgreement(145l));
        return timeBankAndPayoutDTO;
    }

    /**
     * @param unitPositionId
     * @param year
     * @return TimeBankDTO
     */
    public TimeBankDTO getOverviewTimeBank(Long unitId, Long unitPositionId, Integer year) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = genericIntegrationService.getCTAbyUnitEmployementPosition(unitPositionId);
        Interval interval = getIntervalByDateTimeBank(unitPositionWithCtaDetailsDTO, year);
        List<DailyTimeBankEntry> dailyTimeBankEntries = new ArrayList<>();
        if (interval.getStart().getYear() <= new DateTime().getYear()) {
            dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndDate(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        }
        return timeBankCalculationService.getTimeBankOverview(unitId, unitPositionId, interval.getStart().dayOfYear().withMinimumValue(), interval.getEnd().dayOfYear().withMaximumValue(), dailyTimeBankEntries, unitPositionWithCtaDetailsDTO);
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
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUnitPosition(unitPositionId, startDate, endDate);
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
        Date unitPositionStartDate = unitPositionWithCtaDetailsDTO.getStartDate().isAfter(LocalDate.now()) ? new DateTime().withTimeAtStartOfDay().toDate() : asDate(unitPositionWithCtaDetailsDTO.getStartDate());
        Date unitPositionEndDate = unitPositionWithCtaDetailsDTO.getEndDate() == null || unitPositionWithCtaDetailsDTO.getEndDate().isAfter(LocalDate.now()) ? new DateTime().withTimeAtStartOfDay().toDate() : asDate(unitPositionWithCtaDetailsDTO.getEndDate());
        Interval unitPositionInterval = new Interval(unitPositionStartDate.getTime(), unitPositionEndDate.getTime());
        Interval selectedInterval = new Interval(startDate.toInstant().toEpochMilli(), endDate.toInstant().toEpochMilli());
        Interval interval = selectedInterval.overlap(unitPositionInterval);
        if (interval == null) {
            interval = new Interval(new DateTime().withTimeAtStartOfDay(), new DateTime().withTimeAtStartOfDay());
        }
        return interval;
    }

    /**
     * @param unitPositionId
     * @param startDate
     * @param staffAdditionalInfoDTO
     * @return
     * @Desc to update Time Bank after applying function in Unit position
     */
    public boolean updateTimeBank(Long unitPositionId, Date startDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Date endDate = asDate(DateUtils.asZoneDateTime(startDate).plusMinutes(ONE_DAY_MINUTES));
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate);
        if (ctaResponseDTO == null) {
            exceptionService.dataNotFoundException("message.cta.notFound");
        }
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        shiftService.setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);

        Shift shift = new Shift(startDate,endDate,unitPositionId,new ArrayList<>());
        shift.setStaffId(staffAdditionalInfoDTO.getUnitPosition().getStaffId());
        saveTimeBank(staffAdditionalInfoDTO,shift);
        return true;
    }

    /**
     * This function is used to update TimeBank when Staff Personalized CTA
     * or individual unitPositionLine is changed at a time
     * @param unitPositionId
     * @param startDate
     * @param staffAdditionalInfoDTO
     * @return
     */
    public boolean updateTimeBankOnUnitPositionModification(BigInteger ctaId,Long unitPositionId, Date startDate, Date endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Map<LocalDate,CTAResponseDTO> ctaResponseDTOMap = new HashMap<>();
        List<Shift> shifts = shiftMongoRepository.findAllShiftByIntervalAndUnitPositionId(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate,endDate);
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>(shifts.size());
        for (Shift shift : shifts) {
            LocalDate shiftDate = DateUtils.asLocalDate(shift.getStartDate());
            CTAResponseDTO ctaResponseDTO;
            if(Optional.ofNullable(ctaId).isPresent()){
                ctaResponseDTO = costTimeAgreementRepository.getOneCtaById(ctaId);
            }else {
                ctaResponseDTO = ctaResponseDTOMap.getOrDefault(shiftDate, costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(unitPositionId, DateUtils.asDate(shiftDate)));
            }
            if (ctaResponseDTO == null) {
                exceptionService.dataNotFoundException("message.cta.notFound");
            }
            staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
            shiftService.setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            dailyTimeBanks.addAll(renewDailyTimeBank(staffAdditionalInfoDTO,shift));
            ctaResponseDTOMap.put(shiftDate,ctaResponseDTO);
        }
        if (!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }
        return true;
    }

    /*private List<DailyTimeBankEntry> renewDailyTimeBankOnUnitPositionModification(StaffAdditionalInfoDTO staffAdditionalInfoDTO,Date firstShiftStartDate,Date lastShiftStartDate, List<ShiftWithActivityDTO> shiftsWithActivities) {
        DateTime startDate = new DateTime(firstShiftStartDate).withTimeAtStartOfDay();
        DateTime endDate = new DateTime(lastShiftStartDate).plusDays(1).withTimeAtStartOfDay();
        timeBankRepository.deleteDailyTimeBank(Arrays.asList(staffAdditionalInfoDTO.getUnitPosition().getId()), firstShiftStartDate, endDate.toDate());
        List<DailyTimeBankEntry> dailyTimeBankEntries=new ArrayList<>();
        Set<DateTimeInterval> dateTimeIntervals = timeBankCalculationService.getPlanningPeriodIntervals(staffAdditionalInfoDTO.getUnitId(),startDate.toDate(),endDate.toDate());
        while (startDate.isBefore(endDate)) {
            DateTime nextDate=startDate.plusDays(1).withTimeAtStartOfDay();
            Interval interval = new Interval(startDate, nextDate);
            List<ShiftWithActivityDTO> shiftsWithActivitiesByInterval = filterShiftsWithActivityByInterval(shiftsWithActivities,interval);
           DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(staffAdditionalInfoDTO, interval, shiftsWithActivitiesByInterval,new HashMap<>(),dateTimeIntervals,staffAdditionalInfoDTO.getDayTypes());
            if (dailyTimeBank != null) {
                dailyTimeBankEntries.add(dailyTimeBank);
            }
            startDate = startDate.plusDays(1);
        }
        return dailyTimeBankEntries;
    }*/


    /**
     *
     * @param shiftsWithActivities
     * @return
     */
    private List<ShiftWithActivityDTO> filterShiftsWithActivityByInterval(List<ShiftWithActivityDTO> shiftsWithActivities,Interval oneDayInterval) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = new ArrayList<>();
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftsWithActivities) {
            if (new Interval(shiftWithActivityDTO.getStartDate().getTime(), shiftWithActivityDTO.getEndDate().getTime()).overlaps(oneDayInterval)) {
                shiftWithActivityDTOS.add(shiftWithActivityDTO);
            }
        }
        return shiftWithActivityDTOS;
    }

    private List<ShiftWithActivityDTO> getShiftsByInterval(List<ShiftWithActivityDTO> shiftWithActivityDTOS, Interval interval) {
        DateTimeInterval dateTimeInterval = new DateTimeInterval(interval.getStartMillis(), interval.getEndMillis());
        List<ShiftWithActivityDTO> shifts = new ArrayList<>();
        shiftWithActivityDTOS.forEach(shift -> {
            DateTimeInterval shiftInterval = new DateTimeInterval(shift.getStartDate(), shift.getEndDate());
            if (dateTimeInterval.overlaps(shiftInterval)) {
                shifts.add(shift);
            }
        });
        return shifts;
    }

    private List<Shift> getShiftsInInterval(List<Shift> shifts, Interval interval) {
        DateTimeInterval dateTimeInterval = new DateTimeInterval(interval.getStartMillis(), interval.getEndMillis());
        List<Shift> filteredShifts = new ArrayList<>();
        shifts.forEach(shift -> {
            DateTimeInterval shiftInterval = new DateTimeInterval(shift.getStartDate(), shift.getEndDate());
            if (dateTimeInterval.overlaps(shiftInterval)) {
                filteredShifts.add(shift);
            }
        });
        return filteredShifts;
    }

    private void updateBonusHoursOfTimeBankInShift(List<ShiftWithActivityDTO> shiftWithActivityDTOS, List<Shift> shifts) {
        if(CollectionUtils.isNotEmpty(shifts)){
            Map<BigInteger, ShiftActivityDTO> shiftActivityDTOMap = shiftWithActivityDTOS.stream().flatMap(shift1 -> shift1.getActivities().stream()).collect(Collectors.toMap(k -> k.getId(), v -> v));
            for (Shift shift : shifts) {
                int timeBankCtaBonusMinutes = 0;
                for (ShiftActivity shiftActivity : shift.getActivities()) {
                    if (shiftActivityDTOMap.containsKey(shiftActivity.getId())) {
                        ShiftActivityDTO shiftActivityDTO = shiftActivityDTOMap.get(shiftActivity.getId());
                        shiftActivity.setTimeBankCtaBonusMinutes(shiftActivityDTO.getTimeBankCtaBonusMinutes());
                        timeBankCtaBonusMinutes += shiftActivityDTO.getTimeBankCtaBonusMinutes();
                        shiftActivity.setTimeBankCTADistributions(ObjectMapperUtils.copyPropertiesOfListByMapper(shiftActivityDTO.getTimeBankCTADistributions(), TimeBankCTADistribution.class));
                    }
                }
                shift.setTimeBankCtaBonusMinutes(timeBankCtaBonusMinutes);
            }
            shiftMongoRepository.saveEntities(shifts);
        }
    }

    public boolean renewTimeBankOfShifts() {
        List<Shift> shifts = shiftMongoRepository.findAllByDeletedFalse();
        Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoDTOMap = new HashMap<>();
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>(shifts.size());
        for (Shift shift : shifts) {
            try {
                StaffAdditionalInfoDTO staffAdditionalInfoDTO = genericIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()), shift.getStaffId(), ORGANIZATION, shift.getUnitPositionId(), new HashSet<>());
                CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shift.getStartDate());
                if(Optional.ofNullable(ctaResponseDTO).isPresent() && CollectionUtils.isNotEmpty(ctaResponseDTO.getRuleTemplates())){
                    staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
                    shiftService.setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
                    staffAdditionalInfoDTOMap.put(staffAdditionalInfoDTO.getUnitPosition().getId(), staffAdditionalInfoDTO);
                    if (staffAdditionalInfoDTOMap.containsKey(shift.getUnitPositionId())) {
                        dailyTimeBanks.addAll(renewDailyTimeBank(staffAdditionalInfoDTOMap.get(shift.getUnitPositionId()),shift));
                    }
                }
            } catch (Exception e) {
                logger.info("staff is not the part of this Unit");
            }

            if (staffAdditionalInfoDTOMap.containsKey(shift.getUnitPositionId()) && CollectionUtils.isNotEmpty(staffAdditionalInfoDTOMap.get(shift.getUnitPositionId()).getUnitPosition().getCtaRuleTemplates())) {
                List<DailyTimeBankEntry> dailyTimeBankEntries = renewDailyTimeBank(staffAdditionalInfoDTOMap.get(shift.getUnitPositionId()), shift);
                dailyTimeBanks.addAll(dailyTimeBankEntries);
            }
        }
        if (CollectionUtils.isNotEmpty(dailyTimeBanks)) {
            save(dailyTimeBanks);
        }
        return true;

    }

    public void updateDailyTimeBankEntries(CopyShiftDTO copyShiftDTO, List<Shift> shifts, StaffUnitPositionDetails staffUnitPosition, Map<DateTimeInterval, PlanningPeriodDTO> planningPeriodMap, Map<BigInteger, ActivityWrapper> activityMap, List<DayTypeDTO> dayTypeDTOS) {

        LocalDate startDate =  copyShiftDTO.getStartDate();
        LocalDate endDate = copyShiftDTO.getEndDate();
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndDate(staffUnitPosition.getId(), asDate( startDate.minusDays(1)), asDate(endDate));
        Map<String, DailyTimeBankEntry> dailyTimeBankEntryAndUnitPositionMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(k -> k.getUnitPositionId() + "" + k.getDate(), v -> v));
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>();
        Optional<DailyTimeBankEntry> dailyTimeBankEntry = dailyTimeBankEntries.stream().filter(timebank -> timebank.getDate().isEqual(copyShiftDTO.getStartDate())).findAny();
        long accumulatedTimeBank = dailyTimeBankEntry.isPresent() ? dailyTimeBankEntry.get().getAccumultedTimeBankMin() : 0;
        Set<DateTimeInterval> dateTimeIntervals = planningPeriodMap.keySet();
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = new ArrayList<>();
        while (startDate.isBefore(endDate)||startDate.isEqual(endDate)) {
            Interval interval = new Interval(DateUtils.getDateByLocalDate(startDate).getTime(),DateUtils.getMillisFromLocalDateTime(startDate.plusDays(1).atStartOfDay()));
            shiftWithActivityDTOS.addAll(convertShiftsIntoDTOS(getShiftsInInterval(shifts, interval), activityMap));

            DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(staffUnitPosition, interval, shiftWithActivityDTOS, dailyTimeBankEntryAndUnitPositionMap, dateTimeIntervals, dayTypeDTOS);
            if (dailyTimeBank != null) {
                accumulatedTimeBank = accumulatedTimeBank + dailyTimeBank.getTotalTimeBankMin();
                dailyTimeBank.setAccumultedTimeBankMin(accumulatedTimeBank);
                dailyTimeBanks.add(dailyTimeBank);
            }
            startDate = startDate.plusDays(1);
        }
        if (!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBankEntries);
            updateBonusHoursOfTimeBankInShiftAndSave(shiftWithActivityDTOS, shifts);
        }
        // return dailyTimeBanks;

    }

    private void updateBonusHoursOfTimeBankInShiftAndSave(List<ShiftWithActivityDTO> shiftWithActivityDTOS, List<Shift> shifts) {
        Map<BigInteger, ShiftActivityDTO> shiftActivityDTOMap = shiftWithActivityDTOS.stream().flatMap(shift1 -> shift1.getActivities().stream()).collect(Collectors.toMap(k -> k.getId(), v -> v));
        shifts.forEach(current -> current.getActivities().forEach(shiftActivity -> {
            ShiftActivityDTO shiftActivityDTO = shiftActivityDTOMap.get(shiftActivity.getId());
            shiftActivity.setTimeBankCtaBonusMinutes(shiftActivityDTO.getTimeBankCtaBonusMinutes());
            shiftActivity.setTimeBankCTADistributions(ObjectMapperUtils.copyPropertiesOfListByMapper(shiftActivityDTO.getTimeBankCTADistributions(), TimeBankCTADistribution.class));
        }));
        shiftMongoRepository.saveAll(shifts);
    }

    private List<ShiftWithActivityDTO> convertShiftsIntoDTOS(List<Shift> shifts, Map<BigInteger, ActivityWrapper> activityMap) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = new ArrayList<>();
        shifts.forEach(shift -> shiftWithActivityDTOS.add(shiftService.convertIntoShiftWithActivity(shift, activityMap)));
        return shiftWithActivityDTOS;
    }

    public ShiftDTO updateShiftDTOWithTimebankDetails(ShiftDTO shiftDTO,StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getUnitPositionDetailDTO(staffAdditionalInfoDTO);
        LocalDate startDate = unitPositionWithCtaDetailsDTO.getStartDate();
        LocalDate endDate = asLocalDate(shiftDTO.getStartDate());
        Set<DateTimeInterval> planningPeriodIntervals = timeBankCalculationService.getPlanningPeriodIntervals(shiftDTO.getUnitId(), asDate(startDate), asDate(endDate));
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndDate(unitPositionWithCtaDetailsDTO.getId(),asDate(startDate),asDate(endDate.plusDays(1)));
        Map<LocalDate,DailyTimeBankEntry> dateDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(toMap(k->k.getDate(),v->v));
        int accumulatedTimebank = 0;
        while (startDate.isBefore(endDate)){
            if(dateDailyTimeBankEntryMap.containsKey(startDate)){
                accumulatedTimebank+= dateDailyTimeBankEntryMap.get(startDate).getTotalTimeBankMin();
            }else {
                accumulatedTimebank+= (- timeBankCalculationService.getContractualAndTimeBankByPlanningPeriod(planningPeriodIntervals,startDate,staffAdditionalInfoDTO.getUnitPosition().getPositionLines()));
            }
            startDate = startDate.plusDays(1);
        }
        int totalTimeBankMinutes;
        if(dateDailyTimeBankEntryMap.containsKey(startDate)){
            totalTimeBankMinutes = dateDailyTimeBankEntryMap.get(startDate).getTotalTimeBankMin();
        }else {
            totalTimeBankMinutes = (- timeBankCalculationService.getContractualAndTimeBankByPlanningPeriod(planningPeriodIntervals,startDate,staffAdditionalInfoDTO.getUnitPosition().getPositionLines()));
        }
        shiftDTO.setDeltaTimeBankMinutes(totalTimeBankMinutes);
        for (ShiftActivityDTO shiftActivityDTO : shiftDTO.getActivities()) {
            shiftActivityDTO.setPlannedMinutes(shiftActivityDTO.getScheduledMinutes() + shiftActivityDTO.getTimeBankCtaBonusMinutes());
        }
        shiftDTO.setPlannedMinutes(shiftDTO.getScheduledMinutes() + shiftDTO.getTimeBankCtaBonusMinutes());
        shiftDTO.setAccumulatedTimeBankMinutes(accumulatedTimebank+totalTimeBankMinutes);
        return shiftDTO;
    }

    public List<ShiftDTO> updateTimebankDetailsInShiftDTO(List<ShiftDTO> shiftDTOS,LocalDate startDate,LocalDate endDate,StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        if(isCollectionNotEmpty(shiftDTOS)){
            ShiftDTO firstShiftDTO = shiftDTOS.get(0);
            UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getUnitPositionDetailDTO(staffAdditionalInfoDTO);
            LocalDate unitPositionEndDate = isNull(unitPositionWithCtaDetailsDTO.getEndDate()) ? endDate : unitPositionWithCtaDetailsDTO.getEndDate().isAfter(endDate) ? endDate : unitPositionWithCtaDetailsDTO.getEndDate();
            List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndDate(unitPositionWithCtaDetailsDTO.getId(),asDate(unitPositionWithCtaDetailsDTO.getStartDate()),asDate(endDate));
            Set<DateTimeInterval> planningPeriodIntervals = timeBankCalculationService.getPlanningPeriodIntervals(firstShiftDTO.getUnitId(), asDate(startDate), asDate(unitPositionEndDate));
            long accumulatedTimebank = 0;//getAccumulatedTimebank(unitPositionWithCtaDetailsDTO,tillDate,planningPeriodIntervals);
            Map<LocalDate,List<ShiftDTO>> dateWiseShiftMap = shiftDTOS.stream().collect(Collectors.groupingBy(s->DateUtils.asLocalDate(s.getStartDate()),toList()));
            Map<LocalDate,DailyTimeBankEntry> dateDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(toMap(k->k.getDate(),v->v));
            while (startDate.isBefore(endDate)){
                if(startDate.isAfter(unitPositionWithCtaDetailsDTO.getStartDate()) || startDate.equals(unitPositionWithCtaDetailsDTO.getStartDate())){
                    int totalTimeBankMinutes;
                    if(dateDailyTimeBankEntryMap.containsKey(startDate)){
                        totalTimeBankMinutes = dateDailyTimeBankEntryMap.get(startDate).getTotalTimeBankMin();
                    }else {
                        totalTimeBankMinutes = (- timeBankCalculationService.getContractualAndTimeBankByPlanningPeriod(planningPeriodIntervals,startDate,staffAdditionalInfoDTO.getUnitPosition().getPositionLines()));
                    }
                    accumulatedTimebank+=totalTimeBankMinutes;
                    System.out.println(startDate +":"+ totalTimeBankMinutes +":"+ accumulatedTimebank);
                    if(dateWiseShiftMap.containsKey(startDate)){
                        for (ShiftDTO shiftDTO : dateWiseShiftMap.get(startDate)) {
                            shiftDTO.setDeltaTimeBankMinutes(totalTimeBankMinutes);
                            shiftDTO.setAccumulatedTimeBankMinutes(accumulatedTimebank);
                            shiftDTO.setPlannedMinutes(shiftDTO.getScheduledMinutes() + shiftDTO.getTimeBankCtaBonusMinutes());
                        }
                    }
                }
                startDate = startDate.plusDays(1);
            }
        }
        return shiftDTOS;
    }


    private UnitPositionWithCtaDetailsDTO getUnitPositionDetailDTO(StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        return new UnitPositionWithCtaDetailsDTO(staffAdditionalInfoDTO.getUnitPosition().getId(),staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyHours(),staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyMinutes(),staffAdditionalInfoDTO.getUnitPosition().getWorkingDaysInWeek(),staffAdditionalInfoDTO.getUnitPosition().getStaffId(),staffAdditionalInfoDTO.getUnitPosition().getStartDate(),staffAdditionalInfoDTO.getUnitPosition().getEndDate(),staffAdditionalInfoDTO.getUnitPosition().getPositionLines());
    }

}
