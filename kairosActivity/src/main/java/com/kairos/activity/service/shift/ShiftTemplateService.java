package com.kairos.activity.service.shift;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.model.shift.ShiftDayTemplate;
import com.kairos.activity.persistence.model.shift.ShiftTemplate;
import com.kairos.activity.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.activity.persistence.repository.shift.ShiftDayTemplateMongoRepository;
import com.kairos.activity.persistence.repository.shift.ShiftTemplateRepository;
import com.kairos.activity.response.dto.shift.ShiftDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.activity.util.userContext.UserContext;
import com.kairos.response.dto.web.shift.ShiftDayTemplateDTO;
import com.kairos.response.dto.web.shift.ShiftTemplateDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class ShiftTemplateService extends MongoBaseService {

    @Inject private ShiftTemplateRepository shiftTemplateRepository;
    @Inject private ShiftDayTemplateMongoRepository shiftDayTemplateMongoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ShiftService shiftService;

    public ShiftTemplateDTO createShiftTemplate(Long unitId, ShiftTemplateDTO shiftTemplateDTO){
        List<ShiftDayTemplateDTO> shiftDayTemplateDTO=shiftTemplateDTO.getShifts();
        List<ShiftDayTemplate> shiftDayTemplates =ObjectMapperUtils.copyProperties(shiftDayTemplateDTO,ShiftDayTemplate.class);
        save(shiftDayTemplates);
        Set<BigInteger> shiftDayTemplateIds=new HashSet<>();
        shiftDayTemplates.forEach(shiftDayTemplate -> {shiftDayTemplateIds.add(shiftDayTemplate.getId());});
        ShiftTemplate shiftTemplate=Optional.ofNullable(shiftTemplateDTO.getId()).isPresent()?shiftTemplateRepository.findOneById(shiftTemplateDTO.getId()):
        new ShiftTemplate(shiftTemplateDTO.getId(),shiftTemplateDTO.getName(),shiftDayTemplateIds,unitId,UserContext.getUserDetails().getId());
        shiftTemplate.getShiftDayTemplateIds().addAll(shiftDayTemplateIds);
        shiftTemplate.setName(shiftTemplateDTO.getName());
        save(shiftTemplate);
        //Preparing DTO Object to return
        List<ShiftDayTemplateDTO> shiftDayTemplateDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(shiftDayTemplates,ShiftDayTemplateDTO.class);
        shiftTemplateDTO.setId(shiftTemplate.getId());
        shiftTemplateDTO.setShifts(shiftDayTemplateDTOS);
        return shiftTemplateDTO;
    }

    public List<ShiftTemplateDTO> getAllShiftTemplates(Long unitId){
        List<ShiftTemplate> shiftTemplate= shiftTemplateRepository.findAllByUnitIdAndCreatedByAndDeletedFalse(unitId,UserContext.getUserDetails().getId());
        List<ShiftTemplateDTO> shiftTemplateDTOS=new ArrayList<>();
        shiftTemplate.forEach(shiftTemplateDTO -> {
            List<ShiftDayTemplateDTO> shiftDayTemplateDTOS=shiftDayTemplateMongoRepository.findAllByIdInAndDeletedFalse(shiftTemplateDTO.getShiftDayTemplateIds());
            ShiftTemplateDTO shiftTemplateDTO1=new ShiftTemplateDTO(shiftTemplateDTO.getId(),shiftTemplateDTO.getName(),shiftDayTemplateDTOS,shiftTemplateDTO.getCreatedBy(),shiftTemplateDTO.getUnitId());
            shiftTemplateDTOS.add(shiftTemplateDTO1);
        });
        return shiftTemplateDTOS;

    }

    public boolean deleteShiftTemplate(BigInteger shiftTemplateId){
        Optional<ShiftTemplate> shiftTemplate=shiftTemplateRepository.findById(shiftTemplateId);
        if(!shiftTemplate.isPresent() || shiftTemplate.get().isDeleted()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent",shiftTemplateId);
        }
        List<ShiftDayTemplate> shiftDayTemplates=shiftDayTemplateMongoRepository.getAllByIdInAndDeletedFalse(shiftTemplate.get().getShiftDayTemplateIds());
        shiftDayTemplates.forEach(shiftDayTemplate -> {shiftDayTemplate.setDeleted(true);});
        save(shiftDayTemplates);
        shiftTemplate.get().setDeleted(true);
        save(shiftTemplate.get());
        return true;

    }

    public ShiftDayTemplateDTO updateShiftDayTemplate(BigInteger shiftDayTemplateId,ShiftDayTemplateDTO shiftDayTemplateDTO){
        Optional<ShiftDayTemplate> shiftDayTemplate= shiftDayTemplateMongoRepository.findById(shiftDayTemplateId);
        if(!shiftDayTemplate.isPresent()|| shiftDayTemplate.get().isDeleted()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent", shiftDayTemplateId);
        }
        shiftDayTemplateDTO.setId(shiftDayTemplate.get().getId());
        BeanUtils.copyProperties(shiftDayTemplateDTO,shiftDayTemplate.get());
        save(shiftDayTemplate.get());
        return shiftDayTemplateDTO;
    }

    public boolean createShiftUsingTemplate(Long unitId, BigInteger shiftTemplateId, Long staffId, Long unitPositionId, LocalDate startDate,LocalDate endDate){
        ShiftTemplate shiftTemplate=shiftTemplateRepository.findOneById(shiftTemplateId);
        List<ShiftDayTemplate> shiftDayTemplate=shiftDayTemplateMongoRepository.getAllByIdInAndDeletedFalse(shiftTemplate.getShiftDayTemplateIds());
        shiftDayTemplate.forEach(shiftTemplate1 ->{
            ShiftDTO shiftDTO=new ShiftDTO(shiftTemplate1.getActivityId(),unitId,staffId,unitPositionId,startDate,endDate,shiftTemplate1.getStartTime(),shiftTemplate1.getEndTime());
            shiftService.createShift(unitId,shiftDTO,"Organization",false);
        });
        return true;
    }

    //
//    public List<ShiftDayTemplateDTO> getAllShiftTemplatesByStaffId(Long unitId, Long staffId){
//        return shiftDayTemplateMongoRepository.getAllShiftTemplatesByStaffId(unitId,staffId);
//    }
//
//
//
//    public boolean deleteShiftTemplate(Long unitId, BigInteger shiftTemplateId){
//        ShiftDayTemplate shiftDayTemplate = shiftDayTemplateMongoRepository.findByIdAndUnitIdAndDeletedFalse(shiftTemplateId,unitId);
//        if(!Optional.ofNullable(shiftDayTemplate).isPresent()){
//            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent",shiftTemplateId);
//        }
//        shiftDayTemplate.setDeleted(true);
//        save(shiftDayTemplate);
//        return true;
//
//    }

}
