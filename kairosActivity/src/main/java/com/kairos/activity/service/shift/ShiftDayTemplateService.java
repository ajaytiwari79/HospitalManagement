package com.kairos.activity.service.shift;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.shift.ShiftDayTemplate;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.shift.ShiftDayTemplateMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.shift.ShiftDayTemplateDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShiftDayTemplateService extends MongoBaseService {
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ShiftDayTemplateMongoRepository shiftDayTemplateMongoRepository;

    public ShiftDayTemplateDTO createShiftTemplate(Long unitId, ShiftDayTemplateDTO shiftDayTemplateDTO){
        Activity activity = activityMongoRepository.findActivityByIdAndEnabled(shiftDayTemplateDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftDayTemplateDTO.getActivityId());
        }
        ShiftDayTemplate shiftDayTemplate =ObjectMapperUtils.copyPropertiesByMapper(shiftDayTemplateDTO,ShiftDayTemplate.class);
        shiftDayTemplate.setUnitId(unitId);
        save(shiftDayTemplate);
        shiftDayTemplateDTO.setId(shiftDayTemplate.getId());
        return shiftDayTemplateDTO;
    }

    public List<ShiftDayTemplateDTO> getAllShiftTemplates(Long unitId){
        return shiftDayTemplateMongoRepository.findAllByUnitId(unitId);
    }

    public List<ShiftDayTemplateDTO> getAllShiftTemplatesByStaffId(Long unitId, Long staffId){
        return shiftDayTemplateMongoRepository.getAllShiftTemplatesByStaffId(unitId,staffId);
    }


    public ShiftDayTemplateDTO updateShiftTemplate(Long unitId, ShiftDayTemplateDTO shiftDayTemplateDTO){
        Optional<ShiftDayTemplate> shiftTemplate= shiftDayTemplateMongoRepository.findById(shiftDayTemplateDTO.getId());
        if(!shiftTemplate.isPresent()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent", shiftDayTemplateDTO.getId());
        }
        ObjectMapperUtils.copyProperties(shiftDayTemplateDTO,shiftTemplate.get());
        shiftTemplate.get().setUnitId(unitId);
        save(shiftTemplate.get());
        return shiftDayTemplateDTO;
    }

    public boolean deleteShiftTemplate(Long unitId, BigInteger shiftTemplateId){
        ShiftDayTemplate shiftDayTemplate = shiftDayTemplateMongoRepository.findByIdAndUnitIdAndDeletedFalse(shiftTemplateId,unitId);
        if(!Optional.ofNullable(shiftDayTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent",shiftTemplateId);
        }
        shiftDayTemplate.setDeleted(true);
        save(shiftDayTemplate);
        return true;

    }

}
