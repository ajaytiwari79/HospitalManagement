package com.kairos.persistence.model.task_type;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.enums.task_type.TaskTypeEnum;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by oodles on 21/6/17.
 */
@Document
public class SlaPerDayInfo extends MongoBaseEntity {

    private TaskTypeEnum.TaskTypeSlaDay taskTypeSlaDay;
    private int slaStartDuration; //in minutes
    private int slaEndDuration;

    public int getSlaEndDuration() {
        return slaEndDuration;
    }

    public void setSlaEndDuration(int slaEndDuration) {
        this.slaEndDuration = slaEndDuration;
    }

    public TaskTypeEnum.TaskTypeSlaDay getTaskTypeSlaDay() {
        return taskTypeSlaDay;
    }

    public void setTaskTypeSlaDay(TaskTypeEnum.TaskTypeSlaDay taskTypeSlaDay) {
        this.taskTypeSlaDay = taskTypeSlaDay;
    }

    public int getSlaStartDuration() {
        return slaStartDuration;
    }

    public void setSlaStartDuration(int slaStartDuration) {
        this.slaStartDuration = slaStartDuration;
    }

    //@Override
    public SlaPerDayInfo copyObject() {
        SlaPerDayInfo slaPerDayInfo = ObjectMapperUtils.copyPropertiesByMapper(this,SlaPerDayInfo.class);
        slaPerDayInfo.setId(null);
        return slaPerDayInfo;
    }



}
