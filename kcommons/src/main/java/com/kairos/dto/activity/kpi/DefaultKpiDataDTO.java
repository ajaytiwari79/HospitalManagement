package com.kairos.dto.activity.kpi;

import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.organization.OrganizationCommonDTO;

import java.util.List;

public class DefaultKpiDataDTO {
    List<StaffKpiFilterDTO> staffKpiFilterDTOs;
    List<DayTypeDTO> dayTypeDTOS;
    List<TimeSlotDTO> timeSlotDTOS;
    OrganizationCommonDTO organizationCommonDTOS;

    public DefaultKpiDataDTO() {
    }

    public DefaultKpiDataDTO(List<StaffKpiFilterDTO> staffKpiFilterDTOs, List<DayTypeDTO> dayTypeDTOS, List<TimeSlotDTO> timeSlotDTOS) {
        this.staffKpiFilterDTOs = staffKpiFilterDTOs;
        this.dayTypeDTOS = dayTypeDTOS;
        this.timeSlotDTOS = timeSlotDTOS;
    }

    public List<StaffKpiFilterDTO> getStaffKpiFilterDTOs() {
        return staffKpiFilterDTOs;
    }

    public void setStaffKpiFilterDTOs(List<StaffKpiFilterDTO> staffKpiFilterDTOs) {
        this.staffKpiFilterDTOs = staffKpiFilterDTOs;
    }

    public List<DayTypeDTO> getDayTypeDTOS() {
        return dayTypeDTOS;
    }

    public void setDayTypeDTOS(List<DayTypeDTO> dayTypeDTOS) {
        this.dayTypeDTOS = dayTypeDTOS;
    }

    public List<TimeSlotDTO> getTimeSlotDTOS() {
        return timeSlotDTOS;
    }

    public void setTimeSlotDTOS(List<TimeSlotDTO> timeSlotDTOS) {
        this.timeSlotDTOS = timeSlotDTOS;
    }

    public List<OrganizationCommonDTO> getOrganizationCommonDTOS() {
        return organizationCommonDTOS;
    }

    public void setOrganizationCommonDTOS(List<OrganizationCommonDTO> organizationCommonDTOS) {
        this.organizationCommonDTOS = organizationCommonDTOS;
    }
}
