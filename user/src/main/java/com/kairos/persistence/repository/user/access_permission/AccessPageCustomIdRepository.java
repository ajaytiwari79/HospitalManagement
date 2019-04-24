package com.kairos.persistence.repository.user.access_permission;

import com.kairos.persistence.model.access_permission.AccessPageCustomId;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by prabjot on 9/10/17.
 */
@Repository
public interface AccessPageCustomIdRepository extends Neo4jBaseRepository<AccessPageCustomId,Long> {

    @Query("Match (n:AccessPageCustomId) return n limit 1")
    AccessPageCustomId findFirst();

}
