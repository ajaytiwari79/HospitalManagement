package com.kairos.persistence.repository.pay_out;

import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.service.pay_out.PayOutTransaction;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author pradeep
 * @date - 18/7/18
 */
@Repository
public interface PayOutTransactionMongoRepository extends MongoBaseRepository<PayOutTransaction,BigInteger>{
    @Query("{unitPositionId:{$in:?0},date:{$gte:?1 , $lte:?2},deleted:false}")
    List<PayOutTransaction> findAllByUnitPositionIdsAndDate(List<Long> unitPositionIds, Date startDate, Date endDate);

    @Query("{unitPositionId:?0,date:{$gte:?1 , $lte:?2},deleted:false}")
    List<PayOutTransaction> findAllByUnitPositionIdAndDate(Long unitPositionId, Date startDate, Date endDate);

    @Query("{unitPositionId:?0,date:{$gte:?1},deleted:false}")
    PayOutTransaction findOneByUnitPositionAndDate(Long unitPositionId, Date payOutDate);

    @Query("{unitPositionId:?0,date:{$lt:?1},deleted:false}")
    List<PayOutTransaction> findAllByUnitPositionAndBeforeDate(Long unitPositionId, Date payOutDate);
}
