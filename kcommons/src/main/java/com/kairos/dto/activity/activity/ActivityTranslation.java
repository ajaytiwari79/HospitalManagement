package com.kairos.dto.activity.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActivityTranslation{
    private String name;
    private String description;

    public String toString(){
        return "Translation data  "+this.getName()+" : "+this.description;
    }
}
