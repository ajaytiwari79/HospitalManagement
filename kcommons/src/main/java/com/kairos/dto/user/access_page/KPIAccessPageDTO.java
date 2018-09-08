package com.kairos.dto.user.access_page;

import java.util.List;

public class KPIAccessPageDTO {
    private String name;
    private String moduleId;
    private List<KPIAccessPageDTO> child;

    public KPIAccessPageDTO(){

    }

    public KPIAccessPageDTO(String name, String moduleId){
        this.name = name;
        this.moduleId = moduleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public List<KPIAccessPageDTO> getChild() {
        return child;
    }

    public void setChild(List<KPIAccessPageDTO> child) {
        this.child = child;
    }
}
