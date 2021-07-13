package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.kpi.CalculationType;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class ShiftEscalationService implements KPIService{

    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;

    public double getEscalatedShiftsOrResolvedShifts(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval, true);
        KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS, shiftActivityCriteria, false).invoke();
        setEscalationDetailInShift(filterShiftActivity.getShifts());
        long escalatedShiftCount;
        if (CalculationType.ESCALATED_SHIFTS.equals(kpiCalculationRelatedInfo.getCalculationType())) {
            escalatedShiftCount = filterShiftActivity.getShifts().stream().filter(k -> ObjectUtils.isCollectionNotEmpty(k.getEscalationReasons()) && !k.isEscalationResolved()).count();
        } else {
            escalatedShiftCount = filterShiftActivity.getShifts().stream().filter(ShiftDTO::isEscalationResolved).count();
        }
        if (XAxisConfig.PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            return ObjectUtils.isCollectionNotEmpty(filterShiftActivity.getShifts()) ? KPIUtils.getValueWithDecimalFormat((escalatedShiftCount * 100.0d) / filterShiftActivity.getShifts().size()) : 0;
        } else {
            return escalatedShiftCount;
        }
    }

    private void setEscalationDetailInShift(List<ShiftWithActivityDTO> shifts) {
        for (ShiftWithActivityDTO shift : shifts) {
            if (ObjectUtils.isNotNull(shift.getShiftViolatedRules())) {
                shift.setEscalationReasons(shift.getShiftViolatedRules().getEscalationReasons());
                shift.setEscalationResolved(shift.getShiftViolatedRules().isEscalationResolved());
            } else {
                shift.setEscalationReasons(ObjectUtils.newHashSet());
                shift.setEscalationResolved(false);
            }
        }
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getEscalatedShiftsOrResolvedShifts(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
    }
}
