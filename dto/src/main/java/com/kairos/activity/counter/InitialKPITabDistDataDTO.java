package com.kairos.activity.counter;

import java.util.List;
import java.util.Map;

public class InitialKPITabDistDataDTO {
    private List kpiTabs;
    private Map tabKPIsMap;

    public InitialKPITabDistDataDTO(){

    }

    public InitialKPITabDistDataDTO(List kpiTabs, Map tabKPIsMap){
        this.tabKPIsMap = tabKPIsMap;
        this.kpiTabs = kpiTabs;
    }

    public Map getTabKPIsMap() {
        return tabKPIsMap;
    }

    public void setTabKPIsMap(Map tabKPIsMap) {
        this.tabKPIsMap = tabKPIsMap;
    }

    public List getKpiTabs() {
        return kpiTabs;
    }

    public void setKpiTabs(List kpiTabs) {
        this.kpiTabs = kpiTabs;
    }
}
