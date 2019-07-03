package com.kairos.dto.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.RetentionDuration;
import lombok.*;

import javax.validation.constraints.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class RelatedDataElementsDTO {


    @NotNull(message = "error.message.id.notnull")
    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;
    @Min(value = 1, message ="message.relativeDeadLine.value.invalid")
    private int relativeDeadlineDuration;
    @NotNull(message = "message.durationType.null")
    private RetentionDuration relativeDeadlineType;



}
