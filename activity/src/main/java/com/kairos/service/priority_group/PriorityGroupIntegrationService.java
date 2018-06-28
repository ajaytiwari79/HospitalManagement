package com.kairos.service.priority_group;

import com.kairos.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.rest_client.UserRestClient;
import com.kairos.user.staff.unit_position.StaffUnitPositionQueryResult;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
