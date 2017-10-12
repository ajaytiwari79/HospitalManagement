package com.kairos.persistence.repository.user.access_permission;

import com.kairos.persistence.model.user.access_permission.AccessPageCustomId;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by prabjot on 9/10/17.
 */
@Repository
public interface AccessPageCustomIdRepository extends GraphRepository<AccessPageCustomId> {

    @Query("Match (n:AccessPageCustomId) return n limit 1")
    AccessPageCustomId findFirst();

}
