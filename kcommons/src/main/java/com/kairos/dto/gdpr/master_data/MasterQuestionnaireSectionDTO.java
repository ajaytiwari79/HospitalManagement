package com.kairos.dto.gdpr.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionnaireSectionDTO {

    private BigInteger id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String title;

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
        return title.trim();
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
