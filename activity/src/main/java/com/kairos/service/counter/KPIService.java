package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;

public interface KPIService {
    <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t);
}
