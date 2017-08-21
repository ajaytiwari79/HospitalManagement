package com.kairos.persistence.repository.user.activity_stream;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.activity_stream.Notification;

/**
 * Created by jasgeet on 1/2/17.
 */
@Repository
public interface NotificationGraphRepository  extends GraphRepository<Notification> {

    @Query("Match (notification:Notification)-[:ORGANIZATION]->(organization:Organization) where id(organization)={0} AND notification.isRead=false return notification")
    List<Notification> getNotificationByUnitId(Long unitId);

}
