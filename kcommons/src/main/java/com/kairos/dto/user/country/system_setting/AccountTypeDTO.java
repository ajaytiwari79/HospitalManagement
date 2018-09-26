package com.kairos.dto.user.country.system_setting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;

//  Created By vipul   On 10/8/18
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountTypeDTO {
    private Long id;
    @NotNull
    private String name;
    private String description;

    public AccountTypeDTO(){
        // dc
    }

    public AccountTypeDTO(@NotNull String name, String description) {
        this.name = name;
        this.description = description;
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
