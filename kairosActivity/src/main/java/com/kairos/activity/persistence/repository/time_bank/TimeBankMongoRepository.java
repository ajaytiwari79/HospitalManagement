package com.kairos.activity.persistence.repository.time_bank;

import com.kairos.activity.persistence.model.time_bank.DailyTimeBank;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
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
public interface TimeBankMongoRepository extends MongoBaseRepository<DailyTimeBank,BigInteger> {

    @Query("{unitPositionId:{$in:?0},date:{$gte:?1 , $lte:?2}}")
    List<DailyTimeBank> findAllByUnitPositionsAndDate(List<Long> unitPositionIds, Date startDate, Date endDate);

    @Query("{unitPositionId:?0,date:{$gte:?1 , $lte:?2}}")
    List<DailyTimeBank> findAllByUnitPositionAndDate(Long unitPositionId, Date startDate, Date endDate);

    @Query("{unitPositionId:?0,date:{$gte:?1}}")
    DailyTimeBank findOneByUnitPositionAndDate(Long unitPositionId, Date timeBankDate);

    @Query("{unitPositionId:?0,date:{$lte:?1}}")
    List<DailyTimeBank> findAllByUnitPositionAndBeforeDate(Long unitPositionId, Date timeBankDate);
}
