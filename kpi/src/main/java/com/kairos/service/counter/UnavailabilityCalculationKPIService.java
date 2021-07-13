package com.kairos.service.counter;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.repository.counter.TimeTypeMongoRepository;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@Service
public class UnavailabilityCalculationKPIService implements KPIService{

    @Inject private KPIBuilderCalculationService kpiBuilderCalculationService;


    public double getUnavailabilityCalculationData(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval,true);
        KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS,shiftActivityCriteria,false).invoke();
        return getTotalOfUnavailabilityShift(kpiCalculationRelatedInfo, filterShiftActivity,shiftWithActivityDTOS);
    }

    private double getTotalOfUnavailabilityShift(KPICalculationRelatedInfo kpiCalculationRelatedInfo, KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity,List<ShiftWithActivityDTO> shiftWithActivityDTOS) {
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

    private double getTotalByCalculationUnitOfUnavailibilityShift(KPICalculationRelatedInfo kpiCalculationRelatedInfo, KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity, List<ShiftActivityDTO> shiftActivityDTOS, double total) {
        XAxisConfig calculationUnit = (XAxisConfig) ((List) ObjectMapperUtils.copyCollectionPropertiesByMapper(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.CALCULATION_UNIT), XAxisConfig.class)).get(0);
        switch (calculationUnit) {
            case COUNT:
                total = shiftActivityDTOS.size();
                break;
            case PERCENTAGE_OF_TIMES:
                total = !filterShiftActivity.getShiftActivityDTOS().isEmpty() ? (double) shiftActivityDTOS.size() * 100 / filterShiftActivity.getShiftActivityDTOS().size() : filterShiftActivity.getShiftActivityDTOS().size();
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

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getUnavailabilityCalculationData(staffId,dateTimeInterval,kpiCalculationRelatedInfo);
    }
}
