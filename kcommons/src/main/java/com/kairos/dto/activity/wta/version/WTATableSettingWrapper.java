package com.kairos.dto.activity.wta.version;

import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.activity.activity.TableConfiguration;

import java.util.List;

public class WTATableSettingWrapper {
    private List<WTAResponseDTO> agreements;
    private TableConfiguration tableConfiguration;

    public WTATableSettingWrapper() {
        // DC
    }

    public List<WTAResponseDTO> getAgreements() {
        return agreements;
    }

    public void setAgreements(List<WTAResponseDTO> agreements) {
        this.agreements = agreements;
    }

    public TableConfiguration getTableConfiguration() {
        return tableConfiguration;
    }

    public void setTableConfiguration(TableConfiguration tableConfiguration) {
        this.tableConfiguration = tableConfiguration;
    }

    public WTATableSettingWrapper(List<WTAResponseDTO> agreements, TableConfiguration tableConfiguration) {
        this.agreements = agreements;
        this.tableConfiguration = tableConfiguration;
    }
}
