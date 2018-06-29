package com.kairos.user.country.system_setting;

public class SystemLanguageDTO {

    private Long id;
    private String name;
    private String code;
    private boolean active;
    private boolean defaultLanguage;

    public SystemLanguageDTO(){
        // default constructor
    }

    public SystemLanguageDTO(String name, String code, boolean active, boolean defaultLanguage){
        this.name = name;
        this.code = code;
//        this.inactive = inactive;
        this.active = active;
        this.defaultLanguage =defaultLanguage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    /*public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }*/

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
