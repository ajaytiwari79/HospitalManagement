package com.kairos.dto.activity.kpi;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.organization.OrganizationCommonDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import lombok.*;

import java.util.List;
import java.util.Map;

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

    public DefaultKpiDataDTO(List<StaffKpiFilterDTO> staffKpiFilterDTOs, List<DayTypeDTO> dayTypeDTOS, List<TimeSlotDTO> timeSlotDTOS) {
        this.staffKpiFilterDTOs = staffKpiFilterDTOs;
        this.dayTypeDTOS = dayTypeDTOS;
        this.timeSlotDTOS = timeSlotDTOS;
    }

    public DefaultKpiDataDTO(Long countryId, List<StaffKpiFilterDTO> staffKpiFilterDTOs, List<DayTypeDTO> dayTypeDTOS, List<TimeSlotDTO> timeSlotDTOS, List<OrganizationCommonDTO> organizationCommonDTOS, List<EmploymentTypeKpiDTO> employmentTypeKpiDTOS, List<ReasonCodeDTO> reasonCodeDTOS, List<TagDTO> tags) {
        this.countryId = countryId;
        this.staffKpiFilterDTOs = staffKpiFilterDTOs;
        this.dayTypeDTOS = dayTypeDTOS;
        this.timeSlotDTOS = timeSlotDTOS;
        this.organizationCommonDTOS = organizationCommonDTOS;
        this.employmentTypeKpiDTOS = employmentTypeKpiDTOS;
        this.reasonCodeDTOS = reasonCodeDTOS;
        this.tags = tags;
    }

}
