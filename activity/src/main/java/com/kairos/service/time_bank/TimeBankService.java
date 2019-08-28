package com.kairos.service.time_bank;

import com.kairos.commons.utils.*;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.time_bank.*;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.persistence.repository.pay_out.PayOutTransactionMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutCalculationService;
import com.kairos.service.pay_out.PayOutTransaction;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.shift.ShiftService;
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
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_CTA_NOTFOUND;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_STAFFEMPLOYMENT_NOTFOUND;
import static com.kairos.constants.AppConstants.ONE_DAY_MINUTES;
import static com.kairos.constants.AppConstants.ORGANIZATION;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.setDayTypeToCTARuleTemplate;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/*
 * Created By Pradeep singh rajawat
 *  Date-27/01/2018
 *
 * */
@Transactional
@Service
public class TimeBankService{

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeBankService.class);

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
    @Inject private CostTimeAgreementService costTimeAgreementService;
    @Inject private PlanningPeriodService planningPeriodService;


    /**
     * @param staffAdditionalInfoDTO
     * @param shift
     * @Description This method is used for update DailyTimebankEntry when Shift Create,Update,Delete
     */
    public void updateTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, boolean validatedByPlanner) {
        staffAdditionalInfoDTO.getEmployment().setStaffId(shift.getStaffId());
        DailyTimeBankEntry dailyTimeBankEntry = renewDailyTimeBank(staffAdditionalInfoDTO, shift, validatedByPlanner,true);
        if(isNotNull(dailyTimeBankEntry)) {
            timeBankRepository.save(dailyTimeBankEntry);
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
        List<Long> employmentIds = new ArrayList<>(staffAdditionalInfoDTOS.stream().map(staffAdditionalInfoDTO -> staffAdditionalInfoDTO.getEmployment().getId()).collect(Collectors.toSet()));
        timeBankRepository.deleteDailyTimeBank(employmentIds, startDateTime, endDateTime);
        List<ShiftWithActivityDTO> shiftsList = shiftMongoRepository.findAllShiftsBetweenDurationByEmployments(employmentIds, startDateTime, endDateTime);
        Map<String, List<ShiftWithActivityDTO>> shiftDateMap = shiftsList.stream().collect(Collectors.groupingBy(k -> k.getEmploymentId() + "-" + DateUtils.asLocalDate(k.getStartDate())));
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>();
        List<PayOutPerShift> payOutPerShiftList = payOutRepository.findAllByEmploymentsAndDate(employmentIds, startDateTime, endDateTime);
        Map<BigInteger, PayOutPerShift> shiftAndPayOutMap = payOutPerShiftList.stream().collect(Collectors.toMap(k -> k.getShiftId(), v -> v));
        List<PayOutPerShift> payOutPerShifts = new ArrayList<>();
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(staffAdditionalInfoDTOS.get(0).getUnitId());
        while (startDateTime.before(shiftEndTime)) {
            for (StaffAdditionalInfoDTO staffAdditionalInfoDTO : staffAdditionalInfoDTOS) {
                List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftDateMap.getOrDefault(staffAdditionalInfoDTO.getEmployment().getId() + "-" + DateUtils.asLocalDate(startDateTime), new ArrayList<>());
                DateTimeInterval interval = new DateTimeInterval(startDateTime.getTime(), DateUtils.asDate(DateUtils.asZoneDateTime(startDateTime).plusDays(1)).getTime());
                staffAdditionalInfoDTO.getEmployment().setStaffId(staffAdditionalInfoDTO.getId());
                DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, shiftWithActivityDTOS, null, planningPeriodInterval, staffAdditionalInfoDTO.getDayTypes(), false);
                if(dailyTimeBank != null) {
                    dailyTimeBanks.add(dailyTimeBank);
                }
                DateTimeInterval dateTimeInterval = new DateTimeInterval(startDateTime.getTime(), plusDays(startDateTime, 1).getTime());
                List<Shift> shiftList = ObjectMapperUtils.copyPropertiesOfListByMapper(shiftWithActivityDTOS, Shift.class);
                List<BigInteger> activityIdsList = shiftList.stream().flatMap(s -> s.getActivities().stream().map(ShiftActivity::getActivityId)).distinct().collect(Collectors.toList());
                List<ActivityWrapper> activities = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(activityIdsList);
                activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
                for (Shift shift : shiftList) {
                    PayOutPerShift payOutPerShift = shiftAndPayOutMap.getOrDefault(shift.getId(), new PayOutPerShift(shift.getId(), shift.getEmploymentId(), shift.getStaffId(), dateTimeInterval.getStartLocalDate(), shift.getUnitId()));
                    payOutPerShift = payOutCalculationService.calculateAndUpdatePayOut(dateTimeInterval, staffAdditionalInfoDTO.getEmployment(), shift, activityWrapperMap, payOutPerShift, staffAdditionalInfoDTO.getDayTypes());
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

    public DailyTimeBankEntry renewDailyTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, boolean validatedByPlanner,boolean updateTimebankDetailsInShifts) {
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = startDate.plusDays(1);
        DailyTimeBankEntry dailyTimeBankEntriy = timeBankRepository.findByEmploymentAndDate(staffAdditionalInfoDTO.getEmployment().getId(), asLocalDate(startDate));
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(shift.getUnitId());
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(staffAdditionalInfoDTO.getEmployment().getId(), startDate.toDate(), endDate.toDate());
        DateTimeInterval interval = new DateTimeInterval(startDate.getMillis(), endDate.getMillis());
        List<ShiftWithActivityDTO> shiftWithActivityDTOList = getShiftsByInterval(shiftWithActivityDTOS, interval);
        staffAdditionalInfoDTO.getEmployment().setStaffId(staffAdditionalInfoDTO.getId());
        if(!updateTimebankDetailsInShifts && isNull(dailyTimeBankEntriy)){
            dailyTimeBankEntriy = new DailyTimeBankEntry(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO.getEmployment().getStaffId(), asLocalDate(shift.getStartDate()));
        }
        dailyTimeBankEntriy = timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, shiftWithActivityDTOList, dailyTimeBankEntriy, planningPeriodInterval, staffAdditionalInfoDTO.getDayTypes(), validatedByPlanner);
        if(updateTimebankDetailsInShifts) {
            updateBonusHoursOfTimeBankInShift(shiftWithActivityDTOS, Arrays.asList(shift));
        }
        return dailyTimeBankEntriy;
    }

    private List<DailyTimeBankEntry> renewDailyTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Date startDateTime, @Nullable Date endDateTime, Long unitId) {
        Date startDate = getStartOfDay(startDateTime);
        Date endDate = isNotNull(endDateTime) ? getEndOfDay(endDateTime) : null;
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllDailyTimeBankByEmploymentIdAndBetweenDates(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate);
        Map<LocalDate,DailyTimeBankEntry> dateDailyTimeBankEntryMap = dailyTimeBankEntries.stream().collect(Collectors.toMap(k->k.getDate(),v->v));
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>();
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate);
        if(isCollectionNotEmpty(shiftWithActivityDTOS)) {
            if(isNull(endDate)) {
                endDate = getEndOfDay(shiftWithActivityDTOS.get(shiftWithActivityDTOS.size() - 1).getEndDate());
            }
            DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
            List<Shift> shifts = shiftMongoRepository.findAllOverlappedShiftsAndEmploymentId(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate);
            while (startDate.before(endDate)) {
                DateTimeInterval interval = new DateTimeInterval(startDate.getTime(), plusDays(startDate, 1).getTime());
                List<ShiftWithActivityDTO> shiftWithActivityDTOList = getShiftsByInterval(shiftWithActivityDTOS, interval);
                staffAdditionalInfoDTO.getEmployment().setStaffId(staffAdditionalInfoDTO.getId());
                DailyTimeBankEntry dailyTimeBank = timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, shiftWithActivityDTOList, dateDailyTimeBankEntryMap.get(asLocalDate(startDate)), planningPeriodInterval, staffAdditionalInfoDTO.getDayTypes(), false);
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
     * @param employmentId
     * @return employmentWithCtaDetailsDTO
     */
    public EmploymentWithCtaDetailsDTO updateCostTimeAgreementDetails(Long employmentId, Date startDate, Date endDate) {
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = userIntegrationService.getEmploymentDetails(employmentId);
        if(!Optional.ofNullable(employmentWithCtaDetailsDTO).isPresent()) {
            exceptionService.dataNotFoundException(MESSAGE_STAFFEMPLOYMENT_NOTFOUND);
        }
        List<CTARuleTemplateDTO> ruleTemplates = costTimeAgreementService.getCtaRuleTemplatesByEmploymentId(employmentId, startDate, endDate);
        employmentWithCtaDetailsDTO.setCtaRuleTemplates(ruleTemplates);
        return employmentWithCtaDetailsDTO;
    }


    /**
     * @param unitId
     * @param employmentId
     * @param query
     * @param startDate
     * @param endDate
     * @return TimeBankAndPayoutDTO
     */
    public TimeBankAndPayoutDTO getAdvanceViewTimeBank(Long unitId, Long employmentId, String query, Date startDate, Date endDate) {
        endDate = asDate(DateUtils.asLocalDate(endDate).plusDays(1));
        List<DailyTimeBankEntry> dailyTimeBanks = timeBankRepository.findAllByEmploymentAndDate(employmentId, startDate, endDate);
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(employmentId, startDate, endDate);
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = updateCostTimeAgreementDetails(employmentId, startDate, endDate);
        long totalTimeBankBeforeStartDate = 0;
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeTypeByCountryId(employmentWithCtaDetailsDTO.getCountryId());
        if(new DateTime(startDate).isAfter(toJodaDateTime(employmentWithCtaDetailsDTO.getStartDate()))) {
            Interval interval = new Interval(toJodaDateTime(employmentWithCtaDetailsDTO.getStartDate()), new DateTime(startDate));
            //totaltimebank is timebank without daily timebank entries
            List<DailyTimeBankEntry> dailyTimeBanksBeforeStartDate = timeBankRepository.findAllByEmploymentIdAndStartDate(employmentId, new DateTime(startDate).toDate());
            DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
            int totalTimeBank = timeBankCalculationService.calculateDeltaTimeBankForInterval(planningPeriodInterval, interval, employmentWithCtaDetailsDTO, false, dailyTimeBanksBeforeStartDate, false);
            totalTimeBankBeforeStartDate = isCollectionNotEmpty(dailyTimeBanksBeforeStartDate) ? dailyTimeBanksBeforeStartDate.stream().mapToInt(dailyTimeBank -> dailyTimeBank.getDeltaTimeBankMinutes()).sum() : 0;
            totalTimeBankBeforeStartDate = totalTimeBankBeforeStartDate - totalTimeBank;
        }
        totalTimeBankBeforeStartDate += employmentWithCtaDetailsDTO.getAccumulatedTimebankMinutes();

        List<Interval> intervals = timeBankCalculationService.getAllIntervalsBetweenDates(startDate, endDate, query);
        Map<Interval, List<PayOutTransaction>> payoutTransactionIntervalMap = timeBankCalculationService.getPayoutTrasactionIntervalsMap(intervals, startDate,endDate,employmentId);
        return timeBankCalculationService.getTimeBankAdvanceView(intervals, unitId, totalTimeBankBeforeStartDate, startDate, endDate, query, shiftQueryResultWithActivities, dailyTimeBanks, employmentWithCtaDetailsDTO, timeTypeDTOS, payoutTransactionIntervalMap);
    }

    /**
     * @param employmentId
     * @param year
     * @return TimeBankDTO
     */
    public TimeBankDTO getOverviewTimeBank(Long unitId, Long employmentId, Integer year) {
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = userIntegrationService.getEmploymentDetails(employmentId);
        //Interval interval = getIntervalByDateTimeBank(employmentWithCtaDetailsDTO, year);
        Date startDate = asDate(ZonedDateTime.now().withYear(year).with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS));
        Date endDate = asDate(ZonedDateTime.now().withYear(year).with(TemporalAdjusters.lastDayOfYear()).truncatedTo(ChronoUnit.DAYS).with(LocalTime.MAX));
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentAndDate(employmentId, startDate, endDate);
        TimeBankDTO timeBankDTO = timeBankCalculationService.getTimeBankOverview(unitId, employmentId, startDate, endDate, dailyTimeBankEntries, employmentWithCtaDetailsDTO);
            Long actualTimebankMinutes = getAccumulatedTimebankAndDelta(employmentId, unitId, true);
            timeBankDTO.setActualTimebankMinutes(actualTimebankMinutes);
        return timeBankDTO;
    }


    public TimeBankVisualViewDTO getTimeBankForVisualView(Long unitId, Long employmentId, String query, Integer value, Date startDate, Date endDate) {
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
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = updateCostTimeAgreementDetails(employmentId, startDate, endDate);
        DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
        DailyTimeBankEntry dailyTimeBankEntry = timeBankRepository.findLastTimeBankByEmploymentId(employmentId);
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(employmentId, startDate, endDate);
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentAndDate(employmentId, startDate, endDate);
        Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
        Map<String, List<TimeType>> presenceAbsenceTimeTypeMap = timeTypeService.getPresenceAbsenceTimeType(countryId);
        return timeBankCalculationService.getVisualViewTimeBank(interval, dailyTimeBankEntry, shifts, dailyTimeBankEntries, presenceAbsenceTimeTypeMap, employmentWithCtaDetailsDTO);
    }

    /**
     * @param employmentWithCtaDetailsDTO
     * @param year
     * @return Interval
     */
    private Interval getIntervalByDateTimeBank(EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, Integer year) {
        ZonedDateTime startDate = ZonedDateTime.now().withYear(year).with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = startDate.with(TemporalAdjusters.lastDayOfYear());
        Date employmentStartDate = employmentWithCtaDetailsDTO.getStartDate().isAfter(LocalDate.now()) ? new DateTime().withTimeAtStartOfDay().toDate() : asDate(employmentWithCtaDetailsDTO.getStartDate());
        Date employmentEndDate = employmentWithCtaDetailsDTO.getEndDate() == null || employmentWithCtaDetailsDTO.getEndDate().isAfter(LocalDate.now()) ? new DateTime().withTimeAtStartOfDay().toDate() : asDate(employmentWithCtaDetailsDTO.getEndDate());
        Interval employmentInterval = new Interval(employmentStartDate.getTime(), employmentEndDate.getTime());
        Interval selectedInterval = new Interval(startDate.toInstant().toEpochMilli(), endDate.toInstant().toEpochMilli());
        Interval interval = selectedInterval.overlap(employmentInterval);
        if(interval == null) {
            interval = new Interval(new DateTime().withTimeAtStartOfDay(), new DateTime().withTimeAtStartOfDay());
        }
        return interval;
    }

    /**
     * @param startDate
     * @param staffAdditionalInfoDTO
     * @return
     * @Desc to update Time Bank after applying function in Employment
     */
    public boolean updateTimeBankOnFunctionChange(Date startDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Date endDate = plusMinutes(startDate, (int) ONE_DAY_MINUTES);
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), startDate);
        if(ctaResponseDTO == null) {
            exceptionService.dataNotFoundException(MESSAGE_CTA_NOTFOUND);
        }
        staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        List<DailyTimeBankEntry> dailyTimeBanks = renewDailyTimeBank(staffAdditionalInfoDTO, startDate, endDate, staffAdditionalInfoDTO.getUnitId());
        if(!dailyTimeBanks.isEmpty()) {
            timeBankRepository.saveEntities(dailyTimeBanks);
        }
        return true;
    }

    /**
     * This function is used to update TimeBank when Staff Personalized CTA
     * or individual employmentLine is changed at a time
     *
     * @param employmentId
     * @param startDate
     * @param staffAdditionalInfoDTO
     * @return
     */
    public boolean updateTimeBankOnEmploymentModification(BigInteger ctaId, Long employmentId, Date startDate, Date endDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Map<LocalDate, CTAResponseDTO> ctaResponseDTOMap = new HashMap<>();
        List<Shift> shifts = shiftMongoRepository.findAllShiftByIntervalAndEmploymentId(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate);
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>(shifts.size());
        for (Shift shift : shifts) {
            LocalDate shiftDate = DateUtils.asLocalDate(shift.getStartDate());
            CTAResponseDTO ctaResponseDTO;
            if(Optional.ofNullable(ctaId).isPresent()) {
                ctaResponseDTO = costTimeAgreementRepository.getOneCtaById(ctaId);
            } else {
                ctaResponseDTO = ctaResponseDTOMap.getOrDefault(shiftDate, costTimeAgreementRepository.getCTAByEmploymentIdAndDate(employmentId, DateUtils.asDate(shiftDate)));
            }
            if(ctaResponseDTO == null) {
                exceptionService.dataNotFoundException(MESSAGE_CTA_NOTFOUND);
            }
            staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            dailyTimeBanks.add(renewDailyTimeBank(staffAdditionalInfoDTO, shift, false,true));
            ctaResponseDTOMap.put(shiftDate, ctaResponseDTO);
        }
        if(!dailyTimeBanks.isEmpty()) {
            timeBankRepository.saveEntities(dailyTimeBanks);
        }
        return true;
    }

    private List<ShiftWithActivityDTO> getShiftsByInterval(List<ShiftWithActivityDTO> shiftWithActivityDTOS, DateTimeInterval dateTimeInterval) {
        List<ShiftWithActivityDTO> shifts = new ArrayList<>();
        shiftWithActivityDTOS.forEach(shift -> {
            if(dateTimeInterval.contains(shift.getStartDate())) {
                shifts.add(shift);
            }
        });
        return shifts;
    }

    private void updateBonusHoursOfTimeBankInShift(List<ShiftWithActivityDTO> shiftWithActivityDTOS, List<Shift> shifts) {
        if(CollectionUtils.isNotEmpty(shifts)) {
            Map<String, ShiftActivityDTO> shiftActivityDTOMap = shiftWithActivityDTOS.stream().flatMap(shift1 -> shift1.getActivities().stream()).collect(Collectors.toMap(k -> k.getActivityId()+"_"+k.getStartDate(), v -> v));
            for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
                for (ShiftActivityDTO activity : shiftWithActivityDTO.getActivities()) {
                    for (ShiftActivityDTO childActivity : activity.getChildActivities()) {
                        shiftActivityDTOMap.put(childActivity.getActivityId()+"_"+childActivity.getStartDate(),childActivity);
                    }
                }
            }
            for (Shift shift : shifts) {
                int timeBankCtaBonusMinutes = 0;
                int plannedMinutesOfTimebank = 0;
                int timeBankScheduledMinutes = 0;
                for (ShiftActivity shiftActivity : shift.getActivities()) {
                    updateTimebankDetailsInShiftActivity(shiftActivityDTOMap, shiftActivity);
                    timeBankCtaBonusMinutes += shiftActivity.getTimeBankCtaBonusMinutes();
                    plannedMinutesOfTimebank += shiftActivity.getPlannedMinutesOfTimebank();
                    timeBankScheduledMinutes+=shiftActivity.getScheduledMinutesOfTimebank();
                    if(Optional.ofNullable(shiftActivity.getChildActivities()).isPresent()) {
                        for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                            updateTimebankDetailsInShiftActivity(shiftActivityDTOMap, childActivity);
                        }
                    }
                }
                shift.setScheduledMinutesOfTimebank(timeBankScheduledMinutes);
                shift.setTimeBankCtaBonusMinutes(timeBankCtaBonusMinutes);
                shift.setPlannedMinutesOfTimebank(plannedMinutesOfTimebank);
            }
            shiftMongoRepository.saveEntities(shifts);
        }
    }

    private void updateTimebankDetailsInShiftActivity(Map<String, ShiftActivityDTO> shiftActivityDTOMap, ShiftActivity shiftActivity) {
        if(shiftActivityDTOMap.containsKey(shiftActivity.getActivityId()+"_"+shiftActivity.getStartDate())) {
            ShiftActivityDTO shiftActivityDTO = shiftActivityDTOMap.get(shiftActivity.getActivityId() + "_" + shiftActivity.getStartDate());
            shiftActivity.setTimeBankCtaBonusMinutes(shiftActivityDTO.getTimeBankCtaBonusMinutes().intValue());
            shiftActivity.setTimeBankCTADistributions(ObjectMapperUtils.copyPropertiesOfListByMapper(shiftActivityDTO.getTimeBankCTADistributions(), TimeBankCTADistribution.class));
            shiftActivity.setPlannedMinutesOfTimebank(shiftActivityDTO.getScheduledMinutesOfTimebank() + shiftActivityDTO.getTimeBankCtaBonusMinutes().intValue());
            shiftActivity.setScheduledMinutesOfTimebank(shiftActivityDTO.getScheduledMinutesOfTimebank());
        }
    }

    public boolean renewTimeBankOfShifts() {
        List<Shift> shifts = shiftMongoRepository.findAllByDeletedFalse();
        Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoDTOMap = new HashMap<>();
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>(shifts.size());
        for (Shift shift : shifts) {
            try {
                StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()), shift.getStaffId(), ORGANIZATION, shift.getEmploymentId(), new HashSet<>());
                CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), shift.getStartDate());
                if(Optional.ofNullable(ctaResponseDTO).isPresent() && CollectionUtils.isNotEmpty(ctaResponseDTO.getRuleTemplates())) {
                    staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
                    setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
                    staffAdditionalInfoDTOMap.put(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO);
                    if(staffAdditionalInfoDTOMap.containsKey(shift.getEmploymentId())) {
                        dailyTimeBanks.add(renewDailyTimeBank(staffAdditionalInfoDTOMap.get(shift.getEmploymentId()), shift, false,true));
                    }
                }
            } catch (Exception e) {
                LOGGER.info("staff is not the part of this Unit");
            }
            if(staffAdditionalInfoDTOMap.containsKey(shift.getEmploymentId()) && CollectionUtils.isNotEmpty(staffAdditionalInfoDTOMap.get(shift.getEmploymentId()).getEmployment().getCtaRuleTemplates())) {
                DailyTimeBankEntry dailyTimeBankEntries = renewDailyTimeBank(staffAdditionalInfoDTOMap.get(shift.getEmploymentId()), shift, false,true);
                dailyTimeBanks.add(dailyTimeBankEntries);
            }
        }
        if(CollectionUtils.isNotEmpty(dailyTimeBanks)) {
            timeBankRepository.saveEntities(dailyTimeBanks);
        }
        return true;

    }

    public void updateDailyTimeBankEntries(List<Shift> shifts, StaffEmploymentDetails staffEmploymentDetails, List<DayTypeDTO> dayTypeDTOS) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = new StaffAdditionalInfoDTO(staffEmploymentDetails, dayTypeDTOS);
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
                    activity.setPlannedMinutesOfTimebank(activity.getScheduledMinutesOfTimebank() + activity.getTimeBankCtaBonusMinutes().intValue());
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

    private EmploymentWithCtaDetailsDTO getEmploymentDetailDTO(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Long unitId) {
        return new EmploymentWithCtaDetailsDTO(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO.getEmployment().getTotalWeeklyHours(), staffAdditionalInfoDTO.getEmployment().getTotalWeeklyMinutes(), staffAdditionalInfoDTO.getEmployment().getWorkingDaysInWeek(), staffAdditionalInfoDTO.getEmployment().getStaffId(), staffAdditionalInfoDTO.getEmployment().getStartDate(), staffAdditionalInfoDTO.getEmployment().getEndDate(), staffAdditionalInfoDTO.getEmployment().getEmploymentLines(), staffAdditionalInfoDTO.getEmployment().getAccumulatedTimebankMinutes(), staffAdditionalInfoDTO.getEmployment().getAccumulatedTimebankDate(),unitId);
    }

    public void deleteDuplicateEntry() {
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllAndDeletedFalse();
        Map<Long, TreeMap<LocalDate, DailyTimeBankEntry>> employmentIdAndDateMap = new TreeMap<>();
        List<DailyTimeBankEntry> duplicateEntry = new ArrayList<>();
        for (DailyTimeBankEntry dailyTimeBankEntry : dailyTimeBankEntries) {
            if(employmentIdAndDateMap.containsKey(dailyTimeBankEntry.getEmploymentId())) {
                Map<LocalDate, DailyTimeBankEntry> localDateDateMap = employmentIdAndDateMap.get(dailyTimeBankEntry.getEmploymentId());
                if(localDateDateMap.containsKey(dailyTimeBankEntry.getDate())) {
                    DailyTimeBankEntry dailyTimeBankEntry1 = localDateDateMap.get(dailyTimeBankEntry.getDate());
                    if(dailyTimeBankEntry1.getUpdatedAt().after(dailyTimeBankEntry.getUpdatedAt())) {
                        duplicateEntry.add(dailyTimeBankEntry);
                    } else {
                        duplicateEntry.add(dailyTimeBankEntry1);
                    }
                } else {
                    localDateDateMap.put(dailyTimeBankEntry.getDate(), dailyTimeBankEntry);
                    LOGGER.info("Date Map :" + localDateDateMap.size());
                    LOGGER.info("employmentId Map :" + employmentIdAndDateMap.get(dailyTimeBankEntry.getEmploymentId()).size());
                }

            } else {
                employmentIdAndDateMap.put(dailyTimeBankEntry.getEmploymentId(), new TreeMap<>());
            }
        }
        LOGGER.info("Duplicate remove entry count is " + duplicateEntry.size());
        timeBankRepository.deleteAll(duplicateEntry);
    }

    public boolean updateDailyTimeBankOnCTAChangeOfEmployment(StaffAdditionalInfoDTO staffAdditionalInfoDTO, CTAResponseDTO ctaResponseDTO) {
        Date startDate = asDate(ctaResponseDTO.getStartDate());
        Date endDate = isNotNull(ctaResponseDTO.getEndDate()) ? asDate(ctaResponseDTO.getEndDate()) : null;
        staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        return updateTimeBankForMultipleShifts(staffAdditionalInfoDTO, startDate, endDate);
    }

    public boolean updateDailyTimeBankEntriesForStaffs(List<Shift> shifts,PlanningPeriod planningPeriod) {
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("staffIds", new ArrayList<>().toString()));
        requestParam.add(new BasicNameValuePair("employmentIds", new ArrayList<>().toString()));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getStaffAditionalDTOS(shifts.get(0).getUnitId(), requestParam);
        Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap = staffAdditionalInfoDTOS.stream().filter(distinctByKey(staffAdditionalInfoDTO -> staffAdditionalInfoDTO.getEmployment().getId())).collect(Collectors.toMap(s -> s.getEmployment().getId(), v -> v));
        if(isCollectionNotEmpty(shifts)) {
            Date startDateTime = new DateTime(shifts.get(0).getStartDate()).withTimeAtStartOfDay().toDate();
            Date endDateTime = new DateTime(shifts.get(shifts.size() - 1).getEndDate()).plusDays(1).withTimeAtStartOfDay().toDate();
            List<DailyTimeBankEntry> updateDailyTimeBanks = new ArrayList<>();
            List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByEmploymentIdsAndDate(new ArrayList<>(staffAdditionalInfoMap.keySet()), startDateTime, endDateTime);
            Map<Long, List<CTAResponseDTO>> employmentAndCTAResponseMap = ctaResponseDTOS.stream().collect(groupingBy(CTAResponseDTO::getEmploymentId));
            for (Shift shift : shifts) {
                StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffAdditionalInfoMap.get(shift.getEmploymentId());
                CTAResponseDTO ctaResponseDTO = getCTAByDate(employmentAndCTAResponseMap.get(shift.getEmploymentId()), asLocalDate(shift.getStartDate()));
                staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
                staffAdditionalInfoDTO.setUnitId(shifts.get(0).getUnitId());
                setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
                updateDailyTimeBanks.add(renewDailyTimeBank(staffAdditionalInfoDTO, shift, false,true));
            }
            if(isCollectionNotEmpty(updateDailyTimeBanks)) {
                timeBankRepository.saveEntities(updateDailyTimeBanks);
            }
        }
        if(isNotNull(planningPeriod)){
            updatePublishBalance(staffAdditionalInfoMap,planningPeriod);
        }
        return true;
    }

    private CTAResponseDTO getCTAByDate(List<CTAResponseDTO> ctaResponseDTOS, LocalDate shiftDate) {
        CTAResponseDTO ctaResponse = null;
        for (CTAResponseDTO ctaResponseDTO : ctaResponseDTOS) {
            DateTimeInterval dateTimeInterval = new DateTimeInterval(asDate(ctaResponseDTO.getStartDate()),isNotNull(ctaResponseDTO.getEndDate()) ? asDateEndOfDay(ctaResponseDTO.getEndDate()) : asDateEndOfDay(shiftDate));
            if(dateTimeInterval.contains(asDate(shiftDate))) {
                ctaResponse = ctaResponseDTO;
                break;
            }
        }
        return ctaResponse;
    }

    public <T> T getAccumulatedTimebankAndDelta(Long employmentId, Long unitId, Boolean includeActualTimebank) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaffByEmploymentId(unitId, null, ORGANIZATION, employmentId, new HashSet<>());
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = getEmploymentDetailDTO(staffAdditionalInfoDTO, unitId);
        T object;
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
        LocalDate periodEndDate = planningPeriodInterval.getEndLocalDate();
        LocalDate employmentStartDate = employmentWithCtaDetailsDTO.getStartDate();
        Date startDate  = asDate(employmentStartDate);
        Date endDate = asDate(periodEndDate);
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentIdAndBeforeDate(employmentId, endDate);
        object = (T)timeBankCalculationService.calculateActualTimebank(planningPeriodInterval,dailyTimeBankEntries,employmentWithCtaDetailsDTO,periodEndDate,employmentStartDate);
        if(isNull(includeActualTimebank)) {
            List<CTARuleTemplateDTO> ruleTemplates = costTimeAgreementService.getCtaRuleTemplatesByEmploymentId(employmentId, startDate, endDate);
            ruleTemplates = ruleTemplates.stream().filter(distinctByKey(ctaRuleTemplateDTO -> ctaRuleTemplateDTO.getName())).collect(toList());
            dailyTimeBankEntries = timeBankRepository.findAllByEmploymentIdAndBeforeDate(employmentId, asDate(periodEndDate));
            object = (T)timeBankCalculationService.getAccumulatedTimebankDTO(planningPeriodInterval, dailyTimeBankEntries, employmentWithCtaDetailsDTO, employmentStartDate, periodEndDate,(Long)object,staffAdditionalInfoDTO.getUserAccessRoleDTO(),ruleTemplates);
        }
        return object;
    }

    public void updateDailyTimebank(Long unitId){
        List<Shift> shifts = shiftMongoRepository.findAllByUnitId(unitId);
        for (Shift shift : shifts) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(asLocalDate(shift.getStartDate()), shift.getStaffId(), ORGANIZATION, shift.getEmploymentId(), new HashSet<>());
            CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), shift.getStartDate());
            if(isNotNull(ctaResponseDTO) && isCollectionNotEmpty(ctaResponseDTO.getRuleTemplates()) && isNotNull(staffAdditionalInfoDTO) && isNotNull(staffAdditionalInfoDTO.getEmployment())) {
                staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
                setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
                updateTimeBank(staffAdditionalInfoDTO,shift, false);
            }
        }
    }

    public void updatePublishBalance(Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap,PlanningPeriod planningPeriod){
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentIdsAndBetweenDate(staffAdditionalInfoMap.keySet(), asDate(planningPeriod.getStartDate()),asDate(planningPeriod.getEndDate()));
        Map<Long,List<DailyTimeBankEntry>> dailyTimeBankMap = dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getEmploymentId,Collectors.toList()));
        List<DailyTimeBankEntry> todayDailytimebankEntries = timeBankRepository.findAllByEmploymentIdsAndBetweenDate(staffAdditionalInfoMap.keySet(), asDate(LocalDate.now()),asDate(LocalDate.now()));
        Map<Long,DailyTimeBankEntry> todayDailyTimebank = todayDailytimebankEntries.stream().collect(Collectors.toMap(DailyTimeBankEntry::getEmploymentId, Function.identity()));
        List<DailyTimeBankEntry> updatedDailyTimebanks = new ArrayList<>();
        for (Map.Entry<Long, StaffAdditionalInfoDTO> employmentIdAndStaffAdditionalInfoDTOEntry : staffAdditionalInfoMap.entrySet()) {
            Map<LocalDate,DailyTimeBankEntry> localDateDailyTimeBankEntryMap = dailyTimeBankMap.getOrDefault(employmentIdAndStaffAdditionalInfoDTOEntry.getKey(),new ArrayList<>()).stream().collect(Collectors.toMap(k->k.getDate(),Function.identity()));
            DailyTimeBankEntry dailyTimeBankEntry = todayDailyTimebank.get(employmentIdAndStaffAdditionalInfoDTOEntry.getKey());
            DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(employmentIdAndStaffAdditionalInfoDTOEntry.getValue().getUnitId());
            if(!todayDailyTimebank.containsKey(employmentIdAndStaffAdditionalInfoDTOEntry.getKey())){
                int contractualMinutes = timeBankCalculationService.getContractualMinutesByDate(planningPeriodInterval, LocalDate.now(), employmentIdAndStaffAdditionalInfoDTOEntry.getValue().getEmployment().getEmploymentLines());
                dailyTimeBankEntry = new DailyTimeBankEntry(employmentIdAndStaffAdditionalInfoDTOEntry.getKey(), employmentIdAndStaffAdditionalInfoDTOEntry.getValue().getId(), LocalDate.now(),contractualMinutes,-contractualMinutes);
            }
            LocalDate startDate = planningPeriod.getStartDate();
            Map<LocalDate,Integer> publishedBalance = new HashMap<>();
            while (!startDate.isAfter(planningPeriod.getEndDate())){
                DailyTimeBankEntry dailyTimeBankEntryByStartDate = localDateDailyTimeBankEntryMap.get(startDate);
                int publishBalance = 0;
                if(isNotNull(dailyTimeBankEntryByStartDate)){
                    publishBalance = dailyTimeBankEntryByStartDate.isPublishedSomeActivities() ? dailyTimeBankEntryByStartDate.getDeltaAccumulatedTimebankMinutes() : dailyTimeBankEntryByStartDate.getDeltaAccumulatedTimebankMinutes();
                }else {
                    publishBalance = -timeBankCalculationService.getContractualMinutesByDate(planningPeriodInterval, startDate, employmentIdAndStaffAdditionalInfoDTOEntry.getValue().getEmployment().getEmploymentLines());
                }
                publishedBalance.put(startDate,publishBalance);
                startDate = startDate.plusDays(1);
            }
            dailyTimeBankEntry.setPublishedBalances(publishedBalance);
            updatedDailyTimebanks.add(dailyTimeBankEntry);
        }
        if(isCollectionNotEmpty(updatedDailyTimebanks)) {
            timeBankRepository.saveEntities(updatedDailyTimebanks);
        }
    }

    public DailyTimeBankEntry calculateTimebankForDraftShift(StaffAdditionalInfoDTO staffAdditionalInfoDTO,Date startDate,Date endDate,DailyTimeBankEntry dailyTimeBankEntry){
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = new ArrayList<>(shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentIdAndDraftShiftExists(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate,false));
        List<ShiftWithActivityDTO> draftShifts = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentIdAndDraftShiftExists(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate,true);
        if(isCollectionNotEmpty(draftShifts)) {
            shiftWithActivityDTOS.addAll(draftShifts);
        }
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(staffAdditionalInfoDTO.getUnitId());
        DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
        staffAdditionalInfoDTO.getEmployment().setStaffId(staffAdditionalInfoDTO.getId());
        if(isCollectionNotEmpty(shiftWithActivityDTOS)) {
            dailyTimeBankEntry = timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, shiftWithActivityDTOS, dailyTimeBankEntry, planningPeriodInterval, staffAdditionalInfoDTO.getDayTypes(), false);
            List<BigInteger> shiftIds = shiftWithActivityDTOS.stream().map(shift -> shift.getId()).collect(toList());
            Iterable<Shift> shifts = shiftMongoRepository.findAllById(shiftIds);
            updateBonusHoursOfTimeBankInShift(shiftWithActivityDTOS,(List<Shift>) shifts);
        }else {
            dailyTimeBankEntry.setDraftDeltaTimebankMinutes(0);
            dailyTimeBankEntry.setAnyShiftInDraft(false);
        }
        return dailyTimeBankEntry;
    }

    public void updateTimeBankForProtectedDaysOff(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, boolean validatedByPlanner) {
        CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), asDate(LocalDate.now()));
        staffAdditionalInfoDTO.getEmployment().setStaffId(shift.getStaffId());
        DailyTimeBankEntry dailyTimeBankEntry = renewDailyTimeBank(staffAdditionalInfoDTO, shift, validatedByPlanner,false);
        if(isNotNull(dailyTimeBankEntry)) {
            timeBankRepository.save(dailyTimeBankEntry);
        }
    }
}
