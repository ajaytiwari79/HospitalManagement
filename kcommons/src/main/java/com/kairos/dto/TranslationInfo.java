package com.kairos.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public String toString(){
        return "Translation data  "+this.getName()+" : "+this.description;
    }
}
