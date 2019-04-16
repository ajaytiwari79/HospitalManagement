package com.kairos.persistence.repository.user.tpa_services;

import com.kairos.persistence.model.user.tpa_services.IntegrationConfiguration;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 17/1/17.
 */
@Repository
public interface IntegrationConfigurationGraphRepository extends Neo4jBaseRepository<IntegrationConfiguration,Long>{

    @Query("Match(integrationConfiguration:IntegrationConfiguration{isEnabled:true}) return {id:id(integrationConfiguration),name:integrationConfiguration.name,description:integrationConfiguration.description, uniqueKey:integrationConfiguration.uniqueKey } as integrationConfiguration")
    List<Map<String,Object>> getAllIntegrationServices();
}
