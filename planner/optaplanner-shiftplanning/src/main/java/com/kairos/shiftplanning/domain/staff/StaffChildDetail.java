package com.kairos.shiftplanning.domain.staff;

import com.kairos.commons.annotation.CPRValidation;
import com.kairos.enums.Gender;
import com.kairos.utils.CPRUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StaffChildDetail {
    private Long id;
    private String name;
    @CPRValidation(message = "error.cpr.number.not.valid")
    private String cprNumber;
    private boolean childCustodyRights;

    public int getAge(LocalDate localDate){
        return CPRUtil.getAgeByCPRNumberAndStartDate(this.getCprNumber(),localDate);
    }
}