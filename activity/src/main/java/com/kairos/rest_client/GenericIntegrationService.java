package com.kairos.rest_client;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.access_group.StaffIdsDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeDTO;
import com.kairos.dto.activity.open_shift.PriorityGroupDefaultData;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.scheduler.SchedulerPanelDTO;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.dto.user.organization.UnitAndParentOrganizationAndCountryDTO;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

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
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, countryId, false, IntegrationOperation.GET, "/country/" + countryId + "/employment_type_and_expertise", null), PriorityGroupDefaultData.class);
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
        List<StaffUnitPositionDetails> staffData = ObjectMapperUtils.copyPropertiesOfListByMapper(genericRestClient.publish(staffIds, unitId, true,IntegrationOperation.CREATE, "/expertise/{expertiseId}/staff_and_unit_positions", null, expertiseId), StaffUnitPositionDetails.class);
        return staffData;
    }

    public UserAccessRoleDTO getAccessRolesOfStaff(Long unitId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/staff/access_roles", null), UserAccessRoleDTO.class);
    }

    public DayTypeEmploymentTypeWrapper getDayTypesAndEmploymentTypes(Long countryId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, countryId, false, IntegrationOperation.GET, "/country/" + countryId + "/day_types_and_employment_types", null), DayTypeEmploymentTypeWrapper.class);
    }

    public DayTypeEmploymentTypeWrapper getDayTypesAndEmploymentTypesAtUnit(Long unitId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, unitId, true,IntegrationOperation.GET, "/day_types_and_employment_types", null), DayTypeEmploymentTypeWrapper.class);
    }

    public List<StaffResultDTO> getStaffIdsByUserId(Long userId){
        List<StaffResultDTO> staffResultDTOS=genericRestClient.publish(null,null,false,IntegrationOperation.GET,"/user/{userId}/staffs",null,userId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(staffResultDTOS,StaffResultDTO.class);

    }

    public List<UnitAndParentOrganizationAndCountryDTO> getParentOrganizationAndCountryOfUnits() {
        return ObjectMapperUtils.copyPropertiesOfListByMapper(genericRestClient.publish(null, null, false, IntegrationOperation.GET, "/unit/parent_org_and_country", null), UnitAndParentOrganizationAndCountryDTO.class);
    }

    public List<KPIAccessPageDTO> getKPIEnabledTabsForModuleForCountry(Long countryId){
        return ObjectMapperUtils.copyPropertiesOfListByMapper(genericRestClient.publish(null, countryId, false, IntegrationOperation.GET, "/country/{countryId}/kpi_details", null,countryId), KPIAccessPageDTO.class);
    }

    public List<KPIAccessPageDTO> getKPIEnabledTabsForModuleForUnit(Long unitId){
        return ObjectMapperUtils.copyPropertiesOfListByMapper(genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/kpi_details", null), KPIAccessPageDTO.class);
    }

    public List<OrgTypeDTO> getOrganizationIdsBySubOrgId(List<Long> orgTypeId){
        return ObjectMapperUtils.copyPropertiesOfListByMapper(genericRestClient.publish(orgTypeId, null, false, IntegrationOperation.CREATE, "/orgtype/get_organization_ids", null),OrgTypeDTO.class);
    }
    public List<StaffIdsDTO> getStaffIdsByunitAndAccessGroupId(Long unitId, List<Long> accessGroupId){
        return ObjectMapperUtils.copyPropertiesOfListByMapper(genericRestClient.publish(accessGroupId,unitId,true,IntegrationOperation.CREATE,"/access_group/staffs",null),StaffIdsDTO.class);
    }

    public List<StaffDTO> getStaffDetailByIds(Long unitId, Set<Long> staffIds){
        return ObjectMapperUtils.copyPropertiesOfListByMapper(genericRestClient.publish(staffIds, unitId, true, IntegrationOperation.CREATE, "/staff/details", null), StaffDTO.class);
    }

    public Long getStaffIdByUserId(Long unitId) {
        Integer value = genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/user/staffId", null,Long.class);
        if (value == null) {
            exceptionService.dataNotFoundByIdException("message.staff.notFound");
        }
        return value.longValue();
    }


    public AccessGroupPermissionCounterDTO getAccessGroupIdsAndCountryAdmin(Long unitId){
            return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null,unitId,true,IntegrationOperation.GET,"/staff/user/accessgroup",null),AccessGroupPermissionCounterDTO.class);
    }

    public Long removeFunctionFromUnitPositionByDate(Long unitId,Long unitPositionId,Date shiftDate){
        BasicNameValuePair appliedDate=new BasicNameValuePair("appliedDate",DateUtils.asLocalDate(shiftDate).toString());
        Long functionId= genericRestClient.publishRequest(null,unitId, true, IntegrationOperation.DELETE, "/unit_position/{unitPositionId}/applyFunction", Collections.singletonList(appliedDate), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {},unitPositionId);
        return functionId;
    }

    public Boolean restoreFunctionFromUnitPositionByDate(Long unitId,Long unitPositionId,Map<Long,Set<LocalDate>> dateAndFunctionIdMap){
        return genericRestClient.publishRequest(dateAndFunctionIdMap,unitId, true, IntegrationOperation.CREATE, "/unit_position/{unitPositionId}/restore_functions",null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {},unitPositionId);

    }

    public Map<LocalDate,Long> removeFunctionFromUnitPositionByDates(Long unitId,Long unitPositionId,Set<LocalDate> dates){
         return genericRestClient.publishRequest(dates,unitId, true, IntegrationOperation.DELETE, "/unit_position/{unitPositionId}/remove_functions", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<LocalDate,Long>>>() {},unitPositionId);
         }

  /*  public SchedulerPanelDTO checkSchedulerLoadBalanceWorking() {

        return schedulerServiceRestClient.publishRequest(null,2567L,true,IntegrationOperation.GET,"/scheduler_panel/12",null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<SchedulerPanelDTO>>() {});
    }*/
 }