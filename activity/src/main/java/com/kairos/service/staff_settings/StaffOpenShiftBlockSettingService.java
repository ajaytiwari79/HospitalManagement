package com.kairos.service.staff_settings;
/*
 *Created By Pavan on 17/8/18
 *
 */

import com.kairos.dto.user.staff.staff.StaffPreferencesDTO;
import com.kairos.persistence.model.staff_settings.StaffOpenShiftBlockSetting;
import com.kairos.persistence.repository.staff_settings.StaffOpenShiftBlockSettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;

import static java.time.temporal.TemporalAdjusters.previousOrSame;

@Service
public class StaffOpenShiftBlockSettingService extends MongoBaseService {

    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private StaffOpenShiftBlockSettingRepository staffOpenShiftBlockSettingRepository;
    @Inject private ExceptionService exceptionService;

    public boolean savePersonalizedSettings(Long unitId, StaffPreferencesDTO staffPreferencesDTO) {
        Long staffId= userIntegrationService.getStaffIdByUserId(unitId);
        StaffOpenShiftBlockSetting staffOpenShiftBlockSetting=staffOpenShiftBlockSettingRepository.findByStaffId(staffId).orElse(new StaffOpenShiftBlockSetting(staffId));

        switch (staffPreferencesDTO.getShiftBlockType()) {
            case SHIFT:
                staffOpenShiftBlockSetting.getActivityIds().add(staffPreferencesDTO.getActivityId());
                break;
            case DAY:
                staffOpenShiftBlockSetting.getDateForDay().add(staffPreferencesDTO.getStartDate());
                break;
            case WEEK:
                staffOpenShiftBlockSetting.getDateForWeek().add(staffPreferencesDTO.getStartDate().with(previousOrSame(DayOfWeek.MONDAY)));
                break;
            default:
                exceptionService.actionNotPermittedException("exception.no.block.type.found");
        }
        save(staffOpenShiftBlockSetting);
        return true;
    }
}
