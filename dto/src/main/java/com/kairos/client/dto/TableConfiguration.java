package com.kairos.client.dto;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TableConfiguration {

    private String tableId;
    private Map<String,Object> settings;

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public String getTableId() {

        return tableId;
    }

    public Map<String, Object> getSettings() {
        return Optional.ofNullable(settings).orElse(new HashMap());
    }
}
