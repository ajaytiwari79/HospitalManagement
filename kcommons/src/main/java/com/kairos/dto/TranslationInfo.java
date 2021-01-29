package com.kairos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TranslationInfo implements Serializable {

    private static final long serialVersionUID = -9068699808609851002L;
    private String name;
    private String description;

    public String toString() {
        return "Translation data  " + this.getName() + " : " + this.description;
    }
}
