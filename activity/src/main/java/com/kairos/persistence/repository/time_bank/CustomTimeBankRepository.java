package com.kairos.persistence.repository.time_bank;

import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;

import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 27/7/18
 */

public interface CustomTimeBankRepository {

    DailyTimeBankEntry findLastTimeBankByEmploymentId(Long employmentId);

    List<DailyTimeBankEntry> findAllDailyTimeBankByEmploymentIdAndBetweenDates(Long employmentId, Date startDate, Date endDate);

     long getTimeBankOffMinutes(Long employmentId);
}
