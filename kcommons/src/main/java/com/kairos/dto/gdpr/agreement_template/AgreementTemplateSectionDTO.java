package com.kairos.dto.gdpr.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementTemplateSectionDTO {


    private boolean coverPageAdded;
    private CoverPageVO coverPageData;
    @Valid
    private List<AgreementSectionDTO> sections=new ArrayList<>();


    public List<AgreementSectionDTO> getSections() { return sections; }

    public void setSections(List<AgreementSectionDTO> sections) { this.sections = sections; }

    public boolean isCoverPageAdded() { return coverPageAdded; }

    public void setCoverPageAdded(boolean coverPageAdded) { this.coverPageAdded = coverPageAdded; }

    public CoverPageVO getCoverPageData() { return coverPageData; }

    public void setCoverPageData(CoverPageVO coverPageData) { this.coverPageData = coverPageData; }

}
