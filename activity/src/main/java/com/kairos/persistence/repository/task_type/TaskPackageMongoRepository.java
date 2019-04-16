package com.kairos.persistence.repository.task_type;

import com.kairos.persistence.model.task.TaskPackage;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prabjot on 16/11/16.
 */
@Repository
public interface TaskPackageMongoRepository extends MongoBaseRepository<TaskPackage,BigInteger> {

    List<TaskPackage> findAllByUnitIdAndIsDeleted(long unitId, boolean isDeleted);
}
