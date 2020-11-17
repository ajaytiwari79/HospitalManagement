package com.kairos.persistence.model.agreement.cta.cta_response;

import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.country.agreement.cta.CalculateValueIfPlanned;
import com.kairos.dto.user.country.agreement.cta.cta_response.*;
import com.kairos.persistence.model.country.default_data.CurrencyDTO;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class CTARuleTemplateDefaultDataWrapper {
    private List<CalculateValueIfPlanned> calculateValueIfPlanned=new ArrayList<>();
    private List<DayTypeDTO> dayTypes=new ArrayList<>();
    private List<PhaseResponseDTO> phases=new ArrayList<>();
    private List<TimeTypeDTO> timeTypes=new ArrayList<>();
    private List<ActivityTypeDTO> activityTypes=new ArrayList<>();
    private List<EmploymentTypeDTO> employmentTypes=new ArrayList<>();
    private List<PresenceTypeDTO> plannedTime=new ArrayList<>();
    private List<CurrencyDTO>currencies=new ArrayList<>();
    private List<Map<String, Object>>holidayMapList=new ArrayList<>();
    private List<FunctionDTO> functions = new ArrayList<FunctionDTO>();
    List<ActivityCategoryDTO> activityCategories;


}
