package com.kairos.activity.service.priority_group;

import com.kairos.activity.client.UserRestClient;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.StaffUnitPositionQueryResult;
import com.kairos.response.dto.web.open_shift.priority_group.StaffIncludeFilter;
import com.kairos.response.dto.web.open_shift.priority_group.StaffIncludeFilterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
@Transactional
public class PriorityGroupIntegrationService {
    @Autowired
    UserRestClient userRestClient;


    public List<StaffUnitPositionQueryResult> getStaffIdsByPriorityGroupIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId) {

        List<StaffUnitPositionQueryResult> staffsUnitPositions = userRestClient.publish(staffIncludeFilterDTO,unitId,24L,true,IntegrationOperation.CREATE,"/staff/priority_group",null);

        return ObjectMapperUtils.copyPropertiesOfListByMapper(staffsUnitPositions,StaffUnitPositionQueryResult.class);
    }

}
