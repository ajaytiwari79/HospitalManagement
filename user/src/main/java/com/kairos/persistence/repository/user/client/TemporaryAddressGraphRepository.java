package com.kairos.persistence.repository.user.client;
import com.kairos.user.client.ClientTemporaryAddress;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by prabjot on 4/5/17.
 */
@Repository
public interface TemporaryAddressGraphRepository extends Neo4jBaseRepository<ClientTemporaryAddress,Long> {
}
