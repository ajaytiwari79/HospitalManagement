package com.kairos.persistence.repository.user.staff;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.staff.EmploymentAccessPageRelation;

/**
 * Created by prabjot on 7/12/16.
 */
@Repository
public interface EmploymentPageGraphRepository extends GraphRepository<EmploymentAccessPageRelation> {
}
