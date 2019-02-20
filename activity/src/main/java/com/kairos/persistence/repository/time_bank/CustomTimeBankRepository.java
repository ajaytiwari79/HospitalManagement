package com.kairos.persistence.repository.time_bank;

import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;

import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 27/7/18
 */

public interface CustomTimeBankRepository {

    DailyTimeBankEntry findLastTimeBankByUnitPositionId(Long unitPositionId, Date date);

    List<DailyTimeBankEntry> findLastTimeBankByUnitPositionIds(List<Long> unitPositionId, Date date);

    void updateAccumulatedTimeBank(Long unitPositionId,int timeBank);

    List<DailyTimeBankEntry> findAllDailyTimeBankByUnitPositionIdAndBetweenDates(Long unitPositionId, Date startDate, Date endDate);
    List<DailyTimeBankEntry> findAllDailyTimeBankByUnitPositionIdsAndBetweenDates(List<Long> unitPositionIds, Date startDate, Date endDate);
}
