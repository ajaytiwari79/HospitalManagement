package com.kairos.response.dto.policy_agreement;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.clause.AgreementSectionClause;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class AgreementSectionResponseDTO {

    private Long id;

    @NotBlank
    private String title;
    private String titleHtml;
    private Integer orderedIndex;
    private List<AgreementSectionClause> clauses;
    private List<AgreementSectionResponseDTO> agreementSubSections = new ArrayList<>();

    public AgreementSectionResponseDTO(Long id, @NotBlank String title, String titleHtml, Integer orderedIndex) {
        this.id = id;
        this.title = title;
        this.titleHtml = titleHtml;
        this.orderedIndex = orderedIndex;
    }
}
