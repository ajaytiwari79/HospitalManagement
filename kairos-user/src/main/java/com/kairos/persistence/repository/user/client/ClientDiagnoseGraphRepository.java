package com.kairos.persistence.repository.user.client;
import com.kairos.persistence.model.user.client.ClientDiagnose;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 11/11/16.
 */
@Repository
public interface ClientDiagnoseGraphRepository extends GraphRepository<ClientDiagnose>{


}
