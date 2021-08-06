package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.pay_out.PayOutCTADistributionDTO;
import com.kairos.dto.activity.pay_out.PayOutPerShiftCTADistributionDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivityWithDuration;
import com.kairos.dto.activity.time_bank.TimeBankCTADistributionDTO;
import com.kairos.dto.activity.time_bank.TimeBankDistributionDTO;
import com.kairos.dto.user.staff.staff.Staff;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.shift.CoverShiftCriteria;
import com.kairos.enums.shift.ShiftActionType;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.pay_out.PayOutPerShift;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.*;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.pay_out.PayOutRepository;
import com.kairos.persistence.repository.shift.CoverShiftMongoRepository;
import com.kairos.persistence.repository.shift.CoverShiftSettingMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.time_slot.TimeSlotMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.day_type.DayTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.staffing_level.StaffingLevelValidatorService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.service.wta.WorkTimeAgreementService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.enums.shift.CoverShiftCriteria.STAFF_WITH_EMPLOYMENT_TYPES;
import static com.kairos.enums.shift.CoverShiftCriteria.STAFF_WITH_TAGS;
import static com.kairos.persistence.model.shift.CoverShift.ApprovalBy.PLANNER;

@Service
public class CoverShiftService {

    public static final String COVER_SHIFT = "Cover Shift";
    private static final Logger LOGGER = LoggerFactory.getLogger(CoverShiftService.class);

    @Inject
    private ShiftService shiftService;
    @Inject
    private ActivityService activityService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject
    private PlanningPeriodService planningPeriodService;
    @Inject
    private PhaseService phaseService;
    @Inject
    private ExecutorService executorService;
    @Inject
    private CoverShiftSettingMongoRepository coverShiftSettingMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CoverShiftMongoRepository coverShiftMongoRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private TimeBankService timeBankCalculationService;
    @Inject
    private OrganizationActivityService organizationActivityService;
    @Inject
    private PayOutService payOutCalculationService;
    @Inject
    private PayOutRepository payOutRepository;
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private DayTypeService dayTypeService;
    @Inject
    private TimeSlotMongoRepository timeSlotMongoRepository;
    @Inject
    private StaffingLevelValidatorService staffingLevelValidatorService;
    @Inject private WorkTimeAgreementService workTimeAgreementService;

    //@CacheEvict(value = "getCoverShiftSettingByUnit", key = "#unitId")
    public CoverShiftSettingDTO createCoverShiftSettingByUnit(Long unitId, CoverShiftSettingDTO coverShiftSettingDTO) {
        if (isNotNull(coverShiftSettingMongoRepository.getCoverShiftSettingByUnitId(unitId))) {
            exceptionService.actionNotPermittedException(ERROR_COVER_SHIFT_SETTING_ALREADY_EXIST_FOR_UNIT);
        }
        if (!unitId.equals(coverShiftSettingDTO.getUnitId())) {
            exceptionService.dataNotFoundByIdException(ERROR_COVER_SHIFT_SETTING_UNIT_ID_INVALID);
        }
        CoverShiftSetting coverShiftSetting = ObjectMapperUtils.copyPropertiesByMapper(coverShiftSettingDTO, CoverShiftSetting.class);
        coverShiftSettingMongoRepository.save(coverShiftSetting);
        coverShiftSettingDTO.setId(coverShiftSetting.getId());
        return coverShiftSettingDTO;
    }

    //@CacheEvict(value = "getCoverShiftSettingByUnit", key = "#unitId")
    public CoverShiftSettingDTO updateCoverShiftSettingByUnit(Long unitId, CoverShiftSettingDTO coverShiftSettingDTO) {
        if (isNull(coverShiftSettingDTO.getId())) {
            exceptionService.actionNotPermittedException(ERROR_COVER_SHIFT_SETTING_ID_NOT_FOUND);
        }
        CoverShiftSetting coverShiftSetting = coverShiftSettingMongoRepository.findOne(coverShiftSettingDTO.getId());
        if (isNull(coverShiftSetting)) {
            exceptionService.dataNotFoundByIdException(ERROR_COVER_SHIFT_SETTING_NOT_FOUND);
        }
        if (!unitId.equals(coverShiftSetting.getUnitId()) || !unitId.equals(coverShiftSettingDTO.getUnitId())) {
            exceptionService.dataNotFoundByIdException(ERROR_COVER_SHIFT_SETTING_UNIT_ID_INVALID);
        }
        coverShiftSetting = ObjectMapperUtils.copyPropertiesByMapper(coverShiftSettingDTO, CoverShiftSetting.class);
        coverShiftSettingMongoRepository.save(coverShiftSetting);
        return coverShiftSettingDTO;
    }

    //@Cacheable(value = "getCoverShiftSettingByUnit", key = "#unitId", cacheManager = "cacheManager")
    public CoverShiftSetting getCoverShiftSettingByUnit(Long unitId) {
        return coverShiftSettingMongoRepository.getCoverShiftSettingByUnitId(unitId);
    }

    public List<Staff> getEligibleStaffs(BigInteger shiftId) {
        Shift shift = shiftService.findOneByShiftId(shiftId);
        CoverShiftSetting coverShiftSetting = getCoverShiftSettingByUnit(shift.getUnitId());
        Set<BigInteger> activityIds = getActivityIdsByShift(Arrays.asList(shift));
        List[] nonProductiveTypeActivityIdsAndAssignedStaffIds = activityService.findAllNonProductiveTypeActivityIdsAndAssignedStaffIds(activityIds);
        List<BigInteger> nonProductiveTypeActivityIds = nonProductiveTypeActivityIdsAndAssignedStaffIds[0];
        List<Long> staffIds = nonProductiveTypeActivityIdsAndAssignedStaffIds[1];
        if (isCollectionNotEmpty(nonProductiveTypeActivityIds)) {
            staffIds = new ArrayList<>();
        }
        List<BigInteger> productiveTypeActivityIds = isCollectionNotEmpty(nonProductiveTypeActivityIds) ? (List<BigInteger>) CollectionUtils.removeAll(activityIds, nonProductiveTypeActivityIds) : new ArrayList<>(activityIds);
        Set<Long> notEligibleStaffIdsForCoverShifts = shiftService.getNotEligibleStaffsForCoverShifts(shift.getStartDate(), shift.getEndDate(), coverShiftSetting, staffIds);
        Set<Long> employmentTypeIds = coverShiftSetting.getCoverShiftCriteria().contains(STAFF_WITH_EMPLOYMENT_TYPES) ? coverShiftSetting.getEmploymentTypeIds() : new HashSet<>();
        Set<Long> tagIds = coverShiftSetting.getCoverShiftCriteria().contains(STAFF_WITH_TAGS) ? coverShiftSetting.getTagIds() : new HashSet<>();
        notEligibleStaffIdsForCoverShifts.add(shift.getStaffId());
        NotEligibleStaffDataDTO notEligibleStaffDataDTO = new NotEligibleStaffDataDTO(employmentTypeIds, tagIds, notEligibleStaffIdsForCoverShifts, asLocalDate(shift.getStartDate()), new HashSet<>(productiveTypeActivityIds), coverShiftSetting.getCoverShiftCriteria().contains(CoverShiftCriteria.STAFF_WITH_WTA_RULE_VIOLATION));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getEligibleStaffsForCoverShifts(notEligibleStaffDataDTO, coverShiftSetting.getUnitId());
        removeStaffWhichHaveWTAViolation(coverShiftSetting, shift, staffAdditionalInfoDTOS, activityIds, UserContext.getUserDetails().getCountryId(), UserContext.getUserDetails().isManagement());
        removeStaffWhichCreatesProblem(coverShiftSetting, shift, staffAdditionalInfoDTOS, activityIds);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(staffAdditionalInfoDTOS, Staff.class);
    }

    private void removeStaffWhichHaveWTAViolation(CoverShiftSetting coverShiftSetting, Shift shift, List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, Set<BigInteger> activityIds, Long countryId, boolean userAccessRole) {
        if (coverShiftSetting.getCoverShiftCriteria().contains(CoverShiftCriteria.STAFF_WITH_WTA_RULE_VIOLATION)) {
            ShiftDataHelper shiftDataHelper = getShiftDataHelperForCoverShift(coverShiftSetting, shift, staffAdditionalInfoDTOS, activityIds, countryId, userAccessRole);
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getActivities().get(0).getStartDate(), shiftDataHelper);
            ShiftWithActivityDTO shiftWithActivityDTO = shiftService.getShiftWithActivityDTO(null, shiftDataHelper.getActivityMap(), shift);
            List<Future<ShiftWithViolatedInfoDTO>> shiftWithViolatedInfoDTOS = new ArrayList<>();
            validateShifts(shift, staffAdditionalInfoDTOS, shiftDataHelper, phase, shiftWithActivityDTO, shiftWithViolatedInfoDTOS);
            List<ShiftWithViolatedInfoDTO> withViolatedInfoDTOS = new ArrayList<>();
            for (Future<ShiftWithViolatedInfoDTO> data : shiftWithViolatedInfoDTOS) {
                try {
                    if (isNotNull(data)) {
                        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = data.get();
                        if (isCollectionNotEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements()) || isCollectionNotEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getActivities())) {
                            staffAdditionalInfoDTOS.removeIf(staffAdditionalInfoDTO -> shiftWithViolatedInfoDTO.getShifts().get(0).getStaffId().equals(staffAdditionalInfoDTO.getId()));
                        }
                        withViolatedInfoDTOS.add(shiftWithViolatedInfoDTO);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    LOGGER.error("error while generate KPI  data", ex);
                }
            }

        }
    }

    private void removeStaffWhichCreatesProblem(CoverShiftSetting coverShiftSetting, Shift shift, List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, Set<BigInteger> activityIds) {
        if (coverShiftSetting.getCoverShiftCriteria().contains(CoverShiftCriteria.STAFF_WITH_OVERLAPPING_SHIFTS)) {
            List<Shift> shifts = shiftMongoRepository.findAllShiftsByStaffIdsAndDate(staffAdditionalInfoDTOS.stream().map(StaffAdditionalInfoDTO::getId).collect(Collectors.toList()), shift.getStartDate(), shift.getEndDate());
            Map<BigInteger, ActivityWrapper> activityWrapperMap = organizationActivityService.getActivityWrapperMap(shifts, ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class));
            Map<Long, List<Shift>> staffWiseShiftsMap = shifts.stream().collect(Collectors.groupingBy(Shift::getStaffId));
            ShiftDataHelper shiftDataHelper = getShiftDataHelperForCoverShift(coverShiftSetting, shift, staffAdditionalInfoDTOS, activityIds, UserContext.getUserDetails().getCountryId(), UserContext.getUserDetails().isManagement());
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getActivities().get(0).getStartDate(), shiftDataHelper);
            for (int i = 0; i < staffAdditionalInfoDTOS.size(); i++) {
                Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMapForUnderStaffing = new HashMap<>();
                for (ShiftActivity shiftActivity : staffWiseShiftsMap.get(staffAdditionalInfoDTOS.get(i).getId()).stream().flatMap(k -> k.getActivities().stream()).collect(Collectors.toList())) {
                    staffingLevelValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, false, shiftActivity, staffingLevelActivityWithDurationMapForUnderStaffing, true);
                }
                Map<BigInteger, StaffingLevelActivityWithDuration> staffingLevelActivityWithDurationMapForOverStaffing = new HashMap<>();
                for (ShiftActivity shiftActivity : shift.getActivities()) {
                    staffingLevelValidatorService.validateStaffingLevel(phase, shift, activityWrapperMap, true, shiftActivity, staffingLevelActivityWithDurationMapForOverStaffing, true);
                }
                boolean result = staffingLevelValidatorService.verifyStaffingLevel(staffingLevelActivityWithDurationMapForUnderStaffing, staffingLevelActivityWithDurationMapForOverStaffing, null, null, activityWrapperMap, false, null, true);
                if (!result) {
                    staffAdditionalInfoDTOS.remove(staffAdditionalInfoDTOS.get(i));
                    continue;
                }
                for (Shift shift1 : staffWiseShiftsMap.get(staffAdditionalInfoDTOS.get(i).getId())) {
                    ViolatedRulesDTO violatedRulesDTO = shiftValidatorService.validateRule(shift1, staffAdditionalInfoDTOS.get(i), false);
                    if (!violatedRulesDTO.getWorkTimeAgreements().isEmpty()) {
                        staffAdditionalInfoDTOS.remove(staffAdditionalInfoDTOS.get(i));
                        break;
                    }
                }
            }
        }
    }

    private void validateShifts(Shift shift, List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, ShiftDataHelper shiftDataHelper, Phase phase, ShiftWithActivityDTO shiftWithActivityDTO, List<Future<ShiftWithViolatedInfoDTO>> shiftWithViolatedInfoDTOS) {
        Iterator<StaffAdditionalInfoDTO> staffAdditionalInfoDTOIterator = staffAdditionalInfoDTOS.iterator();
        Set<LocalDate> localDates = shiftDataHelper.getPlanningPeriods().stream().flatMap(planningPeriod -> planningPeriod.getLocalDates().stream()).collect(Collectors.toSet());
        Map<LocalDate, Phase> phaseMapByDate = phaseService.getPhasesByDates(localDates, shiftDataHelper);
        shiftDataHelper.setPhaseMap(phaseMapByDate);
        while (staffAdditionalInfoDTOIterator.hasNext()) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffAdditionalInfoDTOIterator.next();
            if (shiftDataHelper.getWtaByDate(asLocalDate(shift.getStartDate()), staffAdditionalInfoDTO.getEmployment().getId()) == null || shiftDataHelper.getCtaByDate(asLocalDate(shift.getStartDate()), staffAdditionalInfoDTO.getEmployment().getId()) == null) {
                staffAdditionalInfoDTOIterator.remove();
                continue;
            }
            Callable<ShiftWithViolatedInfoDTO> data = () -> {
                ShiftWithActivityDTO shift1 = ObjectMapperUtils.copyPropertiesByMapper(shiftWithActivityDTO, ShiftWithActivityDTO.class);
                shift1.setStaffId(staffAdditionalInfoDTO.getId());
                shift1.setEmploymentId(staffAdditionalInfoDTO.getEmployment().getId());
                return shiftValidatorService.validateShiftWithActivity(phase, shift1, staffAdditionalInfoDTO, shiftDataHelper);
            };
            Future<ShiftWithViolatedInfoDTO> responseData = executorService.submit(data);
            shiftWithViolatedInfoDTOS.add(responseData);
        }
    }

    private ShiftDataHelper getShiftDataHelperForCoverShift(CoverShiftSetting coverShiftSetting, Shift shift, List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, Set<BigInteger> activityIds, Long countryId, boolean userAccessRole) {
        String timeZone = userIntegrationService.getTimeZoneByUnitId(coverShiftSetting.getUnitId());
        Set<Long> employmentIds = new HashSet<>();
        Set<Long> expertiseIds = new HashSet<>();
        Set<Long> staffIds = new HashSet<>();
        for (StaffAdditionalInfoDTO staffAdditionalInfoDTO : staffAdditionalInfoDTOS) {
            employmentIds.add(staffAdditionalInfoDTO.getEmployment().getId());
            expertiseIds.add(staffAdditionalInfoDTO.getEmployment().getExpertise().getId());
            staffIds.add(staffAdditionalInfoDTO.getId());
        }
        ShiftDataHelper shiftDataHelper = planningPeriodService.getDataForShiftOperation(shift.getStartDate(), shift.getUnitId(), employmentIds, expertiseIds, staffIds, countryId, activityIds, null, userAccessRole);
        shiftDataHelper.setTimeZone(timeZone);
        Set<LocalDate> localDates = shiftDataHelper.getPlanningPeriods().stream().flatMap(planningPeriod -> planningPeriod.getLocalDates().stream()).collect(Collectors.toSet());
        Map<LocalDate, Phase> phaseMapByDate = phaseService.getPhasesByDates(localDates, shiftDataHelper);
        shiftDataHelper.setPhaseMap(phaseMapByDate);
        return shiftDataHelper;
    }

    private Set<BigInteger> getActivityIdsByShift(List<Shift> shifts) {
        Set<BigInteger> activityIds = new HashSet<>();
        for (Shift shift : shifts) {
            activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(ShiftActivity::getActivityId).collect(Collectors.toList()));
            activityIds.addAll(shift.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()));
        }
        return activityIds;
    }

    public CoverShift getCoverShiftDetails(BigInteger shiftId, Long staffId) {
        return coverShiftMongoRepository.findByShiftIdAndStaffIdAndDeletedFalse(shiftId, staffId);
    }

    public void updateCoverShiftDetails(CoverShiftDTO coverShiftDTO) {
        CoverShift coverShift = ObjectMapperUtils.copyPropertiesByMapper(coverShiftDTO, CoverShift.class);
        coverShiftMongoRepository.save(coverShift);
        if (coverShiftDTO.getId() == null) {
            Shift shift = shiftMongoRepository.findOne(coverShift.getShiftId());
            shift.setCoverShiftDate(getDate());
            shiftMongoRepository.save(shift);
        }
    }

    public void cancelCoverShiftDetails(BigInteger id) {
        CoverShift coverShift = coverShiftMongoRepository.findOne(id);
        coverShift.setDeleted(true);
        coverShiftMongoRepository.save(coverShift);
        Shift shift = shiftMongoRepository.findOne(coverShift.getShiftId());
        shift.setCoverShiftDate(null);
        shiftMongoRepository.save(shift);
    }

    public void showInterestInCoverShift(BigInteger id, Long staffId, Long employmentId, StaffInterest staffInterest) {
        CoverShift coverShift = coverShiftMongoRepository.findByIdAndDeletedFalse(id);
        if (isNull(coverShift)) {
            exceptionService.actionNotPermittedException(MESSAGE_DATA_NOTFOUND, COVER_SHIFT);
        }
        if (coverShift.getApprovalBy().equals(CoverShift.ApprovalBy.AUTO_PICK)) {
            assignCoverShift(staffId, employmentId, coverShift);

        }
        coverShift.getInterestedStaffs().put(staffId, new StaffInterest(DateUtils.getDate(),staffInterest.getAccountType()));
        coverShift.getRequestedStaffs().remove(staffId);
        coverShift.getDeclinedStaffIds().remove(staffId);
        coverShiftMongoRepository.save(coverShift);
    }

    public List<ShiftWithViolatedInfoDTO> assignCoverShiftToStaff(BigInteger id, Long staffId, Long employmentId) {
        ShiftDTO shift = shiftMongoRepository.findByIdAndDeletedFalse(id);
        CoverShift coverShift = coverShiftMongoRepository.findByShiftIdAndStaffIdAndDeletedFalse(id, shift.getStaffId());

        if (isNull(coverShift)) {
            exceptionService.actionNotPermittedException(MESSAGE_DATA_NOTFOUND, COVER_SHIFT);
        }
        validateApprovalSettings(coverShift);
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = assignCoverShift(staffId, employmentId, coverShift);
        if (shiftWithViolatedInfoDTOS.get(0).getViolatedRules().getActivities().isEmpty() && shiftWithViolatedInfoDTOS.get(0).getViolatedRules().getWorkTimeAgreements().isEmpty()) {
            coverShift.setAssignedStaffId(staffId);
            coverShiftMongoRepository.save(coverShift);
        }
        return shiftWithViolatedInfoDTOS;

    }

    public List<ShiftWithViolatedInfoDTO> assignCoverShift(Long staffId, Long employmentId, CoverShift coverShift) {
        ShiftDTO shift = shiftMongoRepository.findByIdAndDeletedFalse(coverShift.getShiftId());
        ShiftDTO shiftDTO = new ShiftDTO(shift.getActivities(), shift.getUnitId(), staffId, employmentId);
        shiftDTO.setId(shift.getId());
        shiftDTO.setStartDate(shift.getStartDate());
        shiftDTO.setEndDate(shift.getEndDate());
        shiftDTO.setCreatedByCoverShift(true);
        return shiftService.updateShift(shiftDTO, false, false, ShiftActionType.SAVE);
    }

    private void validateApprovalSettings(CoverShift coverShift) {
        if (PLANNER.equals(coverShift.getApprovalBy()) && !UserContext.getUserDetails().isManagement()) {
            exceptionService.actionNotPermittedException(EXCEPTION_INVALID_USER);
        }
    }

    public CoverShiftStaffDetails getCoverShiftStaffDetails(LocalDate startDate, LocalDate endDate, Long unitId, Long staffId, Long employmentId) {
        List<CoverShiftDTO> coverShifts = coverShiftMongoRepository.findAllByDateGreaterThanEqualsAndLessThanEqualsAndDeletedFalse(startDate, endDate).stream().sorted(Comparator.comparing(CoverShiftDTO::getDate)).collect(Collectors.toList());
        List<Shift> shifts = (List<Shift>) shiftMongoRepository.findAllById(coverShifts.stream().map(CoverShiftDTO::getShiftId).collect(Collectors.toList()));
        List<CoverShiftDTO> totalRequests = coverShifts.stream().filter(k -> k.getRequestedStaffs().containsKey(staffId)).collect(Collectors.toList());
        List<CoverShiftDTO> totalInterests = coverShifts.stream().filter(k -> k.getInterestedStaffs().containsKey(staffId)).collect(Collectors.toList());
        List<CoverShiftDTO> totalDeclined = coverShifts.stream().filter(k -> k.getDeclinedStaffIds().containsKey(staffId)).collect(Collectors.toList());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = shiftService.getStaffAdditionalInfoDTO(unitId,startDate, staffId, employmentId);
        shiftService.updateCTADetailsOfEmployement(startDate, staffAdditionalInfoDTO);
        updateTimeBankInCoverShifts(coverShifts, staffAdditionalInfoDTO, shifts);
        List<CoverShiftDTO> totalEligibleShifts = getEligibleShifts(shifts,unitId,staffId,employmentId,coverShifts);
        return new CoverShiftStaffDetails(totalRequests, totalInterests, totalEligibleShifts, totalDeclined);
    }

    public List<CoverShiftDTO> getEligibleShifts(List<Shift> shifts, Long unitId, Long staffId, Long employmentId,List<CoverShiftDTO> coverShiftDTOS) {
        Map<BigInteger,CoverShiftDTO> coverShiftMap=coverShiftDTOS.stream().collect(Collectors.toMap(CoverShiftDTO::getShiftId,Function.identity()));
        shifts = shifts.stream().filter(k->coverShiftMap.containsKey(k.getId())).collect(Collectors.toList());
        CoverShiftSetting coverShiftSetting = getCoverShiftSettingByUnit(unitId);
        List<CoverShiftDTO> shiftList = new ArrayList<>();
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(null, staffId, employmentId);
        staffAdditionalInfoDTO.setDayTypes(dayTypeService.getDayTypeWithCountryHolidayCalender(UserContext.getUserDetails().getCountryId()));
        staffAdditionalInfoDTO.setTimeSlotSets(timeSlotMongoRepository.findByUnitIdAndTimeSlotTypeOrderByStartDate(unitId, TimeSlotType.SHIFT_PLANNING).getTimeSlots());
        if (isCollectionEmpty(shifts) || coverShiftSetting.getCoverShiftCriteria().contains(STAFF_WITH_EMPLOYMENT_TYPES) && coverShiftSetting.getEmploymentTypeIds().contains(employmentId)) {
            return shiftList;
        }
        Set<BigInteger> activityIds = getActivityIdsByShift(shifts);
        Map<Date, Phase> datePhaseMap = phaseService.getPhasesByDates(unitId, shifts.stream().map(k -> asLocalDateTime(k.getStartDate())).collect(Collectors.toSet()));
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(activityIds).stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        for (Shift shift : shifts) {
            ShiftWithActivityDTO shiftWithActivityDTO = shiftService.getShiftWithActivityDTO(null, activityWrapperMap, shift);
            ShiftDataHelper shiftDataHelper = getShiftDataHelperForCoverShift(coverShiftSetting, shift, Arrays.asList(staffAdditionalInfoDTO), activityIds, UserContext.getUserDetails().getCountryId(), false);
            ShiftWithActivityDTO shift1 = ObjectMapperUtils.copyPropertiesByMapper(shiftWithActivityDTO, ShiftWithActivityDTO.class);
            shift1.setStaffId(staffAdditionalInfoDTO.getId());
            shift1.setEmploymentId(staffAdditionalInfoDTO.getEmployment().getId());
            ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(datePhaseMap.get(shift.getStartDate()), shift1, staffAdditionalInfoDTO, shiftDataHelper);
            if (isCollectionEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements()) && isCollectionEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getActivities())) {
                shiftList.add(coverShiftMap.get(shift1.getId()));
            }
        }
        return shiftList;
    }

    public void notInterestInCoverShift(BigInteger id, Long staffId, LocalDate selectedDate, boolean doNotAddDeclined) {
        List<CoverShift> coverShifts=selectedDate!=null?coverShiftMongoRepository.findAllByDateAndDeletedFalse(selectedDate):coverShiftMongoRepository.findAllByIdInAndDeletedFalse(Arrays.asList(id));
        if (isCollectionEmpty(coverShifts)) {
            exceptionService.actionNotPermittedException(MESSAGE_DATA_NOTFOUND, COVER_SHIFT);
        }
        for(CoverShift coverShift:coverShifts){
            coverShift.getRequestedStaffs().remove(staffId);
            if(!doNotAddDeclined) {
                coverShift.getDeclinedStaffIds().put(staffId, DateUtils.getDate());
            }else if(doNotAddDeclined){
                coverShift.getRequestedStaffs().put(staffId,asDate(coverShift.getDate()));
            }
            coverShift.getInterestedStaffs().remove(staffId);
        }
        coverShiftMongoRepository.saveEntities(coverShifts);
    }

    private void updateTimeBankInCoverShifts(List<CoverShiftDTO> coverShiftDTOS, StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<Shift> shifts) {
        Map<BigInteger, ActivityWrapper> activityWrapperMap = isCollectionEmpty(shifts) ? new HashMap<>() : organizationActivityService.getActivityWrapperMap(shifts, null);
        Map<BigInteger, Shift> shiftMap = shifts.stream().collect(Collectors.toMap(MongoBaseEntity::getId,Function.identity()));
        coverShiftDTOS = coverShiftDTOS.stream().filter(coverShiftDTO -> shiftMap.containsKey(coverShiftDTO.getShiftId())).collect(Collectors.toList());
        for (CoverShiftDTO coverShiftDTO : coverShiftDTOS) {
            Shift shift = shiftMap.get(coverShiftDTO.getShiftId());
            if(isNull(shift)){
                exceptionService.dataNotFoundByIdException(MESSAGE_SHIFT_ID, coverShiftDTO.getShiftId());
            }
            shift.setStaffId(staffAdditionalInfoDTO.getId());
            shift.setEmploymentId(staffAdditionalInfoDTO.getEmployment().getId());
            DailyTimeBankEntry dailyTimeBankEntry = timeBankCalculationService.calculateTimeBankForCoverShift(staffAdditionalInfoDTO,shift);
            coverShiftDTO.setDeltaTimeBankMinutes(isNotNull(dailyTimeBankEntry) ? dailyTimeBankEntry.getDeltaTimeBankMinutes() : 0);
            coverShiftDTO.setTimeBankCTADistributionList(isNotNull(dailyTimeBankEntry)?ObjectMapperUtils.copyCollectionPropertiesByMapper(dailyTimeBankEntry.getTimeBankCTADistributionList(), TimeBankDistributionDTO.class):new ArrayList<>());
            PayOutPerShift payOutPerShift = payOutCalculationService.updatePayOutForCoverShift(staffAdditionalInfoDTO, shift, activityWrapperMap);
            coverShiftDTO.setTotalPayOutMinutes(payOutPerShift.getTotalPayOutMinutes());
            coverShiftDTO.setPayOutPerShiftCTADistributions(ObjectMapperUtils.copyCollectionPropertiesByMapper(payOutPerShift.getPayOutPerShiftCTADistributions(), PayOutPerShiftCTADistributionDTO.class));

        }
    }

    public Map<String,ViolatedRulesDTO> getWTADetails(BigInteger shiftId,Long employmentId) {
        Shift shift = shiftService.findOneByShiftId(shiftId);
        shift.setEmploymentId(employmentId);
        List<Shift> exitsingShifts = shiftMongoRepository.findShiftBetweenDurationByEmploymentId(employmentId,shift.getStartDate(),shift.getEndDate());
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = shiftService.getStaffAdditionalInfoDTO(shift.getUnitId(), DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()), shift.getStaffId(), shift.getEmploymentId());
        if (staffAdditionalInfoDTO.getUserAccessRoleDTO().isStaff() && !staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaffId().equals(shift.getStaffId())) {
            exceptionService.actionNotPermittedException(MESSAGE_SHIFT_PERMISSION);
        }
        Map<String,ViolatedRulesDTO> violatedRulesDTOMap = new HashMap<>();
        Set<BigInteger> shiftIds = new HashSet<>();
        if(isCollectionNotEmpty(exitsingShifts)){
            List<WorkTimeAgreementRuleViolation> workTimeAgreements = new ArrayList<>();
            for (Shift exitsingShift : exitsingShifts) {
                shiftIds.add(exitsingShift.getId());
                ViolatedRulesDTO violatedRulesDTO = shiftValidatorService.validateRule(exitsingShift, staffAdditionalInfoDTO,false);
                workTimeAgreements.addAll(violatedRulesDTO.getWorkTimeAgreements());
            }
            violatedRulesDTOMap.put("violatedRulesForExistingShift",new ViolatedRulesDTO(workTimeAgreements));
        }
        Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(), shift.getActivities().get(0).getStartDate(), shift.getActivities().get(0).getEndDate());
        WTAQueryResultDTO wtaQueryResultDTO = workTimeAgreementService.getWTAByEmploymentIdAndDate(staffAdditionalInfoDTO.getEmployment().getId(), DateUtils.onlyDate(shift.getActivities().get(0).getStartDate()));
        Map<BigInteger, ActivityWrapper> activityWrapperMap = organizationActivityService.getActivityWrapperMap(newArrayList(shift), null);
        ShiftWithActivityDTO shiftWithActivityDTO = shiftService.getShiftWithActivityDTO(null, activityWrapperMap, shift);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftValidatorService.validateShiftWithActivity(phase, wtaQueryResultDTO, shiftWithActivityDTO, staffAdditionalInfoDTO, null, activityWrapperMap, false, false, false,shiftIds);
        violatedRulesDTOMap.put("violatedRulesForPlannedShift",shiftWithViolatedInfoDTO.getViolatedRules());
        return violatedRulesDTOMap;
    }

}
