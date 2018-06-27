package com.kairos.user.patient.web;

import com.kairos.user.client.ClientMinimumDTO;
import com.kairos.user.staff.EmploymentQueryResult;
import com.kairos.persistence.model.user.unit_position.UnitPositionQueryResult;

import java.util.List;


/**
 * Created by prabjot on 15/11/17.
 */
public class PositionWrapper {

    private List<ClientMinimumDTO> relatedCitizens;
    private UnitPositionQueryResult unitPosition;
    private EmploymentQueryResult employment;

    public EmploymentQueryResult getEmployment() {
        return employment;
    }

    public void setEmployment(EmploymentQueryResult employment) {
        this.employment = employment;
    }


    public PositionWrapper() {
        //default constructor
    }

    public PositionWrapper(List<ClientMinimumDTO> relatedCitizens) {
        this.relatedCitizens = relatedCitizens;
    }

    public PositionWrapper(UnitPositionQueryResult unitPosition, EmploymentQueryResult employment) {
        this.unitPosition = unitPosition;
        this.employment = employment;
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
