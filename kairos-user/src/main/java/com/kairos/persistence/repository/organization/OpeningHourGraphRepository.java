package com.kairos.persistence.repository.organization;
import com.kairos.persistence.model.organization.OpeningHours;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 16/11/16.
 */
@Repository
public interface OpeningHourGraphRepository extends Neo4jBaseRepository<OpeningHours,Long> {


}
