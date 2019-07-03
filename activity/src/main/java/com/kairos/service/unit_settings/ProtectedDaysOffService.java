package com.kairos.service.unit_settings;

import com.kairos.dto.activity.unit_settings.ProtectedDaysOffDTO;
import com.kairos.dto.user.country.day_type.PublicHoliday;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.unit_settings.ProtectedDaysOff;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.unit_settings.ProtectedDaysOffRepository;
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
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private ShiftMongoRepository shiftMongoRepository;

    public ProtectedDaysOffDTO saveProtectedDaysOff(Long unitId, ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings){
        ProtectedDaysOffDTO protectedDaysOffDTO = new ProtectedDaysOffDTO(unitId, protectedDaysOffUnitSettings);
        ProtectedDaysOff protectedDaysOff=protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOff).isPresent()) {
            protectedDaysOff = new ProtectedDaysOff(protectedDaysOffDTO.getId(), protectedDaysOffDTO.getUnitId(), protectedDaysOffDTO.getProtectedDaysOffUnitSettings());
            protectedDaysOffRepository.save(protectedDaysOff);
        }
        protectedDaysOffDTO.setId(protectedDaysOff.getId());
        return protectedDaysOffDTO;
    }

    public ProtectedDaysOffDTO updateProtectedDaysOffByUnitId(Long unitId, ProtectedDaysOffDTO protectedDaysOffDTO){
        ProtectedDaysOff protectedDaysOff=protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOff).isPresent()) {
            exceptionService.dataNotFoundException("Data Not Found ",protectedDaysOffDTO.getId());
        }
        protectedDaysOff.setProtectedDaysOffUnitSettings(protectedDaysOffDTO.getProtectedDaysOffUnitSettings());
        protectedDaysOffRepository.save(protectedDaysOff);
        return protectedDaysOffDTO;
    }

    public ProtectedDaysOffDTO getProtectedDaysOffByUnitId(Long unitId){
        ProtectedDaysOff protectedDaysOff=protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOff).isPresent()) {
            exceptionService.dataNotFoundException("Data Not Found ",unitId);
        }
        ProtectedDaysOffDTO protectedDaysOffDTO=new ProtectedDaysOffDTO(protectedDaysOff.getId(),protectedDaysOff.getUnitId(),protectedDaysOff.getProtectedDaysOffUnitSettings());
        return protectedDaysOffDTO;
    }

    public List<ProtectedDaysOffDTO> getAllProtectedDaysOffByUnitIds(List<Long> unitIds){
        List<ProtectedDaysOff> protectedDaysOffs=protectedDaysOffRepository.getAllProtectedDaysOffByUnitIdsAndDeletedFalse(unitIds);
        List<ProtectedDaysOffDTO> protectedDaysOffDTOS=new ArrayList<>();
        protectedDaysOffs.forEach(protectedDaysOff -> {
            protectedDaysOffDTOS.add(new ProtectedDaysOffDTO(protectedDaysOff.getId(),protectedDaysOff.getUnitId(),protectedDaysOff.getProtectedDaysOffUnitSettings()));
        });
        return protectedDaysOffDTOS;
    }

    public void updateProtectedDaysOffDetails(){
        List<Long> unitIds = new ArrayList<>();
        List<ProtectedDaysOff> protectedDaysOffs = protectedDaysOffRepository.getAllProtectedDaysOffByUnitIdsAndDeletedFalse(unitIds);
        Map<Long,ProtectedDaysOffUnitSettings> unitIdProtectedDaysOffUnitSettingsMap = protectedDaysOffs.stream().collect(Collectors.toMap(k->k.getUnitId(),v->v.getProtectedDaysOffUnitSettings()));
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
       /* if(LocalDate.now().equals(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()))){
            List<Activity> activities = activityMongoRepository.findAllBySecondLevelTimeType(TimeTypeEnum.PROTECTED_DAYS_OFF);
            List<BigInteger> activityIds = activities.stream().map(activity -> activity.getId()).collect(Collectors.toList());
            ZonedDateTime startDate = ZonedDateTime.now().minusYears(1).with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDate = startDate.with(TemporalAdjusters.lastDayOfYear()).plusDays(1);
            List<Shift> shifts = shiftMongoRepository.findAllShiftByActivityIdAndBetweenDuration(any(Long.class),asDate(startDate),asDate(endDate),activityIds);
            if(shifts.size()<publicHolidayCount){

            }
        }*/
    }

    private void updateProtectedDaysOffByActivityCutOff(){

    }

    private void updateProtectedDaysOffByFirstDayOfTheYear(){
        if(LocalDate.now().equals(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()))){

        }
    }
}
