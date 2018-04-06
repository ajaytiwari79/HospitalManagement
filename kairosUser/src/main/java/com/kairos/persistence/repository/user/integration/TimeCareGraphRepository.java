package com.kairos.persistence.repository.user.integration;
import com.kairos.persistence.model.user.integration.TimeCare;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 21/2/17.
 */
@Repository
public interface TimeCareGraphRepository extends Neo4jBaseRepository<TimeCare,Long> {
    TimeCare findByOrganizationId(Long organizationId);
}
