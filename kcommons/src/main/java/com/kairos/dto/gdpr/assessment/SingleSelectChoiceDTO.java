package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleSelectChoiceDTO extends SelectedChoiceDTO {

    private MetaDataDTO selectedChoice;

    public MetaDataDTO getSelectedChoice() {
        return selectedChoice;
    }

    public void setSelectedChoice(MetaDataDTO selectedChoice) {
        this.selectedChoice = selectedChoice;
    }
}
