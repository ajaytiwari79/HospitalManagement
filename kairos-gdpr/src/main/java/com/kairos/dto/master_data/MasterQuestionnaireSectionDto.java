package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionnaireSectionDto {

    @NotNullOrEmpty(message = "error.title.cannot.be.empty.or.null")
    @Pattern(message = "special character or numberic data not excepted",regexp = "^[a-zA-Z\\s]+$")
    private String title;

    @NotNull(message = "list.cannot.be.null")
    @NotEmpty(message = "list.cannot.be.empty")
    private List<BigInteger> questions;

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

    public List<BigInteger> getQuestions() {
        return questions;
    }

    public void setQuestions(List<BigInteger> questions) {
        this.questions = questions;
    }
}
