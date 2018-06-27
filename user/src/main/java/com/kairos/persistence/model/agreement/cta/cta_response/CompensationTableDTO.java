package com.kairos.persistence.model.agreement.cta.cta_response;

import com.kairos.persistence.model.agreement.cta.CompensationMeasurementType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 20/12/17.
 */
public class CompensationTableDTO {

    private int granularityLevel;
    private CompensationMeasurementType compensationMeasurementType;
    private List<CompensationTableIntervalDTO> compensationTableIntervalDTOList = new ArrayList<>();

    public int getGranularityLevel() {
        return granularityLevel;
    }

    public void setGranularityLevel(int granularityLevel) {
        this.granularityLevel = granularityLevel;
    }

    public CompensationMeasurementType getCompensationMeasurementType() {
        return compensationMeasurementType;
    }

    public void setCompensationMeasurementType(CompensationMeasurementType compensationMeasurementType) {
        this.compensationMeasurementType = compensationMeasurementType;
    }

    public List<CompensationTableIntervalDTO> getCompensationTableIntervalDTOList() {
        return compensationTableIntervalDTOList;
    }

    public void setCompensationTableIntervalDTOList(List<CompensationTableIntervalDTO> compensationTableIntervalDTOList) {
        this.compensationTableIntervalDTOList = compensationTableIntervalDTOList;
    }
}
