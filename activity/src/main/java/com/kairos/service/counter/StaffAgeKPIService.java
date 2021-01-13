package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import org.springframework.stereotype.Service;

import static com.kairos.commons.utils.DateUtils.asLocalDate;

@Service
public class StaffAgeKPIService implements KPIService{

    private long getStaffAgeData(Long staffId, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        StaffKpiFilterDTO staff = kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().get(staffId);
        int age = 0;
        if (XAxisConfig.AGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))) {
            age = staff.getStaffAge(asLocalDate(kpiCalculationRelatedInfo.getStartDate()));
        }
        return age;
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getStaffAgeData(staffId, kpiCalculationRelatedInfo);
    }
}
