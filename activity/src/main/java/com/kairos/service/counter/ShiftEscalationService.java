package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.service.shift.ShiftValidatorService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.PERCENTAGE;
import static com.kairos.enums.kpi.CalculationType.ESCALATED_SHIFTS;
import static com.kairos.utils.counter.KPIUtils.getValueWithDecimalFormat;

@Service
public class ShiftEscalationService implements KPIService{

    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;
    @Inject private ShiftValidatorService shiftValidatorService;

    public double getEscalatedShiftsOrResolvedShifts(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
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
        List<ShiftViolatedRules> shiftViolatedRules = shiftValidatorService.findAllViolatedRulesByShiftIds(shifts.stream().map(ShiftDTO::getId).collect(Collectors.toList()), false);
        Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = shiftViolatedRules.stream().collect(Collectors.toMap(ShiftViolatedRules::getShiftId, v -> v));
        for (ShiftWithActivityDTO shift : shifts) {
            if (shiftViolatedRulesMap.containsKey(shift.getId())) {
                shift.setEscalationReasons(shiftViolatedRulesMap.get(shift.getId()).getEscalationReasons());
                shift.setEscalationResolved(shiftViolatedRulesMap.get(shift.getId()).isEscalationResolved());
            } else {
                shift.setEscalationReasons(newHashSet());
                shift.setEscalationResolved(false);
            }
        }
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getEscalatedShiftsOrResolvedShifts(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
    }
}
