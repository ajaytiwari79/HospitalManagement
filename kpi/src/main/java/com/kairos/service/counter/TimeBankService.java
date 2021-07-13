package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.persistence.model.DailyTimeBankEntry;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class TimeBankService implements KPIService{
    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return 0;
    }
    public Long calculateActualTimebank(DateTimeInterval planningPeriodInterval, Map<LocalDate, DailyTimeBankEntry> dateDailyTimeBankEntryMap, EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO, LocalDate endLocalDate, LocalDate startDate, Object o) {
        return null;
    }
}
