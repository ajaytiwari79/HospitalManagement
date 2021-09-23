package com.kairos.service.time_bank;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
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
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.kpi.CalculationType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftDataHelper;
import com.kairos.persistence.model.time_bank.AdvanceViewData;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.time_bank.TimeBankCTADistribution;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.day_type.DayTypeRepository;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.persistence.repository.pay_out.PayOutTransactionMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.time_slot.TimeSlotMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.service.day_type.DayTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.service.pay_out.PayOutCalculationService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.pay_out.PayOutTransaction;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.redis.RedisService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.unit_settings.ProtectedDaysOffService;
import lombok.Getter;
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
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getValidDays;
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
@Getter
public class TimeBankService {

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
    private TimeBankAndPayOutCalculationService timeBankAndPayOutCalculationService;
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
    @Inject
    private CostTimeAgreementService costTimeAgreementService;
    @Inject
    private PlanningPeriodService planningPeriodService;
    @Inject
    private PayOutRepository payOutRepository;
    @Inject
    private OrganizationActivityService organizationActivityService;
    @Inject
    private MongoSequenceRepository mongoSequenceRepository;
    @Inject
    private DayTypeService dayTypeService;
    @Inject
    private ProtectedDaysOffService protectedDaysOffService;
    @Inject
    private PhaseService phaseService;
    @Inject
    private TimeSlotMongoRepository timeSlotMongoRepository;
    @Inject private AsyncTimeBankCalculationService asyncTimeBankCalculationService;
    @Inject private RedisService redisService;
    @Inject private ExecutorService executorService;
    @Inject private DayTypeRepository dayTypeRepository;



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
        redisService.removeKeyFromCache(newHashSet("getAccumulatedTimebankAndDelta::"+shift.getEmploymentId()+"*"));
        executorService.submit(()->{
            asyncTimeBankCalculationService.getAccumulatedTimebankAndDelta(shift.getEmploymentId(), shift.getUnitId(), false,staffAdditionalInfoDTO.getEmployment(),null);
            asyncTimeBankCalculationService.getAccumulatedTimebankAndDelta(shift.getEmploymentId(), shift.getUnitId(), true,staffAdditionalInfoDTO.getEmployment(),null);
        });
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
        Map<BigInteger, ActivityWrapper> activityWrapperMap = organizationActivityService.getActivityWrapperMap(shiftsList,null);
        for (StaffAdditionalInfoDTO staffAdditionalInfoDTO : staffAdditionalInfoDTOS) {
            List<Shift> shiftList = shiftMapByEmploymentId.getOrDefault(staffAdditionalInfoDTO.getEmployment().getId(), new ArrayList<>());
            for (Shift shift : shiftList) {
                shiftService.updateCTADetailsOfEmployement(asLocalDate(shift.getStartDate()),staffAdditionalInfoDTO);
                payOutService.updatePayOut(staffAdditionalInfoDTO,shift,activityWrapperMap);
                DailyTimeBankEntry dailyTimeBankEntry = renewDailyTimeBank(staffAdditionalInfoDTO,shift,false);
                timeBankRepository.save(dailyTimeBankEntry);
            }
            if(isCollectionNotEmpty(shiftList)){
                shiftMongoRepository.saveEntities(shiftList);
            }
        }
    }

    public DailyTimeBankEntry renewDailyTimeBank(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift, boolean validatedByPlanner) {
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = startDate.plusDays(1);
        DailyTimeBankEntry dailyTimeBankEntry = timeBankRepository.findByEmploymentAndDate(staffAdditionalInfoDTO.getEmployment().getId(), asLocalDate(startDate));
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(shift.getUnitId());
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(shift.getId(),staffAdditionalInfoDTO.getEmployment().getId(), startDate.toDate(), endDate.toDate(),null);
        if(!shift.isDeleted()) {
            shiftWithActivityDTOS.add(shiftService.getShiftWithActivityDTO(null, organizationActivityService.getActivityWrapperMap(newArrayList(shift), null), shift));
        }
        List<ShiftWithActivityDTO> draftShifts = getDraftShift(shiftWithActivityDTOS);
        shiftWithActivityDTOS = shiftWithActivityDTOS.stream().filter(shiftWithActivityDTO -> !shiftWithActivityDTO.isDraft()).collect(toList());
        DateTimeInterval interval = new DateTimeInterval(startDate.getMillis(), endDate.getMillis());
        shiftWithActivityDTOS = getShiftsByInterval(shiftWithActivityDTOS, interval);
        dailyTimeBankEntry = updateDailyTimeBankEntry(staffAdditionalInfoDTO, shift, validatedByPlanner, dailyTimeBankEntry, planningPeriodInterval, shiftWithActivityDTOS, interval);
        timeBankCalculationService.updatePublishedBalances(dailyTimeBankEntry, staffAdditionalInfoDTO.getEmployment().getEmploymentLines(), staffAdditionalInfoDTO.getUnitId());
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

    public DailyTimeBankEntry calculateTimeBankForCoverShift(StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift shift){
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = startDate.plusDays(1);
        ShiftWithActivityDTO shiftWithActivityDTO = shiftService.getShiftWithActivityDTO(null, organizationActivityService.getActivityWrapperMap(newArrayList(shift), null), shift);
        DateTimeInterval interval = new DateTimeInterval(startDate.getMillis(), endDate.getMillis());
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(shift.getUnitId());
        DailyTimeBankEntry dailyTimeBankEntry = new DailyTimeBankEntry(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO.getEmployment().getStaffId(), asLocalDate(shift.getStartDate()));
        return timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, newArrayList(shiftWithActivityDTO), dailyTimeBankEntry, planningPeriodInterval, staffAdditionalInfoDTO.getDayTypes(), false);
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
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(null,staffAdditionalInfoDTO.getEmployment().getId(), startDate, endDate,null);
        staffAdditionalInfoDTO.getEmployment().setFunctionId(null);
        if(isCollectionNotEmpty(shiftWithActivityDTOS)) {
            shiftWithActivityDTOS = shiftWithActivityDTOS.stream().sorted(Comparator.comparing(ShiftWithActivityDTO::getStartDate)).collect(Collectors.toList());
            if(isNull(endDate)) {
                endDate = getEndOfDay(shiftWithActivityDTOS.get(shiftWithActivityDTOS.size() - 1).getEndDate());
            }
            List<Shift> shifts = shiftMongoRepository.findAllOverlappedShiftsAndEmploymentId(newArrayList(staffAdditionalInfoDTO.getEmployment().getId()), startDate, endDate);
            for (Shift shift : shifts) {
                shift.setScheduledMinutesOfPayout(0);
                shift.setScheduledMinutesOfTimebank(0);
                DailyTimeBankEntry dailyTimeBankEntry = renewDailyTimeBank(staffAdditionalInfoDTO,shift,false);
                timeBankRepository.save(dailyTimeBankEntry);
            }
            if(isCollectionNotEmpty(shifts)){
                shiftMongoRepository.saveEntities(shifts);
            }
        }
    }

    public EmploymentWithCtaDetailsDTO updateCostTimeAgreementDetails(Long employmentId, Date startDate, Date endDate) {
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = userIntegrationService.getEmploymentDetails(employmentId);
        employmentWithCtaDetailsDTO.getExpertise().setProtectedDaysOffSettings(protectedDaysOffService.getProtectedDaysOffByExpertiseId(employmentWithCtaDetailsDTO.getCountryId()));
        employmentWithCtaDetailsDTO.setCtaRuleTemplates(updateCostTimeAggrement(employmentId, startDate, endDate, employmentWithCtaDetailsDTO));
        return employmentWithCtaDetailsDTO;
    }

    public List<CTARuleTemplateDTO> updateCostTimeAggrement(Long employmentId, Date startDate, Date endDate, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO) {
        if(!Optional.ofNullable(employmentWithCtaDetailsDTO).isPresent()) {
            exceptionService.dataNotFoundException(MESSAGE_STAFFEMPLOYMENT_NOTFOUND);
        }
        return costTimeAgreementService.getCtaRuleTemplatesByEmploymentId(employmentId, startDate, endDate);
    }

    public TimeBankAndPayoutDTO getAdvanceViewTimeBank(TimebankFilterDTO timebankFilterDTO,Long unitId, Long employmentId, String query, Date startDate, Date endDate) {
        Object[] objects = getDayTypeDetails(timebankFilterDTO);
        Set<DayOfWeek> dayOfWeeks = (Set<DayOfWeek>)objects[1];
        Set<LocalDate> dates = (Set<LocalDate>)objects[0];
        AdvanceViewData advanceViewData = new AdvanceViewData(dates,dayOfWeeks,unitId, employmentId, query, startDate, endDate,this).invoke();
        endDate = advanceViewData.getEndDate();
        List<EmploymentWithCtaDetailsDTO> employmentDetails = advanceViewData.getEmploymentDetails();
        List<DateTimeInterval> intervals = advanceViewData.getIntervals();
        long totalTimeBankBeforeStartDate = advanceViewData.getTotalTimeBankBeforeStartDate();
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = advanceViewData.getShiftQueryResultWithActivities();
        List<TimeTypeDTO> timeTypeDTOS = advanceViewData.getTimeTypeDTOS();
        List<PayOutPerShift> payOutPerShifts = advanceViewData.getPayOutPerShifts();
        List<Long> employmentIds = advanceViewData.getEmploymentIds();
        List<DailyTimeBankEntry> dailyTimeBanks = timeBankRepository.findAllByEmploymentIdsAndBeforDate(dates,dayOfWeeks,employmentIds, endDate);
        Map<DateTimeInterval, List<PayOutTransaction>> payoutTransactionIntervalMap = timeBankCalculationService.getPayoutTrasactionIntervalsMap(intervals, startDate,endDate,employmentId);
        boolean includeTimeTypeCalculation = !newHashSet("DAILY-VIEW", "INDIVIDUAL-VIEW").contains(query);
        return timeBankAndPayOutCalculationService.getTimeBankAdvanceView(dates,dayOfWeeks,intervals, unitId, totalTimeBankBeforeStartDate, startDate, endDate, query, shiftQueryResultWithActivities, dailyTimeBanks, employmentDetails, timeTypeDTOS, payoutTransactionIntervalMap,payOutPerShifts, includeTimeTypeCalculation);
    }

    /**
     * @param employmentId
     * @param year
     * @return TimeBankDTO
     */
    public TimeBankDTO getOverviewTimeBank(Long unitId, Long employmentId, Integer year) {
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = userIntegrationService.getEmploymentDetails(employmentId);
        employmentWithCtaDetailsDTO.getExpertise().setProtectedDaysOffSettings(protectedDaysOffService.getProtectedDaysOffByExpertiseId(employmentWithCtaDetailsDTO.getCountryId()));
        Date startDate = asDate(ZonedDateTime.now().withYear(year).with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS));
        Date endDate = asDate(ZonedDateTime.now().withYear(year).with(TemporalAdjusters.lastDayOfYear()).truncatedTo(ChronoUnit.DAYS).with(LocalTime.MAX));
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentAndDate(employmentId, startDate, endDate);
        TimeBankDTO timeBankDTO = timeBankCalculationService.getTimeBankOverview(unitId, employmentId, startDate, endDate, dailyTimeBankEntries, employmentWithCtaDetailsDTO);
        Long actualTimebankMinutes = asyncTimeBankCalculationService.getAccumulatedTimebankAndDelta(employmentId, unitId, false,null,null);
        timeBankDTO.setActualTimebankMinutes(actualTimebankMinutes);
        PlanningPeriodDTO planningPeriodDTO = planningPeriodService.findStartDateAndEndDateOfPlanningPeriodByUnitId(unitId);
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
        DateTimeInterval interval = new DateTimeInterval(startDate, endDate);//
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(null,employmentId, startDate, endDate,null);
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
            exceptionService.dataNotFoundException(MESSAGE_CTA_NOTFOUND,asLocalDate(startDate));
        }
        staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        staffAdditionalInfoDTO.setDayTypes(dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId()));
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
                exceptionService.dataNotFoundException(MESSAGE_CTA_NOTFOUND,shiftDate);
            }
            staffAdditionalInfoDTO.setDayTypes(dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId()));
            staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
            setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
            dailyTimeBanks.add(renewDailyTimeBank(staffAdditionalInfoDTO, shift, false));
            ctaResponseDTOMap.put(shiftDate, ctaResponseDTO);
        }
        if(isCollectionNotEmpty(shifts)){
            shiftMongoRepository.saveEntities(shifts);
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
            Map<String, ShiftActivityDTO> shiftActivityDTOMap = shiftWithActivityDTOS.stream().flatMap(shift1 -> shift1.getActivities().stream()).filter(distinctByKey(shiftWithActivityDTO -> shiftWithActivityDTO.getActivityId()+"_"+shiftWithActivityDTO.getStartDate())).collect(Collectors.toMap(k -> k.getActivityId()+"_"+k.getStartDate(), v -> v));
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
            updateTimebankDetailsInShifts(shifts, shiftActivityDTOMap,shiftWithActivityDTOS);
        }
    }

    private void updateTimebankDetailsInShifts(List<Shift> shifts, Map<String, ShiftActivityDTO> shiftActivityDTOMap,List<ShiftWithActivityDTO> shiftWithActivityDTOS) {
        Map<BigInteger,ShiftWithActivityDTO> shiftWithActivityDTOMap = shiftWithActivityDTOS.stream().collect(Collectors.toMap(shiftWithActivityDTO -> shiftWithActivityDTO.getId(),v->v));
        for (Shift shift : shifts) {
            if (!shift.isDeleted()) {
                if (isNotNull(shift.getDraftShift())) {
                    shift = shift.getDraftShift();
                }
                int timeBankCtaBonusMinutes = 0;
                int plannedMinutesOfTimebank = 0;
                int timeBankScheduledMinutes = 0;
                for (ShiftActivity shiftActivity : shift.getActivities()) {
                    updateTimebankDetailsInShiftActivity(shiftActivityDTOMap, shiftActivity);
                    timeBankCtaBonusMinutes += shiftActivity.getTimeBankCtaBonusMinutes();
                    plannedMinutesOfTimebank += shiftActivity.getPlannedMinutesOfTimebank();
                    timeBankScheduledMinutes += shiftActivity.getScheduledMinutesOfTimebank();
                    if (Optional.ofNullable(shiftActivity.getChildActivities()).isPresent()) {
                        for (ShiftActivity childActivity : shiftActivity.getChildActivities()) {
                            updateTimebankDetailsInShiftActivity(shiftActivityDTOMap, childActivity);
                        }
                    }
                }
                if (isCollectionNotEmpty(shift.getBreakActivities())) {
                    for (ShiftActivity breakActivity : shift.getBreakActivities()) {
                        updateTimebankDetailsInShiftActivity(shiftActivityDTOMap, breakActivity);
                        timeBankCtaBonusMinutes += breakActivity.getTimeBankCtaBonusMinutes();
                        plannedMinutesOfTimebank += breakActivity.getPlannedMinutesOfTimebank();
                        timeBankScheduledMinutes += breakActivity.getScheduledMinutesOfTimebank();
                    }
                }
                if(shiftWithActivityDTOMap.containsKey(shift.getId())){
                    ShiftWithActivityDTO shiftWithActivityDTO = shiftWithActivityDTOMap.get(shift.getId());
                    shift.setTimeBankCTADistributions(ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftWithActivityDTO.getTimeBankCTADistributions(), TimeBankCTADistribution.class));
                }
                shift.setScheduledMinutesOfTimebank(timeBankScheduledMinutes);
                shift.setTimeBankCtaBonusMinutes(timeBankCtaBonusMinutes);
                shift.setPlannedMinutesOfTimebank(plannedMinutesOfTimebank);
            }
        }
    }

    private void updateTimebankDetailsInShiftActivity(Map<String, ShiftActivityDTO> shiftActivityDTOMap, ShiftActivity shiftActivity) {
        if(shiftActivityDTOMap.containsKey(shiftActivity.getActivityId()+"_"+shiftActivity.getStartDate())) {
            ShiftActivityDTO shiftActivityDTO = shiftActivityDTOMap.get(shiftActivity.getActivityId() + "_" + shiftActivity.getStartDate());
            shiftActivity.setTimeBankCtaBonusMinutes((int)shiftActivityDTO.getTimeBankCtaBonusMinutes());
            shiftActivity.setTimeBankCTADistributions(ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftActivityDTO.getTimeBankCTADistributions(), TimeBankCTADistribution.class));
            shiftActivity.setPlannedMinutesOfTimebank(shiftActivityDTO.getScheduledMinutesOfTimebank() + (int)shiftActivityDTO.getTimeBankCtaBonusMinutes());
            shiftActivity.setScheduledMinutesOfTimebank(shiftActivityDTO.getScheduledMinutesOfTimebank());
        }
    }

    public boolean renewTimeBankOfShifts() {
        List<Shift> shifts = shiftMongoRepository.findAllByDeletedFalse();
        Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoDTOMap = new HashMap<>();
        List<DailyTimeBankEntry> dailyTimeBanks = new ArrayList<>(shifts.size());
        Set<Long> employmentIds = new HashSet<>();
        for (Shift shift : shifts) {
            try {
                StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()), shift.getStaffId(), shift.getEmploymentId());
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
                employmentIds.add(shift.getEmploymentId());
                LOGGER.info("staff is not the part of this Unit {}",e.getMessage());
            }
            if(staffAdditionalInfoDTOMap.containsKey(shift.getEmploymentId()) && CollectionUtils.isNotEmpty(staffAdditionalInfoDTOMap.get(shift.getEmploymentId()).getEmployment().getCtaRuleTemplates())) {
                DailyTimeBankEntry dailyTimeBankEntries = renewDailyTimeBank(staffAdditionalInfoDTOMap.get(shift.getEmploymentId()), shift, false);
                timeBankRepository.save(dailyTimeBankEntries);
            }
        }
        return true;

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

    public EmploymentWithCtaDetailsDTO getEmploymentDetailDTO(StaffEmploymentDetails employment, Long unitId) {
        return new EmploymentWithCtaDetailsDTO(employment.getId(), employment.getTotalWeeklyHours(), employment.getTotalWeeklyMinutes(), employment.getWorkingDaysInWeek(), employment.getStaffId(), employment.getStartDate(), employment.getEndDate(), employment.getEmploymentLines(), employment.getAccumulatedTimebankMinutes(), employment.getAccumulatedTimebankDate(),unitId,employment.getEmploymentType().getId(),employment.getUnitTimeZone());
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
        List<DayTypeDTO> dayTypeDTOS=dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId());
        staffAdditionalInfoDTOS.forEach(staff-> staff.setDayTypes(dayTypeDTOS));
        Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap = staffAdditionalInfoDTOS.stream().filter(distinctByKey(staffAdditionalInfoDTO -> staffAdditionalInfoDTO.getEmployment().getId())).collect(Collectors.toMap(s -> s.getEmployment().getId(), v -> v));
        if(isCollectionNotEmpty(shifts)) {
            Date startDateTime = new DateTime(shifts.get(0).getStartDate()).withTimeAtStartOfDay().toDate();
            Date endDateTime = new DateTime(shifts.get(shifts.size() - 1).getEndDate()).plusDays(1).withTimeAtStartOfDay().toDate();
            List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByEmploymentIdsAndDate(new ArrayList<>(staffAdditionalInfoMap.keySet()), startDateTime, endDateTime);
            Map<Long, List<CTAResponseDTO>> employmentAndCTAResponseMap = ctaResponseDTOS.stream().collect(groupingBy(CTAResponseDTO::getEmploymentId));
            for (Shift shift : shifts) {
                updateAndRenewDailyTimeBank(shifts, planningPeriod, staffAdditionalInfoMap, employmentAndCTAResponseMap, shift);
            }
            shiftMongoRepository.saveEntities(shifts);
        }
        if(isNotNull(planningPeriod)){
            updatePublishBalance(staffAdditionalInfoMap,planningPeriod);
        }
        return true;
    }

    private void updateAndRenewDailyTimeBank(List<Shift> shifts, PlanningPeriod planningPeriod, Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoMap, Map<Long, List<CTAResponseDTO>> employmentAndCTAResponseMap, Shift shift) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffAdditionalInfoMap.get(shift.getEmploymentId());
        CTAResponseDTO ctaResponseDTO = getCTAByDate(employmentAndCTAResponseMap.get(shift.getEmploymentId()), asLocalDate(shift.getStartDate()));
        if(isNull(ctaResponseDTO)){
            if(isNotNull(planningPeriod)){
                return;
            }
            exceptionService.dataNotFoundException(MESSAGE_CTA_NOTFOUND,asLocalDate(shift.getStartDate()));
        }
        staffAdditionalInfoDTO.getEmployment().setCtaRuleTemplates(ctaResponseDTO.getRuleTemplates());
        staffAdditionalInfoDTO.setUnitId(shifts.get(0).getUnitId());
        setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO);
        if(isNotNull(planningPeriod)) {
            DailyTimeBankEntry dailyTimeBankEntry = renewDailyTimeBank(staffAdditionalInfoDTO, shift, planningPeriod.getPublishEmploymentIds().contains(staffAdditionalInfoDTO.getEmployment().getEmploymentType().getId()));
            timeBankRepository.save(dailyTimeBankEntry);
        }
    }

    private CTAResponseDTO getCTAByDate(List<CTAResponseDTO> ctaResponseDTOS, LocalDate shiftDate) {
        CTAResponseDTO ctaResponse = null;
        if(isCollectionNotEmpty(ctaResponseDTOS)) {
            for (CTAResponseDTO ctaResponseDTO : ctaResponseDTOS) {
                DateTimeInterval dateTimeInterval = new DateTimeInterval(asDate(ctaResponseDTO.getStartDate()), isNotNull(ctaResponseDTO.getEndDate()) ? asDateEndOfDay(ctaResponseDTO.getEndDate()) : asDateEndOfDay(shiftDate));
                if (dateTimeInterval.contains(asDate(shiftDate))) {
                    ctaResponse = ctaResponseDTO;
                    break;
                }
            }
        }
        return ctaResponse;
    }



    public void updateDailyTimebank(Long unitId){
        List<Shift> shifts = shiftMongoRepository.findAllByUnitId(unitId);
        for (Shift shift : shifts) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(asLocalDate(shift.getStartDate()), shift.getStaffId(), shift.getEmploymentId());
            staffAdditionalInfoDTO.setDayTypes(dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId()));
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
                if(isNotNull(dailyTimeBankEntryByStartDate) && dailyTimeBankEntryByStartDate.isPublishedSomeActivities()){
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

    private Object[] getDayTypeDetails(TimebankFilterDTO timebankFilterDTO){
        Set<DayOfWeek> dayOfWeeks = new HashSet<>();
        Set<LocalDate> dates = new HashSet<>();
        if(isNotNull(timebankFilterDTO) && isCollectionNotEmpty(timebankFilterDTO.getDayTypeIds())) {
            List<DayTypeDTO> dayTypeDTOS = dayTypeRepository.findAllByIdInAndDeletedFalse(timebankFilterDTO.getDayTypeIds());
            for (DayTypeDTO dayTypeDTO : dayTypeDTOS) {
                if (dayTypeDTO.isHolidayType()) {
                    dates.addAll(dayTypeDTO.getCountryHolidayCalenderData().stream().map(countryHolidayCalenderDTO -> countryHolidayCalenderDTO.getHolidayDate()).collect(Collectors.toSet()));
                } else {
                    dayOfWeeks.add(DayOfWeek.valueOf(dayTypeDTO.getValidDays().get(0).toString()));
                }
            }
        }
        return new Object[]{dates,dayOfWeeks};
    }

    public long getExpectedTimebankByDate(ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = startDate.plusDays(1);
        DailyTimeBankEntry dailyTimeBankEntriy = timeBankRepository.findByEmploymentAndDate(staffAdditionalInfoDTO.getEmployment().getId(), asLocalDate(startDate));
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(shift.getUnitId());
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(shift.getId(),staffAdditionalInfoDTO.getEmployment().getId(), startDate.toDate(), endDate.toDate(),null);
        DateTimeInterval interval = new DateTimeInterval(startDate.getMillis(), endDate.getMillis());
        List<ShiftWithActivityDTO> shiftWithActivityDTOList = getShiftsByInterval(shiftWithActivityDTOS, interval);
        if(!shift.isDeleted()) {
            shiftWithActivityDTOList.add(shift);
        }
        staffAdditionalInfoDTO.getEmployment().setStaffId(staffAdditionalInfoDTO.getId());
        DailyTimeBankEntry dailyTimeBankEntry = timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, shiftWithActivityDTOList, dailyTimeBankEntriy, planningPeriodInterval, staffAdditionalInfoDTO.getDayTypes(), false);
        TreeMap<LocalDate, TimeBankIntervalDTO> timeBankByDateDTOMap = asyncTimeBankCalculationService.getAccumulatedTimebankAndDelta(staffAdditionalInfoDTO.getEmployment().getId(),shift.getUnitId(),true,staffAdditionalInfoDTO.getEmployment(),null);
        long expectedTimebank = timeBankByDateDTOMap.lastEntry().getValue().getExpectedTimebankMinutes();
        if (isNotNull(dailyTimeBankEntry)) {
            expectedTimebank += dailyTimeBankEntry.getDeltaTimeBankMinutes();
        }
        return expectedTimebank;
    }

    public long getExpectedTimebankByDate(ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDataHelper shiftDataHelper) {
        DateTime startDate = new DateTime(shift.getStartDate()).withTimeAtStartOfDay();
        DateTime endDate = startDate.plusDays(1);
        DailyTimeBankEntry dailyTimeBankEntriy = timeBankRepository.findByEmploymentAndDate(staffAdditionalInfoDTO.getEmployment().getId(), asLocalDate(startDate));
        DateTimeInterval planningPeriodInterval = shiftDataHelper.getPlanningPeriodDateTimeInterval();
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentId(shift.getId(),staffAdditionalInfoDTO.getEmployment().getId(), startDate.toDate(), endDate.toDate(),null);
        DateTimeInterval interval = new DateTimeInterval(startDate.getMillis(), endDate.getMillis());
        List<ShiftWithActivityDTO> shiftWithActivityDTOList = getShiftsByInterval(shiftWithActivityDTOS, interval);
        if(!shift.isDeleted()) {
            shiftWithActivityDTOList.add(shift);
        }
        staffAdditionalInfoDTO.getEmployment().setStaffId(staffAdditionalInfoDTO.getId());
        DailyTimeBankEntry dailyTimeBankEntry = timeBankCalculationService.calculateDailyTimeBank(staffAdditionalInfoDTO, interval, shiftWithActivityDTOList, dailyTimeBankEntriy, planningPeriodInterval, shiftDataHelper.getDayTypes(), false);
        TreeMap<LocalDate, TimeBankIntervalDTO> timeBankByDateDTOMap = asyncTimeBankCalculationService.getAccumulatedTimebankAndDelta(staffAdditionalInfoDTO.getEmployment().getId(),shift.getUnitId(),true,staffAdditionalInfoDTO.getEmployment(),shiftDataHelper);
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

    public int[] updateScheduledHoursAndActivityDetailsInShiftActivity(ShiftActivity shiftActivity, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        int scheduledMinutes = 0;
        int durationMinutes = 0;
        //if (shiftActivity.getId() == null) {
            shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
        //}
        ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
        shiftActivity.setTimeType(activityWrapper.getTimeType());
        Activity activity = activityWrapper.getActivity();
        if (CollectionUtils.isNotEmpty(staffAdditionalInfoDTO.getDayTypes())) {
            Map<BigInteger, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
            Set<DayOfWeek> activityDayTypes = getValidDays(dayTypeDTOMap, activity.getActivityTimeCalculationSettings().getDayTypes(), asLocalDate(shiftActivity.getStartDate()));
            if (activityDayTypes.contains(DateUtils.asLocalDate(shiftActivity.getStartDate()).getDayOfWeek())) {
                timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity, activity, staffAdditionalInfoDTO.getEmployment(), false);
                scheduledMinutes = shiftActivity.getScheduledMinutes();
                durationMinutes = shiftActivity.getDurationMinutes();
            }
        }
        shiftActivity.setSecondLevelTimeType(activity.getActivityBalanceSettings().getTimeType());
        shiftActivity.setBackgroundColor(activity.getActivityGeneralSettings().getBackgroundColor());
        shiftActivity.setActivityName(activity.getName());
        shiftActivity.setUltraShortName(activity.getActivityGeneralSettings().getUltraShortName());
        shiftActivity.setShortName(activity.getActivityGeneralSettings().getShortName());
        return new int[]{scheduledMinutes, durationMinutes};
    }
    public void updateShiftDailyTimeBankAndPaidOut(List<Shift> shifts, List<Shift> shiftsList, Long unitId) {
        if (isCollectionEmpty(shifts)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SHIFT_IDS);
        }
        List<Long> staffIds = shifts.stream().map(Shift::getStaffId).collect(Collectors.toList());
        List<Long> employmentIds = shifts.stream().map(Shift::getEmploymentId).collect(Collectors.toList());
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("staffIds", staffIds.toString()));
        requestParam.add(new BasicNameValuePair("employmentIds", employmentIds.toString()));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getStaffAditionalDTOS(unitId, requestParam);
        List<DayTypeDTO> dayTypeDTOS=dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId());
        staffAdditionalInfoDTOS.forEach(staff-> staff.setDayTypes(dayTypeDTOS));
        List<TimeSlotDTO> timeSlotDTOS= timeSlotMongoRepository.findByUnitIdAndTimeSlotTypeOrderByStartDate(unitId, TimeSlotType.SHIFT_PLANNING).getTimeSlots();
        staffAdditionalInfoDTOS.forEach(staff-> staff.setTimeSlotSets(timeSlotDTOS));
        shifts.sort(Comparator.comparing(Shift::getStartDate));
        shiftsList.sort((shift, shiftSecond) -> shift.getStartDate().compareTo(shiftSecond.getStartDate()));
        Date startDate = shifts.get(0).getStartDate();
        Date endDate = shifts.get(shifts.size() - 1).getEndDate();
        Date shiftStartDate = shiftsList.get(0).getStartDate();
        Date shiftEndDate = shiftsList.get(shiftsList.size() - 1).getEndDate();
        startDate = startDate.before(shiftStartDate) ? startDate : shiftStartDate;
        endDate = endDate.after(shiftEndDate) ? endDate : shiftEndDate;
        saveTimeBanksAndPayOut(staffAdditionalInfoDTOS, startDate, endDate);
    }

}
