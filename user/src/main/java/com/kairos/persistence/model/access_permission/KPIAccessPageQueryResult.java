package com.kairos.persistence.model.access_permission;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

@QueryResult
@Getter
@Setter
public class KPIAccessPageQueryResult {
    private String name;
    private String moduleId;
    private boolean read;
    private boolean write;
    private boolean active;
    private List<KPIAccessPageDTO> child;
    private Map<String, TranslationInfo> translations;

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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
