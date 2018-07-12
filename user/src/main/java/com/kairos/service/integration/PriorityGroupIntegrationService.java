package com.kairos.service.integration;

import com.kairos.activity.activity.ActivityWithTimeTypeDTO;
import com.kairos.activity.unit_settings.TAndAGracePeriodSettingDTO;
import com.kairos.client.dto.TableConfiguration;
import com.kairos.enums.IntegrationOperation;
import com.kairos.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.persistence.model.user.expertise.Response.OrderAndActivityDTO;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
public class PriorityGroupIntegrationService {
    @Autowired
    GenericRestClient genericRestClient;

    public void createDefaultPriorityGroupsFromCountry(long countryId, long unitId) {
        Map<String, Object> countryDetail = new HashMap<>();
        countryDetail.put("countryId", countryId);
        genericRestClient.publish(null, unitId, true, IntegrationOperation.CREATE, "/priority_groups", countryDetail);
    }

    public OrderAndActivityDTO getAllOrderAndActivitiesByUnit(long unitId) {
        return ObjectMapperUtils.copyPropertiesByMapper(genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/orders_and_activities", null), OrderAndActivityDTO.class);
    }

    public void crateDefaultDataForOrganization(Long unitId, Long parentOrganizationId, Long countryId) {
        Map<String, Object> countryDetail = new HashMap<>();
        countryDetail.put("countryId", countryId);
        countryDetail.put("parentOrganizationId", parentOrganizationId);
        genericRestClient.publish(null, unitId, true, IntegrationOperation.CREATE, "/organization_default_data", countryDetail);
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
}

