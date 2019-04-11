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

    @Query("{employmentId:?0,date:{$gte:?1 , $lte:?2},deleted:false}")
    List<PayOutTransaction> findAllByEmploymentIdAndDate(Long employmentId, Date startDate, Date endDate);
}
