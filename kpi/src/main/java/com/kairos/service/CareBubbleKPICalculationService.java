package com.kairos.service;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.shift.AuditShiftDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CareBubbleKPICalculationService implements KPIService{

    public int calculateCareBubble(KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval, Long staffId) {
        int calculateValue = 0;
        List<AuditShiftDTO> shifts = kpiCalculationRelatedInfo.getShiftAuditByStaffIdAndInterval(staffId, dateTimeInterval);
        Optional<TagDTO> optionalTagDTO = kpiCalculationRelatedInfo.getStaffKPIFilterDTO(staffId).stream().flatMap(staffKpiFilterDTO -> staffKpiFilterDTO.getTags().stream()).filter(tagDTO -> kpiCalculationRelatedInfo.getTagIds().contains(tagDTO.getId())).findFirst();
        if (optionalTagDTO.isPresent()) {
            DateTimeInterval interval = optionalTagDTO.get().getOverlapInterval(dateTimeInterval);
            for (AuditShiftDTO shift : shifts) {
                if (shift.isChanged() && interval.contains(shift.getActivities().get(0).getStartDate())) {
                    calculateValue += XAxisConfig.HOURS.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0)) ? shift.getChangedHours() : 1;
                }
            }
        }
        return XAxisConfig.HOURS.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0)) ? DateUtils.getHourByMinutes(calculateValue) : calculateValue;
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return calculateCareBubble(kpiCalculationRelatedInfo, dateTimeInterval, staffId);
    }
}
