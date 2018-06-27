package com.kairos.persistence.repository.user.staff;
import com.kairos.user.staff.EmploymentAccessPageRelation;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by prabjot on 7/12/16.
 */
@Repository
public interface EmploymentPageGraphRepository extends Neo4jBaseRepository<EmploymentAccessPageRelation,Long> {
}
