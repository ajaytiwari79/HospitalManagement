package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextChoiceDTO extends SelectedChoiceDTO {

    private String textChoice;

    public TextChoiceDTO() {
    }

    public String getTextChoice() {
        return textChoice;
    }

    public void setTextChoice(String textChoice) {
        this.textChoice = textChoice;
    }
}
