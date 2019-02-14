package com.kairos.dto.activity.kpi;

import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.organization.OrganizationCommonDTO;

import java.util.List;

public class DefaultKpiDataDTO {
    private  List<StaffKpiFilterDTO> staffKpiFilterDTOs;
    private List<DayTypeDTO> dayTypeDTOS;
    private List<TimeSlotDTO> timeSlotDTOS;
    private List<OrganizationCommonDTO> organizationCommonDTOS;
    private List<EmploymentTypeKpiDTO> employmentTypeKpiDTOS;
    private Long countryId;
    public DefaultKpiDataDTO() {
    }

    public DefaultKpiDataDTO(List<StaffKpiFilterDTO> staffKpiFilterDTOs, List<DayTypeDTO> dayTypeDTOS, List<TimeSlotDTO> timeSlotDTOS) {
        this.staffKpiFilterDTOs = staffKpiFilterDTOs;
        this.dayTypeDTOS = dayTypeDTOS;
        this.timeSlotDTOS = timeSlotDTOS;
    }

    public DefaultKpiDataDTO(Long countryId,List<StaffKpiFilterDTO> staffKpiFilterDTOs, List<DayTypeDTO> dayTypeDTOS, List<TimeSlotDTO> timeSlotDTOS, List<OrganizationCommonDTO> organizationCommonDTOS,List<EmploymentTypeKpiDTO> employmentTypeKpiDTOS) {
        this.countryId=countryId;
        this.staffKpiFilterDTOs = staffKpiFilterDTOs;
        this.dayTypeDTOS = dayTypeDTOS;
        this.timeSlotDTOS = timeSlotDTOS;
        this.organizationCommonDTOS = organizationCommonDTOS;
        this.employmentTypeKpiDTOS=employmentTypeKpiDTOS;
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

    public List<EmploymentTypeKpiDTO> getEmploymentTypeKpiDTOS() {
        return employmentTypeKpiDTOS;
    }

    public void setEmploymentTypeKpiDTOS(List<EmploymentTypeKpiDTO> employmentTypeKpiDTOS) {
        this.employmentTypeKpiDTOS = employmentTypeKpiDTOS;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}
