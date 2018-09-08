package com.kairos.activity.open_shift;

import com.kairos.activity.open_shift.priority_group.PriorityGroupDTO;

import java.util.List;

public class PriorityGroupWrapper {
    private PriorityGroupDefaultData defaultData;
    private List<PriorityGroupDTO> priorityGroupData;

    public PriorityGroupWrapper() {
        //Default Constructor
    }

    public PriorityGroupWrapper(PriorityGroupDefaultData defaultData, List<PriorityGroupDTO> priorityGroupData) {
        this.defaultData = defaultData;
        this.priorityGroupData = priorityGroupData;
    }

    public PriorityGroupDefaultData getDefaultData() {
        return defaultData;
    }

    public void setDefaultData(PriorityGroupDefaultData defaultData) {
        this.defaultData = defaultData;
    }

    public List<PriorityGroupDTO> getPriorityGroupData() {
        return priorityGroupData;
    }

    public void setPriorityGroupData(List<PriorityGroupDTO> priorityGroupData) {
        this.priorityGroupData = priorityGroupData;
    }
}
