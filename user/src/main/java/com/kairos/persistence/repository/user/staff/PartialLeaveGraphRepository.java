package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.staff.PartialLeave;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;

/**
 * Created by prabjot on 23/2/17.
 */
public interface PartialLeaveGraphRepository extends Neo4jBaseRepository<PartialLeave,Long> {
}
