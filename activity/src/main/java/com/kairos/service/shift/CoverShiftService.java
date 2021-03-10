package com.kairos.service.shift;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.CoverShiftSettingDTO;
import com.kairos.dto.activity.shift.NotEligibleStaffDataDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithViolatedInfoDTO;
import com.kairos.dto.user.staff.staff.Staff;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.shift.CoverShiftCriteria;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.CoverShiftSetting;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftDataHelper;
import com.kairos.persistence.repository.shift.CoverShiftSettingMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.enums.shift.CoverShiftCriteria.STAFF_WITH_EMPLOYMENT_TYPES;
import static com.kairos.enums.shift.CoverShiftCriteria.STAFF_WITH_TAGS;

@Service
public class CoverShiftService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoverShiftService.class);

    @Inject private ShiftService shiftService;
    @Inject private ActivityService activityService;
    @Inject private UserIntegrationService userIntegrationService;
    @Inject private ShiftValidatorService shiftValidatorService;
    @Inject private PlanningPeriodService planningPeriodService;
    @Inject private PhaseService phaseService;
    @Inject private ExecutorService executorService;
    @Inject private CoverShiftSettingMongoRepository coverShiftSettingMongoRepository;
    @Inject private ExceptionService exceptionService;

    //@CacheEvict(value = "getCoverShiftSettingByUnit", key = "#unitId")
    public CoverShiftSettingDTO createCoverShiftSettingByUnit(Long unitId,CoverShiftSettingDTO coverShiftSettingDTO) {
        if(isNotNull(coverShiftSettingMongoRepository.getCoverShiftSettingByUnitId(unitId))){
            exceptionService.actionNotPermittedException(ERROR_COVER_SHIFT_SETTING_ALREADY_EXIST_FOR_UNIT);
        }
        if(!unitId.equals(coverShiftSettingDTO.getUnitId())){
            exceptionService.dataNotFoundByIdException(ERROR_COVER_SHIFT_SETTING_UNIT_ID_INVALID);
        }
        CoverShiftSetting coverShiftSetting = ObjectMapperUtils.copyPropertiesByMapper(coverShiftSettingDTO, CoverShiftSetting.class);
        coverShiftSettingMongoRepository.save(coverShiftSetting);
        coverShiftSettingDTO.setId(coverShiftSetting.getId());
        return coverShiftSettingDTO;
    }

    //@CacheEvict(value = "getCoverShiftSettingByUnit", key = "#unitId")
    public CoverShiftSettingDTO updateCoverShiftSettingByUnit(Long unitId,CoverShiftSettingDTO coverShiftSettingDTO) {
        if(isNull(coverShiftSettingDTO.getId())){
            exceptionService.actionNotPermittedException(ERROR_COVER_SHIFT_SETTING_ID_NOT_FOUND);
        }
        CoverShiftSetting coverShiftSetting = coverShiftSettingMongoRepository.findOne(coverShiftSettingDTO.getId());
        if(isNull(coverShiftSetting)){
            exceptionService.dataNotFoundByIdException(ERROR_COVER_SHIFT_SETTING_NOT_FOUND);
        }
        if(!unitId.equals(coverShiftSetting.getUnitId()) || !unitId.equals(coverShiftSettingDTO.getUnitId())){
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

    public List<Staff> getEligibleStaffs(BigInteger shiftId,CoverShiftSetting coverShiftSetting){
        Shift shift = shiftService.findOneByShiftId(shiftId);
        coverShiftSetting = getCoverShiftSettingByUnit(shift.getUnitId());
        Set<BigInteger> activityIds = getActivityIdsByShift(shift);
        List[] nonProductiveTypeActivityIdsAndAssignedStaffIds = activityService.findAllNonProductiveTypeActivityIdsAndAssignedStaffIds(activityIds);
        List<BigInteger> nonProductiveTypeActivityIds = nonProductiveTypeActivityIdsAndAssignedStaffIds[0];
        List<Long> staffIds = nonProductiveTypeActivityIdsAndAssignedStaffIds[1];
        if(isCollectionNotEmpty(nonProductiveTypeActivityIds)){
            staffIds = new ArrayList<>();
        }
        List<BigInteger> productiveTypeActivityIds = isCollectionNotEmpty(nonProductiveTypeActivityIds) ? (List<BigInteger>) CollectionUtils.removeAll(activityIds,nonProductiveTypeActivityIds) : new ArrayList<>(activityIds);
        Set<Long> notEligibleStaffIdsForCoverShifts = shiftService.getNotEligibleStaffsForCoverShifts(shift.getStartDate(),shift.getEndDate(),coverShiftSetting,staffIds);
        Set<Long> employmentTypeIds = coverShiftSetting.getCoverShiftCriteria().contains(STAFF_WITH_EMPLOYMENT_TYPES) ? coverShiftSetting.getEmploymentTypeIds() : new HashSet<>();
        Set<Long> tagIds = coverShiftSetting.getCoverShiftCriteria().contains(STAFF_WITH_TAGS) ? coverShiftSetting.getTagIds() : new HashSet<>();
        notEligibleStaffIdsForCoverShifts.add(shift.getStaffId());
        NotEligibleStaffDataDTO notEligibleStaffDataDTO = new NotEligibleStaffDataDTO(employmentTypeIds,tagIds, notEligibleStaffIdsForCoverShifts,asLocalDate(shift.getStartDate()),new HashSet<>(productiveTypeActivityIds),coverShiftSetting.getCoverShiftCriteria().contains(CoverShiftCriteria.STAFF_WITH_WTA_RULE_VIOLATION));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getEligibleStaffsForCoverShifts(notEligibleStaffDataDTO,coverShiftSetting.getUnitId());
        removeStaffWhichHaveWTAViolation(coverShiftSetting,shift,staffAdditionalInfoDTOS,activityIds, UserContext.getUserDetails().getCountryId(),UserContext.getUserDetails().isManagement());
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(staffAdditionalInfoDTOS,Staff.class);
    }

    private void removeStaffWhichHaveWTAViolation(CoverShiftSetting coverShiftSetting, Shift shift, List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, Set<BigInteger> activityIds,Long countryId,boolean userAccessRole) {
        if(coverShiftSetting.getCoverShiftCriteria().contains(CoverShiftCriteria.STAFF_WITH_WTA_RULE_VIOLATION)){
            ShiftDataHelper shiftDataHelper = getShiftDataHelperForCoverShift(coverShiftSetting, shift, staffAdditionalInfoDTOS, activityIds, countryId, userAccessRole);
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getActivities().get(0).getStartDate(), shiftDataHelper);
            ShiftWithActivityDTO shiftWithActivityDTO = shiftService.getShiftWithActivityDTO(null,shiftDataHelper.getActivityMap(),shift);
            List<Future<ShiftWithViolatedInfoDTO>> shiftWithViolatedInfoDTOS = new ArrayList<>();
            Iterator<StaffAdditionalInfoDTO> staffAdditionalInfoDTOIterator = staffAdditionalInfoDTOS.iterator();
            Set<LocalDate> localDates = shiftDataHelper.getPlanningPeriods().stream().flatMap(planningPeriod -> planningPeriod.getLocalDates().stream()).collect(Collectors.toSet());
            Map<LocalDate, Phase> phaseMapByDate = phaseService.getPhasesByDates(localDates,shiftDataHelper);
            shiftDataHelper.setPhaseMap(phaseMapByDate);
            while (staffAdditionalInfoDTOIterator.hasNext()){
                StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffAdditionalInfoDTOIterator.next();
                if(shiftDataHelper.getWtaByDate(asLocalDate(shift.getStartDate()),staffAdditionalInfoDTO.getEmployment().getId())==null || shiftDataHelper.getCtaByDate(asLocalDate(shift.getStartDate()),staffAdditionalInfoDTO.getEmployment().getId())==null){
                    staffAdditionalInfoDTOIterator.remove();
                    continue;
                }
                Callable<ShiftWithViolatedInfoDTO> data = () -> {
                    ShiftWithActivityDTO shift1 = ObjectMapperUtils.copyPropertiesByMapper(shiftWithActivityDTO,ShiftWithActivityDTO.class);
                    shift1.setStaffId(staffAdditionalInfoDTO.getId());
                    shift1.setEmploymentId(staffAdditionalInfoDTO.getEmployment().getId());
                    return shiftValidatorService.validateShiftWithActivity(phase, shift1, staffAdditionalInfoDTO, shiftDataHelper);
                };
                Future<ShiftWithViolatedInfoDTO> responseData = executorService.submit(data);
                shiftWithViolatedInfoDTOS.add(responseData);
            }
            List<ShiftWithViolatedInfoDTO> withViolatedInfoDTOS = new ArrayList<>();
            for (Future<ShiftWithViolatedInfoDTO> data : shiftWithViolatedInfoDTOS) {
                try {
                    if(isNotNull(data)){
                        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = data.get();
                        if(isCollectionNotEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements())){
                            staffAdditionalInfoDTOS.removeIf(staffAdditionalInfoDTO -> shiftWithViolatedInfoDTO.getShifts().get(0).getStaffId().equals(staffAdditionalInfoDTO.getId()));
                        }
                        withViolatedInfoDTOS.add(shiftWithViolatedInfoDTO);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    LOGGER.error("error while generate KPI  data",ex);
                }
            }

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
        ShiftDataHelper shiftDataHelper = planningPeriodService.getDataForShiftOperation(shift.getStartDate(), shift.getUnitId(), employmentIds,expertiseIds,staffIds,countryId, activityIds,null, userAccessRole);
        shiftDataHelper.setTimeZone(timeZone);
        return shiftDataHelper;
    }

    private Set<BigInteger> getActivityIdsByShift(Shift shift) {
        Set<BigInteger> activityIds = new HashSet<>();
        activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(ShiftActivity::getActivityId).collect(Collectors.toList()));
        activityIds.addAll(shift.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()));
        return activityIds;
    }

}
