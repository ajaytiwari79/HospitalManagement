package com.kairos.service.integration;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityWithTimeTypeDTO;
import com.kairos.dto.activity.activity.TableConfiguration;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.activity.unit_settings.TAndAGracePeriodSettingDTO;
import com.kairos.dto.activity.wta.CTAWTAResponseDTO;
import com.kairos.dto.user.employment.UnitPositionIdDTO;
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.user.expertise.Response.OrderAndActivityDTO;
import com.kairos.rest_client.RestClientForSchedulerMessages;
import com.kairos.rest_client.priority_group.GenericRestClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
@Transactional
public class ActivityIntegrationService {
    @Inject
    GenericRestClient genericRestClient;
    @Inject
    RestClientForSchedulerMessages restClientForSchedulerMessages;
    private Logger logger = LoggerFactory.getLogger(ActivityIntegrationService.class);

    public void createDefaultPriorityGroupsFromCountry(long countryId, long unitId) {
        Map<String, Object> countryDetail = new HashMap<>();
        countryDetail.put("countryId", countryId);
        genericRestClient.publish(null, unitId, true, IntegrationOperation.CREATE, "/priority_groups", countryDetail);
    }

    public OrderAndActivityDTO getAllOrderAndActivitiesByUnit(long unitId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/orders_and_activities", null), OrderAndActivityDTO.class);
    }

    public void crateDefaultDataForOrganization(Long unitId, Long parentOrganizationId, OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO) {

        genericRestClient.publish(orgTypeAndSubTypeDTO, unitId, true, IntegrationOperation.CREATE, "/organization_default_data", null);
    }

    public ActivityWithTimeTypeDTO getAllActivitiesAndTimeTypes(long countryId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, countryId, false, IntegrationOperation.GET, "/activities_with_time_types", null), ActivityWithTimeTypeDTO.class);
    }

    public ActivityWithTimeTypeDTO getAllActivitiesAndTimeTypesByUnit(Long unitId, Long countryId) {
        Map<String, Object> countryDetail = new HashMap<>();
        countryDetail.put("countryId", countryId);
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/activities_with_time_types", countryDetail), ActivityWithTimeTypeDTO.class);
    }

    public void createDefaultOpenShiftRuleTemplate(OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO, long unitId) {
        genericRestClient.publish(orgTypeAndSubTypeDTO, unitId, true, IntegrationOperation.CREATE, "/open_shift/copy_rule_template", null);
    }


    public void createDefaultGracePeriodSetting(TAndAGracePeriodSettingDTO tAndAGracePeriodSettingDTO, Long unitId) {
        genericRestClient.publish(tAndAGracePeriodSettingDTO, unitId, true, IntegrationOperation.CREATE, "/grace_period_setting", null);
    }

    public TableConfiguration getTableSettings(Long unitId, BigInteger tableSettingsId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/table_settings/" + tableSettingsId, null), TableConfiguration.class);
    }

    public void createDefaultKPISetting(DefaultKPISettingDTO defaultKPISettingDTO, Long unitId) {
        genericRestClient.publish(defaultKPISettingDTO, unitId, true, IntegrationOperation.CREATE, "/counter/dist/default_kpi_setting", null);
    }

    public void createDefaultKPISettingForStaff(DefaultKPISettingDTO defaultKPISettingDTO, Long unitId) {
        genericRestClient.publish(defaultKPISettingDTO, unitId, true, IntegrationOperation.CREATE, "/counter/dist/staff_default_kpi_setting", null);
    }

    public void deleteShiftsAndOpenShift(Long unitId, Long staffId, LocalDateTime employmentEndDate) {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        DateUtils.asDate(employmentEndDate);
        queryParams.put("employmentEndDate", DateUtils.asDate(employmentEndDate).getTime());
        restClientForSchedulerMessages.publish(null, unitId, true, IntegrationOperation.UPDATE, "/staff/" + staffId + "/shifts_and_openshifts", queryParams);
    }

    public void deleteShiftsAfterEmploymentEndDate(Long unitId, LocalDate endDate, Long staffId) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("endDate", endDate);
        genericRestClient.publish(null, unitId, true, IntegrationOperation.DELETE, "/delete_shifts/staff/" + staffId, queryParams);
    }

    public List<CTAWTAResponseDTO> copyWTACTA(List<UnitPositionIdDTO> unitPositionIdDTOS) {


        List<CTAWTAResponseDTO> ctawtaResponseDTOS = restClientForSchedulerMessages.publishRequest(unitPositionIdDTOS, null, false, IntegrationOperation.CREATE, "copy_wta_cta", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<CTAWTAResponseDTO>>>() {
        });

        return ctawtaResponseDTOS;
    }

    public void updateTimeBank(Long unitPositionId, LocalDate shiftStartDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        BasicNameValuePair startDate = new BasicNameValuePair("shiftStartDate", shiftStartDate.toString());
        BasicNameValuePair unitPosition = new BasicNameValuePair("unitPositionId", unitPositionId + "");
        List<NameValuePair> param = new ArrayList<>();
        param.add(unitPosition);
        param.add(startDate);
        genericRestClient.publishRequest(staffAdditionalInfoDTO, staffAdditionalInfoDTO.getUnitId(), true, IntegrationOperation.UPDATE, "/timeBank/update_time_bank", param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public void updateTimeBankOnUnitPositionUpdation(BigInteger collectiveTimeAgreementId,Long unitPositionId, LocalDate unitPositionLineStartDate, LocalDate unitPositionLineEndDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        BasicNameValuePair startDate = new BasicNameValuePair("unitPositionLineStartDate", unitPositionLineStartDate.toString());
        BasicNameValuePair endDate = new BasicNameValuePair("unitPositionLineEndDate", unitPositionLineEndDate!=null?unitPositionLineEndDate.toString():null);
        BasicNameValuePair ctaId = new BasicNameValuePair("ctaId", collectiveTimeAgreementId.toString());
        List<NameValuePair> param = new ArrayList<>();
        param.add(startDate);
        param.add(endDate);
        param.add(ctaId);
        genericRestClient.publishRequest(staffAdditionalInfoDTO, staffAdditionalInfoDTO.getUnitId(), true, IntegrationOperation.UPDATE, "/timeBank/unit_position/{unitPositionId}/update_time_bank", param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        },unitPositionId);
    }

    public void createTimeTypes(Long countryId){
        restClientForSchedulerMessages.publish(null,countryId, false, IntegrationOperation.CREATE, "/timeType/default",null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public void updateActivities(Long expertiseId,Long updatedExpertiseId,Long countryId){
        genericRestClient.publishRequest(null,countryId,false,IntegrationOperation.UPDATE,"/update_expertise/{expertiseId}",null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {},expertiseId);
    }

}

