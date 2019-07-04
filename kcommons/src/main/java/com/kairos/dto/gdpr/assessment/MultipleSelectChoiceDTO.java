package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown =true )
@Getter
@Setter
@NoArgsConstructor
public class MultipleSelectChoiceDTO extends SelectedChoiceDTO{

    private List<MetaDataDTO> selectedChoice;

}
