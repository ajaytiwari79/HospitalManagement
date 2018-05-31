package com.kairos.persistence.repository.user.user_filter;

import com.kairos.persistence.model.user.filter.FilterSelection;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by prerna on 1/5/18.
 */
@Repository
public interface FilterDetailGraphRepository  extends Neo4jBaseRepository<FilterSelection, Long> {

}
