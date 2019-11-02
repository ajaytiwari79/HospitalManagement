package com.kairos.dto.activity.shift;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 1/6/18.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftFunctionWrapper {
    private Map<LocalDate,List<ShiftDTO>> shifts;
    private Map<LocalDate,List<FunctionDTO>> assignedFunctionsObj;

}
