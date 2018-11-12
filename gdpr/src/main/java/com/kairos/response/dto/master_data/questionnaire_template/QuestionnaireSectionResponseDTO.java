package com.kairos.response.dto.master_data.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireSectionResponseDTO {

    private BigInteger id;

    @NotBlank(message = "name.cannot.be.empty.or.null")
    private String title;

    private List<QuestionBasicResponseDTO> questions=new ArrayList<>();


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<QuestionBasicResponseDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionBasicResponseDTO> questions) {
        this.questions = questions;
    }


    public QuestionnaireSectionResponseDTO() {
    }
}
