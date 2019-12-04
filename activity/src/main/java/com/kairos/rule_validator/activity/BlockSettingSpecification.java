package com.kairos.rule_validator.activity;

import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import com.kairos.dto.activity.shift.BlockSettingDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.persistence.model.shift.BlockSetting;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.shift.ShiftValidatorService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.constants.ActivityMessagesConstants.*;

/**
 * Created By G.P.Ranjan on 3/12/19
 **/
@NoArgsConstructor
@Getter
@Setter
public class BlockSettingSpecification extends AbstractSpecification<ShiftWithActivityDTO> {

    private BlockSettingDTO blockSetting;

    public BlockSettingSpecification(BlockSettingDTO blockSetting) {
        this.blockSetting = blockSetting;
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shift) {
        return true;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shift) {
        Set<BigInteger> activityIds = shift.getActivities().stream().map(activity->activity.getActivityId()).collect(Collectors.toSet());
        if(ObjectUtils.isNotNull(blockSetting) && blockSetting.getBlockDetails().containsKey(shift.getStaffId()) && CollectionUtils.containsAny(blockSetting.getBlockDetails().get(shift.getStaffId()), activityIds)){
            ShiftValidatorService.throwException(MESSAGE_BLOCKED_FOR_SHIFT_ENTER_AT_DATE,asDate(blockSetting.getDate()));
        }
    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        return Collections.emptyList();
    }
}