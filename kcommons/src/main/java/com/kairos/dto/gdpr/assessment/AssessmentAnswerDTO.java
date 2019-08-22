package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class AssessmentAnswerDTO {



    private Long id;
    @NotNull(message = "error.message.question.id.notNull")
    private Long questionId;
    @NotBlank(message = "error.message.attribute.name.null")
    private String attributeName;
    private SelectedChoiceDTO value;
    @NotNull(message = "error.message.questionType.name.null")
    private QuestionType questionType;


}
