package com.kairos.response.dto.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class RelatedProcessingActivityResponseDTO {

    private Long id;

    private String name;

    private boolean subProcessingActivity;

    private RelatedProcessingActivityResponseDTO parent;

    public RelatedProcessingActivityResponseDTO(Long id, String name, boolean subProcessingActivity, BigInteger parentProcessingActivityId, String parentProcessingActivityName) {
        this.id = id;
        this.name = name;
        this.subProcessingActivity = subProcessingActivity;
        if (parentProcessingActivityId != null) {
            this.parent = new RelatedProcessingActivityResponseDTO(parentProcessingActivityId.longValue(), parentProcessingActivityName);
        }
    }

    public RelatedProcessingActivityResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
