package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kairos.enums.gdpr.QuestionType;

import javax.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "questionType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleSelectChoiceDTO.class, name = "MULTIPLE_CHOICE"),
        @JsonSubTypes.Type(value = TextChoiceDTO.class, name = "TEXTBOX"),
        @JsonSubTypes.Type(value = SingleSelectChoiceDTO.class, name = "SELECT_BOX"),
})
public class SelectedChoiceDTO {

    private Long id;

    @NotNull(message = "error.message.questionType.name.null")
    private QuestionType questionType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public SelectedChoiceDTO() {


    }
}
