package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import org.springframework.stereotype.Service;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@Service
public class StaffChildCountKPIService implements KPIService {
    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getChildrenCount(staffId, kpiCalculationRelatedInfo);
    }

    private long getChildrenCount(Long staffId, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        StaffKpiFilterDTO staff = kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().get(staffId);
        return isNotNull(staff) && isCollectionNotEmpty(staff.getStaffChildDetails()) ? staff.getStaffChildDetails().size() : 0;
    }
}
