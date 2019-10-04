package com.kairos.response.dto.master_data.questionnaire_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.gdpr.QuestionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class QuestionBasicResponseDTO {

    private Long id;
    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String question;
    private String description;
    private Object value;
    private Object assessmentAnswerChoices;
    private QuestionType questionType;
    private String attributeName;
    private boolean required;
    private boolean notSureAllowed;
}
