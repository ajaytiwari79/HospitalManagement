package com.kairos.service.shift;

import com.kairos.dto.activity.shift.NotEligibleStaffDataDTO;
import com.kairos.dto.user.staff.staff.Staff;
import com.kairos.persistence.model.shift.CoverShiftSetting;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.ActivityService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.persistence.model.shift.CoverShiftSetting.CoverShiftCriteria.STAFF_WITH_EMPLOYEMENT_TYPES;
import static com.kairos.persistence.model.shift.CoverShiftSetting.CoverShiftCriteria.STAFF_WITH_TAGS;

@Service
public class CoverShiftService {

    @Inject
    private ShiftService shiftService;
    @Inject private ActivityService activityService;
    @Inject private UserIntegrationService userIntegrationService;

    public CoverShiftSetting getCoverShiftSettingsForUnit(Long unitId){
        Set<CoverShiftSetting.CoverShiftCriteria> coverShiftCriteria = newHashSet(CoverShiftSetting.CoverShiftCriteria.values());
        CoverShiftSetting coverShiftSetting = new CoverShiftSetting();
        coverShiftSetting.setUnitId(unitId);
        coverShiftSetting.setCoverShiftCriteria(coverShiftCriteria);
        return coverShiftSetting;
    }

    public List<Staff> getEligibleStaffs(BigInteger shiftId){
        Shift shift = shiftService.findOneByShiftId(shiftId);
        CoverShiftSetting coverShiftSetting = getCoverShiftSettingsForUnit(shift.getUnitId());
        Set<BigInteger> activityIds = getActivityIdsByShift(shift);
        List[] nonProductiveTypeActivityIdsAndAssignedStaffIds = activityService.findAllNonProductiveTypeActivityIdsAndAssignedStaffIds(activityIds);
        List<BigInteger> nonProductiveTypeActivityIds = nonProductiveTypeActivityIdsAndAssignedStaffIds[0];
        List<Long> staffIds = nonProductiveTypeActivityIdsAndAssignedStaffIds[1];
        if(isCollectionNotEmpty(nonProductiveTypeActivityIds)){
            staffIds = new ArrayList<>();
        }
        Set<Long> notEligibleStaffIdsForCoverShifts = shiftService.getNotEligibleStaffsForCoverShifts(shift.getStartDate(),shift.getEndDate(),coverShiftSetting,staffIds);
        Set<Long> employmentTypeIds = coverShiftSetting.getCoverShiftCriteria().contains(STAFF_WITH_EMPLOYEMENT_TYPES) ? coverShiftSetting.getEmploymentTypeIds() : new HashSet<>();
        Set<Long> tagIds = coverShiftSetting.getCoverShiftCriteria().contains(STAFF_WITH_TAGS) ? coverShiftSetting.getTagIds() : new HashSet<>();
        NotEligibleStaffDataDTO notEligibleStaffDataDTO = new NotEligibleStaffDataDTO(employmentTypeIds,tagIds,notEligibleStaffIdsForCoverShifts,asLocalDate(shift.getStartDate()));
        userIntegrationService.getEligibleStaffsForCoverShifts(notEligibleStaffDataDTO,coverShiftSetting.getUnitId());
        return null;
    }

    private Set<BigInteger> getActivityIdsByShift(Shift shift) {
        Set<BigInteger> activityIds = new HashSet<>();
        activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(ShiftActivity::getActivityId).collect(Collectors.toList()));
        activityIds.addAll(shift.getActivities().stream().map(ShiftActivity::getActivityId).collect(Collectors.toList()));
        return activityIds;
    }

}
