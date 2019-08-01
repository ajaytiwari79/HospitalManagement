package com.kairos.persistence.model.data_inventory.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Entity;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TextChoice extends SelectedChoice {


    private String textChoice;
}
