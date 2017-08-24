package com.kairos.persistence.repository.organization;
import com.kairos.persistence.model.organization.OpeningHours;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 16/11/16.
 */
@Repository
public interface OpeningHourGraphRepository extends GraphRepository<OpeningHours> {


}
