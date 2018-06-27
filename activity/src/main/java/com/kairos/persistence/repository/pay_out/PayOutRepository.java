package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.model.pay_out.DailyPOEntry;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public interface PayOutRepository extends MongoBaseRepository<DailyPOEntry, BigInteger> {
    @Query("{unitPositionId:{$in:?0},date:{$gte:?1 , $lte:?2}}")
    List<DailyPOEntry> findAllByUnitPositionsAndDate(List<Long> unitPositionIds, Date startDate, Date endDate);

    @Query("{unitPositionId:?0,date:{$gte:?1 , $lte:?2}}")
    List<DailyPOEntry> findAllByUnitPositionAndDate(Long unitPositionId, Date startDate, Date endDate);

    @Query("{unitPositionId:?0,date:{$gte:?1}}")
    DailyPOEntry findOneByUnitPositionAndDate(Long unitPositionId, Date payOutDate);

    @Query("{unitPositionId:?0,date:{$lt:?1}}")
    List<DailyPOEntry> findAllByUnitPositionAndBeforeDate(Long unitPositionId, Date payOutDate);
}
