package com.kairos.activity.counter.distribution.tab;

import com.kairos.user.access_page.KPIAccessPageDTO;

import java.util.List;

public class InitialKPITabDistDataDTO {
    private List<KPIAccessPageDTO> kpiTabs;
    private List<TabKPIMappingDTO> tabKPIsMap;

    public InitialKPITabDistDataDTO(){

    }

    public InitialKPITabDistDataDTO(List<KPIAccessPageDTO> kpiTabs, List<TabKPIMappingDTO> tabKPIsMap){
        this.tabKPIsMap = tabKPIsMap;
        this.kpiTabs = kpiTabs;
    }

    public List<TabKPIMappingDTO> getTabKPIsMap() {
        return tabKPIsMap;
    }

    public void setTabKPIsMap(List<TabKPIMappingDTO> tabKPIsMap) {
        this.tabKPIsMap = tabKPIsMap;
    }

    public List<KPIAccessPageDTO> getKpiTabs() {
        return kpiTabs;
    }

    public void setKpiTabs(List<KPIAccessPageDTO> kpiTabs) {
        this.kpiTabs = kpiTabs;
    }
}
