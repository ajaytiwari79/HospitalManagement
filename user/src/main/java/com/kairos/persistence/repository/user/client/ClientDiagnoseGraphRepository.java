package com.kairos.persistence.repository.user.client;
import com.kairos.user.client.ClientDiagnose;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 11/11/16.
 */
@Repository
public interface ClientDiagnoseGraphRepository extends Neo4jBaseRepository<ClientDiagnose,Long>{


}
