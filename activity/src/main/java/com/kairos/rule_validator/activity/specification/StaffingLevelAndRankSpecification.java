package com.kairos.rule_validator.activity.specification;
/*
 *Created By Pavan on 9/8/19
 *
 */

import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.shift.ShiftValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class StaffingLevelAndRankSpecification extends AbstractSpecification<ShiftWithActivityDTO> {
    private static final Logger logger=LoggerFactory.getLogger(StaffingLevelAndRankSpecification.class);
    private List<ShiftActivityDTO> shiftActivities;
    private Long unitId;
    private Collection<ActivityWrapper> activities;
    private Phase phase;
    private UserAccessRoleDTO userAccessRoleDTO;
    private PhaseSettings phaseSettings;
    private ShiftValidatorService shiftValidatorService;

    public StaffingLevelAndRankSpecification(List<ShiftActivityDTO> shiftActivities,Long unitId, Collection<ActivityWrapper> activities, Phase phase, UserAccessRoleDTO userAccessRoleDTO) {
        this.shiftActivities=shiftActivities;
        this.unitId=unitId;
        this.activities=activities;
        this.phase=phase;
        this.userAccessRoleDTO=userAccessRoleDTO;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shiftWithActivityDTO) {
      shiftValidatorService.verifyRankAndStaffingLevel(shiftActivities,unitId,activities,phase,userAccessRoleDTO);
    }


    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        return null;
    }
}
