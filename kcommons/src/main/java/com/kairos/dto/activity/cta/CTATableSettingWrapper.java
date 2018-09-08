package com.kairos.dto.activity.cta;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.activity.TableConfiguration;

import java.util.List;

public class CTATableSettingWrapper {
    private List<CTAResponseDTO> agreements;
    private TableConfiguration tableConfiguration;

    public CTATableSettingWrapper(List<CTAResponseDTO> agreements, TableConfiguration tableConfiguration) {
        this.agreements = agreements;
        this.tableConfiguration = tableConfiguration;
    }


    public CTATableSettingWrapper() {
    }

    public List<CTAResponseDTO> getAgreements() {
        return agreements;
    }

    public void setAgreements(List<CTAResponseDTO> agreements) {
        this.agreements = agreements;
    }

    public TableConfiguration getTableConfiguration() {
        return tableConfiguration;
    }

    public void setTableConfiguration(TableConfiguration tableConfiguration) {
        this.tableConfiguration = tableConfiguration;
    }
}
