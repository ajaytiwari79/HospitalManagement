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

    public CoverPageVO(String coverPageLogoUrl) {
        this.coverPageLogoUrl = coverPageLogoUrl;
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
