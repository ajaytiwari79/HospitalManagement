package com.kairos.dto.user.equipment;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 14/12/17.
 */
@Getter
@Setter
public class VehicleEquipmentDTO {

    private List<Long> equipments = new ArrayList<>();

}
