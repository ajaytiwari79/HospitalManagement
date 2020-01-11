package com.kairos.service.time_bank;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.time_bank.*;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.Activity;
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
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.pay_out.PayOutTransaction;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.shift.ShiftService;
import lombok.Getter;
import lombok.Setter;
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
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_CTA_NOTFOUND;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_STAFFEMPLOYMENT_NOTFOUND;
import static com.kairos.constants.AppConstants.*;
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
    private PayOutService payOutService;
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
    @Inject private PayOutRepository payOutRepository;





    /**
     * @param staffAdditionalInfoDTO
     * @param shift
     * @Description This method is used for update DailyTimebankEntry when Shift Create,Update,Delete
     */
    public void updateTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, boolean validatedByPlanner) {
        staffAdditionalInfoDTO.getEmployment().setStaffId(shift.getStaffId());
        DailyTimeBankEntry dailyTimeBankEntry = renewDailyTimeBank(staffAdditionalInfoDTO, shift, validatedByPlanner);
        if(isNotNull(dailyTimeBankEntry)) {
            timeBankRepository.save(dailyTimeBankEntry);
        }
    }

    public boolean updateTimeBankForMultipleShifts(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Date startDate, Date endDate) {
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        renewDailyTimeBank(staffAdditionalInfoDTO, startDate, endDate);
        return true;
    }

    public void saveTimeBanksAndPayOut(List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, Date startDate, Date endDate) {
        Date startDateTime = new DateTime(startDate).withTimeAtStartOfDay().toDate();
        Date endDateTime = new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate();
        List<Long> employmentIds = new ArrayList<>(staffAdditionalInfoDTOS.stream().map(staffAdditionalInfoDTO -> staffAdditionalInfoDTO.getEmployment().getId()).collect(Collectors.toSet()));
        timeBankRepository.deleteDailyTimeBank(employmentIds, startDateTime, endDateTime);
        List<Shift> shiftsList = shiftMongoRepository.findAllOverlappedShiftsAndEmploymentId(employmentIds, startDateTime, endDateTime);
        Map<Long, List<Shift>> shiftMapByEmploymentId = shiftsList.stream().collect(Collectors.groupingBy(Shift::getEmploymentId));
        Map<BigInteger, ActivityWrapper> activityWrapperMap = shiftService.getActivityWrapperMap(shiftsList,null);
        List<DailyTimeBankEntry> dailyTimeBankEntries = new ArrayList<>();
        for (StaffAdditionalInfoDTO staffAdditionalInfoDTO : staffAdditionalInfoDTOS) {
            List<Shift> shiftList = shiftMapByEmploymentId.getOrDefault(staffAdditionalInfoDTO.getEmployment().getId(), new ArrayList<>());
            for (Shift shift : shiftList) {
                shiftService.updateCTADetailsOfEmployement(asLocalDate(shift.getStartDate()),staffAdditionalInfoDTO);
                payOutService.updatePayOut(staffAdditionalInfoDTO,shift,activityWrapperMap);
                dailyTimeBankEntries.add(renewDailyTimeBank(staffAdditionalInfoDTO,shift,false));
            }
        }
        if(isCollectionNotEmpty(dailyTimeBankEntries)){
            timeBankRepository.saveEntities(dailyTimeBankEntries);
        }
    }

    public DailyTimeBankEntry renewDailyTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, boolean validatedByPlanner) {
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = startDate.plusDays(1);
        DailyTimeBankEntry dailyTimeBankEntry = timeBankRepository.findByEmploymentAndDate(staffAdditionalInfoDTO.getEmployment().getId(), asLocalDate(startDate));
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(shift.getUnitId());
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(staffAdditionalInfoDTO.getEmployment().getId(), startDate.toDate(), endDate.toDate(),null);
        List<ShiftWithActivityDTO> draftShifts = getDraftShift(shiftWithActivityDTOS);
        shiftWithActivityDTOS = shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> !shiftWithActivityDTO.isDraft()).collect(toList());
        DateTimeInterval interval = new DateTimeInterval(startDate.getMillis(), endDate.getMillis());
        shiftWithActivityDTOS = getShiftsByInterval(shiftWithActivityDTOS, interval);
        dailyTimeBankEntry = updateDailyTimeBankEntry(staffAdditionalInfoDTO, shift, validatedByPlanner, dailyTimeBankEntry, planningPeriodInterval, shiftWithActivityDTOS, interval);
        if(isCollectionNotEmpty(draftShifts)){
            DailyTimeBankEntry draftDailyTimeBankEntry = updateDailyTimeBankEntry(staffAdditionalInfoDTO, shift, validatedByPlanner, null, planningPeriodInterval, draftShifts, interval);
            if(isNull(dailyTimeBankEntry)){
                dailyTimeBankEntry = ObjectMapperUtils.copyPropertiesByMapper(draftDailyTimeBankEntry,DailyTimeBankEntry.class);
            }
            dailyTimeBankEntry.setDraftDailyTimeBankEntry(draftDailyTimeBankEntry);
        }
        return dailyTimeBankEntry;
    }

    private DailyTimeBankEntry updateDailyTimeBankEntry(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, boolean validatedByPlanner, DailyTimeBankEntry dailyTimeBankEntry, DateTimeInterval planningPeriodInterval, List<ShiftWithActivityDTO> shiftWithActivityDTOS, DateTimeInterval interval) {
        staffAdditionalInfoDTO.getEmployment().setStaffId(staffAdditionalInfoDTO.getId());
        if(isNull(dailyTimeBankEntry)){
            dailyTimeBankEntry = new DailyTimeBankEntry(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO.getEmployment().getStaffId(), asLocalDate(shift.getStartDate()));
        }
        shiftWithActivityDTOS.forEach(shiftWithActivityDTO -> {
            shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO -> {
                resetCTARelatedData(shiftActivityDTO);
            });
            if(isCollectionNotEmpty(shiftWithActivityDTO.getBreakActivities())){
                shiftWithActivityDTO.getBreakActivities().forEach(shiftActivityDTO -> {
                    resetCTARelatedData(shiftActivityDTO);
                });
            }
        });
        dailyTimeBankEntry = timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, shiftWithActivityDTOS, dailyTimeBankEntry, planningPeriodInterval, staffAdditionalInfoDTO.getDayTypes(), validatedByPlanner);
        updateBonusHoursOfTimeBankInShift(shiftWithActivityDTOS, Arrays.asList(shift));
        return dailyTimeBankEntry;
    }

    private void resetCTARelatedData(ShiftActivityDTO shiftActivityDTO) {
        shiftActivityDTO.setPayoutPerShiftCTADistributions(new ArrayList<>());
        shiftActivityDTO.setTimeBankCTADistributions(new ArrayList<>());
        shiftActivityDTO.setPlannedMinutesOfPayout(0);
        shiftActivityDTO.setPlannedMinutesOfTimebank(0);
        shiftActivityDTO.setTimeBankCtaBonusMinutes(0);
        shiftActivityDTO.setScheduledMinutesOfTimebank(0);
        shiftActivityDTO.setPlannedMinutesOfPayout(0);
    }

    private List<ShiftWithActivityDTO> getDraftShift(List<ShiftWithActivityDTO> shiftWithActivityDTOS){
        List<ShiftWithActivityDTO> shiftWithActivityDTOList = new ArrayList<>();
        for (ShiftWithActivityDTO shiftWithActivityDTO : shiftWithActivityDTOS) {
            shiftWithActivityDTO.resetTimebankDetails();
            if(shiftWithActivityDTO.isDraft()){
                shiftWithActivityDTOList.add(shiftWithActivityDTO);
            }else if(isNotNull(shiftWithActivityDTO.getDraftShift())){
                shiftWithActivityDTOList.add(shiftWithActivityDTO.getDraftShift());
            }
        }
        return shiftWithActivityDTOList;
    }

    private void renewDailyTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Date startDateTime, @Nullable Date endDateTime) {
        Date startDate = getStartOfDay(startDateTime);
        Date endDate = isNotNull(endDateTime) ? getEndOfDay(endDateTime) : null;
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate,null);
        staffAdditionalInfoDTO.getEmployment().setFunctionId(null);
        List<DailyTimeBankEntry> dailyTimeBankEntries = new ArrayList<>();
        if(isCollectionNotEmpty(shiftWithActivityDTOS)) {
            shiftWithActivityDTOS = shiftWithActivityDTOS.stream().sorted(Comparator.comparing(ShiftWithActivityDTO::getStartDate)).collect(Collectors.toList());
            if(isNull(endDate)) {
                endDate = getEndOfDay(shiftWithActivityDTOS.get(shiftWithActivityDTOS.size() - 1).getEndDate());
            }
            List<Shift> shifts = shiftMongoRepository.findAllOverlappedShiftsAndEmploymentId(newArrayList(staffAdditionalInfoDTO.getEmployment().getId()), startDate, endDate);
            for (Shift shift : shifts) {
                shift.setScheduledMinutesOfPayout(0);
                shift.setScheduledMinutesOfTimebank(0);
                renewDailyTimeBank(staffAdditionalInfoDTO,shift,false);
            }
        }
        if(isCollectionNotEmpty(dailyTimeBankEntries)){
            timeBankRepository.saveEntities(dailyTimeBankEntries);
        }
    }

    /**
     * @param employmentId
     * @return employmentWithCtaDetailsDTO
     */
    public EmploymentWithCtaDetailsDTO updateCostTimeAgreementDetails(Long employmentId, Date startDate, Date endDate) {
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = userIntegrationService.getEmploymentDetails(employmentId);
        employmentWithCtaDetailsDTO.setCtaRuleTemplates(updateCostTimeAggrement(employmentId, startDate, endDate, employmentWithCtaDetailsDTO));
        return employmentWithCtaDetailsDTO;
    }

    private List<CTARuleTemplateDTO> updateCostTimeAggrement(Long employmentId, Date startDate, Date endDate, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        if(!Optional.ofNullable(employmentWithCtaDetailsDTO).isPresent()) {
            exceptionService.dataNotFoundException(MESSAGE_STAFFEMPLOYMENT_NOTFOUND);
        }
        return costTimeAgreementService.getCtaRuleTemplatesByEmploymentId(employmentId, startDate, endDate);
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
        PlanningPeriod planningPeriod = planningPeriodService.findOneByUnitIdAndDate(unitId, startDate);
        List<EmploymentWithCtaDetailsDTO> employmentDetails;
        List<Interval> intervals;
        long totalTimeBankBeforeStartDate = 0;
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities;
        List<TimeTypeDTO> timeTypeDTOS = null;
        Map<Interval,Integer> sequenceIntervalMap  = new HashMap<>();
        List<PayOutPerShift> payOutPerShifts;
        List<Long> employmentIds = newArrayList(employmentId);
        if(isNotNull(endDate)){
            shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(employmentId, startDate, endDate,null);
            endDate = asDate(DateUtils.asLocalDate(endDate).plusDays(1));
            EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = updateCostTimeAgreementDetails(employmentId, startDate, endDate);
            employmentDetails = newArrayList(employmentWithCtaDetailsDTO);
            timeTypeDTOS = timeTypeService.getAllTimeTypeByCountryId(employmentWithCtaDetailsDTO.getCountryId());
            if(new DateTime(startDate).isAfter(toJodaDateTime(employmentWithCtaDetailsDTO.getStartDate()))) {
                Interval interval = new Interval(toJodaDateTime(employmentWithCtaDetailsDTO.getStartDate()), new DateTime(startDate));
                //totaltimebank is timebank without daily timebank entries
                List<DailyTimeBankEntry> dailyTimeBanksBeforeStartDate = timeBankRepository.findAllByEmploymentIdAndStartDate(employmentId, new DateTime(startDate).toDate());
                DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(unitId);
                totalTimeBankBeforeStartDate = (int)timeBankCalculationService.calculateDeltaTimeBankForInterval(planningPeriodInterval, interval, employmentWithCtaDetailsDTO, new HashSet<>(), dailyTimeBanksBeforeStartDate, false)[0];
            }
            totalTimeBankBeforeStartDate += employmentWithCtaDetailsDTO.getAccumulatedTimebankMinutes();
            intervals = timeBankCalculationService.getAllIntervalsBetweenDates(startDate, endDate, query);
            payOutPerShifts = payOutRepository.findAllByEmploymentAndDate(employmentWithCtaDetailsDTO.getId(), startDate, endDate);
        }else {
            endDate = asDate(planningPeriod.getEndDate());
            Interval todayInterval = new Interval(startDate.getTime(),query.equals(WEEK) ? asDate(asZoneDateTime(startDate).with(TemporalAdjusters.next(DayOfWeek.SUNDAY))).getTime() : getEndOfDay(startDate).getTime());
            Interval planningPeriodInterval = new Interval(asDate(planningPeriod.getStartDate()).getTime(),getEndOfDay(asDate(planningPeriod.getEndDate())).getTime());
            Interval yearTillDate = new Interval(asDate(planningPeriod.getStartDate().with(TemporalAdjusters.firstDayOfYear())).getTime(),getEndOfDay(startDate).getTime());
            intervals = newArrayList(todayInterval,planningPeriodInterval,yearTillDate);
            sequenceIntervalMap = new HashMap<>();
            sequenceIntervalMap.put(todayInterval,1);
            sequenceIntervalMap.put(planningPeriodInterval,2);
            sequenceIntervalMap.put(yearTillDate,3);
            if(isNotNull(employmentId)){
                EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = updateCostTimeAgreementDetails(employmentId, startDate, endDate);
                employmentDetails = newArrayList(employmentWithCtaDetailsDTO);
            }else {
                employmentDetails = userIntegrationService.getAllEmploymentByUnitId(unitId);
                employmentDetails.forEach(employmentWithCtaDetailsDTO -> employmentWithCtaDetailsDTO.setCtaRuleTemplates(updateCostTimeAggrement(employmentWithCtaDetailsDTO.getId(),startDate,planningPeriodInterval.getEnd().toDate(),employmentWithCtaDetailsDTO)));
            }
            employmentIds = employmentDetails.stream().map(employmentWithCtaDetailsDTO -> employmentWithCtaDetailsDTO.getId()).collect(toList());
            payOutPerShifts = payOutRepository.findAllByEmploymentsAndDate(employmentIds, asDate(planningPeriod.getStartDate().with(TemporalAdjusters.firstDayOfYear())), endDate);
            shiftQueryResultWithActivities = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentIds(employmentIds, asDate(planningPeriod.getStartDate().with(TemporalAdjusters.firstDayOfYear())), endDate,null);
        }

        List<DailyTimeBankEntry> dailyTimeBanks = timeBankRepository.findAllByEmploymentIdsAndBeforDate(employmentIds, endDate);
        Map<Interval, List<PayOutTransaction>> payoutTransactionIntervalMap = timeBankCalculationService.getPayoutTrasactionIntervalsMap(intervals, startDate,endDate,employmentId);
        return timeBankCalculationService.getTimeBankAdvanceView(intervals, unitId, totalTimeBankBeforeStartDate, startDate, endDate, query, shiftQueryResultWithActivities, dailyTimeBanks, employmentDetails, timeTypeDTOS, payoutTransactionIntervalMap,sequenceIntervalMap,payOutPerShifts);
    }

    /**
     * @param employmentId
     * @param year
     * @return TimeBankDTO
     */
    public TimeBankDTO getOverviewTimeBank(Long unitId, Long employmentId, Integer year) {
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = userIntegrationService.getEmploymentDetails(employmentId);
        Date startDate = asDate(ZonedDateTime.now().withYear(year).with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS));
        Date endDate = asDate(ZonedDateTime.now().withYear(year).with(TemporalAdjusters.lastDayOfYear()).truncatedTo(ChronoUnit.DAYS).with(LocalTime.MAX));
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentAndDate(employmentId, startDate, endDate);
        TimeBankDTO timeBankDTO = timeBankCalculationService.getTimeBankOverview(unitId, employmentId, startDate, endDate, dailyTimeBankEntries, employmentWithCtaDetailsDTO);
            Long actualTimebankMinutes = getAccumulatedTimebankAndDelta(employmentId, unitId, true);
            timeBankDTO.setActualTimebankMinutes(actualTimebankMinutes);
        PlanningPeriodDTO planningPeriodDTO = planningPeriodService.getStartDateAndEndDateOfPlanningPeriodByUnitId(unitId);
        timeBankDTO.setPlanningPeriodStartDate(planningPeriodDTO.getStartDate());
        timeBankDTO.setPlanningPeriodEndDate(planningPeriodDTO.getEndDate());
        return timeBankDTO;
    }


    public TimeBankVisualViewDTO getTimeBankForVisualView(Long unitId, Long employmentId, String query, Integer value, Date startDate, Date endDate) {
        ZonedDateTime endZonedDate = null;
        ZonedDateTime startZonedDate = null;
        if(StringUtils.isNotEmpty(query)) {
            if(query.equals(WEEK)) {
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
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(employmentId, startDate, endDate,null);
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentAndDate(employmentId, startDate, endDate);
        Long countryId = userIntegrationService.getCountryIdOfOrganization(unitId);
        Map<String, List<TimeType>> presenceAbsenceTimeTypeMap = timeTypeService.getPresenceAbsenceTimeType(countryId);
        return timeBankCalculationService.getVisualViewTimeBank(interval, shifts, dailyTimeBankEntries, presenceAbsenceTimeTypeMap, employmentWithCtaDetailsDTO);
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
        if(isNull(ctaResponseDTO)) {
            exceptionService.dataNotFoundException(MESSAGE_CTA_NOTFOUND);
        }
        staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        renewDailyTimeBank(staffAdditionalInfoDTO, startDate, endDate);
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
        List<Shift> shifts = shiftMongoRepository.findAllShiftByIntervalAndEmploymentId(staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate);
        updateDailyTimebankForShifts(ctaId, employmentId, staffAdditionalInfoDTO, shifts);
        return true;
    }

    public void updateDailyTimebankForShifts(BigInteger ctaId, Long employmentId, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<Shift> shifts) {
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>(shifts.size());
        Map<LocalDate, CTAResponseDTO> ctaResponseDTOMap = new HashMap<>();
        for (Shift shift : shifts) {
            LocalDate shiftDate = DateUtils.asLocalDate(shift.getStartDate());
            CTAResponseDTO ctaResponseDTO;
            if(Optional.ofNullable(ctaId).isPresent()) {
                ctaResponseDTO = costTimeAgreementRepository.getOneCtaById(ctaId);
            } else {
                ctaResponseDTO = ctaResponseDTOMap.getOrDefault(shiftDate, costTimeAgreementRepository.getCTAByEmploymentIdAndDate(employmentId, DateUtils.asDate(shiftDate)));
            }
            if(isNull(ctaResponseDTO)) {
                exceptionService.dataNotFoundException(MESSAGE_CTA_NOTFOUND);
            }
            staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            dailyTimeBanks.add(renewDailyTimeBank(staffAdditionalInfoDTO, shift, false));
            ctaResponseDTOMap.put(shiftDate, ctaResponseDTO);
        }
        if(!dailyTimeBanks.isEmpty()) {
            timeBankRepository.saveEntities(dailyTimeBanks);
        }
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
                if(isCollectionNotEmpty(shiftWithActivityDTO.getBreakActivities())){
                    shiftWithActivityDTO.getBreakActivities().forEach(shiftActivityDTO ->shiftActivityDTOMap.put(shiftActivityDTO.getActivityId()+"_"+shiftActivityDTO.getStartDate(),shiftActivityDTO));

                }
            }
            for (Shift shift : shifts) {
                if(isNotNull(shift.getDraftShift())){
                    shift = shift.getDraftShift();
                }
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
                if(isCollectionNotEmpty(shift.getBreakActivities())){
                    for (ShiftActivity breakActivity : shift.getBreakActivities()) {
                        updateTimebankDetailsInShiftActivity(shiftActivityDTOMap, breakActivity);
                        timeBankCtaBonusMinutes += breakActivity.getTimeBankCtaBonusMinutes();
                        plannedMinutesOfTimebank += breakActivity.getPlannedMinutesOfTimebank();
                        timeBankScheduledMinutes+=breakActivity.getScheduledMinutesOfTimebank();
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
            shiftActivity.setTimeBankCtaBonusMinutes((int)shiftActivityDTO.getTimeBankCtaBonusMinutes());
            shiftActivity.setTimeBankCTADistributions(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shiftActivityDTO.getTimeBankCTADistributions(), TimeBankCTADistribution.class));
            shiftActivity.setPlannedMinutesOfTimebank(shiftActivityDTO.getScheduledMinutesOfTimebank() + (int)shiftActivityDTO.getTimeBankCtaBonusMinutes());
            shiftActivity.setScheduledMinutesOfTimebank(shiftActivityDTO.getScheduledMinutesOfTimebank());
        }
    }

    public boolean renewTimeBankOfShifts() {
        List<Shift> shifts = shiftMongoRepository.findAllByDeletedFalse();
        Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoDTOMap = new HashMap<>();
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>(shifts.size());
        for (Shift shift : shifts) {
            try {
                StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()), shift.getStaffId(), shift.getEmploymentId(), new HashSet<>());
                CTAResponseDTO ctaResponseDTO = costTimeAgreementRepository.getCTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), shift.getStartDate());
                if(Optional.ofNullable(ctaResponseDTO).isPresent() && CollectionUtils.isNotEmpty(ctaResponseDTO.getRuleTemplates())) {
                    staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
                    setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
                    staffAdditionalInfoDTOMap.put(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO);
                    if(staffAdditionalInfoDTOMap.containsKey(shift.getEmploymentId())) {
                        dailyTimeBanks.add(renewDailyTimeBank(staffAdditionalInfoDTOMap.get(shift.getEmploymentId()), shift, false));
                    }
                }
            } catch (Exception e) {
                LOGGER.info("staff is not the part of this Unit");
            }
            if(staffAdditionalInfoDTOMap.containsKey(shift.getEmploymentId()) && CollectionUtils.isNotEmpty(staffAdditionalInfoDTOMap.get(shift.getEmploymentId()).getEmployment().getCtaRuleTemplates())) {
                DailyTimeBankEntry dailyTimeBankEntries = renewDailyTimeBank(staffAdditionalInfoDTOMap.get(shift.getEmploymentId()), shift, false);
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
                    activity.setPlannedMinutesOfTimebank(activity.getScheduledMinutesOfTimebank() + (int)activity.getTimeBankCtaBonusMinutes());
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
        return new EmploymentWithCtaDetailsDTO(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO.getEmployment().getTotalWeeklyHours(), staffAdditionalInfoDTO.getEmployment().getTotalWeeklyMinutes(), staffAdditionalInfoDTO.getEmployment().getWorkingDaysInWeek(), staffAdditionalInfoDTO.getEmployment().getStaffId(), staffAdditionalInfoDTO.getEmployment().getStartDate(), staffAdditionalInfoDTO.getEmployment().getEndDate(), staffAdditionalInfoDTO.getEmployment().getEmploymentLines(), staffAdditionalInfoDTO.getEmployment().getAccumulatedTimebankMinutes(), staffAdditionalInfoDTO.getEmployment().getAccumulatedTimebankDate(),unitId,staffAdditionalInfoDTO.getEmployment().getId());
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
                    LOGGER.info("Date Map : {}" , localDateDateMap.size());
                    LOGGER.info("employmentId Map : {}" , employmentIdAndDateMap.get(dailyTimeBankEntry.getEmploymentId()).size());
                }

            } else {
                employmentIdAndDateMap.put(dailyTimeBankEntry.getEmploymentId(), new TreeMap<>());
            }
        }
        LOGGER.info("Duplicate remove entry count is {}" , duplicateEntry.size());
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
                if(isNull(ctaResponseDTO)){
                    exceptionService.dataNotFoundException(MESSAGE_CTA_NOTFOUND);
                }
                staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
                staffAdditionalInfoDTO.setUnitId(shifts.get(0).getUnitId());
                setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
                updateDailyTimeBanks.add(renewDailyTimeBank(staffAdditionalInfoDTO, shift, false));
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
        Date endDate = asDate(periodEndDate);
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentIdAndBeforeDate(employmentId, endDate);
        LocalDate employmentStartDate = employmentWithCtaDetailsDTO.getStartDate();
        Date startDate  = asDate(employmentStartDate);
        object = (T)timeBankCalculationService.calculateActualTimebank(planningPeriodInterval,dailyTimeBankEntries,employmentWithCtaDetailsDTO,periodEndDate,employmentStartDate);
        if(isNull(includeActualTimebank)) {
            List<CTARuleTemplateDTO> ruleTemplates = costTimeAgreementService.getCtaRuleTemplatesByEmploymentId(employmentId, startDate, endDate);
            ruleTemplates = ruleTemplates.stream().filter(distinctByKey(CTARuleTemplateDTO::getName)).collect(toList());
            dailyTimeBankEntries = timeBankRepository.findAllByEmploymentIdAndBeforeDate(employmentId, asDate(periodEndDate));
            java.time.LocalDate firstRequestPhasePlanningPeriodEndDate = planningPeriodService.findFirstRequestPhasePlanningPeriodByUnitId(unitId).getEndDate();
            object = (T)timeBankCalculationService.getAccumulatedTimebankDTO(firstRequestPhasePlanningPeriodEndDate,planningPeriodInterval, dailyTimeBankEntries, employmentWithCtaDetailsDTO, employmentStartDate, periodEndDate,(Long)object,ruleTemplates);
        }
        return object;
    }

    public void updateDailyTimebank(Long unitId){
        List<Shift> shifts = shiftMongoRepository.findAllByUnitId(unitId);
        for (Shift shift : shifts) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(asLocalDate(shift.getStartDate()), shift.getStaffId(), shift.getEmploymentId(), new HashSet<>());
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
            Map<LocalDate,DailyTimeBankEntry> localDateDailyTimeBankEntryMap = dailyTimeBankMap.getOrDefault(employmentIdAndStaffAdditionalInfoDTOEntry.getKey(),new ArrayList<>()).stream().collect(Collectors.toMap(DailyTimeBankEntry::getDate,Function.identity()));
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
                    publishBalance = dailyTimeBankEntryByStartDate.getDeltaAccumulatedTimebankMinutes();
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

    public void updateTimeBankForProtectedDaysOff(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, boolean validatedByPlanner) {
        staffAdditionalInfoDTO.getEmployment().setStaffId(shift.getStaffId());
        DailyTimeBankEntry dailyTimeBankEntry = renewDailyTimeBank(staffAdditionalInfoDTO, shift, validatedByPlanner);
        if(isNotNull(dailyTimeBankEntry)) {
            timeBankRepository.save(dailyTimeBankEntry);
        }
    }

    public long getExpectedTimebankByDate(ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = startDate.plusDays(1);
        DailyTimeBankEntry dailyTimeBankEntriy = timeBankRepository.findByEmploymentAndDate(staffAdditionalInfoDTO.getEmployment().getId(), asLocalDate(startDate));
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(shift.getUnitId());
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(staffAdditionalInfoDTO.getEmployment().getId(), startDate.toDate(), endDate.toDate(),null);
        DateTimeInterval interval = new DateTimeInterval(startDate.getMillis(), endDate.getMillis());
        List<ShiftWithActivityDTO> shiftWithActivityDTOList = getShiftsByInterval(shiftWithActivityDTOS, interval);
        shiftWithActivityDTOList.add(shift);
        staffAdditionalInfoDTO.getEmployment().setStaffId(staffAdditionalInfoDTO.getId());
        DailyTimeBankEntry dailyTimeBankEntry = timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, shiftWithActivityDTOList, dailyTimeBankEntriy, planningPeriodInterval, staffAdditionalInfoDTO.getDayTypes(), false);
        TreeMap<LocalDate, TimeBankIntervalDTO> timeBankByDateDTOMap = getAccumulatedTimebankAndDelta(staffAdditionalInfoDTO.getEmployment().getId(),shift.getUnitId(),null);
        long expectedTimebank = timeBankByDateDTOMap.lastEntry().getValue().getExpectedTimebankMinutes();
        if (isNotNull(dailyTimeBankEntry)) {
            expectedTimebank += dailyTimeBankEntry.getDeltaTimeBankMinutes();
        }
        return expectedTimebank;
    }

    public List<CTARuleTemplateDTO> getCTARultemplateByEmploymentId(Long employmentId){
        return costTimeAgreementRepository.getCTARultemplateByEmploymentId(employmentId).stream().filter(distinctByKey(CTARuleTemplateDTO::getName)).collect(toList());
    }

    public List<DailyTimeBankEntry> findAllByEmploymentIdsAndBetweenDate(Collection<Long> employmentIds, LocalDate startDate, LocalDate endDate){
        return timeBankRepository.findAllByEmploymentIdsAndBetweenDate(employmentIds,asDate(startDate),asDate(endDate));
    }

    public void updateTimeBanOnApproveTimebankOFF(ShiftActivity shiftActivity,Long employmentId,Map<BigInteger, Activity> activityIdAndActivityMap,StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        Activity activity = activityIdAndActivityMap.get(shiftActivity.getActivityId());
        if(TimeTypeEnum.TIME_BANK.equals(activity.getBalanceSettingsActivityTab().getTimeType()) && ((CommonConstants.FULL_WEEK.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime()) || CommonConstants.FULL_DAY_CALCULATION.equals(activity.getTimeCalculationActivityTab().getMethodForCalculatingTime())))){
            timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity,activityIdAndActivityMap.get(shiftActivity.getActivityId()),staffAdditionalInfoDTO.getEmployment(),true);
            DailyTimeBankEntry dailyTimeBankEntry = timeBankRepository.findByEmploymentAndDate(employmentId, asLocalDate(shiftActivity.getStartDate()));
            if(isNull(dailyTimeBankEntry)){
                DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(staffAdditionalInfoDTO.getUnitId());
                int contractualMinutes = timeBankCalculationService.getContractualMinutesByDate(planningPeriodInterval, asLocalDate(shiftActivity.getStartDate()), staffAdditionalInfoDTO.getEmployment().getEmploymentLines());
                dailyTimeBankEntry = new DailyTimeBankEntry(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO.getId(),asLocalDate(shiftActivity.getStartDate()),contractualMinutes,-contractualMinutes);
            }
            dailyTimeBankEntry.setTimeBankOffMinutes(shiftActivity.getDurationMinutes());
            shiftActivity.setDurationMinutes(0);
            shiftActivity.setScheduledMinutes(0);
            timeBankRepository.save(dailyTimeBankEntry);
        }
    }

}
