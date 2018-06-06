package com.kairos.activity.service.unit_settings;

import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.persistence.model.unit_settings.TAndAGracePeriod;
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

//   public TAndAGracePeriod createDefaultGracePeriodSetting(Long unitId) {
//       TAndAGracePeriod tAndAGracePeriod=new TAndAGracePeriod(unitId, AppConstants.GRACE_PERIOD_DAYS);
//       save(tAndAGracePeriod);
//       return tAndAGracePeriod;
//   }

   public TAndAGracePeriodSettingDTO getTAndAGracePeriodSetting(Long unitId){
       TAndAGracePeriod tAndAGracePeriod=tAndAGracePeriodRepository.findByUnitId(unitId);
        if(!Optional.ofNullable(tAndAGracePeriod).isPresent()){
            exceptionService.dataNotFoundByIdException("message.unit.graceperiod.notFound",unitId);
        }
        TAndAGracePeriodSettingDTO tAndAGracePeriodSettingDTO=new TAndAGracePeriodSettingDTO(tAndAGracePeriod.getGracePeriodDays());
       return tAndAGracePeriodSettingDTO;
   }

   public TAndAGracePeriodSettingDTO updateTAndAGracePeriodSetting(Long unitId,TAndAGracePeriodSettingDTO tAndAGracePeriodSettingDTO){
        TAndAGracePeriod tAndAGracePeriod=tAndAGracePeriodRepository.findByUnitId(unitId);
        if(!Optional.ofNullable(tAndAGracePeriod).isPresent()){
            tAndAGracePeriod=new TAndAGracePeriod();
            tAndAGracePeriod.setUnitId(unitId);
        }
        tAndAGracePeriod.setGracePeriodDays(tAndAGracePeriodSettingDTO.getGracePeriodDays());
       save(tAndAGracePeriod);
       return tAndAGracePeriodSettingDTO;
   }

}
