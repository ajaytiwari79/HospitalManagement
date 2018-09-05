package com.kairos.persistence.model.access_permission;

import com.kairos.user.access_page.KPIAccessPageDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class KPIAccessPageQueryResult {
    private String name;
    private String moduleId;
    private List<KPIAccessPageDTO> child;

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
