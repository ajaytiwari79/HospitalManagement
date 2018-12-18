package com.kairos.dto.gdpr.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CoverPageVO {

    private String coverPageContent;
    private String coverPageTitle;
    private String coverPageLogoUrl;
    private boolean logoPositionLeft;
    private boolean logoPositionRight;
    private boolean logoPositionCenter;
    private boolean coverPageContentAdded;
    private boolean coverPageContentPartAdded;
    private boolean coverPageContentFullAdded;
    private String  coverPageContentOneTextSection;
    private String coverPageContentTwoTextSectionLeft;
    private String coverPageContentTwoTextSectionRight;
    public boolean isCoverPageContentPartAdded() { return coverPageContentPartAdded; }

    public void setCoverPageContentPartAdded(boolean coverPageContentPartAdded) { this.coverPageContentPartAdded = coverPageContentPartAdded; }

    public boolean isCoverPageContentFullAdded() { return coverPageContentFullAdded; }

    public void setCoverPageContentFullAdded(boolean coverPageContentFullAdded) { this.coverPageContentFullAdded = coverPageContentFullAdded; }

    public String getCoverPageContent() { return coverPageContent; }

    public void setCoverPageContent(String coverPageContent) { this.coverPageContent = coverPageContent; }

    public String getCoverPageTitle() { return coverPageTitle; }

    public void setCoverPageTitle(String coverPageTitle) { this.coverPageTitle = coverPageTitle; }

    public String getCoverPageLogoUrl() { return coverPageLogoUrl; }

    public void setCoverPageLogoUrl(String coverPageLogoUrl) { this.coverPageLogoUrl = coverPageLogoUrl; }

    public boolean isLogoPositionLeft() { return logoPositionLeft; }

    public void setLogoPositionLeft(boolean logoPositionLeft) { this.logoPositionLeft = logoPositionLeft; }

    public boolean isLogoPositionRight() { return logoPositionRight; }

    public void setLogoPositionRight(boolean logoPositionRight) { this.logoPositionRight = logoPositionRight; }

    public boolean isLogoPositionCenter() { return logoPositionCenter; }

    public void setLogoPositionCenter(boolean logoPositionCenter) { this.logoPositionCenter = logoPositionCenter; }

    public CoverPageVO(String coverPageLogoUrl) { this.coverPageLogoUrl = coverPageLogoUrl; }

    public boolean isCoverPageContentAdded() { return coverPageContentAdded; }

    public void setCoverPageContentAdded(boolean coverPageContentAdded) { this.coverPageContentAdded = coverPageContentAdded; }

    public String getCoverPageContentOneTextSection() {
        return coverPageContentOneTextSection;
    }

    public void setCoverPageContentOneTextSection(String coverPageContentOneTextSection) {
        this.coverPageContentOneTextSection = coverPageContentOneTextSection;
    }

    public String getCoverPageContentTwoTextSectionLeft() {
        return coverPageContentTwoTextSectionLeft;
    }

    public void setCoverPageContentTwoTextSectionLeft(String coverPageContentTwoTextSectionLeft) {
        this.coverPageContentTwoTextSectionLeft = coverPageContentTwoTextSectionLeft;
    }

    public String getCoverPageContentTwoTextSectionB() {
        return coverPageContentTwoTextSectionRight;
    }

    public void setCoverPageContentTwoTextSectionB(String coverPageContentTwoTextSectionB) {
        this.coverPageContentTwoTextSectionRight = coverPageContentTwoTextSectionB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoverPageVO that = (CoverPageVO) o;
        return logoPositionLeft == that.logoPositionLeft &&
                logoPositionRight == that.logoPositionRight &&
                logoPositionCenter == that.logoPositionCenter &&
                Objects.equals(coverPageContent, that.coverPageContent) &&
                Objects.equals(coverPageTitle, that.coverPageTitle) &&
                Objects.equals(coverPageLogoUrl, that.coverPageLogoUrl);
    }

    @Override
    public int hashCode() {

        return Objects.hash(coverPageContent, coverPageTitle, coverPageLogoUrl, logoPositionLeft, logoPositionRight, logoPositionCenter);
    }

    public CoverPageVO() {
    }
}
