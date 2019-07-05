package com.kairos.dto.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class RelatedDataSubjectDTO {


    @NotNull(message = "error.message.id.notnull")
    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;

    @Valid
    @NotEmpty
    private List<RelatedDataCategoryDTO> dataCategories = new ArrayList<>();

}
