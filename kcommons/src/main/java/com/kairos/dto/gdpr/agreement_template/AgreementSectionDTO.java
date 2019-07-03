package com.kairos.dto.gdpr.agreement_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.master_data.ClauseBasicDTO;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @NoArgsConstructor
public class AgreementSectionDTO {


    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String title;
    private Integer orderedIndex;
    private String titleHtml;

    @Valid
    private List<ClauseBasicDTO> clauses = new ArrayList<>();
    @Valid
    private List<AgreementSectionDTO> agreementSubSections = new ArrayList<>();

    public List<AgreementSectionDTO> getAgreementSubSections() {
        return agreementSubSections;
    }


}
