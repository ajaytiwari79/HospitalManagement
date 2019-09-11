package com.kairos.dto.gdpr.agreement_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
@Getter
@Setter
@NoArgsConstructor
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


}
