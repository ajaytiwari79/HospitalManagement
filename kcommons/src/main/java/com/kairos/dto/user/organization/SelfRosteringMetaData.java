package com.kairos.dto.user.organization;

import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelfRosteringMetaData {

    private List<DayType> dayTypes;
    private ReasonCodeWrapper reasonCodeWrapper;
    private List<Map<String,Object>> publicHolidays;
}
