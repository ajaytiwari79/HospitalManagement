package com.kairos.persistence.model.data_inventory.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MultipleSelectChoice extends SelectedChoice {

    @ElementCollection
    private List<MetaDataVO> selectedChoice;

}
