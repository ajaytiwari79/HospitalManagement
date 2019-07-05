package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class SingleSelectChoiceDTO extends SelectedChoiceDTO {

    private MetaDataDTO selectedChoice;


}
