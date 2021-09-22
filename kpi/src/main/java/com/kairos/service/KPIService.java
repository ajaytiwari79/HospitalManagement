package com.kairos.service;

import com.kairos.commons.utils.DateTimeInterval;

public interface KPIService {
    <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t);
}
