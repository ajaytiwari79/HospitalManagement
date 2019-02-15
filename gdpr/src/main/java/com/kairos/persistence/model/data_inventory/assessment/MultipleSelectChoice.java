package com.kairos.persistence.model.data_inventory.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class MultipleSelectChoice extends SelectedChoice {

    @ElementCollection
    private List<MetaDataVO> selectedChoice;

    public MultipleSelectChoice() {
    }

    public List<MetaDataVO> getSelectedChoice() {
        return selectedChoice;
    }

    public void setSelectedChoice(List<MetaDataVO> selectedChoice) {
        this.selectedChoice = selectedChoice;
    }

    public MultipleSelectChoice(List<MetaDataVO> selectedChoice) {
        this.selectedChoice = selectedChoice;
    }
}
