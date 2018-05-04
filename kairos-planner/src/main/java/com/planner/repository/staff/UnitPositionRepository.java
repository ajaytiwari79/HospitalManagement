package com.planner.repository.staff;

import com.planner.domain.staff.UnitPosition;
import com.planner.repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitPositionRepository  extends MongoBaseRepository<UnitPosition,String> {
}
