package com.kairos.response.dto.master_data.questionnaire_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionnaireSectionResponseDTO {

    private BigInteger id;

    @NotNullOrEmpty(message = "name.cannot.be.empty.or.null")
    private String title;

    private List<MasterQuestionBasicResponseDTO> questions;


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

    public List<MasterQuestionBasicResponseDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MasterQuestionBasicResponseDTO> questions) {
        this.questions = questions;
    }


    public MasterQuestionnaireSectionResponseDTO() {
    }
}
