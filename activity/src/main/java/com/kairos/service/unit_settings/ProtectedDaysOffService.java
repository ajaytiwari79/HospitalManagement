package com.kairos.service.unit_settings;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.unit_settings.ProtectedDaysOffSetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.unit_settings.ProtectedDaysOffRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getCutoffInterval;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created By G.P.Ranjan on 1/7/19
 **/
@Service
public class ProtectedDaysOffService extends MongoBaseService {
    @Inject
    private ProtectedDaysOffRepository protectedDaysOffRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private ShiftMongoRepository shiftMongoRepository;

    public ProtectedDaysOffSettingDTO saveProtectedDaysOff(Long unitId, ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings){
        ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO = new ProtectedDaysOffSettingDTO(unitId, protectedDaysOffUnitSettings);
        ProtectedDaysOffSetting protectedDaysOffSetting =protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOffSetting).isPresent()) {
            protectedDaysOffSetting = new ProtectedDaysOffSetting(protectedDaysOffSettingDTO.getId(), protectedDaysOffSettingDTO.getUnitId(), protectedDaysOffSettingDTO.getProtectedDaysOffUnitSettings());
            protectedDaysOffRepository.save(protectedDaysOffSetting);
        }
        protectedDaysOffSettingDTO.setId(protectedDaysOffSetting.getId());
        return protectedDaysOffSettingDTO;
    }

    public ProtectedDaysOffSettingDTO updateProtectedDaysOffByUnitId(Long unitId, ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO){
        ProtectedDaysOffSetting protectedDaysOffSetting =protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOffSetting).isPresent()) {
            exceptionService.dataNotFoundException("Data Not Found ", protectedDaysOffSettingDTO.getId());
        }
        protectedDaysOffSetting.setProtectedDaysOffUnitSettings(protectedDaysOffSettingDTO.getProtectedDaysOffUnitSettings());
        protectedDaysOffRepository.save(protectedDaysOffSetting);
        return protectedDaysOffSettingDTO;
    }

    public ProtectedDaysOffSettingDTO getProtectedDaysOffByUnitId(Long unitId){
        ProtectedDaysOffSetting protectedDaysOffSetting =protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOffSetting).isPresent()) {
            exceptionService.dataNotFoundException("Data Not Found ",unitId);
        }
        ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO =new ProtectedDaysOffSettingDTO(protectedDaysOffSetting.getId(), protectedDaysOffSetting.getUnitId(), protectedDaysOffSetting.getProtectedDaysOffUnitSettings());
        return protectedDaysOffSettingDTO;
    }

    public List<ProtectedDaysOffSettingDTO> getAllProtectedDaysOffByUnitIds(List<Long> unitIds){
        List<ProtectedDaysOffSetting> protectedDaysOffSettings =protectedDaysOffRepository.getAllProtectedDaysOffByUnitIdsAndDeletedFalse(unitIds);
        List<ProtectedDaysOffSettingDTO> protectedDaysOffSettingDTOS =new ArrayList<>();
        protectedDaysOffSettings.forEach(protectedDaysOffSetting -> {
            protectedDaysOffSettingDTOS.add(new ProtectedDaysOffSettingDTO(protectedDaysOffSetting.getId(), protectedDaysOffSetting.getUnitId(), protectedDaysOffSetting.getProtectedDaysOffUnitSettings()));
        });
        return protectedDaysOffSettingDTOS;
    }

    public Boolean createAutoProtectedDaysOffOfAllUnits(Long countryId){
        List<Long> units=userIntegrationService.getUnitIds(countryId);
        units.forEach(unit->{ saveProtectedDaysOff(unit,ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR);});
        return true;
    }

    public void updateProtectedDaysOffDetails(){
        List<Long> unitIds = new ArrayList<>();
        List<ProtectedDaysOffSetting> protectedDaysOffSettings = protectedDaysOffRepository.getAllProtectedDaysOffByUnitIdsAndDeletedFalse(unitIds);
        Map<Long,ProtectedDaysOffUnitSettings> unitIdProtectedDaysOffUnitSettingsMap = protectedDaysOffSettings.stream().collect(Collectors.toMap(k->k.getUnitId(), v->v.getProtectedDaysOffUnitSettings()));
        for (Long unitId : unitIds) {
            ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings = unitIdProtectedDaysOffUnitSettingsMap.get(unitId);
            switch (protectedDaysOffUnitSettings){
                case ONCE_IN_A_YEAR:updateProtectedDaysOffByOnceInAYear();
                    break;
                case ACTIVITY_CUT_OFF_INTERVAL:updateProtectedDaysOffByActivityCutOff();
                    break;
                case UPDATE_IN_TIMEBANK_ON_FIRST_DAY_OF_YEAR:updateProtectedDaysOffByFirstDayOfTheYear();
                    break;
                default:
                    break;
            }
        }
    }

    private void updateProtectedDaysOffByOnceInAYear(){
        if(LocalDate.now().equals(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()))){
            List<Activity> activities = activityMongoRepository.findAllBySecondLevelTimeType(TimeTypeEnum.PROTECTED_DAYS_OFF);
            List<BigInteger> activityIds = activities.stream().map(activity -> activity.getId()).collect(Collectors.toList());
            ZonedDateTime startDate = ZonedDateTime.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDate = startDate.with(TemporalAdjusters.lastDayOfYear()).plusDays(1);
            List<Shift> shifts = shiftMongoRepository.findAllShiftByActivityIdAndBetweenDuration(any(Long.class),asDate(startDate),asDate(endDate),activityIds);
           /* if(shifts.size()<publicHolidayCount){

            }*/
        }
    }

    private void updateProtectedDaysOffByActivityCutOff(){
        List<Activity> activities = activityMongoRepository.findAllBySecondLevelTimeType(TimeTypeEnum.PROTECTED_DAYS_OFF);
        for (Activity activity : activities) {
            DateTimeInterval dateTimeInterval = getCutoffInterval(activity.getRulesActivityTab().getCutOffStartFrom(), activity.getRulesActivityTab().getCutOffIntervalUnit(), activity.getRulesActivityTab().getCutOffdayValue(),asDate(LocalDate.now()));
            List<Shift> shifts = shiftMongoRepository.findAllShiftByActivityIdAndBetweenDuration(any(Long.class),dateTimeInterval.getStartDate(),dateTimeInterval.getEndDate(),newArrayList(activity.getId()));
        }
    }

    private void updateProtectedDaysOffByFirstDayOfTheYear(){
        if(LocalDate.now().equals(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()))){

        }
    }
}
