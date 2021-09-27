package com.kairos.persistence.model.agreement.cta.cta_response;

import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.persistence.model.country.default_data.CurrencyDTO;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CTARuleTemplateDefaultDataWrapper {
    @Builder.Default
    private List<EmploymentTypeDTO> employmentTypes=new ArrayList<>();
    @Builder.Default
    private List<CurrencyDTO>currencies=new ArrayList<>();
    @Builder.Default
    private List<CountryHolidayCalenderDTO> holidayMapList=new ArrayList<>();
    @Builder.Default
    private List<FunctionDTO> functions = new ArrayList<FunctionDTO>();



}
