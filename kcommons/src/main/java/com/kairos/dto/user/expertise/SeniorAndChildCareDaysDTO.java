package com.kairos.dto.user.expertise;

import com.kairos.dto.user.country.experties.CareDaysDetails;
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
public class SeniorAndChildCareDaysDTO {
    private List<CareDaysDetails> seniorDays;
    private List<CareDaysDetails> childCareDays;

    public SeniorAndChildCareDaysDTO() {
        this.seniorDays = new ArrayList<>();
        this.childCareDays = new ArrayList<>();
    }
}
