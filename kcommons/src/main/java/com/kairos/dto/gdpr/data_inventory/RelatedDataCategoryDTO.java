package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class RelatedDataCategoryDTO {


    @NotNull(message = "error.message.id.notnull")
    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;

    @Valid
    @NotEmpty
    private List<RelatedDataElementsDTO> dataElements= new ArrayList<>();
}
