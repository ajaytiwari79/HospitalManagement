package com.kairos.service.counter;

import com.kairos.dto.activity.counter.data.RawRepresentationData;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.KPI;

import java.util.List;
import java.util.Map;

public class PlannedHoursCalculationService implements CounterService {
    @Override
    public RawRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long countryId, KPI kpi) {
        return null;
    }

    @Override
    public RawRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long countryId, KPI kpi) {
        return null;
    }
}
