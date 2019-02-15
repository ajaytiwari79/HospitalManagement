package com.kairos.persistence.model.data_inventory.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class TextChoice extends SelectedChoice {


    private String textChoice;

    public String getTextChoice() { return textChoice; }
    public void setTextChoice(String textChoice) { this.textChoice = textChoice; }


    public TextChoice() {
    }

    public TextChoice(String textChoice) {
        this.textChoice = textChoice;
    }
}
