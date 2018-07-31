package com.kairos.scheduler.persistence.repository;

import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.math.BigInteger;
@Repository
public interface IntegrationConfigurationRepository extends MongoRepository<IntegrationSettings, BigInteger> {


    @Query("{isEnabled:true}")
     List<IntegrationSettings> findAllAndIsEnabledTrue();
}
