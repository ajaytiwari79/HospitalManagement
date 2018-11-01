package com.kairos.persistence.repository.user.client;

import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 7/10/16.
 */
@Repository
public interface ContactAddressGraphRepository extends Neo4jBaseRepository<ContactAddress,Long>{



}
