package com.kairos.activity.counter.distribution.tab;

import com.kairos.user.access_page.KPIAccessPageDTO;

import java.util.List;

public class TabsKPIDistDTO {
    private List<KPIAccessPageDTO> tabIds;
    private TabKPIMappingDTO tabKPIMapping;

    public TabsKPIDistDTO(){}

    public TabsKPIDistDTO(List<KPIAccessPageDTO> tabIds, TabKPIMappingDTO tabKPIMapping){
        this.tabIds = tabIds;
        this.tabKPIMapping = tabKPIMapping;
    }

    public List<KPIAccessPageDTO> getTabIds() {
        return tabIds;
    }

    public void setTabIds(List<KPIAccessPageDTO> tabIds) {
        this.tabIds = tabIds;
    }

    public TabKPIMappingDTO getTabKPIMapping() {
        return tabKPIMapping;
    }

    public void setTabKPIMapping(TabKPIMappingDTO tabKPIMapping) {
        this.tabKPIMapping = tabKPIMapping;
    }
}
