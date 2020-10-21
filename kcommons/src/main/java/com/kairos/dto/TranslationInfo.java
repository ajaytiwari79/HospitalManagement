package com.kairos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TranslationInfo {
    private String name;
    private String description;

    public void setDescription(String description){
        if(this.name.equals("")){
            this.description = "";
        }else {
            this.description=description;
        }
    }

    public String toString(){
        return "Translation data  "+this.getName()+" : "+this.description;
    }
}
