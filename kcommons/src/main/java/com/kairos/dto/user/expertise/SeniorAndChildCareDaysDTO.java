package com.kairos.dto.user.expertise;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 16/11/18
 */

public class SeniorAndChildCareDaysDTO {
    private List<CareDaysDTO> seniorDays = new ArrayList<>();
    private List<CareDaysDTO> childCareDays = new ArrayList<>();

    public SeniorAndChildCareDaysDTO() {
    }

    public SeniorAndChildCareDaysDTO(List<CareDaysDTO> seniorDays, List<CareDaysDTO> childCareDays) {
        this.seniorDays = seniorDays;
        this.childCareDays = childCareDays;
    }

    public List<CareDaysDTO> getSeniorDays() {
        return seniorDays;
    }

    public void setSeniorDays(List<CareDaysDTO> seniorDays) {
        this.seniorDays = seniorDays;
    }

    public List<CareDaysDTO> getChildCareDays() {
        return childCareDays;
    }

    public void setChildCareDays(List<CareDaysDTO> childCareDays) {
        this.childCareDays = childCareDays;
    }
}
