package com.kairos.activity.service.unit_settings;

import com.kairos.activity.persistence.model.unit_settings.TimeAttendanceGracePeriod;
import com.kairos.activity.persistence.repository.unit_settings.TAndAGracePeriodRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.response.dto.web.unit_settings.TAndAGracePeriodSettingDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

@Service
public class TAndAGracePeriodService extends MongoBaseService {

   @Inject
   private ExceptionService exceptionService;

   @Inject
    private TAndAGracePeriodRepository tAndAGracePeriodRepository;


   public TAndAGracePeriodSettingDTO getTAndAGracePeriodSetting(Long unitId){
       TimeAttendanceGracePeriod tAndAGracePeriod=tAndAGracePeriodRepository.findByUnitId(unitId);
        if(!Optional.ofNullable(tAndAGracePeriod).isPresent()){
            exceptionService.dataNotFoundByIdException("message.unit.graceperiod.notFound",unitId);
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
