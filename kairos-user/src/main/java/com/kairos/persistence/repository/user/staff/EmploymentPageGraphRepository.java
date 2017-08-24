package com.kairos.persistence.repository.user.staff;
import com.kairos.persistence.model.user.staff.EmploymentAccessPageRelation;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by prabjot on 7/12/16.
 */
@Repository
public interface EmploymentPageGraphRepository extends GraphRepository<EmploymentAccessPageRelation> {
}
