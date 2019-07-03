package com.kairos.response.dto.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingActivityBasicDTO {

    private Long processingActivityId;
    private String processingActivityName;
    private boolean isSubProcessingActivity;
}
