package com.kairos.repositories.task_type;
import com.kairos.persistence.model.task.TaskReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by prabjot on 4/10/16.
 */
@Repository
public interface TaskReportMongoRepository extends MongoRepository<TaskReport,BigInteger> {
    List<TaskReport> findAll();

    List<TaskReport> findAllByStaffName(String name);

    @Query(value="{ 'updatedAt' : {'$gte':?1, '$lte':?2}}")
    List<TaskReport> findByStaffId(Long staffId, Date startDate, Date endDate);

    TaskReport findByTaskId(BigInteger taskId);

//     List<Map<String,Object>> getStaffWiseReport();

}
