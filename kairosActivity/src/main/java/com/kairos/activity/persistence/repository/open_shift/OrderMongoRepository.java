package com.kairos.activity.persistence.repository.open_shift;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.open_shift.Order;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.web.open_shift.ShiftAssignmentCriteria;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface OrderMongoRepository extends MongoBaseRepository<Order, BigInteger> {

    @Query("{'deleted':false, '_id':?0}")
    Order findOrderByIdAndEnabled(BigInteger id);

    @Query("{'deleted':false, 'unitId':?0}")
    List<Order> findOrdersByUnitId(Long unitId);

    @Query(value = "{'deleted':false, 'unitId':?0, 'orderId':?1}",fields = "shiftAssignmentCriteria")
    Order findShiftAssignmentCriteria(Long unitId,BigInteger orderId);


}

