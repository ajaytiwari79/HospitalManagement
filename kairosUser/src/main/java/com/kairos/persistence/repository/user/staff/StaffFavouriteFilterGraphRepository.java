package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.user.staff.StaffFavouriteFilter;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by prerna on 1/5/18.
 */
@Repository
public interface StaffFavouriteFilterGraphRepository extends Neo4jBaseRepository<StaffFavouriteFilter, Long> {
}
