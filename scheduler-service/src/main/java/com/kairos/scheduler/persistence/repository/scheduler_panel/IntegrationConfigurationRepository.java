package com.kairos.scheduler.persistence.repository.scheduler_panel;

import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationSettings;
import com.kairos.scheduler.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.math.BigInteger;
@Repository
public interface IntegrationConfigurationRepository extends MongoBaseRepository<IntegrationSettings, BigInteger> {


    @Query("{isEnabled:true}")
     List<IntegrationSettings> findAllAndIsEnabledTrue();
}
