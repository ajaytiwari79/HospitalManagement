package com.kairos.persistence.repository.open_shift;

import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.open_shift.Order;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.activity.open_shift.OpenShiftDTO;
import com.kairos.activity.open_shift.OpenShiftResponseDTO;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
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

    List<OpenShift> getOpenShiftsByUnitIdAndOrderId(Long unitId, BigInteger orderId);

    @Query("{'deleted':false,  'activityId':?0, '$or':[{'startDate':{$gte:?1,$lte:?2}},{'endDate':{$gte:?1,$lte:?2}}]}")
    List<OpenShift> findAllOpenShiftsByActivityIdAndBetweenDuration(BigInteger activityId, Date startDate, Date endDate);

    @Query("{'deleted':false, interestedStaff:?0, 'startDate':{$gt:?1}}")
    List<OpenShift> findAllOpenShiftsByInterestedStaff(Long staffId, LocalDateTime employmentEnd);

}
