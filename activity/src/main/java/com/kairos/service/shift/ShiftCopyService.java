package com.kairos.service.shift;

import com.kairos.commons.service.locale.LocaleService;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.unit_settings.ActivityConfiguration;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.unit_settings.ActivityConfigurationRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.wrapper.ShiftResponseDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.NO_CONFLICTS;
import static com.kairos.constants.AppConstants.TIME_AND_ATTENDANCE;
import static com.kairos.service.shift.ShiftValidatorService.convertMessage;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getValidDays;
import static java.util.stream.Collectors.groupingBy;

/**
 * CreatedBy vipulpandey on 28/11/18
 **/
@Service
public class ShiftCopyService extends MongoBaseService {

    public static final String ERROR = "error";
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityMongoRepository activityRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private ShiftBreakService shiftBreakService;
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private LocaleService localeService;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private ActivityConfigurationRepository activityConfigurationRepository;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private PayOutService payOutService;
    @Inject
    private PhaseService phaseService;


    public CopyShiftResponse copyShifts(Long unitId, CopyShiftDTO copyShiftDTO) {
        List<ShiftResponseDTO> shifts = shiftMongoRepository.findAllByIdGroupByDate(copyShiftDTO.getShiftIds());
        if (!Optional.ofNullable(shifts).isPresent() || shifts.isEmpty()) {
            exceptionService.invalidOperationException(MESSAGE_SHIFT_NOTBLANK);
        }
        Set<BigInteger> activityIds = shifts.stream().flatMap(s -> s.getShifts().stream().flatMap(ss -> ss.getActivities().stream().map(a -> a.getActivityId()))).collect(Collectors.toSet());
        List<ActivityWrapper> activities = activityRepository.findActivitiesAndTimeTypeByActivityId(new ArrayList<>(activityIds));
        Map<BigInteger, ActivityWrapper> activityMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        StaffEmploymentUnitDataWrapper dataWrapper = userIntegrationService.getStaffsEmployment(unitId, copyShiftDTO.getStaffIds(), copyShiftDTO.getExpertiseId());
        List<StaffEmploymentDetails> staffDataList = dataWrapper.getStaffEmploymentDetails();
        List<Long> employmentIds = staffDataList.stream().map(StaffEmploymentDetails::getId).collect(Collectors.toList());
        findAndAddCTAInEmployments(staffDataList, copyShiftDTO, dataWrapper, employmentIds);
        Map<Long, List<WTAQueryResultDTO>> wtaMapByEmploymentId = findAllWTAGroupByEmploymentId(employmentIds, copyShiftDTO);
        List<ActivityConfiguration> activityConfigurations = activityConfigurationRepository.findAllByUnitIdAndDeletedFalse(unitId); // might we add more optimization later
        if (activityConfigurations.isEmpty()) {
            exceptionService.dataNotFoundException(ERROR_ACTIVITYCONFIGURATION_NOTFOUND);
        }
        Integer unCopiedShiftCount = 0;

        List<PlanningPeriodDTO> planningPeriods = planningPeriodMongoRepository.findAllPlanningPeriodBetweenDatesAndUnitId(unitId, asDate(copyShiftDTO.getStartDate()), asDate(copyShiftDTO.getEndDate()));
        if (planningPeriods.isEmpty()) {
            exceptionService.actionNotPermittedException("message.shift.planning.period.exits", copyShiftDTO.getStartDate());
        }

        Map<DateTimeInterval, PlanningPeriodDTO> planningPeriodMap = planningPeriods.stream().collect(Collectors.toMap(k -> new DateTimeInterval(k.getStartDate(), k.getEndDate().plusDays(1)), v -> v));
        CopyShiftResponse copyShiftResponse = new CopyShiftResponse();
        List<ShiftResponseDTO> previousShiftBetweenDatesByEmployment = shiftMongoRepository.findShiftsBetweenDurationByEmploymentIds(employmentIds, asDate(copyShiftDTO.getStartDate().atTime(LocalTime.MIN)), asDate(copyShiftDTO.getEndDate().atTime(LocalTime.MAX)));
        Map<Long, List<Shift>> employmentWiseShifts = previousShiftBetweenDatesByEmployment.stream().collect(Collectors.toMap(key -> key.getEmploymentId(), value -> value.getShifts()));
        for (Long currentStaffId : copyShiftDTO.getStaffIds()) {
            StaffEmploymentDetails staffEmployment = staffDataList.parallelStream().filter(employment -> employment.getStaff().getId().equals(currentStaffId)).findFirst().get();

            List<WTAQueryResultDTO> wtaQueryResultDTOS = wtaMapByEmploymentId.get(staffEmployment.getId());
            List<Shift> currentStaffPreviousShifts = employmentWiseShifts.get(staffEmployment.getId());

            Map<String, List<ShiftResponse>> response = copyForThisStaff(shifts, staffEmployment, activityMap, copyShiftDTO,dataWrapper, wtaQueryResultDTOS, planningPeriodMap, activityConfigurations, currentStaffPreviousShifts);
            StaffWiseShiftResponse successfullyCopied = new StaffWiseShiftResponse(staffEmployment.getStaff(), response.get("success"));
            StaffWiseShiftResponse errorInCopy = new StaffWiseShiftResponse(staffEmployment.getStaff(), response.get(ERROR));
            unCopiedShiftCount += response.get(ERROR).size();
            copyShiftResponse.getSuccessFul().add(successfullyCopied);
            copyShiftResponse.getFailure().add(errorInCopy);
        }
        copyShiftResponse.setUnCopiedShiftCount(unCopiedShiftCount);
        return copyShiftResponse;
    }

    private Map<String, List<ShiftResponse>> copyForThisStaff(List<ShiftResponseDTO> shifts, StaffEmploymentDetails staffEmployment, Map<BigInteger, ActivityWrapper> activityMap, CopyShiftDTO copyShiftDTO,  StaffEmploymentUnitDataWrapper dataWrapper, List<WTAQueryResultDTO> wtaQueryResultDTOS, Map<DateTimeInterval, PlanningPeriodDTO> planningPeriodMap, List<ActivityConfiguration> activityConfigurations, List<Shift> currentStaffPreviousShifts) {
        List<Shift> newShifts = new ArrayList<>(shifts.size());
        Map<String, List<ShiftResponse>> statusMap = new HashMap<>();
        List<ShiftWithActivityDTO> newCreatedShiftWithActivityDTOs = new ArrayList<>();
        List<ShiftResponse> successfullyCopiedShifts = new ArrayList<>();
        List<ShiftResponse> errorInCopyingShifts = new ArrayList<>();
        int counter = 0;
        LocalDate copyShiftStartDate = copyShiftDTO.getStartDate();
        LocalDate copyShiftEndDate = copyShiftDTO.getEndDate();
        List<LocalDate> shiftCreationlocalDates = new ArrayList<>();
        if (isCollectionNotEmpty(copyShiftDTO.getSelectedDays())) {
            Set<DayOfWeek> dayOfWeeks =  copyShiftDTO.getSelectedDays().stream().map(day -> DayOfWeek.valueOf(day.toString())).collect(Collectors.toSet()) ;
            while (copyShiftEndDate.isAfter(copyShiftStartDate) || copyShiftEndDate.equals(copyShiftStartDate)) {
                if (dayOfWeeks.contains(copyShiftStartDate.getDayOfWeek())) {
                    shiftCreationlocalDates.add(copyShiftStartDate);
                }
                copyShiftStartDate = copyShiftStartDate.plusDays(1);
            }
        } else {
            shiftCreationlocalDates.add(copyShiftDTO.getStartDate());
        }
        ShiftResponse shiftResponse;
        for (LocalDate shiftCreationStartDate : shiftCreationlocalDates) {
            ShiftResponseDTO shiftResponseDTO = shifts.get(counter);
            List<String> validationMessages = new ArrayList<>();
            for (Shift sourceShift : shiftResponseDTO.getShifts()) {
                PlanningPeriodDTO planningPeriod = getCurrentPlanningPeriod(planningPeriodMap, shiftCreationStartDate);
                Date startDate = DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, DateUtils.asLocalTime(sourceShift.getStartDate()));
                Date endDate = DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, DateUtils.asLocalTime(sourceShift.getEndDate()));
                if ((shiftCreationStartDate.equals(staffEmployment.getStartDate()) || shiftCreationStartDate.isAfter(staffEmployment.getStartDate())) &&
                        (staffEmployment.getEndDate() == null || shiftCreationStartDate.equals(staffEmployment.getEndDate()) || shiftCreationStartDate.isBefore(staffEmployment.getEndDate()))) {
                    ShiftDTO shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(sourceShift,ShiftDTO.class);
                    shiftDTO.getActivities().forEach(act->{
                        act.setStartDate(DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, DateUtils.asLocalTime(act.getStartDate())));
                        act.setEndDate(DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, DateUtils.asLocalTime(act.getEndDate())));
                    });

                    ShiftWithActivityDTO shiftWithActivityDTO = shiftService.buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(shiftDTO, activityMap);
                    shiftWithActivityDTO.setEndDate(endDate);
                    shiftWithActivityDTO.setStartDate(startDate);
                    shiftWithActivityDTO.setStaffId(staffEmployment.getStaff().getId());
                    String shiftExistsMessage = validateShiftExistanceBetweenDuration(shiftCreationStartDate, sourceShift, currentStaffPreviousShifts);
                    if (shiftExistsMessage != null) {
                        validationMessages.add(shiftExistsMessage);
                    }
                    validationMessages.addAll(shiftValidatorService.validateShiftWhileCopy(dataWrapper, shiftWithActivityDTO, staffEmployment, wtaQueryResultDTOS, planningPeriod, activityMap, newCreatedShiftWithActivityDTOs));
                } else {
                    validationMessages.add(convertMessage(MESSAGE_EMPLOYMENT_NOT_ACTIVE, shiftCreationStartDate));
                }
                shiftResponse = addShift(validationMessages, sourceShift, staffEmployment, startDate, endDate, newShifts, activityMap, dataWrapper, activityConfigurations, planningPeriod);
                if (shiftResponse.isSuccess()) {
                    successfullyCopiedShifts.add(shiftResponse);
                    newCreatedShiftWithActivityDTOs.add(convertIntoShiftWithActivity(newShifts.get(counter), activityMap,shiftCreationStartDate));
                } else {
                    errorInCopyingShifts.add(shiftResponse);
                }
            }
            if (counter++ == shifts.size() - 1) {
                counter = 0;
            }

        }
        statusMap.put("success", successfullyCopiedShifts);
        statusMap.put(ERROR, errorInCopyingShifts);
        if (!newShifts.isEmpty()) {

            save(newShifts);
            timeBankService.updateDailyTimeBankEntries(newShifts, staffEmployment, dataWrapper.getDayTypes());
            payOutService.savePayOuts(staffEmployment, newShifts, null, activityMap, dataWrapper.getDayTypes());

        }
        return statusMap;
    }

    public ShiftWithActivityDTO convertIntoShiftWithActivity(Shift sourceShift, Map<BigInteger, ActivityWrapper> activityMap,LocalDate shiftCreationStartDate) {
        ShiftWithActivityDTO shiftWithActivityDTO = ObjectMapperUtils.copyPropertiesByMapper(sourceShift, ShiftWithActivityDTO.class);
        shiftWithActivityDTO.getActivities().forEach(s -> {
            s.setStartDate(DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, DateUtils.asLocalTime(s.getStartDate())));
            s.setEndDate(DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, DateUtils.asLocalTime(s.getEndDate())));
            ActivityDTO activityDTO = ObjectMapperUtils.copyPropertiesByMapper(activityMap.get(s.getActivityId()).getActivity(), ActivityDTO.class);
            s.setActivity(activityDTO);
        });
        return shiftWithActivityDTO;
    }

    private ShiftResponse addShift(List<String> responseMessages, Shift sourceShift, StaffEmploymentDetails staffEmployment, Date startDate, Date endDate, List<Shift> newShifts,  Map<BigInteger, ActivityWrapper> activityMap, StaffEmploymentUnitDataWrapper dataWrapper, List<ActivityConfiguration> activityConfigurations, PlanningPeriodDTO planningPeriod) {
        if (responseMessages.isEmpty()) {
            List<ShiftActivity> shiftActivities = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(sourceShift.getActivities(),ShiftActivity.class);
            for (ShiftActivity shiftActivity : shiftActivities) {
                shiftActivity.setStartDate(asDate(asLocalDate(startDate),asLocalTime(shiftActivity.getStartDate())));
                shiftActivity.setEndDate(asDate(asLocalDate(endDate),asLocalTime(shiftActivity.getEndDate())));
            }
            Shift copiedShift = new Shift(startDate, endDate,
                    sourceShift.getRemarks(), shiftActivities, staffEmployment.getStaff().getId(), sourceShift.getUnitId(),
                    sourceShift.getScheduledMinutes(), sourceShift.getDurationMinutes(), sourceShift.getExternalId(), staffEmployment.getId(), sourceShift.getParentOpenShiftId(), sourceShift.getId()
                    , planningPeriod.getCurrentPhaseId(), planningPeriod.getId(), staffEmployment.getUserId(), sourceShift.getShiftType());

            WTAQueryResultDTO wtaQueryResultDTO = workingTimeAgreementMongoRepository.getWTAByEmploymentIdAndDate(staffEmployment.getId(), DateUtils.onlyDate(copiedShift.getStartDate()));
            if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
                exceptionService.actionNotPermittedException(MESSAGE_WTA_NOTFOUND);
            }
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = new StaffAdditionalInfoDTO(staffEmployment,dataWrapper.getDayTypes());
                List<ShiftActivity> breakActivities = shiftBreakService.updateBreakInShift(false,copiedShift, activityMap, staffAdditionalInfoDTO,wtaQueryResultDTO.getBreakRule(),dataWrapper.getTimeSlotWrappers(),sourceShift);
                copiedShift.setBreakActivities(breakActivities);

            copiedShift.setActivities(shiftActivities);
            setScheduleMinuteAndHours(copiedShift, activityMap, dataWrapper, staffEmployment, planningPeriod, activityConfigurations);
            copiedShift.setShiftType(sourceShift.getShiftType());
            newShifts.add(copiedShift);

            return new ShiftResponse(sourceShift.getId(), sourceShift.getActivities().get(0).getActivityName(), Collections.singletonList(NO_CONFLICTS), true, asLocalDate(startDate));

        } else {
            List<String> errors = new ArrayList<>();
            responseMessages.forEach(responseMessage -> {
                try {
                    errors.add(localeService.getMessage(responseMessage));
                } catch (Exception e) {
                    // if its not appropriate to convert then we will add this simply
                    errors.add(responseMessage);
                }
            });
            return new ShiftResponse(sourceShift.getId(), sourceShift.getActivities().get(0).getActivityName(), errors, false, asLocalDate(startDate));
        }
    }

    private Map<Long, List<WTAQueryResultDTO>> findAllWTAGroupByEmploymentId(List<Long> employmentIds, CopyShiftDTO copyShiftDTO) {
        List<WTAQueryResultDTO> wtaQueryResults = workingTimeAgreementMongoRepository.getWTAByEmploymentIdsAndDates(employmentIds, asDate(copyShiftDTO.getStartDate()), asDate(copyShiftDTO.getEndDate()));
        return wtaQueryResults.stream().collect(groupingBy(WTAQueryResultDTO::getEmploymentId));

    }

    private void findAndAddCTAInEmployments(List<StaffEmploymentDetails> staffDataList, CopyShiftDTO copyShiftDTO, StaffEmploymentUnitDataWrapper dataWrapper, List<Long> employmentIds) {
        List<CTAResponseDTO> ctaResponseDTOS = costTimeAgreementRepository.getCTAByEmploymentIdsAndDate(employmentIds, asDate(copyShiftDTO.getStartDate()), asDate(copyShiftDTO.getEndDate()));
        Map<Long, List<CTAResponseDTO>> CTAResponseMapByEmploymentIds = ctaResponseDTOS.stream().collect(groupingBy(CTAResponseDTO::getEmploymentId));
        staffDataList.stream().forEach(staffAdditionalInfoDTO -> {
            if (CTAResponseMapByEmploymentIds.get(staffAdditionalInfoDTO.getId()) != null) {
                List<CTAResponseDTO> ctaResponseDTOSList = CTAResponseMapByEmploymentIds.get(staffAdditionalInfoDTO.getId());
                List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = ctaResponseDTOSList.stream().flatMap(ctaResponseDTO -> ctaResponseDTO.getRuleTemplates().stream()).collect(Collectors.toList());
                staffAdditionalInfoDTO.setCtaRuleTemplates(ctaRuleTemplateDTOS);
                ShiftCalculationService.setDayTypeToCTARuleTemplate(staffAdditionalInfoDTO, dataWrapper.getDayTypes(), dataWrapper.getPublicHoliday());
            }
        });
    }

    private void setScheduleMinuteAndHours(Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffEmploymentUnitDataWrapper dataWrapper, StaffEmploymentDetails staffEmployment,
                                           PlanningPeriodDTO planningPeriod, List<ActivityConfiguration> activityConfigurations) {
        int scheduledMinutes = 0, durationMinutes = 0;
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            if (shiftActivity.getId() == null) {
                shiftActivity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            }
            ActivityWrapper currentActivityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
            if (CollectionUtils.isNotEmpty(dataWrapper.getDayTypes())) {
                Map<Long, DayTypeDTO> dayTypeDTOMap = dataWrapper.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
                Set<DayOfWeek> activityDayTypes = getValidDays(dayTypeDTOMap, currentActivityWrapper.getActivity().getTimeCalculationActivityTab().getDayTypes());
                if (activityDayTypes.contains(asLocalDate(shiftActivity.getStartDate()).getDayOfWeek())) {
                    timeBankCalculationService.calculateScheduledAndDurationInMinutes(shiftActivity, currentActivityWrapper.getActivity(), staffEmployment,false);
                    scheduledMinutes += shiftActivity.getScheduledMinutes();
                    durationMinutes += shiftActivity.getDurationMinutes();
                }
            }
            shiftActivity.setBackgroundColor(currentActivityWrapper.getActivity().getGeneralActivityTab().getBackgroundColor());
            shiftActivity.setActivityName(currentActivityWrapper.getActivity().getName());
            shiftActivity.setTimeType(currentActivityWrapper.getTimeType());
        }
        shift.setScheduledMinutes(scheduledMinutes);
        shift.setDurationMinutes(durationMinutes);
    }

    private String validateShiftExistanceBetweenDuration(LocalDate shiftCreationStartDate, Shift sourceShift, List<Shift> currentStaffPreviousShifts) {
        String response = null;
        if (currentStaffPreviousShifts != null) {
            Date startDateTime = DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, asLocalTime(sourceShift.getStartDate()));
            Date endDateTime = DateUtils.getDateByLocalDateAndLocalTime(shiftCreationStartDate, asLocalTime(sourceShift.getEndDate()));
            DateTimeInterval shiftInterval = new DateTimeInterval(startDateTime, endDateTime);
            Optional<Shift> shift = currentStaffPreviousShifts.stream().filter(s -> shiftInterval.overlaps(new DateTimeInterval(s.getStartDate().getTime(), s.getEndDate().getTime()))).findFirst();
            if (shift.isPresent()) {
                response = ("message.shift.date.startandend");
            }
        }
        return response;
    }

    private PlanningPeriodDTO getCurrentPlanningPeriod(Map<DateTimeInterval, PlanningPeriodDTO> planningPeriodMap, LocalDate shiftCreationStartDate) {
        PlanningPeriodDTO planningPeriod = null;
        for (DateTimeInterval currentInterval : planningPeriodMap.keySet()) {
            if (currentInterval.contains(DateUtils.getLongFromLocalDate(shiftCreationStartDate))) {
                planningPeriod = planningPeriodMap.get(currentInterval);
                break;
            }
        }
        return planningPeriod;
    }

}
