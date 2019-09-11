package com.kairos.dto.gdpr.questionnaire_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class QuestionDTO {

    private Long id;

    @NotBlank(message = "error.message.question.title.notNull")
    private String question;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;

    private boolean required;

    @NotNull(message = "error.message.questionType.name.notnull")
    private QuestionType questionType;

    private String attributeName;

    private boolean notSureAllowed;


    public String getQuestion() { return question.trim(); }


}
