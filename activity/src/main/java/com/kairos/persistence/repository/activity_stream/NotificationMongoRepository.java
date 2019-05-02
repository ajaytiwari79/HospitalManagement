package com.kairos.persistence.repository.activity_stream;

import com.kairos.persistence.model.activity_stream.Notification;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;

import java.math.BigInteger;
import java.util.List;


public interface NotificationMongoRepository extends MongoBaseRepository<Notification,BigInteger> {
    

    List<Notification> findNotificationByOrganizationIdAndUserIdAndSource(Long unitId, Long userId, String source);

}
