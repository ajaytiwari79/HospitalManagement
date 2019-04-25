package com.kairos.persistence.repository.task_type;

import com.kairos.persistence.model.task_type.TaskTypeSlaConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by oodles on 15/6/17.
 */
@Repository
public interface TaskTypeSlaConfigMongoRepository extends MongoRepository<TaskTypeSlaConfig, BigInteger> {


    TaskTypeSlaConfig findByUnitIdAndTaskTypeIdAndTimeSlotId(long unitId, BigInteger taskTypeId, long timeSlotId);

    List<TaskTypeSlaConfig> findAllByUnitIdAndTaskTypeIdAndTimeSlotIdIn(long unitId, BigInteger taskTypeId, List timeSlotIds);

    List<TaskTypeSlaConfig> findByUnitIdAndTaskTypeId(Long unitId,BigInteger taskTypeId);

}
