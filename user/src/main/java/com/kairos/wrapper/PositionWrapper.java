package com.kairos.wrapper;

import com.kairos.persistence.model.client.query_results.ClientMinimumDTO;
import com.kairos.persistence.model.staff.position.PositionQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionQueryResult;

import java.util.List;


/**
 * Created by prabjot on 15/11/17.
 */
public class PositionWrapper {

    private List<ClientMinimumDTO> relatedCitizens;
    private UnitPositionQueryResult unitPosition;
    private PositionQueryResult position;


    public PositionQueryResult getPosition() {
        return position;
    }

    public void setPosition(PositionQueryResult position) {
        this.position = position;
    }


    public PositionWrapper() {
        //default constructor
    }

    public PositionWrapper(List<ClientMinimumDTO> relatedCitizens) {
        this.relatedCitizens = relatedCitizens;
    }

    public PositionWrapper(UnitPositionQueryResult unitPosition, PositionQueryResult position) {
        this.unitPosition = unitPosition;
        this.position = position;
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
