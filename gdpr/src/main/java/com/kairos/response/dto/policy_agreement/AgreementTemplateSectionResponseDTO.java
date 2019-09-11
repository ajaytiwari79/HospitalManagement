package com.kairos.response.dto.policy_agreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.agreement_template.CoverPageVO;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class AgreementTemplateSectionResponseDTO {


    private boolean signatureComponentAdded;
    private boolean signatureComponentLeftAlign;
    private boolean signatureComponentRightAlign;
    private String  signatureHtml;
    private boolean coverPageAdded;
    private boolean includeContentPage;
    private CoverPageVO coverPageData;

    private List<ClauseBasicResponseDTO> clauseListForTemplate = new ArrayList<>();

    private List<AgreementSectionResponseDTO> agreementSections = new ArrayList<>();

}
