package com.kairos.persistence.repository.user.auth;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.kairos.persistence.model.user.auth.Role;
import com.kairos.persistence.model.user.auth.UserRole;

/**
 * Interface for CRUD operation on UserRole
 */
public interface UserRoleGraphRepository extends GraphRepository<UserRole>{

    /**
     * find list of all Roles by user id
     * @param id
     * @return List of Role
     */
    @Query("MATCH (ur:UserRole)-[:USER]->(mu:User) WHERE id(mu)={0} with ur match (ur)-[:ROLE]-(role:Role) return role")
    List<Role> findAllByUser(long id);

    /**
     * Find all UserRole
     * @return List of UserRole
     */
    List<UserRole> findAll();

    /**
     * Get UserRile by user id
     * @param id
     * @return UserRole
     */
    @Query("MATCH (ur:UserRole)-[:USER_IS]->(mu:User) WHERE id(mu)= {0} return ur")
    UserRole getUserRoleByUserId(Long id);

    /**
     * Create a Unique node constraint for Priviledge
     */
    @Query("CREATE CONSTRAINT ON (p:Privilege) ASSERT p.privilege IS UNIQUE")
    void createConstraintOnPrivilege();

    /**
     * Find all Privileges by User id provided as argument
     * @param id
     * @return List of Priviledges
     */
//    @Query("MATCH (ur:UserRole)-[USER_IS]->(u:User) where id(u)={0} with ur MATCH (ur)-[HAS_PRIVILEGE]->(p:Privileges) return p")
//    List<Privileges> getAssigedPrivileges(Long id);



 }
