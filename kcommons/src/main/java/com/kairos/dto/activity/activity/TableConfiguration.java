package com.kairos.dto.activity.activity;

import java.util.*;

public class TableConfiguration {

    private String tabId;
    private Map<String,Object> settings;

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public String getTabId() {

        return tabId;
    }

    public Map<String, Object> getSettings() {
        return Optional.ofNullable(settings).orElse(new HashMap());
    }
}
