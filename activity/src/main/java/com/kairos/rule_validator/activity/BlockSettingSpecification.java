package com.kairos.rule_validator.activity;

import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.persistence.model.shift.BlockSetting;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.shift.ShiftValidatorService;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_STAFF_EMPLOYMENTTYPE_ABSENT;

/**
 * Created By G.P.Ranjan on 3/12/19
 **/
public class BlockSettingSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private BlockSetting blockSetting;

    public BlockSettingSpecification(BlockSetting blockSetting) {
        this.blockSetting = blockSetting;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        return true;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        if(blockSetting.getBlockDetails().containsKey(shift.getStaffId()) && CollectionUtils.containsAny(blockSetting.getBlockDetails().get(shift.getStaffId()), shift.getActivityIds())){
            ShiftValidatorService.throwException(MESSAGE_STAFF_EMPLOYMENTTYPE_ABSENT);
        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        return Collections.emptyList();
    }
}