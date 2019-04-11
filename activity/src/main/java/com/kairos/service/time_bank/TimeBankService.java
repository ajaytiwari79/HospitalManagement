package com.kairos.service.time_bank;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.activity.time_bank.*;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
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
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutCalculationService;
import com.kairos.service.pay_out.PayOutTransaction;
import com.kairos.service.shift.ShiftService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
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
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.setDayTypeToCTARuleTemplate;
import static java.util.stream.Collectors.*;

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
    private UserIntegrationService userIntegrationService;
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
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;

    /**
     * @param staffAdditionalInfoDTO
     * @param shift
     * @Description This method is used for update DailyTimebankEntry when Shift Create,Update,Delete
     */
    public void updateTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, boolean validatedByPlanner) {
        staffAdditionalInfoDTO.getUnitPosition().setStaffId(shift.getStaffId());
        List<DailyTimeBankEntry> dailyTimeBanks = renewDailyTimeBank(staffAdditionalInfoDTO, shift, validatedByPlanner);
        if(!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }
    }

    public boolean updateTimeBankForMultipleShifts(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Date startDate, Date endDate) {
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        List<DailyTimeBankEntry> updatedDailyTimeBankEntries = new ArrayList<>();
        List<DailyTimeBankEntry> dailyTimeBankEntries = renewDailyTimeBank(staffAdditionalInfoDTO, startDate, endDate, staffAdditionalInfoDTO.getUnitId());
        updatedDailyTimeBankEntries.addAll(dailyTimeBankEntries);
        if(isCollectionNotEmpty(updatedDailyTimeBankEntries)) {
            timeBankRepository.saveEntities(updatedDailyTimeBankEntries);
        }
        return true;
    }

    public void saveTimeBanksAndPayOut(List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, List<Shift> shifts, Map<BigInteger, ActivityWrapper> activityWrapperMap, Date startDate, Date endDate) {
        Date startDateTime = new DateTime(startDate).withTimeAtStartOfDay().toDate();
        Date endDateTime = new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate();
        Date shiftEndTime = new DateTime(shifts.get(shifts.size() - 1).getEndDate()).plusDays(1).withTimeAtStartOfDay().toDate();
        List<Long> unitPositionIds = new ArrayList<>(staffAdditionalInfoDTOS.stream().map(staffAdditionalInfoDTO -> staffAdditionalInfoDTO.getUnitPosition().getId()).collect(Collectors.toSet()));
        timeBankRepository.deleteDailyTimeBank(unitPositionIds, startDateTime, endDateTime);
        List<ShiftWithActivityDTO> shiftsList = shiftMongoRepository.findAllShiftsBetweenDurationByUnitPositions(unitPositionIds, startDateTime, endDateTime);
        Map<String, List<ShiftWithActivityDTO>> shiftDateMap = shiftsList.stream().collect(Collectors.groupingBy(k -> k.getUnitPositionId() + "-" + DateUtils.asLocalDate(k.getStartDate())));
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>();
        List<PayOutPerShift> payOutPerShiftList = payOutRepository.findAllByUnitPositionsAndDate(unitPositionIds, startDateTime, endDateTime);
        Map<BigInteger, PayOutPerShift> shiftAndPayOutMap = payOutPerShiftList.stream().collect(Collectors.toMap(k -> k.getShiftId(), v -> v));
        List<PayOutPerShift> payOutPerShifts = new ArrayList<>();
        Set<DateTimeInterval> dateTimeIntervals = timeBankCalculationService.getPlanningPeriodIntervals(shifts.get(0).getUnitId(), startDateTime, endDateTime);
        while (startDateTime.before(shiftEndTime)) {
            for (StaffAdditionalInfoDTO staffAdditionalInfoDTO : staffAdditionalInfoDTOS) {
                List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftDateMap.getOrDefault(staffAdditionalInfoDTO.getUnitPosition().getId() + "-" + DateUtils.asLocalDate(startDateTime), new ArrayList<>());
                DateTimeInterval interval = new DateTimeInterval(startDateTime.getTime(), DateUtils.asDate(DateUtils.asZoneDateTime(startDateTime).plusDays(1)).getTime());
                staffAdditionalInfoDTO.getUnitPosition().setStaffId(staffAdditionalInfoDTO.getId());
                DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(staffAdditionalInfoDTO.getUnitPosition(), interval, shiftWithActivityDTOS, new HashMap<>(), dateTimeIntervals, staffAdditionalInfoDTO.getDayTypes(), false);
                if(dailyTimeBank != null) {
                    dailyTimeBanks.add(dailyTimeBank);
                }
                DateTimeInterval dateTimeInterval = new DateTimeInterval(startDateTime.getTime(), plusDays(startDateTime, 1).getTime());
                List<Shift> shiftList = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftWithActivityDTOS, Shift.class);
                for (Shift shift : shiftList) {
                    PayOutPerShift payOutPerShift = shiftAndPayOutMap.getOrDefault(shift.getId(), new PayOutPerShift(shift.getId(), shift.getUnitPositionId(), shift.getStaffId(), dateTimeInterval.getStartLocalDate(), shift.getUnitId()));
                    payOutPerShift = payOutCalculationService.calculateAndUpdatePayOut(dateTimeInterval, staffAdditionalInfoDTO.getUnitPosition(), shift, activityWrapperMap, payOutPerShift, staffAdditionalInfoDTO.getDayTypes());
                    if(payOutPerShift.getTotalPayOutMinutes() > 0) {
                        payOutPerShifts.add(payOutPerShift);
                    }
                }
            }
            startDateTime = plusDays(startDateTime, 1);
        }
        Map<BigInteger, Shift> shiftIdAndShiftMap = shifts.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        shiftAndPayOutMap.entrySet().forEach(k -> {
            if(!shiftIdAndShiftMap.containsKey(k.getKey())) {
                PayOutPerShift deletePayOutPerShift = shiftAndPayOutMap.get(k.getKey());
                deletePayOutPerShift.setDeleted(true);
                payOutPerShifts.add(deletePayOutPerShift);
            }
        });
        if(!payOutPerShifts.isEmpty()) {
            payOutRepository.saveEntities(payOutPerShifts);
        }
        if(!dailyTimeBanks.isEmpty()) {
            timeBankRepository.saveEntities(dailyTimeBanks);
        }
        if(CollectionUtils.isNotEmpty(dailyTimeBanks)) {
            updateBonusHoursOfTimeBankInShift(shiftsList, shifts);
        }
    }

    private List<DailyTimeBankEntry> renewDailyTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, boolean validatedByPlanner) {
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = new DateTime(shift.getEndDate()).plusDays(1).withTimeAtStartOfDay();
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByUnitPositionIdAndBetweenDates(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate.toDate(), endDate.toDate());
        Map<String, DailyTimeBankEntry> dailyTimeBankEntryAndUnitPositionMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(k -> k.getUnitPositionId() + "-" + k.getDate(), v -> v));
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>();
        Set<DateTimeInterval> dateTimeIntervals = timeBankCalculationService.getPlanningPeriodIntervals(shift.getUnitId(), startDate.toDate(), endDate.toDate());
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByUnitPosition(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate.toDate(), endDate.toDate());
        while (startDate.isBefore(endDate)) {
            DateTimeInterval interval = new DateTimeInterval(startDate.getMillis(), startDate.plusDays(1).getMillis());
            List<ShiftWithActivityDTO> shiftWithActivityDTOList = getShiftsByInterval(shiftWithActivityDTOS, interval);
            staffAdditionalInfoDTO.getUnitPosition().setStaffId(staffAdditionalInfoDTO.getId());
            DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(staffAdditionalInfoDTO.getUnitPosition(), interval, shiftWithActivityDTOList, dailyTimeBankEntryAndUnitPositionMap, dateTimeIntervals, staffAdditionalInfoDTO.getDayTypes(), validatedByPlanner);
            if(isNotNull(dailyTimeBank)) {
                dailyTimeBanks.add(dailyTimeBank);
            }
            startDate = startDate.plusDays(1);
        }
        if(CollectionUtils.isNotEmpty(dailyTimeBanks)) {
            updateBonusHoursOfTimeBankInShift(shiftWithActivityDTOS, Arrays.asList(shift));
        }
        return dailyTimeBanks;
    }

    private List<DailyTimeBankEntry> renewDailyTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Date startDateTime, @Nullable Date endDateTime, Long unitId) {
        Date startDate = getStartOfDay(startDateTime);
        Date endDate = isNotNull(endDateTime) ? getEndOfDay(endDateTime) : null;
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByUnitPositionIdAndBetweenDates(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
        Map<String, DailyTimeBankEntry> dailyTimeBankEntryAndUnitPositionMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(k -> k.getUnitPositionId() + "-" + k.getDate(), v -> v));
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>();
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByUnitPosition(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
        if(isCollectionNotEmpty(shiftWithActivityDTOS)) {
            if(isNull(endDate)) {
                endDate = getEndOfDay(shiftWithActivityDTOS.get(shiftWithActivityDTOS.size() - 1).getEndDate());
            }
            Set<DateTimeInterval> dateTimeIntervals = timeBankCalculationService.getPlanningPeriodIntervals(unitId, startDate, endDate);
            List<Shift> shifts = shiftMongoRepository.findAllOverlappedShiftsAndUnitPositionId(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
            while (startDate.before(endDate)) {
                DateTimeInterval interval = new DateTimeInterval(startDate.getTime(), plusDays(startDate, 1).getTime());
                List<ShiftWithActivityDTO> shiftWithActivityDTOList = getShiftsByInterval(shiftWithActivityDTOS, interval);
                staffAdditionalInfoDTO.getUnitPosition().setStaffId(staffAdditionalInfoDTO.getId());
                DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(staffAdditionalInfoDTO.getUnitPosition(), interval, shiftWithActivityDTOList, dailyTimeBankEntryAndUnitPositionMap, dateTimeIntervals, staffAdditionalInfoDTO.getDayTypes(), false);
                if(dailyTimeBank != null) {
                    dailyTimeBanks.add(dailyTimeBank);
                }
                startDate = plusDays(startDate, 1);
            }
            if(CollectionUtils.isNotEmpty(dailyTimeBanks)) {
                updateBonusHoursOfTimeBankInShift(shiftWithActivityDTOS, shifts);
            }
        }
        return dailyTimeBanks;
    }

    /**
     * @param unitPositionId
     * @return UnitPositionWithCtaDetailsDTO
     */
    public UnitPositionWithCtaDetailsDTO getCostTimeAgreement(Long unitPositionId, Date startDate, Date endDate) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = userIntegrationService.getCTAbyUnitEmployementPosition(unitPositionId);
        if(!Optional.ofNullable(unitPositionWithCtaDetailsDTO).isPresent()) {
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
        if(new DateTime(startDate).isAfter(toJodaDateTime(unitPositionWithCtaDetailsDTO.getStartDate()))) {
            Interval interval = new Interval(toJodaDateTime(unitPositionWithCtaDetailsDTO.getStartDate()), new DateTime(startDate));
            //totaltimebank is timebank without daily timebank entries
            List<DailyTimeBankEntry> dailyTimeBanksBeforeStartDate = timeBankRepository.findAllByUnitPositionAndBeforeDate(unitPositionId, new DateTime(startDate).toDate());
            Set<DateTimeInterval> planningPeriodIntervals = timeBankCalculationService.getPlanningPeriodIntervals(unitId, interval.getStart().toDate(), interval.getEnd().toDate());
            int totalTimeBank = timeBankCalculationService.calculateTimeBankForInterval(planningPeriodIntervals, interval, unitPositionWithCtaDetailsDTO, false, dailyTimeBanksBeforeStartDate, false);
            totalTimeBankBeforeStartDate = isCollectionNotEmpty(dailyTimeBanksBeforeStartDate) ? dailyTimeBanksBeforeStartDate.stream().mapToInt(dailyTimeBank -> dailyTimeBank.getDeltaTimeBankMinutes()).sum() : 0;
            totalTimeBankBeforeStartDate = totalTimeBankBeforeStartDate - totalTimeBank;
        }
        totalTimeBankBeforeStartDate += unitPositionWithCtaDetailsDTO.getAccumulatedTimebankMinutes();
        List<PayOutTransaction> payOutTransactions = payOutTransactionMongoRepository.findAllByUnitPositionIdAndDate(unitPositionId, startDate, endDate);
        List<Interval> intervals = timeBankCalculationService.getAllIntervalsBetweenDates(startDate, endDate, query);
        Map<Interval, List<PayOutTransaction>> payoutTransactionIntervalMap = timeBankCalculationService.getPayoutTrasactionIntervalsMap(intervals, payOutTransactions);
        return timeBankCalculationService.getTimeBankAdvanceView(intervals, unitId, totalTimeBankBeforeStartDate, startDate, endDate, query, shiftQueryResultWithActivities, dailyTimeBanks, unitPositionWithCtaDetailsDTO, timeTypeDTOS, payoutTransactionIntervalMap);
    }

    /**
     * @param unitPositionId
     * @param year
     * @return TimeBankDTO
     */
    public TimeBankDTO getOverviewTimeBank(Long unitId, Long unitPositionId, Integer year) {
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = userIntegrationService.getCTAbyUnitEmployementPosition(unitPositionId);
        Interval interval = getIntervalByDateTimeBank(unitPositionWithCtaDetailsDTO, year);
        List<DailyTimeBankEntry> dailyTimeBankEntries = new ArrayList<>();
        if(interval.getStart().getYear() <= new DateTime().getYear()) {
            dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndDate(unitPositionId, interval.getStart().toDate(), interval.getEnd().toDate());
        }
        return timeBankCalculationService.getTimeBankOverview(unitId, unitPositionId, interval.getStart().dayOfYear().withMinimumValue(), interval.getEnd().dayOfYear().withMaximumValue(), dailyTimeBankEntries, unitPositionWithCtaDetailsDTO);
    }

    public TimeBankVisualViewDTO getTimeBankForVisualView(Long unitId, Long unitPositionId, String query, Integer value, Date startDate, Date endDate) {
        ZonedDateTime endZonedDate = null;
        ZonedDateTime startZonedDate = null;
        if(StringUtils.isNotEmpty(query)) {
            if(query.equals(AppConstants.WEEK)) {
                startZonedDate = ZonedDateTime.now().with(ChronoField.ALIGNED_WEEK_OF_YEAR, value).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS);
                endZonedDate = startZonedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            } else if(query.equals(AppConstants.MONTH)) {
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
        Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
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
        if(interval == null) {
            interval = new Interval(new DateTime().withTimeAtStartOfDay(), new DateTime().withTimeAtStartOfDay());
        }
        return interval;
    }

    /**
     * @param startDate
     * @param staffAdditionalInfoDTO
     * @return
     * @Desc to update Time Bank after applying function in Unit position
     */
    public boolean updateTimeBankOnFunctionChange(Date startDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Date endDate = plusMinutes(startDate, (int) ONE_DAY_MINUTES);
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate);
        if(ctaResponseDTO == null) {
            exceptionService.dataNotFoundException("message.cta.notFound");
        }
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        List<DailyTimeBankEntry> dailyTimeBanks = renewDailyTimeBank(staffAdditionalInfoDTO, startDate, endDate, staffAdditionalInfoDTO.getUnitId());
        if(!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }
        return true;
    }

    /**
     * This function is used to update TimeBank when Staff Personalized CTA
     * or individual unitPositionLine is changed at a time
     *
     * @param unitPositionId
     * @param startDate
     * @param staffAdditionalInfoDTO
     * @return
     */
    public boolean updateTimeBankOnUnitPositionModification(BigInteger ctaId, Long unitPositionId, Date startDate, Date endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Map<LocalDate, CTAResponseDTO> ctaResponseDTOMap = new HashMap<>();
        List<Shift> shifts = shiftMongoRepository.findAllShiftByIntervalAndUnitPositionId(staffAdditionalInfoDTO.getUnitPosition().getId(), startDate, endDate);
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>(shifts.size());
        for (Shift shift : shifts) {
            LocalDate shiftDate = DateUtils.asLocalDate(shift.getStartDate());
            CTAResponseDTO ctaResponseDTO;
            if(Optional.ofNullable(ctaId).isPresent()) {
                ctaResponseDTO = costTimeAgreementRepository.getOneCtaById(ctaId);
            } else {
                ctaResponseDTO = ctaResponseDTOMap.getOrDefault(shiftDate, costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(unitPositionId, DateUtils.asDate(shiftDate)));
            }
            if(ctaResponseDTO == null) {
                exceptionService.dataNotFoundException("message.cta.notFound");
            }
            staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            dailyTimeBanks.addAll(renewDailyTimeBank(staffAdditionalInfoDTO, shift, false));
            ctaResponseDTOMap.put(shiftDate, ctaResponseDTO);
        }
        if(!dailyTimeBanks.isEmpty()) {
            save(dailyTimeBanks);
        }
        return true;
    }

    private List<ShiftWithActivityDTO> getShiftsByInterval(List<ShiftWithActivityDTO> shiftWithActivityDTOS, DateTimeInterval interval) {
        DateTimeInterval dateTimeInterval = new DateTimeInterval(interval.getStartMillis(), interval.getEndMillis());
        List<ShiftWithActivityDTO> shifts = new ArrayList<>();
        shiftWithActivityDTOS.forEach(shift -> {
            DateTimeInterval shiftInterval = new DateTimeInterval(shift.getStartDate(), shift.getEndDate());
            if(dateTimeInterval.overlaps(shiftInterval)) {
                shifts.add(shift);
            }
        });
        return shifts;
    }

    private void updateBonusHoursOfTimeBankInShift(List<ShiftWithActivityDTO> shiftWithActivityDTOS, List<Shift> shifts) {
        if(CollectionUtils.isNotEmpty(shifts)) {
            Map<BigInteger, ShiftActivityDTO> shiftActivityDTOMap = shiftWithActivityDTOS.stream().flatMap(shift1 -> shift1.getActivities().stream()).collect(Collectors.toMap(k -> k.getId(), v -> v));
            for (Shift shift : shifts) {
                int timeBankCtaBonusMinutes = 0;
                int plannedMinutesOfTimebank = 0;
                for (ShiftActivity shiftActivity : shift.getActivities()) {
                    if(shiftActivityDTOMap.containsKey(shiftActivity.getId())) {
                        ShiftActivityDTO shiftActivityDTO = shiftActivityDTOMap.get(shiftActivity.getId());
                        shiftActivity.setTimeBankCtaBonusMinutes(shiftActivityDTO.getTimeBankCtaBonusMinutes());
                        timeBankCtaBonusMinutes += shiftActivityDTO.getTimeBankCtaBonusMinutes();
                        shiftActivity.setTimeBankCTADistributions(ObjectMapperUtils.copyPropertiesOfListByMapper(shiftActivityDTO.getTimeBankCTADistributions(), TimeBankCTADistribution.class));
                        shiftActivity.setPlannedMinutesOfTimebank(shiftActivityDTO.getScheduledMinutes() + shiftActivityDTO.getTimeBankCtaBonusMinutes());
                        plannedMinutesOfTimebank += shiftActivity.getPlannedMinutesOfTimebank();
                    }
                }
                shift.setTimeBankCtaBonusMinutes(timeBankCtaBonusMinutes);
                shift.setPlannedMinutesOfTimebank(plannedMinutesOfTimebank);
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
                StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()), shift.getStaffId(), ORGANIZATION, shift.getUnitPositionId(), new HashSet<>());
                CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByUnitPositionIdAndDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shift.getStartDate());
                if(Optional.ofNullable(ctaResponseDTO).isPresent() && CollectionUtils.isNotEmpty(ctaResponseDTO.getRuleTemplates())) {
                    staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
                    setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
                    staffAdditionalInfoDTOMap.put(staffAdditionalInfoDTO.getUnitPosition().getId(), staffAdditionalInfoDTO);
                    if(staffAdditionalInfoDTOMap.containsKey(shift.getUnitPositionId())) {
                        dailyTimeBanks.addAll(renewDailyTimeBank(staffAdditionalInfoDTOMap.get(shift.getUnitPositionId()), shift, false));
                    }
                }
            } catch (Exception e) {
                logger.info("staff is not the part of this Unit");
            }
            if(staffAdditionalInfoDTOMap.containsKey(shift.getUnitPositionId()) && CollectionUtils.isNotEmpty(staffAdditionalInfoDTOMap.get(shift.getUnitPositionId()).getUnitPosition().getCtaRuleTemplates())) {
                List<DailyTimeBankEntry> dailyTimeBankEntries = renewDailyTimeBank(staffAdditionalInfoDTOMap.get(shift.getUnitPositionId()), shift, false);
                dailyTimeBanks.addAll(dailyTimeBankEntries);
            }
        }
        if(CollectionUtils.isNotEmpty(dailyTimeBanks)) {
            save(dailyTimeBanks);
        }
        return true;

    }

    public void updateDailyTimeBankEntries(List<Shift> shifts, StaffUnitPositionDetails staffUnitPosition, List<DayTypeDTO> dayTypeDTOS) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = new StaffAdditionalInfoDTO(staffUnitPosition, dayTypeDTOS);
        if(isCollectionNotEmpty(shifts)) {
            shifts.sort(Comparator.comparing(Shift::getStartDate));
            Date startDate = shifts.get(0).getStartDate();
            Date endDate = shifts.get(shifts.size() - 1).getEndDate();
            updateTimeBankForMultipleShifts(staffAdditionalInfoDTO, startDate, endDate);
        }
    }

    public List<ShiftDTO> updateTimebankDetailsInShiftDTO(List<ShiftDTO> shiftDTOS) {
        if(isCollectionNotEmpty(shiftDTOS)) {
            for (ShiftDTO shiftDTO : shiftDTOS) {
                int plannedMinutes = 0;
                int timeBankCtaBonusMinutes = 0;
                int scheduledMinutes = 0;
                for (ShiftActivityDTO activity : shiftDTO.getActivities()) {
                    activity.setPlannedMinutesOfTimebank(activity.getScheduledMinutes() + activity.getTimeBankCtaBonusMinutes());
                    plannedMinutes += activity.getPlannedMinutesOfTimebank();
                    timeBankCtaBonusMinutes += activity.getTimeBankCtaBonusMinutes();
                    scheduledMinutes += activity.getScheduledMinutes();
                }
                shiftDTO.setPlannedMinutesOfTimebank(plannedMinutes);
                shiftDTO.setTimeBankCtaBonusMinutes(timeBankCtaBonusMinutes);
                shiftDTO.setScheduledMinutes(scheduledMinutes);
            }
        }
        return shiftDTOS;
    }

    private UnitPositionWithCtaDetailsDTO getUnitPositionDetailDTO(StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        return new UnitPositionWithCtaDetailsDTO(staffAdditionalInfoDTO.getUnitPosition().getId(), staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyHours(), staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyMinutes(), staffAdditionalInfoDTO.getUnitPosition().getWorkingDaysInWeek(), staffAdditionalInfoDTO.getUnitPosition().getStaffId(), staffAdditionalInfoDTO.getUnitPosition().getStartDate(), staffAdditionalInfoDTO.getUnitPosition().getEndDate(), staffAdditionalInfoDTO.getUnitPosition().getPositionLines(), staffAdditionalInfoDTO.getUnitPosition().getAccumulatedTimebankMinutes(), staffAdditionalInfoDTO.getUnitPosition().getAccumulatedTimebankDate());
    }

    public void deleteDuplicateEntry() {
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllAndDeletedFalse();
        Map<Long, TreeMap<LocalDate, DailyTimeBankEntry>> unitPositionIdAndDateMap = new TreeMap<>();
        List<DailyTimeBankEntry> duplicateEntry = new ArrayList<>();
        for (DailyTimeBankEntry dailyTimeBankEntry : dailyTimeBankEntries) {
            if(unitPositionIdAndDateMap.containsKey(dailyTimeBankEntry.getUnitPositionId())) {
                Map<LocalDate, DailyTimeBankEntry> localDateDateMap = unitPositionIdAndDateMap.get(dailyTimeBankEntry.getUnitPositionId());
                if(localDateDateMap.containsKey(dailyTimeBankEntry.getDate())) {
                    DailyTimeBankEntry dailyTimeBankEntry1 = localDateDateMap.get(dailyTimeBankEntry.getDate());
                    if(dailyTimeBankEntry1.getUpdatedAt().after(dailyTimeBankEntry.getUpdatedAt())) {
                        duplicateEntry.add(dailyTimeBankEntry);
                    } else {
                        duplicateEntry.add(dailyTimeBankEntry1);
                    }
                } else {
                    localDateDateMap.put(dailyTimeBankEntry.getDate(), dailyTimeBankEntry);
                    logger.info("Date Map :" + localDateDateMap.size());
                    logger.info("UnitPositionId Map :" + unitPositionIdAndDateMap.get(dailyTimeBankEntry.getUnitPositionId()).size());
                }

            } else {
                unitPositionIdAndDateMap.put(dailyTimeBankEntry.getUnitPositionId(), new TreeMap<>());
            }
        }
        logger.info("Duplicate remove entry count is " + duplicateEntry.size());
        timeBankRepository.deleteAll(duplicateEntry);
    }

    public boolean updateDailyTimeBankOnCTAChangeOfUnitPosition(StaffAdditionalInfoDTO staffAdditionalInfoDTO, CTAResponseDTO ctaResponseDTO) {
        Date startDate = asDate(ctaResponseDTO.getStartDate());
        Date endDate = isNotNull(ctaResponseDTO.getEndDate()) ? asDate(ctaResponseDTO.getEndDate()) : null;
        staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        return updateTimeBankForMultipleShifts(staffAdditionalInfoDTO, startDate, endDate);
    }

    public boolean updateDailyTimeBankEntriesForStaffs(List<Shift> shifts) {
        if(isCollectionNotEmpty(shifts)) {
            List<Long> staffIds = shifts.stream().map(shift -> shift.getStaffId()).collect(Collectors.toList());
            List<Long> unitPositionIds = shifts.stream().map(shift -> shift.getUnitPositionId()).collect(Collectors.toList());
            List<NameValuePair> requestParam = new ArrayList<>();
            requestParam.add(new BasicNameValuePair("staffIds", staffIds.toString()));
            requestParam.add(new BasicNameValuePair("unitPositionIds", unitPositionIds.toString()));
            List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getStaffAditionalDTOS(shifts.get(0).getUnitId(), requestParam);
            Date startDateTime = new DateTime(shifts.get(0).getStartDate()).withTimeAtStartOfDay().toDate();
            Date endDateTime = new DateTime(shifts.get(shifts.size() - 1).getEndDate()).plusDays(1).withTimeAtStartOfDay().toDate();
            List<ShiftWithActivityDTO> shiftsList = shiftMongoRepository.findAllShiftsBetweenDurationByUnitPositions(unitPositionIds, startDateTime, endDateTime);
            Map<String, List<ShiftWithActivityDTO>> shiftDateMap = shiftsList.stream().collect(Collectors.groupingBy(k -> k.getUnitPositionId() + "-" + DateUtils.asLocalDate(k.getStartDate())));
            List<DailyTimeBankEntry> updateDailyTimeBanks = new ArrayList<>();
            List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByUnitPositionIdsAndBetweenDates(unitPositionIds, startDateTime, endDateTime);
            Map<String, DailyTimeBankEntry> dailyTimeBankEntryAndUnitPositionMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(k -> k.getUnitPositionId() + "-" + k.getDate(), v -> v));
            Set<DateTimeInterval> dateTimeIntervals = timeBankCalculationService.getPlanningPeriodIntervals(shifts.get(0).getUnitId(), startDateTime, endDateTime);
            List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByUnitPositionIdsAndDate(unitPositionIds, startDateTime, endDateTime);
            Map<Long, List<CTAResponseDTO>> unitPositionAndCTAResponseMap = ctaResponseDTOS.stream().collect(groupingBy(CTAResponseDTO::getUnitPositionId));
            Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap = staffAdditionalInfoDTOS.stream().collect(Collectors.toMap(s -> s.getUnitPosition().getId(), v -> v));
            for (Shift shift : shifts) {
                StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffAdditionalInfoMap.get(shift.getUnitPositionId());
                CTAResponseDTO ctaResponseDTO = getCTAByDate(unitPositionAndCTAResponseMap.get(shift.getUnitPositionId()), asLocalDate(shift.getStartDate()));
                DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
                DateTime endDate = new DateTime(shift.getEndDate()).plusDays(1).withTimeAtStartOfDay();
                if(isNotNull(ctaResponseDTO)) {
                    staffAdditionalInfoDTO.getUnitPosition().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
                    setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
                    while (startDate.isBefore(endDate)) {
                        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftDateMap.getOrDefault(staffAdditionalInfoDTO.getUnitPosition().getId() + "-" + DateUtils.asLocalDate(startDateTime), new ArrayList<>());
                        DateTimeInterval interval = new DateTimeInterval(startDate.getMillis(), startDate.plusDays(1).getMillis());
                        staffAdditionalInfoDTO.getUnitPosition().setStaffId(staffAdditionalInfoDTO.getId());
                        DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.getTimeBankByInterval(staffAdditionalInfoDTO.getUnitPosition(), interval, shiftWithActivityDTOS, dailyTimeBankEntryAndUnitPositionMap, dateTimeIntervals, staffAdditionalInfoDTO.getDayTypes(), false);
                        if(dailyTimeBank != null) {
                            updateDailyTimeBanks.add(dailyTimeBank);
                        }
                        startDate = startDate.plusDays(1);
                    }
                }
            }
            if(isCollectionNotEmpty(updateDailyTimeBanks)) {
                timeBankRepository.saveEntities(updateDailyTimeBanks);
            }
        }
        return true;
    }

    private CTAResponseDTO getCTAByDate(List<CTAResponseDTO> ctaResponseDTOS, LocalDate shiftDate) {
        CTAResponseDTO ctaResponse = null;
        for (CTAResponseDTO ctaResponseDTO : ctaResponseDTOS) {
            if((ctaResponseDTO.getStartDate().equals(shiftDate) || ctaResponseDTO.getStartDate().isBefore(shiftDate)) && (ctaResponseDTO.getEndDate() == null || (ctaResponseDTO.getEndDate() != null && (ctaResponseDTO.getEndDate().equals(shiftDate) || ctaResponseDTO.getEndDate().isAfter(shiftDate))))) {
                ctaResponse = ctaResponseDTO;
                break;
            }
        }
        return ctaResponse;
    }

    public Map<LocalDate, TimeBankByDateDTO> getAccumulatedTimebankAndDeltaDTO(Long unitPositionId, Long unitId, LocalDate startDate, LocalDate endDate) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByUnitPositionId(unitId, null, ORGANIZATION, unitPositionId, new HashSet<>());
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndBeforeAndEqualsDate(unitPositionId, asDate(endDate));
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = getUnitPositionDetailDTO(staffAdditionalInfoDTO);
        Set<DateTimeInterval> planningPeriodIntervals = timeBankCalculationService.getPlanningPeriodIntervals(unitId, asDate(startDate), asDate(endDate));
        return timeBankCalculationService.getAccumulatedTimebankDTO(planningPeriodIntervals, dailyTimeBankEntries, unitPositionWithCtaDetailsDTO, startDate, endDate);
    }

    /*public Map<Long,Map<Long,Long>> getAccumulatedTimebankByUnitPositions(Map<Long, List<UnitPositionLinesDTO>> positionLinesMap){
        List<UnitPositionLinesDTO> unitPositionLinesDTOS = positionLinesMap.values().stream().flatMap(unitPositionLines -> unitPositionLines.stream()).collect(toList());
        unitPositionLinesDTOS.sort(Comparator.comparing(UnitPositionLinesDTO::getEndDateForAccumulatedTimebank));
        unitPositionLinesDTOS.
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndBeforeAndEqualsDate(unitPositionId, asDate(endDate));
    }*/

}
