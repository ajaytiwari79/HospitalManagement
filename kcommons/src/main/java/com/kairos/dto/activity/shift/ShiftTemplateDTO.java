package com.kairos.dto.activity.shift;

import com.kairos.dto.activity.common.UserInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ShiftTemplateDTO implements Serializable {
    private BigInteger id;
    private LocalDate startDate;
    @NotBlank
    private String name;
    private List<IndividualShiftTemplateDTO> shiftList=new ArrayList<>();
    private UserInfo createdBy;
    private Long unitId;
    private Set<BigInteger> individualShiftTemplateIds=new HashSet<>();
}
