package com.kairos.persistence.repository.user.integration;
import com.kairos.persistence.model.user.integration.Visitour;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 21/2/17.
 */
@Repository
public interface VisitourGraphRepository extends GraphRepository<Visitour>{
    Visitour findByOrganizationId(Long organizationId);
}
