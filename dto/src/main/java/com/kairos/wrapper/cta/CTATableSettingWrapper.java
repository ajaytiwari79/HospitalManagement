package com.kairos.wrapper.cta;

import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.client.dto.TableConfiguration;

import java.util.List;

public class CTATableSettingWrapper {
    private List<CTAResponseDTO> agreements;
    private TableConfiguration tableConfiguration;

    public CTATableSettingWrapper(List<CTAResponseDTO> agreements, TableConfiguration tableConfiguration) {
        this.agreements = agreements;
        this.tableConfiguration = tableConfiguration;
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
