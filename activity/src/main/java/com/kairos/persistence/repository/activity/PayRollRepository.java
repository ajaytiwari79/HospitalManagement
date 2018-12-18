package com.kairos.persistence.repository.activity;
/*
 *Created By Pavan on 14/12/18
 *
 */

import com.kairos.dto.activity.payroll.PayRollDTO;
import com.kairos.persistence.model.payroll.PayRoll;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface PayRollRepository extends MongoBaseRepository<PayRoll,BigInteger> {

    PayRollDTO findByIdAndDeletedFalse(BigInteger id);

    List<PayRollDTO> findAllByDeletedFalse();

    @Query("{deleted:false,$or:[{name:{$regex:?0,$options:'i'},code:?1}]}")
    PayRoll getByDeletedFalseAndNameIgnoreCaseOrCode(String name, int code);

    @Query("{deleted:false,_id:{$ne:?0},$or:[{name:{$regex:?1,$options:'i'},code:?2}]}")
    PayRoll getByDeletedFalseAndIdNotOrNameIgnoreCaseAndCode(BigInteger id, String name, int code);

    PayRoll getByIdAndDeletedFalse(BigInteger id);
}
