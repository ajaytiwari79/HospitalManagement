package com.kairos.dto.user.organization;

import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelfRosteringMetaData {

    private List<DayTypeDTO> dayTypes;
    private ReasonCodeWrapper reasonCodeWrapper;
    private List<CountryHolidayCalenderDTO> publicHolidays;

    public SelfRosteringMetaData(ReasonCodeWrapper reasonCodeWrapper) {
        this.reasonCodeWrapper = reasonCodeWrapper;
    }
}
