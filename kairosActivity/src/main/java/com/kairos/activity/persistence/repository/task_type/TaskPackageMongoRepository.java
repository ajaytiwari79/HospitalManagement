package com.kairos.activity.persistence.repository.task_type;
import com.kairos.activity.persistence.model.task.TaskPackage;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
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
