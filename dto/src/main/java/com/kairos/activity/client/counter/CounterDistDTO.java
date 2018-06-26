package com.kairos.activity.client.counter;

import java.util.List;
import java.util.Map;

public class CounterDistDTO {
    private List<CounterTypeDefDTO> counterTypeDefs;
    private Map countersIdMap;
    private List<ModuleCounterGroupingDTO> moduleCounters;
    private List<RoleCounterDTO> roleCounters;

    public CounterDistDTO(List<CounterTypeDefDTO> counterTypeDefs, Map countersIdMap, List<ModuleCounterGroupingDTO> moduleCounters, List<RoleCounterDTO> roleCounters){
        this.counterTypeDefs = counterTypeDefs;
        this.countersIdMap = countersIdMap;
        this.moduleCounters = moduleCounters;
        this.roleCounters = roleCounters;
    }

    public List<CounterTypeDefDTO> getCounterTypeDefs() {
        return counterTypeDefs;
    }

    public void setCounterTypeDefs(List<CounterTypeDefDTO> counterTypeDefs) {
        this.counterTypeDefs = counterTypeDefs;
    }

    public Map getCountersIdMap() {
        return countersIdMap;
    }

    public void setCountersIdMap(Map countersIdMap) {
        this.countersIdMap = countersIdMap;
    }

    public List<ModuleCounterGroupingDTO> getModuleCounters() {
        return moduleCounters;
    }

    public void setModuleCounters(List<ModuleCounterGroupingDTO> moduleCounters) {
        this.moduleCounters = moduleCounters;
    }

    public List<RoleCounterDTO> getRoleCounters() {
        return roleCounters;
    }

    public void setRoleCounters(List<RoleCounterDTO> roleCounters) {
        this.roleCounters = roleCounters;
    }
}
