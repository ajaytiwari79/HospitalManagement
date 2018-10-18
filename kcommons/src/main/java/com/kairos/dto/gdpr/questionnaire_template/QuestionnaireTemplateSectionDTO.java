package com.kairos.dto.gdpr.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireTemplateSectionDTO {


    private QuestionnaireTemplateStatus templateStatus;

    @Valid
    private List<QuestionnaireSectionDTO> sections;

    public QuestionnaireTemplateStatus getTemplateStatus() { return templateStatus; }

    public void setTemplateStatus(QuestionnaireTemplateStatus templateStatus) { this.templateStatus = templateStatus; }

    public List<QuestionnaireSectionDTO> getSections() { return sections; }

    public void setSections(List<QuestionnaireSectionDTO> sections) { this.sections = sections; }
}
