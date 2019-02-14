package com.kairos.dto.activity.cta;

import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 8/8/18
 */

public class CTAWTAAndAccumulatedTimebankWrapper {

    private List<CTAResponseDTO> cta = new ArrayList<>();
    private List<WTAResponseDTO> wta = new ArrayList<>();
    //This is a Map of UnitPositionId and UnitpositionLineId and AccumulatedTimebank in Minutes
    private Map<Long, Map<Long,Long>> unitPositionLineAndTimebankMinuteMap;

    public CTAWTAAndAccumulatedTimebankWrapper(List<CTAResponseDTO> cta, List<WTAResponseDTO> wta) {
        this.cta = cta;
        this.wta = wta;
    }

    public CTAWTAAndAccumulatedTimebankWrapper() {
    }

    public List<CTAResponseDTO> getCta() {
        return cta;
    }

    public void setCta(List<CTAResponseDTO> cta) {
        this.cta = cta;
    }

    public List<WTAResponseDTO> getWta() {
        return wta;
    }

    public void setWta(List<WTAResponseDTO> wta) {
        this.wta = wta;
    }

    public Map<Long, Map<Long, Long>> getUnitPositionLineAndTimebankMinuteMap() {
        return unitPositionLineAndTimebankMinuteMap;
    }

    public void setUnitPositionLineAndTimebankMinuteMap(Map<Long, Map<Long, Long>> unitPositionLineAndTimebankMinuteMap) {
        this.unitPositionLineAndTimebankMinuteMap = unitPositionLineAndTimebankMinuteMap;
    }
}
