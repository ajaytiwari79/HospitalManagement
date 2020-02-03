package com.kairos.dto.user.expertise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private List<CareDaysDTO> seniorDays = new ArrayList<>();
    private List<CareDaysDTO> childCareDays = new ArrayList<>();

    public SeniorAndChildCareDaysDTO() {
        this.seniorDays = new ArrayList<>();
        this.childCareDays = new ArrayList<>();
    }
}
