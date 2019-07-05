package com.kairos.persistence.model.data_inventory.processing_activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ProcessingActivityRelatedDataElements {


    @NotNull
    private Long id;

    @NotEmpty
    private String name;

}
