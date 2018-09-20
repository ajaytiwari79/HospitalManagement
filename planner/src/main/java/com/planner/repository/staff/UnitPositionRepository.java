package com.planner.repository.staff;

import com.planner.domain.staff.UnitPosition;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitPositionRepository  extends MongoBaseRepository<UnitPosition,String> {
    List<UnitPosition> getAllUnitPositionsByUnitId(Long unit);
}
