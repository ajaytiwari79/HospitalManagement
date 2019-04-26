package com.kairos.persistence.repository.user;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

/**
 * Base Repository to Handle all independent CRUD operations on All Entities that Extends Base Entity
 */
@Repository
public interface UserBaseRepository extends Neo4jBaseRepository<UserBaseEntity,Long> {

    @Query("MATCH (b:UserBaseEntity) WHERE id(b) = {0} SET b.isEnabled = false return b")
    UserBaseEntity safeDelete(Long id);

    @Query("MATCH (n:UserBaseEntity) where id(n) = {0} DETACH DELETE n")
    void delete(Long aLong);


    @Query("MATCH (s:UserBaseEntity) where id(s)={0} AND s.isEnabled= true return s")
    UserBaseEntity findOne(Long id);

    @Query("CREATE (n:First_DB_NODE)")
    void createFirstDBNode();


}
