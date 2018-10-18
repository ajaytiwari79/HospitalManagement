package com.kairos.persistence.repository.time_bank;

import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */

@Repository
public interface TimeBankRepository extends MongoBaseRepository<DailyTimeBankEntry,BigInteger> ,CustomTimeBankRepository{



    @Query("{unitPositionId:?0,date:{$gte:?1 , $lt:?2}}")
    List<DailyTimeBankEntry> findAllByUnitPositionAndDate(Long unitPositionId, Date startDate, Date endDate);

    @Query(value = "{unitPositionId:?0,date:{$gte:?1 , $lt:?2}}")
    List<DailyTimeBankEntry> findAllDailyTimeBankByUnitPositionIdAndBetweenDates(Long unitPositionId, Date startDate, Date endDate);

    @Query(value = "{unitPositionId:{$in:?0},date:{$gte:?1 , $lt:?2}}")
    List<DailyTimeBankEntry> findAllDailyTimeBankByIdsAndBetweenDates(List<Long> unitPositionIds, Date startDate, Date endDate);

    @Query("{unitPositionId:?0,date:{$lt:?1}}")
    List<DailyTimeBankEntry> findAllByUnitPositionAndBeforeDate(Long unitPositionId, Date timeBankDate);

    @Query("{unitPositionId:{$in:?0},date:{ $lte:?1}}")
    List<DailyTimeBankEntry> findAllByUnitPositionsAndBeforDate(List<Long> unitPositionIds, Date endDate);

    @Query(value = "{unitPositionId:{$in:?0},date:{$gte:?1 , $lt:?2}}",delete = true)
    void deleteDailyTimeBank(List<Long> unitPositionIds, Date startDate, Date endDate);

}
