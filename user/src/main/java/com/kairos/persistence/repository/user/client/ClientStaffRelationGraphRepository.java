package com.kairos.persistence.repository.user.client;
import com.kairos.persistence.model.client.ClientStaffRelation;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 28/11/16.
 */
@Repository
public interface ClientStaffRelationGraphRepository extends Neo4jBaseRepository<ClientStaffRelation,Long>{
}
