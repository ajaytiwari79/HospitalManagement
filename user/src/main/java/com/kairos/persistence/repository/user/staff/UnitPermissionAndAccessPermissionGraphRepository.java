package com.kairos.persistence.repository.user.staff;
import com.kairos.persistence.model.staff.permission.UnitPermissionAccessPermissionRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by prabjot on 30/1/17.
 */
@Repository
public interface UnitPermissionAndAccessPermissionGraphRepository extends Neo4jBaseRepository<UnitPermissionAccessPermissionRelationship,Long> {
}
