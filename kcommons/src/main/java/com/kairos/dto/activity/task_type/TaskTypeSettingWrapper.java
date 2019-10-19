package com.kairos.dto.activity.task_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskTypeSettingWrapper {

    private List<TaskTypeDTO> taskTypeList;
    private List<TaskTypeSettingDTO> taskTypeSettings;
}
