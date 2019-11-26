package com.kairos.service.counter;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.counter.enums.DisplayUnit;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesOfCollectionByMapper;
import static com.kairos.enums.FilterType.CALCULATION_UNIT;
import static com.kairos.enums.FilterType.TIME_TYPE;

@Service
public class UnavailabilityCalculationKPIService {

    @Inject
    private TimeTypeService timeTypeService;
    @Inject private KPIBuilderCalculationService kpiBuilderCalculationService;


    public double getUnavailabilityCalculationData(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS, kpiCalculationRelatedInfo.getFilterBasedCriteria(),false).invoke();
        return getTotalOfUnavailabilityShift(kpiCalculationRelatedInfo, filterShiftActivity,shiftWithActivityDTOS);
    }

    private double getTotalOfUnavailabilityShift(KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity,List<ShiftWithActivityDTO> shiftWithActivityDTOS) {
        List<ShiftActivityDTO> shifts = new CopyOnWriteArrayList<>(shiftWithActivityDTOS.stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream()).collect(Collectors.toList()));
        List<ShiftActivityDTO> shiftActivityDTOS = new ArrayList<>();
        double total = 0;
        for (ShiftActivityDTO shiftActivityDTO : filterShiftActivity.getShiftActivityDTOS()) {
            for (ShiftActivityDTO shift : shifts) {
                DateTimeInterval dateTimeIntervalOfUnavailabilityShift = new DateTimeInterval(shiftActivityDTO.getStartDate(), shiftActivityDTO.getEndDate());
                DateTimeInterval dateTimeIntervalOfShift = new DateTimeInterval(shift.getStartDate(), shift.getEndDate());
                if (!shiftActivityDTO.getActivityId().equals(shift.getActivityId()) && dateTimeIntervalOfUnavailabilityShift.overlaps(dateTimeIntervalOfShift)) {
                    total += dateTimeIntervalOfUnavailabilityShift.overlap(dateTimeIntervalOfShift).getHours();
                    shiftActivityDTOS.add(shift);
                    shifts.remove(shift);
                }
            }
        }
        return getTotalByCalculationUnitOfUnavailibilityShift(kpiCalculationRelatedInfo, filterShiftActivity, shiftActivityDTOS, total);
    }

    private double getTotalByCalculationUnitOfUnavailibilityShift(KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity, List<ShiftActivityDTO> shiftActivityDTOS, double total) {
        DisplayUnit calculationUnit = ((List<DisplayUnit>)copyPropertiesOfCollectionByMapper(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_UNIT), DisplayUnit.class)).get(0);
        switch (calculationUnit) {
            case COUNT:
                total = shiftActivityDTOS.size();
                break;
            case PERCENTAGE_OF_TIMES:
                total = filterShiftActivity.getShiftActivityDTOS().size() > 0 ? shiftActivityDTOS.size() * 100 / filterShiftActivity.getShiftActivityDTOS().size() : filterShiftActivity.getShiftActivityDTOS().size();
                break;
            case PERCENTAGE_OF_HOURS:
                Set<BigInteger> shiftIds = filterShiftActivity.getShiftActivityDTOS().stream().map(shiftActivityDTO -> shiftActivityDTO.getActivityId()).collect(Collectors.toSet());
                long sumOfShifts = filterShiftActivity.getShifts().stream().flatMap(shiftWithActivityDTO -> shiftWithActivityDTO.getActivities().stream().filter(shiftActivityDTO -> shiftIds.contains(shiftActivityDTO.getActivityId()))).mapToLong(shiftActivityDTO -> new DateTimeInterval(shiftActivityDTO.getStartDate(), shiftActivityDTO.getEndDate()).getHours()).sum();
                total = sumOfShifts > 0 ? (total * 100 / sumOfShifts) : sumOfShifts;
                total = KPIUtils.getValueWithDecimalFormat(total);
                break;
            default:
                break;
        }
        return total;
    }

}
