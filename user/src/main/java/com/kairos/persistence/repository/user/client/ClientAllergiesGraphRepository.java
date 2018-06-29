package com.kairos.persistence.repository.user.client;
import com.kairos.persistence.model.client.ClientAllergies;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 9/10/16.
 */
@Repository
public interface ClientAllergiesGraphRepository extends Neo4jBaseRepository<ClientAllergies,Long>{

}
