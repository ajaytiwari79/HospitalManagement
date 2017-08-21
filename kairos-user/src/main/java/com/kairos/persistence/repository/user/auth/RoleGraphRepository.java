package com.kairos.persistence.repository.user.auth;

import java.util.List;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.auth.Role;

/**
 * Interface for CRUD operation on Roleu
 */
@Repository
public interface RoleGraphRepository extends GraphRepository<Role>{
        Role findByAuthority(String role);
        List<Role> findAll();

}
