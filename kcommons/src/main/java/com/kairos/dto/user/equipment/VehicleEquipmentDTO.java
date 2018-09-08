package com.kairos.dto.user.equipment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 14/12/17.
 */
public class VehicleEquipmentDTO {

    private List<Long> equipments = new ArrayList<>();

    public List<Long> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<Long> equipments) {
        this.equipments = equipments;
    }

    public VehicleEquipmentDTO(){
        //default constructor
    }
}
