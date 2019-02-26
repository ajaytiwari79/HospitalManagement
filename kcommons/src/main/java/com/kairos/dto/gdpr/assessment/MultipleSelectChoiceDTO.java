package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown =true )
public class MultipleSelectChoiceDTO extends SelectedChoiceDTO{

    private List<MetaDataDTO> selectedChoice;

    public List<MetaDataDTO> getSelectedChoice() {
        return selectedChoice;
    }

    public void setSelectedChoice(List<MetaDataDTO> selectedChoice) {
        this.selectedChoice = selectedChoice;
    }

    public MultipleSelectChoiceDTO() {
    }
}
