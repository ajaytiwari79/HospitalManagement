package com.kairos.service.shift;

import com.google.inject.Inject;
import com.kairos.persistence.model.shift.IndividualShiftTemplate;
import com.kairos.persistence.model.shift.ShiftTemplate;
import com.kairos.persistence.repository.shift.IndividualShiftTemplateMongoRepository;
import com.kairos.persistence.repository.shift.ShiftTemplateRepository;
import com.kairos.response.dto.web.shift.IndividualShiftTemplateDTO;
import com.kairos.response.dto.web.shift.ShiftTemplateDTO;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.util.userContext.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftTemplateService extends MongoBaseService {

    @Autowired
    private ShiftTemplateRepository shiftTemplateRepository;
    @Autowired private IndividualShiftTemplateMongoRepository individualShiftTemplateMongoRepository;
    @Inject private ExceptionService exceptionService;
    @Inject private ShiftService shiftService;

    public ShiftTemplateDTO createShiftTemplate(Long unitId, ShiftTemplateDTO shiftTemplateDTO){
        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOs =shiftTemplateDTO.getShiftList();
        Set<BigInteger> individualShiftTemplateIds=new HashSet<>();
        individualShiftTemplateDTOs.forEach(individualShiftTemplateDTO -> {
            int i;
            List<IndividualShiftTemplateDTO> subShifts=individualShiftTemplateDTO.getSubShifts();
            List<IndividualShiftTemplate> individualShiftTemplates1=ObjectMapperUtils.copyProperties(subShifts,IndividualShiftTemplate.class);
            if(Optional.ofNullable(individualShiftTemplates1).isPresent() && individualShiftTemplates1.size()>0){
                save(individualShiftTemplates1);
                individualShiftTemplateDTO.setSubShifts(ObjectMapperUtils.copyProperties(individualShiftTemplates1,IndividualShiftTemplateDTO.class));
            }
            Set<BigInteger> subShiftIds=individualShiftTemplates1.stream().map(subShifts1-> subShifts1.getId()).collect(Collectors.toSet());
            IndividualShiftTemplate individualShiftTemplate=new IndividualShiftTemplate();
            BeanUtils.copyProperties(individualShiftTemplateDTO,individualShiftTemplate,"shiftList");
            individualShiftTemplate.setSubShiftIds(subShiftIds);
            save(individualShiftTemplate);
            individualShiftTemplateIds.add(individualShiftTemplate.getId());
            individualShiftTemplateDTO.setId(individualShiftTemplate.getId());
        });
//        List<IndividualShiftTemplate> individualShiftTemplates =ObjectMapperUtils.copyProperties(individualShiftTemplateDTOs,IndividualShiftTemplate.class);
//        save(individualShiftTemplates);
//        Set<BigInteger> individualShiftTemplateIds=new HashSet<>();
//        individualShiftTemplates.forEach(individualShiftTemplate -> {individualShiftTemplateIds.add(individualShiftTemplate.getId());});
        ShiftTemplate shiftTemplate=new ShiftTemplate(shiftTemplateDTO.getName(),individualShiftTemplateIds,unitId,UserContext.getUserDetails().getId());
        save(shiftTemplate);
        shiftTemplateDTO.setId(shiftTemplate.getId());
       //Preparing DTO Object to return
//        List<IndividualShiftTemplateDTO> individualShiftTemplateDTOS =ObjectMapperUtils.copyPropertiesOfListByMapper(individualShiftTemplates,IndividualShiftTemplateDTO.class);
//        shiftTemplateDTO.setId(shiftTemplate.getId());
//        shiftTemplateDTO.setShiftList(individualShiftTemplateDTOS);
        return shiftTemplateDTO;
    }

    public List<ShiftTemplateDTO> getAllShiftTemplates(Long unitId){
        List<ShiftTemplateDTO> shiftTemplateDTOS=   shiftTemplateRepository.getAllByUnitIdAndCreatedByAndDeletedFalse(unitId,UserContext.getUserDetails().getId());
        return shiftTemplateDTOS;
//        List<ShiftTemplate> shiftTemplates= shiftTemplateRepository.findAllByUnitIdAndCreatedByAndDeletedFalse(unitId,UserContext.getUserDetails().getId());
//        List<ShiftTemplateDTO> shiftTemplateDTOS=new ArrayList<>();
//        shiftTemplates.forEach(shiftTemplateDTO -> {
//            List<IndividualShiftTemplateDTO> individualShiftTemplateDTOS = individualShiftTemplateMongoRepository.findAllByIdInAndDeletedFalse(shiftTemplateDTO.getIndividualShiftTemplateIds());
//            ShiftTemplateDTO shiftTemplateDTO1=new ShiftTemplateDTO(shiftTemplateDTO.getId(),shiftTemplateDTO.getName(), individualShiftTemplateDTOS,shiftTemplateDTO.getCreatedBy(),shiftTemplateDTO.getUnitId());
//            shiftTemplateDTOS.add(shiftTemplateDTO1);
//        });
//        return shiftTemplateDTOS;
    }

    public ShiftTemplateDTO updateShiftTemplate(BigInteger shiftTemplateId, ShiftTemplateDTO shiftTemplateDTO){
        ShiftTemplate shiftTemplate= shiftTemplateRepository.findOneById(shiftTemplateId);
        if(!Optional.ofNullable(shiftTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent", shiftTemplateId);
        }
        BeanUtils.copyProperties(shiftTemplateDTO,shiftTemplate,"shiftList");
        save(shiftTemplate);
        return shiftTemplateDTO;
    }

    public boolean deleteShiftTemplate(BigInteger shiftTemplateId){
        ShiftTemplate shiftTemplate=shiftTemplateRepository.findOneById(shiftTemplateId);
        if(!Optional.ofNullable(shiftTemplate).isPresent()){
            exceptionService.dataNotFoundByIdException("message.shiftTemplate.absent",shiftTemplateId);
        }
        //Need to verify that individual shifts should also be deleted or not / mapping
//        List<IndividualShiftTemplate> individualShiftTemplates = individualShiftTemplateMongoRepository.getAllByIdInAndDeletedFalse(shiftTemplate.get().getIndividualShiftTemplateIds());
//        individualShiftTemplates.forEach(individualShiftTemplate -> {
//            individualShiftTemplate.setDeleted(true);});
//        save(individualShiftTemplates);
        shiftTemplate.setDeleted(true);
        save(shiftTemplate);
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
