package com.kairos.wrapper;

import com.kairos.persistence.model.client.query_results.ClientMinimumDTO;
import com.kairos.persistence.model.staff.employment.EmploymentQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionQueryResult;

import java.util.List;


/**
 * Created by prabjot on 15/11/17.
 */
public class PositionWrapper {

    private List<ClientMinimumDTO> relatedCitizens;
    private UnitPositionQueryResult unitPosition;
    private EmploymentQueryResult employment;
    private boolean alreadyMainUnitPositionExists;

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

    public boolean isAlreadyMainUnitPositionExists() {
        return alreadyMainUnitPositionExists;
    }

    public void setAlreadyMainUnitPositionExists(boolean alreadyMainUnitPositionExists) {
        this.alreadyMainUnitPositionExists = alreadyMainUnitPositionExists;
    }
}
