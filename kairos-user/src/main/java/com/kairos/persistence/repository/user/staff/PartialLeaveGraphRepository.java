package com.kairos.persistence.repository.user.staff;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.kairos.persistence.model.user.staff.PartialLeave;

/**
 * Created by prabjot on 23/2/17.
 */
public interface PartialLeaveGraphRepository extends GraphRepository<PartialLeave> {
}
