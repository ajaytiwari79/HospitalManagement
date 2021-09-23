package com.kairos.persistence.repository.time_bank;

import com.kairos.dto.activity.time_bank.TimebankFilterDTO;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import org.springframework.data.mongodb.repository.Query;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author pradeep
 * @date - 27/7/18
 */

public interface CustomTimeBankRepository {

    DailyTimeBankEntry findLastTimeBankByEmploymentId(Long employmentId);

    List<DailyTimeBankEntry> findAllDailyTimeBankByEmploymentIdAndBetweenDates(Long employmentId, Date startDate, Date endDate);

    public long getTimeBankOffMinutes(Long employmentId);

    void deleteDailyTimeBank(List<Long> employmentIds, Date startDate, Date endDate);


    List<DailyTimeBankEntry> findAllByEmploymentIdsAndBeforDate(Set<LocalDate> dateSet, Set<DayOfWeek> dayOfWeekSet, List<Long> employmentIds, Date endDate);
}
