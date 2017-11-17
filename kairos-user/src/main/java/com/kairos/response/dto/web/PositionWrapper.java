package com.kairos.response.dto.web;

import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.position.Position;

import java.util.List;

/**
 * Created by prabjot on 15/11/17.
 */
public class PositionWrapper {

    private List<ClientMinimumDTO> relatedCitizens;
    private Position position;

    public PositionWrapper() {
        //default constructor
    }

    public PositionWrapper(List<ClientMinimumDTO> relatedCitizens) {
        this.relatedCitizens = relatedCitizens;
    }

    public PositionWrapper(Position position) {
        this.position = position;
    }

    public List<ClientMinimumDTO> getRelatedCitizens() {
        return relatedCitizens;
    }

    public void setRelatedCitizens(List<ClientMinimumDTO> relatedCitizens) {
        this.relatedCitizens = relatedCitizens;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
