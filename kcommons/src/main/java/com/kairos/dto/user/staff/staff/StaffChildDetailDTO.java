package com.kairos.dto.user.staff.staff;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.commons.annotation.CPRValidation;
import com.kairos.enums.Gender;
import com.kairos.utils.CPRUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static com.kairos.enums.Gender.MALE;

/**
 * Created By G.P.Ranjan on 5/11/19
 **/
@Getter
@Setter
@NoArgsConstructor
public class StaffChildDetailDTO {
    private Long id;
    private String name;
    @CPRValidation(message = "error.cpr.number.not.valid")
    private String cprNumber;
    private boolean childCustodyRights;

    public Gender getGender(){
        return CPRUtil.getGenderFromCPRNumber(this.getCprNumber());
    }

    public LocalDate getDateOfBirth(){
        return CPRUtil.fetchDateOfBirthFromCPR(this.getCprNumber());
    }
}
