package com.kairos.activity.counter;

import java.util.List;

public class TabsKPIDistDTO {
    private List<String> tabIds;
    private TabKPIMappingDTO tabKPIMapping;

    public TabsKPIDistDTO(){}

    public TabsKPIDistDTO(List<String> tabIds, TabKPIMappingDTO tabKPIMapping){
        this.tabIds = tabIds;
        this.tabKPIMapping = tabKPIMapping;
    }

    public List<String> getTabIds() {
        return tabIds;
    }

    public void setTabIds(List<String> tabIds) {
        this.tabIds = tabIds;
    }

    public TabKPIMappingDTO getTabKPIMapping() {
        return tabKPIMapping;
    }

    public void setTabKPIMapping(TabKPIMappingDTO tabKPIMapping) {
        this.tabKPIMapping = tabKPIMapping;
    }
}
