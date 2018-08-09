package com.kairos.user.country.system_setting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.validation.constraints.NotNull;

//  Created By vipul   On 9/8/18
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitTypeDTO {
    private Long id;
    @NotNull
    private String name;
    private String description;

    public UnitTypeDTO(){
        // dc
    }
    public Long getId(){
        return id;
    }
    public String getName(){
        return  this.name.trim();
    }
    public String getDescription(){
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
