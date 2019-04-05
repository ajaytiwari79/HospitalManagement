package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.DurationType;
import com.kairos.enums.gdpr.RetentionDuration;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RelatedDataElementsDTO {


    @NotNull(message = "error.message.id.notnull")
    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;
    @Min(value = 1, message ="message.relativeDeadLine.value.invalid")
    private int relativeDeadlineDuration;
    @NotNull(message = "message.durationType.null")
    private RetentionDuration relativeDeadlineType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRelativeDeadlineDuration() { return relativeDeadlineDuration; }

    public void setRelativeDeadlineDuration(int relativeDeadlineDuration) { this.relativeDeadlineDuration = relativeDeadlineDuration; }

    public RetentionDuration getRelativeDeadlineType() {
        return relativeDeadlineType;
    }

    public void setRelativeDeadlineType(RetentionDuration relativeDeadlineType) {
        this.relativeDeadlineType = relativeDeadlineType;
    }

    public RelatedDataElementsDTO() {
    }
}
