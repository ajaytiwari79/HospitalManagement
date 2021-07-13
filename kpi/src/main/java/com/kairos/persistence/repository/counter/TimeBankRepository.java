package com.kairos.persistence.repository.counter;

import com.kairos.persistence.model.DailyTimeBankEntry;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public class TimeBankRepository {
    public List<DailyTimeBankEntry> findAllDailyTimeBankByIdsAndBetweenDates(Collection<Long> employmentIds, Date asDate, Date asDate1) {
        return null;
    }
    public Collection<DailyTimeBankEntry> findAllByEmploymentIdsAndBeforDate(ArrayList<Long> longs, Date endDate) {
        return null;
    }
    public List<DailyTimeBankEntry> findAllDailyTimeBankByStaffIdsAndBetweenDates(List<Long> staffIds, LocalDate startDate, LocalDate endDate) {
        return null;
    }
}
