package com.kairos.service.shift;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.NotEligibleStaffDataDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithViolatedInfoDTO;
import com.kairos.dto.user.staff.staff.Staff;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.CoverShiftSetting;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftDataHelper;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.persistence.model.shift.CoverShiftSetting.CoverShiftCriteria.STAFF_WITH_EMPLOYEMENT_TYPES;
import static com.kairos.persistence.model.shift.CoverShiftSetting.CoverShiftCriteria.STAFF_WITH_TAGS;

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


    public CoverShiftSetting getCoverShiftSettingsForUnit(Long unitId,CoverShiftSetting coverShiftSetting){
        coverShiftSetting.setUnitId(unitId);
        //coverShiftSetting.setCoverShiftCriteria(coverShiftCriteria);
        return coverShiftSetting;
    }

    public List<Staff> getEligibleStaffs(BigInteger shiftId,CoverShiftSetting coverShiftSetting){
        Shift shift = shiftService.findOneByShiftId(shiftId);
        coverShiftSetting = getCoverShiftSettingsForUnit(shift.getUnitId(),coverShiftSetting);
        Set<BigInteger> activityIds = getActivityIdsByShift(shift);
        List[] nonProductiveTypeActivityIdsAndAssignedStaffIds = activityService.findAllNonProductiveTypeActivityIdsAndAssignedStaffIds(activityIds);
        List<BigInteger> nonProductiveTypeActivityIds = nonProductiveTypeActivityIdsAndAssignedStaffIds[0];
        List<Long> staffIds = nonProductiveTypeActivityIdsAndAssignedStaffIds[1];
        if(isCollectionNotEmpty(nonProductiveTypeActivityIds)){
            staffIds = new ArrayList<>();
        }
        List<BigInteger> productiveTypeActivityIds = (List<BigInteger>) CollectionUtils.removeAll(activityIds,nonProductiveTypeActivityIds);
        Set<Long> notEligibleStaffIdsForCoverShifts = shiftService.getNotEligibleStaffsForCoverShifts(shift.getStartDate(),shift.getEndDate(),coverShiftSetting,staffIds);
        Set<Long> employmentTypeIds = coverShiftSetting.getCoverShiftCriteria().contains(STAFF_WITH_EMPLOYEMENT_TYPES) ? coverShiftSetting.getEmploymentTypeIds() : new HashSet<>();
        Set<Long> tagIds = coverShiftSetting.getCoverShiftCriteria().contains(STAFF_WITH_TAGS) ? coverShiftSetting.getTagIds() : new HashSet<>();
        notEligibleStaffIdsForCoverShifts.add(shift.getStaffId());
        NotEligibleStaffDataDTO notEligibleStaffDataDTO = new NotEligibleStaffDataDTO(employmentTypeIds,tagIds, notEligibleStaffIdsForCoverShifts,asLocalDate(shift.getStartDate()),new HashSet<>(productiveTypeActivityIds),coverShiftSetting.getCoverShiftCriteria().contains(CoverShiftSetting.CoverShiftCriteria.STAFF_WITH_WTA_RULE_VIOLATION));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getEligibleStaffsForCoverShifts(notEligibleStaffDataDTO,coverShiftSetting.getUnitId());
        removeStaffWhichHaveWTAViolation(coverShiftSetting,shift,staffAdditionalInfoDTOS,activityIds, UserContext.getUserDetails().getCountryId(),UserContext.getUserDetails().isManagement());
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(staffAdditionalInfoDTOS,Staff.class);
    }

    private void removeStaffWhichHaveWTAViolation(CoverShiftSetting coverShiftSetting, Shift shift, List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, Set<BigInteger> activityIds,Long countryId,boolean userAccessRole) {
        if(coverShiftSetting.getCoverShiftCriteria().contains(CoverShiftSetting.CoverShiftCriteria.STAFF_WITH_WTA_RULE_VIOLATION)){
            ShiftDataHelper shiftDataHelper = getShiftDataHelperForCoverShift(coverShiftSetting, shift, staffAdditionalInfoDTOS, activityIds, countryId, userAccessRole);
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getActivities().get(0).getStartDate(), null,shiftDataHelper);
            ShiftWithActivityDTO shiftWithActivityDTO = shiftService.getShiftWithActivityDTO(null,shiftDataHelper.getActivityMap(),shift);
            List<Future<ShiftWithViolatedInfoDTO>> shiftWithViolatedInfoDTOS = new ArrayList<>();
            Iterator<StaffAdditionalInfoDTO> staffAdditionalInfoDTOIterator = staffAdditionalInfoDTOS.iterator();
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
