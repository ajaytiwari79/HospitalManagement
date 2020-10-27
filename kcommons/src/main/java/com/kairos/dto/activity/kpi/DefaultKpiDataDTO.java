package com.kairos.dto.activity.kpi;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.organization.OrganizationCommonDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DefaultKpiDataDTO {
    private List<StaffKpiFilterDTO> staffKpiFilterDTOs;
    private List<DayTypeDTO> dayTypeDTOS;
    private List<TimeSlotDTO> timeSlotDTOS;
    private List<OrganizationCommonDTO> organizationCommonDTOS;
    private List<EmploymentTypeKpiDTO> employmentTypeKpiDTOS;
    private List<ReasonCodeDTO> reasonCodeDTOS;
    private Long countryId;
    private List<TagDTO> tags;
    private List<DateTimeInterval> dateTimeIntervals;
    private List<CountryHolidayCalenderDTO> holidayCalenders;


    public DefaultKpiDataDTO(Long countryId, List<StaffKpiFilterDTO> staffKpiFilterDTOs, List<TimeSlotDTO> timeSlotDTOS, List<OrganizationCommonDTO> organizationCommonDTOS, List<EmploymentTypeKpiDTO> employmentTypeKpiDTOS, List<TagDTO> tags) {
        this.countryId = countryId;
        this.staffKpiFilterDTOs = staffKpiFilterDTOs;
        this.timeSlotDTOS = timeSlotDTOS;
        this.organizationCommonDTOS = organizationCommonDTOS;
        this.employmentTypeKpiDTOS = employmentTypeKpiDTOS;
        this.tags = tags;
    }

    public DefaultKpiDataDTO(List<StaffKpiFilterDTO> staffKpiFilterDTOs) {
        this.staffKpiFilterDTOs = staffKpiFilterDTOs;
    }
}
