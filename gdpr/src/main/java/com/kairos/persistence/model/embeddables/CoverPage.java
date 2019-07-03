package com.kairos.persistence.model.embeddables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class CoverPage {

    @Nullable
    @Column(columnDefinition = "text")
    private String coverPageContent;

    @Column(columnDefinition = "text")
    private String coverPageTitle;

    @Column(columnDefinition = "text")
    private String coverPageLogoUrl;
    private boolean logoPositionLeft = false;
    private boolean logoPositionRight = false;
    private boolean logoPositionCenter = false;
    private boolean coverPageContentAdded = false;
    private boolean coverPageContentPartAdded = false;
    private boolean coverPageContentFullAdded = false;

    @Column(columnDefinition = "text")
    private String  coverPageContentOneTextSection;

    @Column(columnDefinition = "text")
    private String coverPageContentTwoTextSectionLeft;

    @Column(columnDefinition = "text")
    private String coverPageContentTwoTextSectionRight;

    public CoverPage(String coverPageLogoUrl) {
        this.coverPageLogoUrl = coverPageLogoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoverPage that = (CoverPage) o;
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

    @Override
    public String toString() {
        return "CoverPage{" +
                "coverPageContent='" + coverPageContent + '\'' +
                ", coverPageTitle='" + coverPageTitle + '\'' +
                ", coverPageLogoUrl='" + coverPageLogoUrl + '\'' +
                ", logoPositionLeft=" + logoPositionLeft +
                ", logoPositionRight=" + logoPositionRight +
                ", logoPositionCenter=" + logoPositionCenter +
                ", coverPageContentAdded=" + coverPageContentAdded +
                ", coverPageContentPartAdded=" + coverPageContentPartAdded +
                ", coverPageContentFullAdded=" + coverPageContentFullAdded +
                ", coverPageContentOneTextSection='" + coverPageContentOneTextSection + '\'' +
                ", coverPageContentTwoTextSectionLeft='" + coverPageContentTwoTextSectionLeft + '\'' +
                ", coverPageContentTwoTextSectionRight='" + coverPageContentTwoTextSectionRight + '\'' +
                '}';
    }
}
