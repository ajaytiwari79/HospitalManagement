package com.kairos.response.dto.web;

import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.position.UnitEmploymentPosition;

import java.util.List;

/**
 * Created by prabjot on 15/11/17.
 */
public class PositionWrapper {

    private List<ClientMinimumDTO> relatedCitizens;
    private UnitEmploymentPosition unitEmploymentPosition;

    public PositionWrapper() {
        //default constructor
    }

    public PositionWrapper(List<ClientMinimumDTO> relatedCitizens) {
        this.relatedCitizens = relatedCitizens;
    }

    public PositionWrapper(UnitEmploymentPosition unitEmploymentPosition) {
        this.unitEmploymentPosition = unitEmploymentPosition;
    }

    public List<ClientMinimumDTO> getRelatedCitizens() {
        return relatedCitizens;
    }

    public void setRelatedCitizens(List<ClientMinimumDTO> relatedCitizens) {
        this.relatedCitizens = relatedCitizens;
    }

    public UnitEmploymentPosition getUnitEmploymentPosition() {
        return unitEmploymentPosition;
    }

    public void setUnitEmploymentPosition(UnitEmploymentPosition unitEmploymentPosition) {
        this.unitEmploymentPosition = unitEmploymentPosition;
    }
}
