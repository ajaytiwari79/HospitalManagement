package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import org.springframework.stereotype.Service;

@Service
public class WorkTimeAgreementBalancesCalculationService implements KPIService{
    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return 0;
    }
}
