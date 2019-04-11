package com.kairos.persistence.repository.time_bank;

import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;

import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 27/7/18
 */

public interface CustomTimeBankRepository {

    DailyTimeBankEntry findLastTimeBankByEmploymentId(Long unitPositionId, Date date);

    List<DailyTimeBankEntry> findAllDailyTimeBankByEmploymentIdAndBetweenDates(Long unitPositionId, Date startDate, Date endDate);

    List<DailyTimeBankEntry> findAllDailyTimeBankByEmploymentsAndBetweenDates(List<Long> unitPositionIds, Date startDate, Date endDate);
}
