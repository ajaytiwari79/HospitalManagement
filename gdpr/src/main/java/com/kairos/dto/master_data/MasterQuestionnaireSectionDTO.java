package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionnaireSectionDTO {

    private BigInteger id;

    @NotNullOrEmpty(message = "Title  can't be empty")
    @Pattern(message = "Special character or Numbers data not excepted in section title",regexp = "^[a-zA-Z\\s]+$")
    private String title;

    @NotNull(message = "Question  can't be null")
    @NotEmpty(message = "Question  can't be  empty")
    @Valid
    private List<MasterQuestionDTO> questions;

    private Boolean deleted;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MasterQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MasterQuestionDTO> questions) {
        this.questions = questions;
    }
}
