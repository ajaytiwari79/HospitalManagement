package com.kairos.activity.service.shift;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.shift.ShiftTemplate;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.shift.ShiftTemplateMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.shift.ShiftTemplateDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShiftTemplateService extends MongoBaseService {
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ShiftTemplateMongoRepository shiftTemplateMongoRepository;

    public ShiftTemplateDTO createShiftTemplate(Long unitId,ShiftTemplateDTO shiftTemplateDTO){
        Activity activity = activityMongoRepository.findActivityByIdAndEnabled(shiftTemplateDTO.getActivityId());
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.activity.id", shiftTemplateDTO.getActivityId());
        }
        ShiftTemplate shiftTemplate=ObjectMapperUtils.copyPropertiesByMapper(shiftTemplateDTO,ShiftTemplate.class);
        shiftTemplate.setUnitId(unitId);
        save(shiftTemplate);
        shiftTemplateDTO.setId(shiftTemplate.getId());
        return shiftTemplateDTO;
    }

    public List<ShiftTemplateDTO> getAllShiftTemplates(Long unitId){
        return shiftTemplateMongoRepository.findAllByUnitId(unitId);
    }

    public List<ShiftTemplateDTO> getAllShiftTemplatesByStaffId(Long unitId,Long staffId){
        return shiftTemplateMongoRepository.getAllShiftTemplatesByStaffId(unitId,staffId);
    }


    public ShiftTemplateDTO updateShiftTemplate(Long unitId,ShiftTemplateDTO shiftTemplateDTO){
        Optional<ShiftTemplate> shiftTemplate=shiftTemplateMongoRepository.findById(shiftTemplateDTO.getId());
        if(!shiftTemplate.isPresent()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent",shiftTemplateDTO.getId());
        }
        ObjectMapperUtils.copyProperties(shiftTemplateDTO,shiftTemplate.get());
        shiftTemplate.get().setUnitId(unitId);
        save(shiftTemplate.get());
        return shiftTemplateDTO;
    }

    public boolean deleteShiftTemplate(Long unitId, BigInteger shiftTemplateId){
        ShiftTemplate shiftTemplate=shiftTemplateMongoRepository.findByIdAndUnitIdAndDeletedFalse(shiftTemplateId,unitId);
        if(!Optional.ofNullable(shiftTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent",shiftTemplateId);
        }
        shiftTemplate.setDeleted(true);
        save(shiftTemplate);
        return true;

    }

}
