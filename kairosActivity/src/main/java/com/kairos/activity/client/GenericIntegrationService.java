package com.kairos.activity.client;

import com.google.inject.Inject;
import com.kairos.activity.client.dto.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.response.dto.shift.StaffUnitPositionDetails;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.open_shift.PriorityGroupDefaultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GenericIntegrationService {
    @Autowired
    GenericRestClient genericRestClient;
    @Inject
    private ExceptionService exceptionService;

    public Long getUnitPositionId(Long unitId, Long staffId, Long expertiseId) {
        Integer value = genericRestClient.publish(null, unitId, true, IntegrationOperation.GET, "/staff/{staffId}/expertise/{expertiseId}/unitPositionId", null, staffId, expertiseId);
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
        List<StaffUnitPositionDetails> staffData = genericRestClient.publish(staffIds, unitId, true, IntegrationOperation.CREATE, "/expertise/{expertiseId}/unitPositions", null, expertiseId);

        return  staffData;
    }
}