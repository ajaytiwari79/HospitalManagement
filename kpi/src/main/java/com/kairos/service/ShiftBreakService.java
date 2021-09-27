package com.kairos.service;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.PERCENTAGE;
import static com.kairos.utils.KPIUtils.getValueWithDecimalFormat;

@Service
public class ShiftBreakService implements KPIService{

    @Inject private KPIBuilderCalculationService kpiBuilderCalculationService;

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getNumberOfBreakInterrupt(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
    }

    public double getNumberOfBreakInterrupt(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval,true);
        KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
        long interruptShift = filterShiftActivity.getShifts().stream().filter(k -> k.getBreakActivities().stream().anyMatch(ShiftActivityDTO::isBreakInterrupt)).count();
        if (PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            return isCollectionNotEmpty(filterShiftActivity.getShifts()) ? getValueWithDecimalFormat((interruptShift * 100.0d) / filterShiftActivity.getShifts().size()) : 0;
        } else
            return interruptShift;
    }
}
