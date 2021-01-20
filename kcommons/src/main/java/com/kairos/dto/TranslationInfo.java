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
    private String name;
    private String description;

    public String toString() {
        return "Translation data  " + this.getName() + " : " + this.description;
    }
}
