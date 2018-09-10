package com.kairos.persistence.repository.task_type;



import com.kairos.dto.activity.task_type.TaskTypeSettingDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 29/6/18
 */

public interface CustomTaskTypeSettingMongoRepository {

    List<TaskTypeSettingDTO> findByStaffIds(List<Long> staffIds);
}
