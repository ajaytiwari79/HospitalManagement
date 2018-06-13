package com.kairos.activity.persistence.repository.open_shift;

import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.persistence.model.open_shift.Order;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.web.open_shift.OpenShiftDTO;
import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;
import org.springframework.data.mongodb.repository.Query;

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

    @Query("{'deleted':false, 'isMainShift':true, 'activityId':?0, '$or':[{'startDate':{$gte:?1,$lte:?2}},{'endDate':{$gte:?1,$lte:?2}}]}")
    List<OpenShiftDTO> findAllOpenShiftsByActivityIdAndBetweenDuration(BigInteger activityId, Date startDate, Date endDat);

}
