package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.response.dto.clause.ClauseBasicResponseDto;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import java.math.BigInteger;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementSectionResponseDto {


    BigInteger id;

    @NotNullOrEmpty
    private String title;

    @NotNullOrEmpty
    List<ClauseBasicResponseDto> clauses;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public List<ClauseBasicResponseDto> getClauses() {
        return clauses;
    }

    public void setClauses(List<ClauseBasicResponseDto> clauses) {
        this.clauses = clauses;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AgreementSectionResponseDto()
    {

    }

}
