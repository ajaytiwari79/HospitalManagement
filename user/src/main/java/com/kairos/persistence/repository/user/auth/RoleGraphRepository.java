package com.kairos.persistence.repository.user.auth;

import com.kairos.persistence.model.auth.Role;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface for CRUD operation on Roleu
 */
@Repository
public interface RoleGraphRepository extends Neo4jBaseRepository<Role,Long>{
        Role findByAuthority(String role);
        List<Role> findAll();

}
