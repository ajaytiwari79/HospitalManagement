package com.planner.repository.activity;

import com.planner.domain.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends MongoRepository<Activity,String> {
    Optional<Activity> findByKairosId(BigInteger kairosId);
    List<Activity> getActivitiesByUnitId(Long unitId);
}
