package com.kairos.dto.gdpr.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgreementTemplateSectionDTO {


    private boolean signatureComponentAdded;
    private boolean signatureComponentLeftAlign;
    private boolean signatureComponentRightAlign;
    private String  signatureHtml;
    private boolean coverPageAdded;
    private boolean includeContentPage;
    private CoverPageVO coverPageData;
    @Valid
    private List<AgreementSectionDTO> sections=new ArrayList<>();


    public boolean isCoverPageAdded() { return coverPageAdded; }

    public void setCoverPageAdded(boolean coverPageAdded) { this.coverPageAdded = coverPageAdded; }

    public CoverPageVO getCoverPageData() { return coverPageData; }

    public void setCoverPageData(CoverPageVO coverPageData) { this.coverPageData = coverPageData; }

    public List<AgreementSectionDTO> getSections() { return sections; }

    public void setSections(List<AgreementSectionDTO> sections) { this.sections = sections; }

    public boolean isSignatureComponentAdded() { return signatureComponentAdded; }

    public void setSignatureComponentAdded(boolean signatureComponentAdded) { this.signatureComponentAdded = signatureComponentAdded; }

    public boolean isSignatureComponentLeftAlign() { return signatureComponentLeftAlign; }

    public void setSignatureComponentLeftAlign(boolean signatureComponentLeftAlign) { this.signatureComponentLeftAlign = signatureComponentLeftAlign; }

    public boolean isSignatureComponentRightAlign() { return signatureComponentRightAlign; }

    public void setSignatureComponentRightAlign(boolean signatureComponentRightAlign) { this.signatureComponentRightAlign = signatureComponentRightAlign; }

    public String getSignatureHtml() { return signatureHtml; }

    public void setSignatureHtml(String signatureHtml) { this.signatureHtml = signatureHtml; }

    public boolean isIncludeContentPage() {
        return includeContentPage;
    }

    public void setIncludeContentPage(boolean includeContentPage) {
        this.includeContentPage = includeContentPage;
    }
}
