package com.kairos.activity.persistence.repository.task_type;

import com.kairos.activity.persistence.model.task_type.TaskTypeSetting;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.activity.response.dto.TaskTypeSettingDTO;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@Repository
public interface TaskTypeSettingMongoRepository extends MongoBaseRepository<TaskTypeSetting,BigInteger>{

    @Query("{'deleted':false,'staffId':0,taskTypeId:?1}")
    TaskTypeSetting findByStaffIdAndTaskType(Long staffId, BigInteger taskTypeId);

    @Query("{deleted:false,staffId:?0}")
    List<TaskTypeSettingDTO> findByStaffId(Long staffId);

    @Query("{'deleted':false,'clientId':?0}")
    List<TaskTypeSettingDTO> findByClientId(Long staffId);
}
