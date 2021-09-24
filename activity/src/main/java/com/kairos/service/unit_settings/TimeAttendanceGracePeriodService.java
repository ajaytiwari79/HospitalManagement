package com.kairos.service.unit_settings;

import com.kairos.dto.activity.unit_settings.TAndAGracePeriodSettingDTO;
import com.kairos.persistence.model.unit_settings.TimeAttendanceGracePeriod;
import com.kairos.persistence.repository.unit_settings.TimeAttendanceGracePeriodRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_UNIT_GRACEPERIOD_NOTFOUND;

@Service
public class TimeAttendanceGracePeriodService extends MongoBaseService {

   @Inject
   private ExceptionService exceptionService;

   @Inject
    private TimeAttendanceGracePeriodRepository tAndAGracePeriodRepository;


   public TAndAGracePeriodSettingDTO getTAndAGracePeriodSetting(Long unitId){
       TimeAttendanceGracePeriod tAndAGracePeriod=tAndAGracePeriodRepository.findByUnitId(unitId);
        if(!Optional.ofNullable(tAndAGracePeriod).isPresent()){
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_GRACEPERIOD_NOTFOUND,unitId);
        }
        TAndAGracePeriodSettingDTO tAndAGracePeriodSettingDTO=new TAndAGracePeriodSettingDTO(tAndAGracePeriod.getStaffGracePeriodDays(),tAndAGracePeriod.getManagementGracePeriodDays());
       return tAndAGracePeriodSettingDTO;
   }

   public TAndAGracePeriodSettingDTO updateTAndAGracePeriodSetting(Long unitId,TAndAGracePeriodSettingDTO tAndAGracePeriodSettingDTO){
        TimeAttendanceGracePeriod tAndAGracePeriod=tAndAGracePeriodRepository.findByUnitId(unitId);
        if(!Optional.ofNullable(tAndAGracePeriod).isPresent()){
            tAndAGracePeriod=new TimeAttendanceGracePeriod(unitId);
        }
        tAndAGracePeriod.setStaffGracePeriodDays(tAndAGracePeriodSettingDTO.getStaffGracePeriodDays());
        tAndAGracePeriod.setManagementGracePeriodDays(tAndAGracePeriodSettingDTO.getManagementGracePeriodDays());
        save(tAndAGracePeriod);
       return tAndAGracePeriodSettingDTO;
   }

}
