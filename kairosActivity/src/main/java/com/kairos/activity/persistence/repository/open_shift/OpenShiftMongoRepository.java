package com.kairos.activity.persistence.repository.open_shift;

import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.persistence.model.open_shift.Order;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

import java.math.BigInteger;

public interface OpenShiftMongoRepository extends MongoBaseRepository<OpenShift,BigInteger>,CustomOpenShiftMongoRepository {

    @Query("{'deleted' : false,'_id':?0}")
    OpenShift findOpenShiftByIdAndEnabled(BigInteger id);

    @Query("{'deleted':false, 'unitId':?0, 'orderId':?1}")
    List<OpenShift> findOpenShiftsByUnitIdAndOrderId(Long unitId, BigInteger orderId);

    @Query("{'deleted' :false, '_id':{$in:?0}}")
    List<OpenShift> findAllByIdsAndDeletedFalse(List<BigInteger> openShiftIds);

    OpenShift findByIdAndUnitIdAndDeletedFalse(BigInteger openShiftId,Long unitId);

}
