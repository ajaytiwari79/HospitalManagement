package com.kairos.activity.service.shift;

import com.kairos.activity.persistence.model.shift.IndividualShiftTemplate;
import com.kairos.activity.persistence.model.shift.ShiftTemplate;
import com.kairos.activity.persistence.repository.shift.IndividualShiftTemplateMongoRepository;
import com.kairos.activity.persistence.repository.shift.ShiftTemplateRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.activity.util.userContext.UserContext;
import com.kairos.response.dto.web.shift.IndividualShiftTemplateDTO;
import com.kairos.response.dto.web.shift.ShiftTemplateDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
@Transactional
public class ShiftTemplateService extends MongoBaseService {

    @Inject private ShiftTemplateRepository shiftTemplateRepository;
    @Inject private IndividualShiftTemplateMongoRepository individualShiftTemplateMongoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ShiftService shiftService;

    public ShiftTemplateDTO createShiftTemplate(Long unitId, ShiftTemplateDTO shiftTemplateDTO){
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOs =shiftTemplateDTO.getShiftList();
        List<IndividualShiftTemplate> individualShiftTemplates =ObjectMapperUtils.copyProperties(individualShiftTemplateDTOs,IndividualShiftTemplate.class);
        save(individualShiftTemplates);
        Set<BigInteger> individualShiftTemplateIds=new HashSet<>();
        individualShiftTemplates.forEach(individualShiftTemplate -> {individualShiftTemplateIds.add(individualShiftTemplate.getId());});
        ShiftTemplate shiftTemplate=new ShiftTemplate(shiftTemplateDTO.getName(),individualShiftTemplateIds,unitId,UserContext.getUserDetails().getId());
        save(shiftTemplate);
       //Preparing DTO Object to return
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOS =ObjectMapperUtils.copyPropertiesOfListByMapper(individualShiftTemplates,IndividualShiftTemplateDTO.class);
        shiftTemplateDTO.setId(shiftTemplate.getId());
        shiftTemplateDTO.setShiftList(individualShiftTemplateDTOS);
        return shiftTemplateDTO;
    }

    public List<ShiftTemplateDTO> getAllShiftTemplates(Long unitId){
        List<ShiftTemplate> shiftTemplates= shiftTemplateRepository.findAllByUnitIdAndCreatedByAndDeletedFalse(unitId,UserContext.getUserDetails().getId());
        List<ShiftTemplateDTO> shiftTemplateDTOS=new ArrayList<>();
        shiftTemplates.forEach(shiftTemplateDTO -> {
            List<IndividualShiftTemplateDTO> individualShiftTemplateDTOS = individualShiftTemplateMongoRepository.findAllByIdInAndDeletedFalse(shiftTemplateDTO.getIndividualShiftTemplateIds());
            ShiftTemplateDTO shiftTemplateDTO1=new ShiftTemplateDTO(shiftTemplateDTO.getId(),shiftTemplateDTO.getName(), individualShiftTemplateDTOS,shiftTemplateDTO.getCreatedBy(),shiftTemplateDTO.getUnitId());
            shiftTemplateDTOS.add(shiftTemplateDTO1);
        });
        return shiftTemplateDTOS;

    }

    public ShiftTemplateDTO updateShiftTemplate(BigInteger shiftTemplateId, ShiftTemplateDTO shiftTemplateDTO){
        Optional<ShiftTemplate> shiftTemplate= shiftTemplateRepository.findById(shiftTemplateId);
        if(!shiftTemplate.isPresent()|| shiftTemplate.get().isDeleted()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent", shiftTemplateId);
        }
        shiftTemplate.get().setName(shiftTemplateDTO.getName());
        save(shiftTemplate.get());
        return shiftTemplateDTO;
    }

    public boolean deleteShiftTemplate(BigInteger shiftTemplateId){
        Optional<ShiftTemplate> shiftTemplate=shiftTemplateRepository.findById(shiftTemplateId);
        if(!shiftTemplate.isPresent() || shiftTemplate.get().isDeleted()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent",shiftTemplateId);
        }
        //Need to verify that individual shifts should also be deleted or not / mapping
//        List<IndividualShiftTemplate> individualShiftTemplates = individualShiftTemplateMongoRepository.getAllByIdInAndDeletedFalse(shiftTemplate.get().getIndividualShiftTemplateIds());
//        individualShiftTemplates.forEach(individualShiftTemplate -> {
//            individualShiftTemplate.setDeleted(true);});
//        save(individualShiftTemplates);
        shiftTemplate.get().setDeleted(true);
        save(shiftTemplate.get());
        return true;

    }

    public IndividualShiftTemplateDTO updateIndividualShiftTemplate(BigInteger individualShiftTemplateId, IndividualShiftTemplateDTO individualShiftTemplateDTO){
        Optional<IndividualShiftTemplate> shiftDayTemplate= individualShiftTemplateMongoRepository.findById(individualShiftTemplateId);
        if(!shiftDayTemplate.isPresent()|| shiftDayTemplate.get().isDeleted()){
            exceptionService.dataNotFoundByIdException("message.individual.shiftTemplate.absent", individualShiftTemplateId);
        }
        individualShiftTemplateDTO.setId(shiftDayTemplate.get().getId());
        BeanUtils.copyProperties(individualShiftTemplateDTO,shiftDayTemplate.get());
        save(shiftDayTemplate.get());
        return individualShiftTemplateDTO;
    }

    public IndividualShiftTemplateDTO addIndividualShiftTemplate(BigInteger shiftTemplateId, IndividualShiftTemplateDTO individualShiftTemplateDTO){
        ShiftTemplate shiftTemplate=shiftTemplateRepository.findOneById(shiftTemplateId);
        if(!Optional.ofNullable(shiftTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.individual.shiftTemplate.absent", shiftTemplateId);
        }
        IndividualShiftTemplate individualShiftTemplate=ObjectMapperUtils.copyPropertiesByMapper(individualShiftTemplateDTO,IndividualShiftTemplate.class);
        save(individualShiftTemplate);
        shiftTemplate.getIndividualShiftTemplateIds().add(individualShiftTemplate.getId());
        save(shiftTemplate);
        individualShiftTemplateDTO.setId(individualShiftTemplate.getId());
        return individualShiftTemplateDTO;
    }

    public boolean deleteIndividualShiftTemplate(BigInteger shiftTemplateId,BigInteger individualShiftTemplateId){
        IndividualShiftTemplate individualShiftTemplate=individualShiftTemplateMongoRepository.findOneById(individualShiftTemplateId);
        if(!Optional.ofNullable(individualShiftTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.individual.shiftTemplate.absent", individualShiftTemplateId);
        }
        individualShiftTemplate.setDeleted(true);
        save(individualShiftTemplate);
        ShiftTemplate shiftTemplate=shiftTemplateRepository.findOneById(shiftTemplateId);
        shiftTemplate.getIndividualShiftTemplateIds().remove(individualShiftTemplate.getId());
        save(shiftTemplate);
        return true;
    }
}
