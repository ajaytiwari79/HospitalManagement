package com.kairos.dto.activity.shift;

import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 4/10/18
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompactViewDTO {

    private List<DetailViewDTO> staffShifts;
    private List<ReasonCodeDTO> reasonCodes;
    private Map<LocalDate, List<FunctionDTO>> assignedFunctionsObj;
}
