package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.shift.BreakPaymentSetting;

import java.util.List;

/**
 * Created by vipul on 6/2/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Expertise {
    private Long id;
    private String name;
    private BreakPaymentSetting breakPaymentSetting;
    private List<ProtectedDaysOffSettingDTO> protectedDaysOffSettings;

    public Expertise() {
        //Not in use
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BreakPaymentSetting getBreakPaymentSetting() {
        return breakPaymentSetting;
    }

    public void setBreakPaymentSetting(BreakPaymentSetting breakPaymentSetting) {
        this.breakPaymentSetting = breakPaymentSetting;
    }

    public List<ProtectedDaysOffSettingDTO> getProtectedDaysOffSettings() {
        return protectedDaysOffSettings;
    }

    public void setProtectedDaysOffSettings(List<ProtectedDaysOffSettingDTO> protectedDaysOffSettings) {
        this.protectedDaysOffSettings = protectedDaysOffSettings;
    }
}
