package com.kairos.persistence.model.system_setting;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class SystemLanguage extends UserBaseEntity {

    private String name;
    private String code;
    private boolean inactive;
    private boolean defaultLanguage;

    public SystemLanguage(){
        // default constructor
    }

    public SystemLanguage(String name, String code, boolean defaultLanguage){
        this.name = name;
        this.code = code;
        this.defaultLanguage = defaultLanguage;
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

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public boolean isDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(boolean defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
}
