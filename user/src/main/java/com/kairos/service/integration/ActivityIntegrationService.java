package com.kairos.service.integration;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.ActivityWithTimeTypeDTO;
import com.kairos.dto.activity.activity.TableConfiguration;
import com.kairos.dto.activity.counter.DefaultKPISettingDTO;
import com.kairos.dto.activity.cta.CTAWTAAndAccumulatedTimebankWrapper;
import com.kairos.dto.activity.night_worker.NightWorkerGeneralResponseDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.unit_settings.TAndAGracePeriodSettingDTO;
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.user.employment.query_result.EmploymentLinesQueryResult;
import com.kairos.persistence.model.user.expertise.response.OrderAndActivityDTO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.constants.ApiConstants.GET_CTA_WTA_AND_ACCUMULATED_TIMEBANK_BY_UPIDS;
import static com.kairos.constants.ApiConstants.GET_CTA_WTA_BY_EXPERTISE;

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

    public List<ActivityDTO> getActivitiesWithCategories(long unitId) {
        return ObjectMapperUtils.copyPropertiesOfListByMapper(genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/activities_categories", null), ActivityDTO.class);
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

    public void deleteShiftsAfterEmploymentEndDate(Long unitId, LocalDate endDate, Long employmentId,StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("endDate", endDate);
        genericRestClient.publish(staffAdditionalInfoDTO, unitId, true, IntegrationOperation.UPDATE, "/delete_shifts/employment/" + employmentId, queryParams);
    }


    public void updateTimeBank(Long employmentId, LocalDate shiftStartDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        BasicNameValuePair startDate = new BasicNameValuePair("shiftStartDate", shiftStartDate.toString());
        BasicNameValuePair employment = new BasicNameValuePair("employmentId", employmentId + "");
        List<NameValuePair> param = new ArrayList<>();
        param.add(employment);
        param.add(startDate);
        genericRestClient.publishRequest(staffAdditionalInfoDTO, staffAdditionalInfoDTO.getUnitId(), true, IntegrationOperation.UPDATE, "/timeBank/update_time_bank", param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public void updateTimeBankOnEmploymentUpdation(BigInteger collectiveTimeAgreementId, Long employmentId, LocalDate employmentLineStartDate, LocalDate employmentLineEndDate, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        BasicNameValuePair startDate = new BasicNameValuePair("employmentLineStartDate", employmentLineStartDate.toString());
        BasicNameValuePair endDate = new BasicNameValuePair("employmentLineEndDate", employmentLineEndDate!=null?employmentLineEndDate.toString():null);
        BasicNameValuePair ctaId = new BasicNameValuePair("ctaId", collectiveTimeAgreementId.toString());
        List<NameValuePair> param = new ArrayList<>();
        param.add(startDate);
        param.add(endDate);
        param.add(ctaId);
        genericRestClient.publishRequest(staffAdditionalInfoDTO, staffAdditionalInfoDTO.getUnitId(), true, IntegrationOperation.UPDATE, "/timeBank/employment/{employmentId}/update_time_bank", param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        },employmentId);
    }

    public void createTimeTypes(Long countryId){
        restClientForSchedulerMessages.publish(null,countryId, false, IntegrationOperation.CREATE, "/timeType/default",null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public CTAWTAAndAccumulatedTimebankWrapper getCTAWTAAndAccumulatedTimebankByEmployment(Map<Long, List<EmploymentLinesQueryResult>> employmentLinesMap, Long unitId){
        return genericRestClient.publishRequest(employmentLinesMap, unitId, true, IntegrationOperation.CREATE, GET_CTA_WTA_AND_ACCUMULATED_TIMEBANK_BY_UPIDS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>>(){});
    }

    public CTAWTAAndAccumulatedTimebankWrapper getCTAWTAByExpertiseAndDate(Long expertiseId, Long unitId,LocalDate selectedDate,Long employmentId){
        List<NameValuePair> param = new ArrayList<>();
        param.add(new BasicNameValuePair("selectedDate", selectedDate.toString()));
        if(employmentId!=null){
            param.add(new BasicNameValuePair("employmentId", employmentId.toString()));
        }
        return genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_CTA_WTA_BY_EXPERTISE, param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>>(){}, expertiseId);
    }
  public boolean verifyTimeType(BigInteger timeTypeId,Long countryId){
      return genericRestClient.publishRequest(null,countryId,false,IntegrationOperation.GET,"/timeType/{timeTypeId}/verify/",null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {},timeTypeId);
    }

    public void updateNightWorkers(List<Map> employments) {
        genericRestClient.publishRequest(employments, null, false, IntegrationOperation.UPDATE, "/update_night_workers", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public Map<Long,Boolean> getNightWorkerDetails(StaffFilterDTO staffFilterDTO, Long unitId,LocalDate startDate,LocalDate endDate) {
        List<NameValuePair> param = null;
        if(isNotNull(startDate) && isNotNull(endDate)) {
            param = newArrayList(new BasicNameValuePair("startDate", startDate.toString()), new BasicNameValuePair("endDate", endDate.toString()));
        }
        return genericRestClient.publishRequest(staffFilterDTO, unitId, true, IntegrationOperation.CREATE, "/get_night_worker_details", param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<Long,Boolean>>>() {
        });
    }

    public List<TimeTypeDTO> getAllTimeType(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, false, IntegrationOperation.GET, "/timeType/", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeTypeDTO>>>() {
        });
    }


    public List<ActivityDTO> getAllAbsenceActivity(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/absence-activities/", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ActivityDTO>>>() {
        });
    }

    public List<PresenceTypeDTO> getAllPlannedTimeType(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, false, IntegrationOperation.GET, "/plannedTimeType", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<PresenceTypeDTO>>>() {
        });
    }



    public Long publishShiftCountWithEmploymentId(Long employmentId){

         return genericRestClient.publishRequest(null, employmentId, true, IntegrationOperation.GET, "/employment/{employmentId}/shift_count", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>(){}, employmentId);
    }

    public boolean isStaffNightWorker(Long unitId, Long staffId) {
        return genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/staff/{staffId}/night_worker_general", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<NightWorkerGeneralResponseDTO>>() {
        },staffId).isNightWorker();
    }

    public Boolean unlinkTagFromActivity(Long unitId, Long tagId) {
        return genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/tag/{tagId}/unlink", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>(){}, tagId);
    }
}

