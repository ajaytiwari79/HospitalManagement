package com.kairos.service.organization;

import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.wrapper.shift.ShiftsAndPlanningSettingsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Set;

@Service
public class ShiftPlanningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftPlanningService.class);
    @Inject
    private UserIntegrationService userIntegrationService;

    public ShiftsAndPlanningSettingsDTO getShiftPlanningDetailsForUnit(long unitId){
        Set<StaffPersonalDetail> staffListWithPersonalDetails = userIntegrationService.getAllPlanningStaffForUnit(unitId);
        LOGGER.debug("staff found for planning are {}", staffListWithPersonalDetails  );

        return null;
    }


}
