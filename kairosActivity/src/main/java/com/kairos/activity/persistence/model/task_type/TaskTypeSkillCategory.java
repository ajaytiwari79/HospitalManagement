package com.kairos.activity.persistence.model.task_type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.view_handler.json_view_handler.TaskTypeViewHandler;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by prabjot on 12/10/16.
 */
@Document
public class TaskTypeSkillCategory extends MongoBaseEntity {

    private Long skillCategoryId;

    @JsonView(TaskTypeViewHandler.JSONViewHandler.class)
    @JsonProperty(defaultValue = "children")
    private List<TaskTypeSkill> skillList;

    public TaskTypeSkillCategory(Long skillCategoryId, List<TaskTypeSkill> skillList) {
        this.skillCategoryId = skillCategoryId;
        this.skillList = skillList;
    }
}
