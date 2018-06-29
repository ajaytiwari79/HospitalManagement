package com.kairos.persistence.model.system_setting;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class SystemLanguage extends UserBaseEntity {

    private String name;
    private String code;
    private boolean active;
    private boolean defaultLanguage;

    public SystemLanguage(){
        // default constructor
    }

    public SystemLanguage(String name, String code, boolean defaultLanguage, boolean active){
        this.name = name;
        this.code = code;
        this.defaultLanguage = defaultLanguage;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(boolean defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
}
