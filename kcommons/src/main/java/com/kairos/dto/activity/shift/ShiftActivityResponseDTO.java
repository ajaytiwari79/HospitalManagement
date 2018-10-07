package com.kairos.dto.activity.shift;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 19/9/18
 */

public class ShiftActivityResponseDTO {

    private BigInteger id;
    private List<ShiftActivityDTO> activities = new ArrayList<>();

    public ShiftActivityResponseDTO(BigInteger id) {
        this.id = id;
    }

    public ShiftActivityResponseDTO() {
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public List<ShiftActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ShiftActivityDTO> activities) {
        this.activities = activities;
    }
}
