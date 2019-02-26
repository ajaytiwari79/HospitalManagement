package com.kairos.persistence.model.data_inventory.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Embedded;
import javax.persistence.Entity;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class SingleSelectChoice extends SelectedChoice {

    @Embedded
    private MetaDataVO selectedChoice;

    public MetaDataVO getSelectedChoice() { return selectedChoice; }

    public void setSelectedChoice(MetaDataVO selectedChoice) {
        this.selectedChoice = selectedChoice;
    }

    public SingleSelectChoice(MetaDataVO selectedChoice) {
        this.selectedChoice = selectedChoice;
    }

    public SingleSelectChoice() {
    }
}
