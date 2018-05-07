package com.kairos.persistence.repository.user.user_filter;

import com.kairos.persistence.model.constants.RelationshipConstants;
import com.kairos.persistence.model.user.filter.FilterDetail;
import com.kairos.persistence.model.user.filter.FilterGroup;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prerna on 1/5/18.
 */
@Repository
public interface FilterDetailGraphRepository  extends Neo4jBaseRepository<FilterDetail, Long> {

}
