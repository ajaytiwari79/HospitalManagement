package com.kairos.response.dto.web.unit_settings;

public class TAndAGracePeriodSettingDTO {


    private int gracePeriodDays;

    public TAndAGracePeriodSettingDTO() {
    }

    public int getGracePeriodDays() {
        return gracePeriodDays;
    }

    public void setGracePeriodDays(int gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }

    public TAndAGracePeriodSettingDTO(int gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }
}
