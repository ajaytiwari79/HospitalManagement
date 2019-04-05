package com.kairos.persistence.model.task_type;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 15/6/17.
 */
@Document
public class TaskTypeSlaConfig extends MongoBaseEntity {

    private long timeSlotId;
    private String timeSlotName;
    private BigInteger taskTypeId;
    private long unitId;
    private List<SlaPerDayInfo> slaPerDayInfo;


    public TaskTypeSlaConfig(){}

    public TaskTypeSlaConfig(BigInteger taskTypeId, long unitId, long timeSlotId, String timeSlotName) {

        this.taskTypeId = taskTypeId;
        this.timeSlotId = timeSlotId;
        this.unitId = unitId;
        this.timeSlotName = timeSlotName;
    }

    public long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public String getTimeSlotName() {
        return timeSlotName;
    }

    public void setTimeSlotName(String timeSlotName) {
        this.timeSlotName = timeSlotName;
    }

    public BigInteger getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(BigInteger taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public List<SlaPerDayInfo> getSlaPerDayInfo() {
        return slaPerDayInfo;
    }

    public void setSlaPerDayInfo(List<SlaPerDayInfo> slaPerDayInfo) {
        this.slaPerDayInfo = slaPerDayInfo;
    }

    public Map<String, Integer> getSlaConfig() {
        Map<String, Integer> map = new HashMap<>();

        List<SlaPerDayInfo> slaPerDayInfoList = this.slaPerDayInfo;
        slaPerDayInfoList.forEach(slaPerDayInfo -> {
            map.put(slaPerDayInfo.getTaskTypeSlaDay().name(), slaPerDayInfo.getSlaStartDuration());
        });

        return map;
    }

    public TaskTypeSlaConfig copyObject() throws CloneNotSupportedException {
        TaskTypeSlaConfig taskTypeSlaConfig = ObjectMapperUtils.copyPropertiesByMapper(this,TaskTypeSlaConfig.class);
        taskTypeSlaConfig.setId(null);
        List<SlaPerDayInfo> slaPerDayInfos = new ArrayList<>(taskTypeSlaConfig.getSlaPerDayInfo().size());
        this.slaPerDayInfo.forEach(slaPerDayInfo -> {
                slaPerDayInfos.add(slaPerDayInfo.copyObject());
        });
        taskTypeSlaConfig.slaPerDayInfo = slaPerDayInfos;
        return taskTypeSlaConfig;
    }

}
