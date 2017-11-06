package com.kairos.persistence.repository.user.country;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;
import com.kairos.persistence.model.user.country.EmploymentType;

import java.util.List;

/**
 * Created by prerna on 3/11/17.
 */
@Repository
public interface EmploymentTypeGraphRepository extends GraphRepository<EmploymentType>{

        List<EmploymentType> findAll();

}
