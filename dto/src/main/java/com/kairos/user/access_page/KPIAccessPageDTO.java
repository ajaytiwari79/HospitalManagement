package com.kairos.user.access_page;

public class KPIAccessPageDTO {
    private String name;
    private String moduleId;

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
}
