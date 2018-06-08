package com.kairos.activity.persistence.repository.open_shift;

import java.util.List;
import com.kairos.activity.persistence.model.open_shift.OpenShiftNotification;
import com.kairos.activity.persistence.model.open_shift.Order;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface OpenShiftNotificationMongoRepository extends MongoBaseRepository<OpenShiftNotification, BigInteger> {

    @Query("{'deleted':false, 'staffId':{'$in':?0},'response':{$ne: true}}")
    public List<OpenShiftNotification> findByOpenShiftIds(List<Long>staffIds);
}
