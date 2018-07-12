package com.kairos.scheduler.persistence.repository;

import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
@Repository
public interface IntegrationConfigurationRepository extends MongoRepository<IntegrationConfiguration, BigInteger> {


}
