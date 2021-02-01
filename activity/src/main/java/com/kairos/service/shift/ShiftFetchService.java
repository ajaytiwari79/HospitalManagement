package com.kairos.service.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.attendance.AttendanceTimeSlotDTO;
import com.kairos.dto.activity.attendance.TimeAndAttendanceDTO;
import com.kairos.dto.activity.open_shift.OpenShiftResponseDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.kpermissions.FieldPermissionUserData;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.staff.staff.StaffAccessRoleDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftFilterParam;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import com.kairos.enums.shift.ViewType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.TimeAndAttendanceRepository;
import com.kairos.persistence.repository.cta.CostTimeAgreementRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.reason_code.ReasonCodeRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.time_slot.TimeSlotRepository;
import com.kairos.persistence.repository.todo.TodoRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.activity.StaffActivityDetailsService;
import com.kairos.service.auto_gap_fill_settings.AutoFillGapSettingsService;
import com.kairos.service.day_type.DayTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.scheduler_service.ActivitySchedulerJobService;
import com.kairos.service.staffing_level.StaffingLevelAvailableCountService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.todo.TodoService;
import com.kairos.service.unit_settings.ActivityConfigurationService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_EMPLOYMENT_ABSENT;
import static com.kairos.dto.user.access_permission.AccessGroupRole.MANAGEMENT;
import static com.kairos.enums.FilterType.INCLUDE_DRAFT_SHIFT;
import static com.kairos.enums.reason_code.ReasonCodeType.TIME_TYPE;
import static com.kairos.enums.shift.ShiftType.SICK;

@Service
public class ShiftFetchService {

    public static final String NAME = "name";
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject private ShiftFilterService shiftFilterService;
    @Inject private ShiftValidatorService shiftValidatorService;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject
    private TimeAndAttendanceRepository timeAndAttendanceRepository;
    @Inject
    private PhaseService phaseService;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private PayOutService payOutService;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private OpenShiftMongoRepository openShiftMongoRepository;
    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private OpenShiftNotificationMongoRepository openShiftNotificationMongoRepository;
    @Inject
    private CostTimeAgreementRepository costTimeAgreementRepository;
    @Inject
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private ShiftStateService shiftStateService;
    @Inject
    private WTARuleTemplateCalculationService wtaRuleTemplateCalculationService;
    @Inject
    private ShiftDetailsService shiftDetailsService;
    @Inject
    private TodoService todoService;
    @Inject
    private TodoRepository todoRepository;
    @Inject
    private ActivityConfigurationService activityConfigurationService;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject
    private ActivitySchedulerJobService activitySchedulerJobService;
    @Inject
    private ActivityService activityService;
    @Inject
    private ShiftFunctionService shiftFunctionService;
    @Inject
    private StaffActivityDetailsService staffActivityDetailsService;
    @Inject
    private ReasonCodeRepository reasonCodeRepository;
    @Inject
    private TimeSlotRepository timeSlotRepository;
    @Inject
    private DayTypeService dayTypeService;
    @Inject
    private AutoFillGapSettingsService gapSettingsService;
    @Inject
    private StaffingLevelAvailableCountService staffingLevelAvailableCountService;

    public Object getAllShiftAndStates(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long employmentId, ViewType viewType, ShiftFilterParam shiftFilterParam, Long expertiseId, StaffFilterDTO staffFilterDTO) {
        Object object = null;
        if (endDate != null) {
            endDate = endDate.plusDays(1);
        }
        switch (shiftFilterParam) {
            case INDIVIDUAL_VIEW:
                object = getShiftByStaffId(unitId, staffId, startDate, endDate, employmentId, staffFilterDTO);
                break;
            case OPEN_SHIFT:
                object = getAllShiftsOfSelectedDate(unitId, startDate, endDate, viewType, staffFilterDTO);
                break;
            case EXPERTISE:
                object = getShiftOfStaffByExpertiseId(unitId, staffId, startDate, endDate, expertiseId, staffFilterDTO);
                break;
            case SHIFT_STATE:
                object = getDetailedAndCompactViewData(staffId, unitId, asDate(startDate), staffFilterDTO);
                break;
            default:
                exceptionService.actionNotPermittedException(PLEASE_SELECT_VALID_CRITERIA);
        }
        return object;
    }

    public CompactViewDTO getDetailedAndCompactViewData(Long selectedStaffId, Long unitId, Date shiftStartDate, StaffFilterDTO staffFilterDTO) {
        List<Long> staffIds;
        if (isNull(selectedStaffId)) {
            List<StaffDTO> staffResponseDTOS = userIntegrationService.getStaffListByUnit();
            staffIds = staffResponseDTOS.stream().map(StaffDTO::getId).collect(Collectors.toList());
        } else {
            staffIds = Arrays.asList(selectedStaffId);
        }
        String timeZone = userIntegrationService.getTimeZoneByUnitId(unitId);
        List<Phase> phases = phaseMongoRepository.findByOrganizationIdAndDeletedFalse(unitId);
        Map<String, Phase> phaseMap = phases.stream().collect(Collectors.toMap(p -> p.getPhaseEnum().toString(), Function.identity()));
        Date endDate = asDate(DateUtils.asZonedDateTime(shiftStartDate).plusDays(1));
        List<TimeAndAttendanceDTO> timeAndAttendance = timeAndAttendanceRepository.findAllAttendanceByStaffIds(staffIds, unitId, asDate(DateUtils.asLocalDate(shiftStartDate).minusDays(1)), shiftStartDate);
        Map<Long, List<AttendanceTimeSlotDTO>> staffsTimeAndAttendance = (CollectionUtils.isNotEmpty(timeAndAttendance)) ? timeAndAttendance.stream().collect(Collectors.toMap(TimeAndAttendanceDTO::getStaffId, TimeAndAttendanceDTO::getAttendanceTimeSlot)) : new HashMap<>();
        List<Shift> shifts = shiftMongoRepository.findShiftByStaffIdsAndDate(staffIds, shiftStartDate, endDate,staffFilterDTO);
        shifts.forEach(shift -> shift.setDurationMinutes((int) shift.getInterval().getMinutes()));
        shifts = shifts.stream().filter(shift -> !shift.isDraft()).collect(Collectors.toList());
        List<ShiftState> shiftStates = shiftStateMongoRepository.getAllByStaffsByIdsBetweenDate(staffIds, shiftStartDate, endDate);
        Map<BigInteger, PhaseDefaultName> phaseIdAndDefaultNameMap = phases.stream().collect(Collectors.toMap(MongoBaseEntity::getId, Phase::getPhaseEnum));
        List<ShiftState> realTimeShiftStatesList = shiftStateService.checkAndCreateRealtimeAndDraftState(shifts, phaseMap, phaseIdAndDefaultNameMap);
        shiftStates.addAll(realTimeShiftStatesList);
        Map<Long, List<Shift>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        List<org.apache.http.NameValuePair> requestParam = Arrays.asList(new BasicNameValuePair("reasonCodeType", TIME_TYPE.toString()));
        List<ReasonCodeDTO> reasonCodeDTOS = userIntegrationService.getReasonCodeDTOList(unitId, requestParam);
        Map<Long, List<ShiftState>> shiftStateMap = shiftStates.stream().collect(Collectors.groupingBy(Shift::getStaffId, Collectors.toList()));
        List<DetailViewDTO> shiftDetailViewDTOMap = staffIds.stream().map(staffId -> new DetailViewDTO(staffId, getShiftDetailsOfStaff(timeZone, phaseMap, shiftsMap.getOrDefault(staffId, new ArrayList<>()), shiftStateMap.getOrDefault(staffId, new ArrayList<>()), staffFilterDTO, phaseIdAndDefaultNameMap), staffsTimeAndAttendance.getOrDefault(staffId, new ArrayList<>()))).collect(Collectors.toList());
        Map<LocalDate, List<FunctionDTO>> functionDTOMap = userIntegrationService.getFunctionsOfEmployment(unitId, asLocalDate(shiftStartDate), asLocalDate(endDate));
        return new CompactViewDTO(shiftDetailViewDTOMap, reasonCodeDTOS, functionDTOMap);
    }

    private ShiftDetailViewDTO getShiftDetailsOfStaff(String timeZone, Map<String, Phase> phaseMap, List<Shift> shifts, List<ShiftState> shiftStatesList, StaffFilterDTO staffFilterDTO, Map<BigInteger, PhaseDefaultName> phaseIdAndDefaultNameMap) {
        List<ShiftDTO> plannedShifts = ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftStatesList.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.DRAFT.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        if (isCollectionEmpty(plannedShifts)) {
            shifts = shifts.stream().filter(shift -> !newHashSet(PhaseDefaultName.TIME_ATTENDANCE, PhaseDefaultName.REALTIME).contains(phaseIdAndDefaultNameMap.get(shift.getPhaseId()))).collect(Collectors.toList());
            plannedShifts = ObjectMapperUtils.copyCollectionPropertiesByMapper(shifts, ShiftDTO.class);
        }
        plannedShifts = shiftFilterService.getShiftsByFilters(plannedShifts, staffFilterDTO, new ArrayList<>());
        plannedShifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(plannedShifts);
        List<ShiftDTO> realTimeShift = ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftStatesList.stream().filter(s -> s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        realTimeShift = shiftFilterService.getShiftsByFilters(realTimeShift, staffFilterDTO, new ArrayList<>());
        List<ShiftDTO> shiftStateDTOs = ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftStatesList, ShiftDTO.class);
        shiftStateDTOs = shiftFilterService.getShiftsByFilters(shiftStateDTOs, staffFilterDTO, new ArrayList<>());
        List<ShiftDTO> staffValidatedShifts = shiftStateDTOs.stream().filter(s -> s.getAccessGroupRole() != null && s.getAccessGroupRole().equals(AccessGroupRole.STAFF) && s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId())).collect(Collectors.toList());
        staffValidatedShifts = shiftFilterService.getShiftsByFilters(staffValidatedShifts, staffFilterDTO, new ArrayList<>());
        Map<String, ShiftDTO> staffAndShiftMap = staffValidatedShifts.stream().collect(Collectors.toMap(k -> k.getStaffId() + "" + k.getId(), v -> v));
        List<ShiftDTO> updateRealTime = getShiftDTOS(timeZone, phaseMap, realTimeShift, staffValidatedShifts, staffAndShiftMap);
        staffValidatedShifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(staffValidatedShifts);
        List<ShiftDTO> plannerValidatedShifts = ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftStateDTOs.stream().filter(s -> s.getAccessGroupRole() != null && s.getAccessGroupRole().equals(MANAGEMENT) && s.getShiftStatePhaseId().equals(phaseMap.get(PhaseDefaultName.TIME_ATTENDANCE.toString()).getId())).collect(Collectors.toList()), ShiftDTO.class);
        plannerValidatedShifts = shiftFilterService.getShiftsByFilters(plannerValidatedShifts, staffFilterDTO, new ArrayList<>());
        //change id because id was same and issue on FE side and this is only for show FE side
        for (ShiftDTO shiftDTO : plannerValidatedShifts) {
            if (shiftDTO.getValidated() == null) {
                shiftDTO.setEditable(true);
            }
        }
        plannerValidatedShifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(plannerValidatedShifts);
        return new ShiftDetailViewDTO(plannedShifts, updateRealTime, staffValidatedShifts, plannerValidatedShifts);
    }

    private List<ShiftDTO> getShiftDTOS(String timeZone, Map<String, Phase> phaseMap, List<ShiftDTO> realTimeShift, List<ShiftDTO> staffValidatedShifts, Map<String, ShiftDTO> staffAndShiftMap) {
        DateTimeInterval graceInterval;
        List<ShiftDTO> updateRealTime = new ArrayList<>();
        for (ShiftDTO shiftDTO : realTimeShift) {
            if (!Optional.ofNullable(staffAndShiftMap.get(shiftDTO.getStaffId() + "" + shiftDTO.getId())).isPresent() && shiftDTO.getValidated() == null && phaseService.shiftEditableInRealtime(timeZone, phaseMap, shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate())) {
                shiftDTO.setEditable(true);
            }
            updateRealTime.add(shiftDTO);
        }
        updateRealTime = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(updateRealTime);
        if (!staffValidatedShifts.isEmpty()) {
            Phase phase = phaseMongoRepository.findByUnitIdAndPhaseEnum(staffValidatedShifts.get(0).getUnitId(), PhaseDefaultName.TIME_ATTENDANCE.toString());
            graceInterval = shiftValidatorService.getGracePeriodInterval(phase, staffValidatedShifts.get(0).getStartDate(), false);
            for (ShiftDTO staffValidatedShift : staffValidatedShifts) {
                if (staffValidatedShift.getValidated() == null && graceInterval.contains(staffValidatedShift.getStartDate())) {
                    staffValidatedShift.setEditable(true);
                }
            }
        }
        return updateRealTime;
    }

    private List<ShiftDTO> getShiftOfStaffByExpertiseId(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long expertiseId, StaffFilterDTO staffFilterDTO) {
        if (isNull(staffId) || isNull(endDate) || isNull(expertiseId)) {
            exceptionService.actionNotPermittedException(STAFF_ID_END_DATE_NULL);
        }
        Long employmentId = userIntegrationService.getEmploymentId(unitId, staffId, expertiseId);
        List<ShiftDTO> shiftDTOS = shiftMongoRepository.getAllShiftBetweenDuration(employmentId, staffId, asDate(startDate), asDate(endDate), unitId,staffFilterDTO);
        return wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS);
    }

    private ShiftWrapper getAllShiftsOfSelectedDate(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate, ViewType viewType, StaffFilterDTO staffFilterDTO) {
        if (endLocalDate == null) {
            exceptionService.actionNotPermittedException(ENDDATE_NULL);
        }
        Date startDate = asDate(startLocalDate);
        Date endDate = asDate(endLocalDate);
        List<ShiftDTO> assignedShifts = shiftMongoRepository.getAllAssignedShiftsByDateAndUnitId(unitId, startDate, endDate,staffFilterDTO);
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessRolesOfStaff(unitId);
        if(!staffFilterDTO.getFiltersData().stream().anyMatch(filterSelectionDTO -> INCLUDE_DRAFT_SHIFT.equals(filterSelectionDTO.getName()))){
            assignedShifts = updateDraftShiftToShift(assignedShifts);
        }
        Map<Long, List<ShiftDTO>> employmentIdAndShiftsMap = assignedShifts.stream().collect(Collectors.groupingBy(ShiftDTO::getEmploymentId, Collectors.toList()));
        assignedShifts = new ArrayList<>(assignedShifts.size());
        Set<BigInteger> sickActivityIds = new HashSet<>();
        for (Map.Entry<Long, List<ShiftDTO>> employmentIdAndShiftEntry : employmentIdAndShiftsMap.entrySet()) {
            assignedShifts.addAll(wtaRuleTemplateCalculationService.updateRestingTimeInShifts(employmentIdAndShiftEntry.getValue()));
            sickActivityIds.addAll(employmentIdAndShiftEntry.getValue().parallelStream().filter(shiftDTO -> SICK.equals(shiftDTO.getShiftType())).flatMap(shiftDTO -> shiftDTO.getActivities().stream()).map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet()));
        }
        List<OpenShift> openShifts = userAccessRoleDTO.isManagement() ? openShiftMongoRepository.getOpenShiftsByUnitIdAndDate(unitId, startDate, endDate) : openShiftNotificationMongoRepository.findValidOpenShiftsForStaff(userAccessRoleDTO.getStaffId(), startDate, endDate);
        ButtonConfig buttonConfig = null;
        if (Optional.ofNullable(viewType).isPresent() && viewType.toString().equalsIgnoreCase(ViewType.WEEKLY.toString())) {
            buttonConfig = shiftStateService.findButtonConfig(assignedShifts, userAccessRoleDTO.isManagement());
        }
        StaffAccessRoleDTO staffAccessRoleDTO = new StaffAccessRoleDTO(userAccessRoleDTO.getStaffId(), getAccessGroupRole(userAccessRoleDTO));
        Map<LocalDate, List<FunctionDTO>> appliedFunctionDTOs = userIntegrationService.getFunctionsOfEmployment(unitId, startLocalDate, endLocalDate);
        List<ShiftDTO> shiftDTOS=shiftDetailsService.setLayerInShifts(assignedShifts,sickActivityIds);
        return new ShiftWrapper(shiftDTOS, getOpenShiftResponceDTOS(openShifts), staffAccessRoleDTO, buttonConfig, appliedFunctionDTOs);
    }

    private List<AccessGroupRole> getAccessGroupRole(UserAccessRoleDTO userAccessRoleDTO){
        List<AccessGroupRole> roles = new ArrayList<>();
        if (!userAccessRoleDTO.isStaff()) {
            roles.add(MANAGEMENT);
        }
        if (userAccessRoleDTO.isStaff()) {
            roles.add(AccessGroupRole.STAFF);
        }
        return roles;
    }

    private List<OpenShiftResponseDTO> getOpenShiftResponceDTOS(List<OpenShift> openShifts){
        List<OpenShiftResponseDTO> openShiftResponseDTOS = new ArrayList<>();
        openShifts.forEach(openShift -> {
            OpenShiftResponseDTO openShiftResponseDTO = new OpenShiftResponseDTO();
            BeanUtils.copyProperties(openShift, openShiftResponseDTO, openShift.getStartDate().toString(), openShift.getEndDate().toString());
            openShiftResponseDTO.setFromTime(DateUtils.asLocalTime(openShift.getStartDate()));
            openShiftResponseDTO.setToTime(DateUtils.asLocalTime(openShift.getEndDate()));
            openShiftResponseDTO.setStartDate(DateUtils.asLocalDate(openShift.getStartDate()));
            openShiftResponseDTO.setEndDate(DateUtils.asLocalDate(openShift.getEndDate()));
            openShiftResponseDTOS.add(openShiftResponseDTO);
        });
        return openShiftResponseDTOS;
    }


    private ShiftFunctionWrapper getShiftByStaffId(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long employmentId, StaffFilterDTO staffFilterDTO) {
        if (staffId == null) {
            exceptionService.actionNotPermittedException(STAFF_ID_NULL);
        }
        Map<LocalDate, List<FunctionDTO>> functionDTOMap = new HashMap<>();
        List<ReasonCodeDTO> reasonCodeDTOS = reasonCodeRepository.findByUnitIdAndReasonCodeTypeAndDeletedFalse(unitId, TIME_TYPE);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = null;
        if (Optional.ofNullable(employmentId).isPresent()) {
            staffAdditionalInfoDTO = userIntegrationService.verifyEmploymentAndFindFunctionsAfterDate(staffId, employmentId);
            if (!Optional.ofNullable(staffAdditionalInfoDTO).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_BELONGS, staffId);
            }
            if (!Optional.ofNullable(staffAdditionalInfoDTO.getEmployment()).isPresent()) {
                exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_ABSENT, startDate.toString());
            }
            List<FunctionDTO> appliedFunctionDTOs = null;
            if (Optional.ofNullable(staffAdditionalInfoDTO.getEmployment()).isPresent()) {
                appliedFunctionDTOs = staffAdditionalInfoDTO.getEmployment().getAppliedFunctions();
            }
            shiftFunctionService.addFunction(functionDTOMap, staffAdditionalInfoDTO, appliedFunctionDTOs);
        } else {
            functionDTOMap = userIntegrationService.getFunctionsOfEmployment(unitId, startDate, endDate);
        }
        Map<BigInteger, ReasonCodeDTO> reasonCodeMap = reasonCodeDTOS.stream().collect(Collectors.toMap(ReasonCodeDTO::getId, v -> v));
        //When employmentID is not present then we are retreiving shifts for all staffs(NOT only for Employment).
        if (endDate == null) {
            endDate = DateUtils.getLocalDate();
        }
        Object[] collections = getShiftDTOSAfterFilterAndUpdateShiftData(unitId, staffId, startDate, endDate, employmentId, staffFilterDTO, reasonCodeMap);
        List<ShiftDTO> shifts = (List<ShiftDTO>)collections[0];
        shifts = shiftDetailsService.setLayerInShifts(shifts,(Set<BigInteger>)collections[1]);
        Map<LocalDate, List<ShiftDTO>> shiftsMap = shifts.stream().collect(Collectors.groupingBy(k -> DateUtils.asLocalDate(k.getStartDate()), Collectors.toList()));
        return new ShiftFunctionWrapper(shiftsMap, functionDTOMap);
    }

    private Object[] getShiftDTOSAfterFilterAndUpdateShiftData(Long unitId, Long staffId, LocalDate startDate, LocalDate endDate, Long employmentId, StaffFilterDTO staffFilterDTO, Map<BigInteger, ReasonCodeDTO> reasonCodeMap) {
        List<ShiftDTO> shifts;
        if (Optional.ofNullable(employmentId).isPresent()) {
            shifts = shiftMongoRepository.findAllShiftsBetweenDuration(employmentId, staffId, asDate(startDate), asDate(endDate), unitId,staffFilterDTO);
        } else {
            shifts = shiftMongoRepository.findAllShiftsBetweenDurationOfUnitAndStaffId(staffId, asDate(startDate), asDate(endDate), unitId,staffFilterDTO);
        }
        updateReasonCodeAndNameInActivitiesAndUpdateSicknessDetails(reasonCodeMap, shifts);
        if(!staffFilterDTO.getFiltersData().stream().anyMatch(filterSelectionDTO -> INCLUDE_DRAFT_SHIFT.equals(filterSelectionDTO.getName()))){
            shifts = updateDraftShiftToShift(shifts);
        }
        shifts = wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shifts);
        Set<BigInteger> sickActivityIds = new HashSet<>();
        for(ShiftDTO shift :shifts){
            if(isNotNull(shift.getShiftViolatedRules())) {
                shift.setEscalationReasons(shift.getShiftViolatedRules().getEscalationReasons());
                shift.setEscalationResolved(shift.getShiftViolatedRules().isEscalationResolved());
            }
            if(shift.getShiftType().equals(SICK)) {
                sickActivityIds.addAll(shift.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet()));
            }
        }

        return new Object[]{shifts,sickActivityIds};
    }

    private void updateReasonCodeAndNameInActivitiesAndUpdateSicknessDetails(Map<BigInteger, ReasonCodeDTO> reasonCodeMap, List<ShiftDTO> shifts) {
        Map<String, Set<FieldLevelPermission>> fieldPermissionMap=activityService.getActivityPermissionMap(UserContext.getUserDetails().getLastSelectedOrganizationId(),UserContext.getUserDetails().getId());
        Set<BigInteger> activityIds = shifts.stream().flatMap(shiftDTO -> shiftDTO.getActivities().stream()).map(ShiftActivityDTO::getActivityId).collect(Collectors.toSet());
        List<Activity> activities = activityMongoRepository.findActivitiesSickSettingByActivityIds(activityIds);
        Map<BigInteger, Activity> activityWrapperMap = activities.stream().collect(Collectors.toMap(Activity::getId, v -> v));
        for (ShiftDTO shift : shifts) {
            shifts = updateActivityAndSicknessDetails(reasonCodeMap, shifts, fieldPermissionMap, activityWrapperMap, shift);
        }
    }

    private List<ShiftDTO> updateActivityAndSicknessDetails(Map<BigInteger, ReasonCodeDTO> reasonCodeMap, List<ShiftDTO> shifts, Map<String, Set<FieldLevelPermission>> fieldPermissionMap, Map<BigInteger, Activity> activityWrapperMap, ShiftDTO shift) {
        shift.setMultipleActivity(shift.getActivities().size()>1);
        for (ShiftActivityDTO activity : shift.getActivities()) {
            if(fieldPermissionMap.get(NAME).contains(FieldLevelPermission.HIDE) || fieldPermissionMap.get(NAME).isEmpty()){
                activity.setActivityName("XXXXX");
                activity.getChildActivities().forEach(k->k.setActivityName("XXXXX"));
            }
            activity.setReasonCode(reasonCodeMap.get(activity.getAbsenceReasonCodeId()));
        }
        ShiftDTO sickShift = shifts.stream().filter(k -> k.getShiftType().equals(SICK)).findAny().orElse(null);
        if (sickShift != null) {
            Activity activity = shiftDetailsService.getWorkingSickActivity(sickShift, activityWrapperMap);
            if (!activity.getActivityRulesSettings().getSicknessSetting().isShowAslayerOnTopOfPublishedShift()) {
                Set<BigInteger> shiftIds = shifts.stream().filter(k -> k.getActivities().stream().anyMatch(act -> act.getStatus().contains(ShiftStatus.PUBLISH) && k.isDisabled())).map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
                shifts = shifts.stream().filter(shiftDTO -> !shiftIds.contains(shift.getId())).collect(Collectors.toList());
            }
            if (!activity.getActivityRulesSettings().getSicknessSetting().isShowAslayerOnTopOfUnPublishedShift()) {
                Set<BigInteger> shiftIds = shifts.stream().filter(k -> k.getActivities().stream().anyMatch(act -> !act.getStatus().contains(ShiftStatus.PUBLISH) && k.isDisabled())).map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
                shifts = shifts.stream().filter(shiftDTO -> !shiftIds.contains(shift.getId())).collect(Collectors.toList());
            }
        }
        return shifts;
    }

    public List<ShiftDTO> updateDraftShiftToShift(List<ShiftDTO> shifts) {
        List<ShiftDTO> shiftDTOS = new ArrayList<>();
        if (UserContext.getUserDetails().isManagement()) {
            for (ShiftDTO shift : shifts) {
                if (isNotNull(shift.getDraftShift())) {
                    ShiftDTO shiftDTO = shift.getDraftShift();
                    shiftDTO.setDraft(true);
                    if (!shift.isDraft()) {
                        shiftDTO.setHasOriginalShift(true);
                    }
                    shiftDTO.setId(shift.getId());
                    shiftDTOS.add(shiftDTO);
                } else {
                    shiftDTOS.add(shift);
                }
            }
        } else {
            shiftDTOS = shifts.stream().filter(shiftDTO -> !shiftDTO.isDraft()).collect(Collectors.toList());
        }
        return shiftDTOS;
    }
}
