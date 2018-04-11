package com.kairos.activity.persistence.model.table_settings;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by prabjot on 28/4/17.
 */
@Document
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
