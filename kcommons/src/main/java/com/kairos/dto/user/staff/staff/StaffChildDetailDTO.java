package com.kairos.dto.user.staff.staff;

import com.kairos.enums.Gender;
import com.kairos.utils.CPRUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created By G.P.Ranjan on 5/11/19
 **/
@Getter
@Setter
@NoArgsConstructor
public class StaffChildDetailDTO {
    private Long id;
    private String name;
    private String cprNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private boolean childCustodyRights;

    public Integer getAge(){
        return CPRUtil.getAgeFromCPRNumber(cprNumber);
    }
}
