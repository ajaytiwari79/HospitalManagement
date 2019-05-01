package com.kairos.persistence.repository.widget;

import com.kairos.persistence.model.widget.DashboardWidget;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface WidgetMongoRepository extends MongoBaseRepository<DashboardWidget, BigInteger> {

    @Query(value = "{ userId:?0 ,deleted:false}")
    DashboardWidget findDashboardWidgetByUserId(Long userId);
}
