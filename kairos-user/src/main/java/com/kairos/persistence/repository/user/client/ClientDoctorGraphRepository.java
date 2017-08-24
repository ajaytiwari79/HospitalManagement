package com.kairos.persistence.repository.user.client;
import com.kairos.persistence.model.user.client.ClientDoctor;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 9/10/16.
 */
@Repository
public interface ClientDoctorGraphRepository extends GraphRepository<ClientDoctor>{

}
