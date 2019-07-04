package com.kairos.persistence.model.data_inventory.processing_activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class ProcessingActivityRelatedDataCategory {


    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotEmpty
    private Set<ProcessingActivityRelatedDataElements> dataElements;

}
