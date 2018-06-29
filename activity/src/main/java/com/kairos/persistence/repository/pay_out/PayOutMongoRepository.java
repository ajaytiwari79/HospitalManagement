package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.model.pay_out.DailyPayOutEntry;
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
public interface PayOutMongoRepository extends MongoBaseRepository<DailyPayOutEntry,BigInteger> {

    @Query("{unitPositionId:{$in:?0},date:{$gte:?1 , $lte:?2}}")
    List<DailyPayOutEntry> findAllByUnitPositionsAndDate(List<Long> unitPositionIds, Date startDate, Date endDate);

    @Query("{unitPositionId:?0,date:{$gte:?1 , $lte:?2}}")
    List<DailyPayOutEntry> findAllByUnitPositionAndDate(Long unitPositionId, Date startDate, Date endDate);

    @Query("{unitPositionId:?0,date:{$gte:?1}}")
    DailyPayOutEntry findOneByUnitPositionAndDate(Long unitPositionId, Date payOutDate);

    @Query("{unitPositionId:?0,date:{$lt:?1}}")
    List<DailyPayOutEntry> findAllByUnitPositionAndBeforeDate(Long unitPositionId, Date payOutDate);
}
