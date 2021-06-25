package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.service.shift.ShiftValidatorService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.PERCENTAGE;
import static com.kairos.enums.kpi.CalculationType.ESCALATED_SHIFTS;
import static com.kairos.utils.counter.KPIUtils.getValueWithDecimalFormat;

@Service
public class ShiftEscalationService implements KPIService{

    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;
    @Inject private ShiftValidatorService shiftValidatorService;

    public double getEscalatedShiftsOrResolvedShifts(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval, true);
        KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
        setEscalationDetailInShift(filterShiftActivity.getShifts());
        long escalatedShiftCount;
        if (ESCALATED_SHIFTS.equals(kpiCalculationRelatedInfo.getCalculationType())) {
            escalatedShiftCount = filterShiftActivity.getShifts().stream().filter(k -> isCollectionNotEmpty(k.getEscalationReasons()) && !k.isEscalationResolved()).count();
        } else {
            escalatedShiftCount = filterShiftActivity.getShifts().stream().filter(ShiftDTO::isEscalationResolved).count();
        }
        if (PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            return isCollectionNotEmpty(filterShiftActivity.getShifts()) ? getValueWithDecimalFormat((escalatedShiftCount * 100.0d) / filterShiftActivity.getShifts().size()) : 0;
        } else {
            return escalatedShiftCount;
        }
    }

    private void setEscalationDetailInShift(List<ShiftWithActivityDTO> shifts) {
        for (ShiftWithActivityDTO shift : shifts) {
            if (isNotNull(shift.getShiftViolatedRules())) {
                shift.setEscalationReasons(shift.getShiftViolatedRules().getEscalationReasons());
                shift.setEscalationResolved(shift.getShiftViolatedRules().isEscalationResolved());
            } else {
                shift.setEscalationReasons(newHashSet());
                shift.setEscalationResolved(false);
            }
        }
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getEscalatedShiftsOrResolvedShifts(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
    }
}
