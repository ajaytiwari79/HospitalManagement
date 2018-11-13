package com.kairos.dto.gdpr.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementTemplateSectionDTO {


    private String coverPageContent;
    private String coverPageTitle;
    @Valid
    private List<AgreementSectionDTO> sections=new ArrayList<>();

    public String getCoverPageContent() { return coverPageContent; }
    public void setCoverPageContent(String coverPageContent) { this.coverPageContent = coverPageContent; }

    public String getCoverPageTitle() { return coverPageTitle; }

    public void setCoverPageTitle(String coverPageTitle) { this.coverPageTitle = coverPageTitle; }

    public List<AgreementSectionDTO> getSections() { return sections; }

    public void setSections(List<AgreementSectionDTO> sections) { this.sections = sections; }

}
