package com.kairos.activity.response.dto;

import com.kairos.activity.response.dto.activity.BalanceSettingActivityTabDTO;

public class ActivityResult {

    private String name;
    private String description;
    private Long countryId;
    private boolean includeTimebank;

    private BalanceSettingActivityTabDTO balanceSettingActivityTab = new BalanceSettingActivityTabDTO();


    public boolean isIncludeTimebank() {
        return includeTimebank;
    }

    public void setIncludeTimebank(boolean includeTimebank) {
        this.includeTimebank = includeTimebank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public BalanceSettingActivityTabDTO getBalanceSettingActivityTab() {
        return balanceSettingActivityTab;
    }

    public void setBalanceSettingActivityTab(BalanceSettingActivityTabDTO balanceSettingActivityTab) {
        this.balanceSettingActivityTab = balanceSettingActivityTab;
    }
}
