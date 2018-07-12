package com.kairos.activity.wta.version;

import com.kairos.client.dto.TableConfiguration;

import java.util.List;

public class WTATableSettingWrapper {
    private List<WTAVersionDTO> agreements;
    private TableConfiguration tableConfiguration;

    public WTATableSettingWrapper() {
        // DC
    }

    public List<WTAVersionDTO> getAgreements() {
        return agreements;
    }

    public void setAgreements(List<WTAVersionDTO> agreements) {
        this.agreements = agreements;
    }

    public TableConfiguration getTableConfiguration() {
        return tableConfiguration;
    }

    public void setTableConfiguration(TableConfiguration tableConfiguration) {
        this.tableConfiguration = tableConfiguration;
    }

    public WTATableSettingWrapper(List<WTAVersionDTO> agreements, TableConfiguration tableConfiguration) {
        this.agreements = agreements;
        this.tableConfiguration = tableConfiguration;
    }
}
