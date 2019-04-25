package com.kairos.persistence.model.questionnaire_template;


import com.kairos.enums.gdpr.QuestionType;
import com.kairos.persistence.model.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Question extends BaseEntity {

    @NotBlank(message = "error.message.question.notnull")
    private String question;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    private boolean required;
    @NotNull
    private QuestionType questionType;
    private boolean notSureAllowed;
    private String attributeName;
    private Long countryId;
    private Long organizationId;


    public Question(@NotBlank(message = "error.message.question.notnull") String question, @NotBlank(message = "error.message.description.notNull.orEmpty") String description, boolean required, @NotNull QuestionType questionType, boolean notSureAllowed, Long countryId, Long organizationId) {
        this.question = question;
        this.description = description;
        this.required = required;
        this.questionType = questionType;
        this.notSureAllowed = notSureAllowed;
        this.countryId = countryId;
        this.organizationId = organizationId;
    }

}
