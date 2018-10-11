package com.kairos.dto.gdpr.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementTemplateSectionDTO {


    private String coverPageHeader;
    private String coverPageFooter;
    private File coverPageImage;
    @Valid
    private List<AgreementSectionDTO> sections=new ArrayList<>();


    public String getCoverPageHeader() { return coverPageHeader; }

    public void setCoverPageHeader(String coverPageHeader) { this.coverPageHeader = coverPageHeader; }

    public String getCoverPageFooter() { return coverPageFooter; }

    public void setCoverPageFooter(String coverPageFooter) { this.coverPageFooter = coverPageFooter; }

    public List<AgreementSectionDTO> getSections() { return sections; }

    public void setSections(List<AgreementSectionDTO> sections) { this.sections = sections; }

    public File getCoverPageImage() { return coverPageImage; }

    public void setCoverPageImage(File coverPageImage) { this.coverPageImage = coverPageImage; }
}
