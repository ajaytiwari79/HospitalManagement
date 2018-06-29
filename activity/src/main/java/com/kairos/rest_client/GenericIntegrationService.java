package com.kairos.rest_client;

import com.kairos.activity.open_shift.PriorityGroupDefaultData;
import com.kairos.activity.shift.StaffUnitPositionDetails;
import com.kairos.enums.IntegrationOperation;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.access_group.UserAccessRoleDTO;
import com.kairos.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GenericIntegrationService {
    @Autowired
    GenericRestClient genericRestClient;
    @Autowired
    ExceptionService exceptionService;

    public Long getUnitPositionId(Long unitId, Long staffId, Long expertiseId, Long dateInMillis) {
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("dateInMillis", dateInMillis);
        Integer value = genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/staff/{staffId}/expertise/{expertiseId}/unitPositionId", queryParam, staffId, expertiseId);
        if (value == null) {
            exceptionService.dataNotFoundByIdException("message.unitPosition.notFound", expertiseId);
        }
        return value.longValue();
    }

    public PriorityGroupDefaultData getExpertiseAndEmployment(Long countryId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, countryId, false, IntegrationOperation.GET, "/basic_details/" + countryId + "/employment_type_and_expertise", null), PriorityGroupDefaultData.class);
    }

    public PriorityGroupDefaultData getExpertiseAndEmploymentForUnit(Long unitId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/employment_type_and_expertise", null), PriorityGroupDefaultData.class);
    }

    public List<StaffUnitPositionDetails> getStaffsUnitPosition(Long unitId, List<Long> staffIds, Long expertiseId) {
        List<StaffUnitPositionDetails> staffData = ObjectMapperUtils.copyPropertiesOfListByMapper(genericRestClient.publish(staffIds, unitId, true, IntegrationOperation.CREATE, "/expertise/{expertiseId}/unitPositions", null, expertiseId), StaffUnitPositionDetails.class);
        return staffData;
    }

    public List<String> getEmailsOfStaffByStaffIds(Long unitId, List<Long> staffIds) {
        return genericRestClient.publish(staffIds, unitId, true, IntegrationOperation.CREATE, "/staff/emails", null);
    }

    public List<StaffUnitPositionDetails> getStaffIdAndUnitPositionId(Long unitId, List<Long> staffIds, Long expertiseId) {
        List<StaffUnitPositionDetails> staffData = ObjectMapperUtils.copyPropertiesOfListByMapper(genericRestClient.publish(staffIds, unitId, true, IntegrationOperation.CREATE, "/expertise/{expertiseId}/staff_and_unit_positions", null, expertiseId), StaffUnitPositionDetails.class);
        return staffData;
    }

    public UserAccessRoleDTO getAccessRolesOfStaff(Long unitId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/staff/access_roles", null), UserAccessRoleDTO.class);
    }

    public DayTypeEmploymentTypeWrapper getDayTypesAndEmploymentTypes(Long countryId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, countryId, false, IntegrationOperation.GET, "/basic_details/" + countryId + "/day_types_and_employment_types", null), DayTypeEmploymentTypeWrapper.class);
    }

    public DayTypeEmploymentTypeWrapper getDayTypesAndEmploymentTypesAtUnit(Long unitId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/day_types_and_employment_types", null), DayTypeEmploymentTypeWrapper.class);
    }

    public List<StaffResultDTO> getStaffIdsByUserId(Long userId){
        List<StaffResultDTO> staffResultDTOS=genericRestClient.publish(null,userId,true,IntegrationOperation.GET,"/staff/get_staff_ids/user/{userId}",null,userId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(staffResultDTOS,StaffResultDTO.class);

    }
}