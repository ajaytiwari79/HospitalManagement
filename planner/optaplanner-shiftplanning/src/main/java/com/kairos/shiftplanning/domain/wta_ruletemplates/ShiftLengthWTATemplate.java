package com.kairos.shiftplanning.domain.wta_ruletemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.ShiftLengthAndAverageSetting;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.*;



/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE5
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ShiftLengthWTATemplate extends WTABaseRuleTemplate {

    private long timeLimit;
    private List<Long> dayTypeIds = new ArrayList<>();
    private List<BigInteger> timeTypeIds = new ArrayList<>();
    private List<PartOfDay> partOfDays = Arrays.asList(PartOfDay.NIGHT);
    private float recommendedValue;
    private MinMaxSetting minMaxSetting = MinMaxSetting.MAXIMUM;
    private ShiftLengthAndAverageSetting shiftLengthAndAverageSetting = ShiftLengthAndAverageSetting.DIFFERENCE_BETWEEN_START_END_TIME;

    public ShiftLengthWTATemplate(String name, String description, long timeLimit) {
        super(name, description);
        this.timeLimit = timeLimit;
        this.wtaTemplateType = WTATemplateType.SHIFT_LENGTH;
    }


    public ShiftLengthWTATemplate() {
        this.wtaTemplateType = WTATemplateType.SHIFT_LENGTH;
    }

    public int checkConstraints(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps) {
        int penality = 0;
        if (!isDisabled() && isValidForPhase(unit.getPhase().getId(), this.phaseTemplateValues)) {
            TimeInterval timeInterval = getTimeSlotByPartOfDay(partOfDays, unit.getTimeSlotMap(), shiftImp);
            if (isNull(timeInterval)) {
                boolean isValidShift = isCollectionEmpty(timeTypeIds) || CollectionUtils.containsAny(timeTypeIds, shiftImp.getActivitiesTimeTypeIds());
                if (isValidShift && isValidForDay(dayTypeIds, unit,shiftImp.getStart())) {
                    int limit = getValueByPhaseAndCounter(unit, phaseTemplateValues);
                    penality = isValid(minMaxSetting, limit, getValueAccordingShiftLengthAndAverageSetting(shiftLengthAndAverageSetting, shiftImp));
                }
            }
        }
        return penality;
    }

}