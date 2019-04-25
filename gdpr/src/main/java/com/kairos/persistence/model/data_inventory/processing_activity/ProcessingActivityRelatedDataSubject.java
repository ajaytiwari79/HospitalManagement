package com.kairos.persistence.model.data_inventory.processing_activity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class ProcessingActivityRelatedDataSubject {


    @NotNull
    private Long id;

    @NotNull
    private String name;

    private List<ProcessingActivityRelatedDataCategory> dataCategories;

}
