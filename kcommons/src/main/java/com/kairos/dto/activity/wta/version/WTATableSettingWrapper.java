package com.kairos.dto.activity.wta.version;

import com.kairos.dto.activity.activity.TableConfiguration;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WTATableSettingWrapper {
    private List<WTAResponseDTO> agreements;
    private TableConfiguration tableConfiguration;

    public WTATableSettingWrapper(List<WTAResponseDTO> agreements, TableConfiguration tableConfiguration) {
        this.agreements = agreements;
        this.tableConfiguration = tableConfiguration;
    }
}
