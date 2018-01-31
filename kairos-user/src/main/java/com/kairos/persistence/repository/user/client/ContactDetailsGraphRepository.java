package com.kairos.persistence.repository.user.client;
import com.kairos.persistence.model.user.client.ContactDetail;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 7/10/16.
 */
@Repository
public interface ContactDetailsGraphRepository extends Neo4jBaseRepository<ContactDetail,Long>{


}
