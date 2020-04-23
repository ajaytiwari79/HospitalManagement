package com.kairos.shiftplanning.domain.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 16/11/18
 */
@Getter
@Setter
@AllArgsConstructor
public class SeniorAndChildCareDays {
    private List<CareDays> seniorDays;
    private List<CareDays> childCareDays;

    public SeniorAndChildCareDays() {
        this.seniorDays = new ArrayList<>();
        this.childCareDays = new ArrayList<>();
    }
}
