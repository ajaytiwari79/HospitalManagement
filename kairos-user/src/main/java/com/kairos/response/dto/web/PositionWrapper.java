package com.kairos.response.dto.web;

import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.unit_position.UnitPositionQueryResult;

import java.util.List;

/**
 * Created by prabjot on 15/11/17.
 */
public class PositionWrapper {

    private List<ClientMinimumDTO> relatedCitizens;
    private UnitPositionQueryResult unitPosition;

    public PositionWrapper() {
        //default constructor
    }

    public PositionWrapper(List<ClientMinimumDTO> relatedCitizens) {
        this.relatedCitizens = relatedCitizens;
    }

    public PositionWrapper(UnitPositionQueryResult unitPosition) {
        this.unitPosition = unitPosition;
    }

    public List<ClientMinimumDTO> getRelatedCitizens() {
        return relatedCitizens;
    }

    public void setRelatedCitizens(List<ClientMinimumDTO> relatedCitizens) {
        this.relatedCitizens = relatedCitizens;
    }

    public UnitPositionQueryResult getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(UnitPositionQueryResult unitPosition) {
        this.unitPosition = unitPosition;
    }
}
