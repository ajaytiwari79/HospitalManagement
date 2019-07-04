package com.kairos.dto.gdpr.questionnaire_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import lombok.*;

import javax.validation.Valid;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)@Getter
@Setter
@NoArgsConstructor
public class QuestionnaireTemplateSectionDTO {


    private QuestionnaireTemplateStatus templateStatus;

    @Valid
    private List<QuestionnaireSectionDTO> sections;

}
