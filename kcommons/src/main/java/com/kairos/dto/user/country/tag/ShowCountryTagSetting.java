package com.kairos.dto.user.country.tag;

/**
 * Created by prerna on 13/11/17.
 */
public class ShowCountryTagSetting {
    boolean showCountryTags;

    public boolean isShowCountryTags() {
        return showCountryTags;
    }

    public void setShowCountryTags(boolean showCountryTags) {
        this.showCountryTags = showCountryTags;
    }

    public ShowCountryTagSetting(boolean showCountryTags){
        this.showCountryTags = showCountryTags;
    }
    ShowCountryTagSetting(){ }
}
