package com.kairos.persistence.model.data_inventory.processing_activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.RetentionDuration;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
public class RelatedDataElements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    private String name;
    @Min(value = 1, message = "message.relativeDeadLine.value.invalid")
    private int relativeDeadlineDuration;
    @NotNull(message = "message.durationType.null")
    private RetentionDuration relativeDeadlineType;


    public RelatedDataElements(Long id, @NotNull String name, @Min(value = 1, message = "message.relativeDeadLine.value.invalid") int relativeDeadlineDuration, @NotNull(message = "message.durationType.null") RetentionDuration relativeDeadlineType) {
        this.id = id;
        this.name = name;
        this.relativeDeadlineDuration = relativeDeadlineDuration;
        this.relativeDeadlineType = relativeDeadlineType;
    }


}
