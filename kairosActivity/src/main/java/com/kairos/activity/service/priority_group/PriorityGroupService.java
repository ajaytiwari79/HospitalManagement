package com.kairos.activity.service.priority_group;

import com.kairos.activity.client.GenericIntegrationService;
import com.kairos.activity.persistence.model.priority_group.PriorityGroup;
import com.kairos.activity.persistence.repository.priority_group.PriorityGroupRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.cta.EmploymentTypeDTO;
import com.kairos.response.dto.web.open_shift.PriorityGroupDTO;
import com.kairos.response.dto.web.open_shift.PriorityGroupDefaultData;
import com.kairos.response.dto.web.open_shift.PriorityGroupWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PriorityGroupService extends MongoBaseService {
    @Inject
    private PriorityGroupRepository priorityGroupRepository;
    @Inject
    private  ExceptionService exceptionService;

    @Inject private GenericIntegrationService genericIntegrationService;


    public boolean createPriorityGroupForCountry(long countryId,List<PriorityGroupDTO> priorityGroupDTO) {
        boolean isPriorityGroupsAlreadyExists=priorityGroupRepository.existsByCountryId(countryId);
        if(isPriorityGroupsAlreadyExists){
            exceptionService.actionNotPermittedException("priorityGroup.already.exists",countryId);
        }
        List<PriorityGroup> priorityGroups=ObjectMapperUtils.copyProperties(priorityGroupDTO, PriorityGroup.class);
        save(priorityGroups);
        return true;
    }

    public PriorityGroupWrapper findAllPriorityGroups(long countryId) {
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.getAllByCountryIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNull(countryId);
        PriorityGroupDefaultData priorityGroupDefaultData1=genericIntegrationService.getExpertiseAndEmployment(countryId);
        PriorityGroupDefaultData priorityGroupDefaultData=new PriorityGroupDefaultData(priorityGroupDefaultData1.getEmploymentTypes(),priorityGroupDefaultData1.getExpertise());
        PriorityGroupWrapper priorityGroupWrapper=new PriorityGroupWrapper(priorityGroupDefaultData,priorityGroupDTOS);
        return priorityGroupWrapper;
    }

    public PriorityGroupDTO updatePriorityGroup(long countryId, BigInteger priorityGroupId, PriorityGroupDTO priorityGroupDTO) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndCountryIdAndDeletedFalse(priorityGroupId, countryId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound","priority-group",priorityGroupId);
        }
        if (priorityGroup.isDeActivated()!=priorityGroupDTO.isDeActivated()){
            priorityGroup.setDeActivated(priorityGroupDTO.isDeActivated());
        }else {
            priorityGroupDTO.setName(priorityGroup.getName());
            ObjectMapperUtils.copyProperties(priorityGroupDTO, priorityGroup);
            }
        priorityGroup.setId(priorityGroupId);
        priorityGroup.setCountryId(countryId);
        save(priorityGroup);
        ObjectMapperUtils.copyProperties(priorityGroup,priorityGroupDTO);
        return priorityGroupDTO;
 }

    public boolean deletePriorityGroup(long countryId, BigInteger priorityGroupId) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndCountryIdAndDeletedFalse(priorityGroupId, countryId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound","priority-group",priorityGroupId);
        }
        priorityGroup.setDeleted(true);
        save(priorityGroup);
        return true;
    }

    public boolean copyPriorityGroupsForUnit(long unitId,long countryId){
        List<PriorityGroup> priorityGroups = priorityGroupRepository.findAllByCountryIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNull(countryId);
        if(!priorityGroups.isEmpty()) {
            priorityGroups.forEach(priorityGroup -> {
                priorityGroup.setParentId(priorityGroup.getId());
                priorityGroup.setUnitId(unitId);
                priorityGroup.setId(null);
                priorityGroup.setCountryId(null);
                });
            save(priorityGroups);
            return true;
        } else  {
            return false;
        }
    }

    public List<PriorityGroupDTO> getPriorityGroupsOfUnit(long unitId) {
        return priorityGroupRepository.getAllByUnitIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNull(unitId);
    }

    public PriorityGroupDTO updatePriorityGroupOfUnit(long unitId, BigInteger priorityGroupId, PriorityGroupDTO priorityGroupDTO) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndUnitIdAndDeletedFalse(priorityGroupId, unitId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound","priority-group",priorityGroupId);
        }
        if (priorityGroup.isDeActivated()!=priorityGroupDTO.isDeActivated()){
            priorityGroup.setDeActivated(priorityGroupDTO.isDeActivated());
        }else {
            ObjectMapperUtils.copyProperties(priorityGroupDTO, priorityGroup);
            priorityGroup.setId(priorityGroupId);
            priorityGroup.setUnitId(unitId);
            priorityGroup.setName(priorityGroupDTO.getName());
        }
        save(priorityGroup);
        ObjectMapperUtils.copyProperties(priorityGroup,priorityGroupDTO);
        return priorityGroupDTO;
  }

    public boolean deletePriorityGroupOfUnit(long unitId, BigInteger priorityGroupId) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndUnitIdAndDeletedFalse(priorityGroupId, unitId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound","priority-group",priorityGroupId);
        }
        priorityGroup.setDeleted(true);
        save(priorityGroup);
        return true;
    }


    public boolean copyPriorityGroupsForOrder(long unitId, BigInteger orderId){

        List<PriorityGroup> priorityGroups = priorityGroupRepository.findAllByUnitIdAndDeActivatedFalseAndDeletedFalseAndRuleTemplateIdIsNullAndOrderIdIsNull(unitId);
        priorityGroups.forEach(priorityGroup -> {
            priorityGroup.setOrderId(orderId);
            priorityGroup.setId(null);
            });
        save(priorityGroups);
        return true;
    }
    public PriorityGroup getPriorityGroupOfCountryById(long countryId,BigInteger priorityGroupId){
        return priorityGroupRepository.findByIdAndCountryIdAndDeletedFalse(priorityGroupId,countryId);
    }
    public PriorityGroup getPriorityGroupOfUnitById(Long unitId, BigInteger priorityGroupId){
        return priorityGroupRepository.findByIdAndUnitIdAndDeletedFalse(priorityGroupId,unitId);
    }
    public List<PriorityGroupDTO> createPriorityGroups(BigInteger orderId,List<PriorityGroupDTO> priorityGroupDTOs) {
        priorityGroupDTOs.forEach(priorityGroupDTO -> {
            priorityGroupDTO.setParentId(priorityGroupDTO.getId());
            priorityGroupDTO.setOrderId(orderId);
            priorityGroupDTO.setId(null);
        });
        List<PriorityGroup> priorityGroups=ObjectMapperUtils.copyProperties(priorityGroupDTOs, PriorityGroup.class);
        save(priorityGroups);
        return priorityGroupDTOs;
    }
    public List<PriorityGroupDTO> updatePriorityGroupsForOrder(List<PriorityGroupDTO> priorityGroupDTOs) {
        List<PriorityGroup> priorityGroups= ObjectMapperUtils.copyProperties(priorityGroupDTOs,PriorityGroup.class);
        save(priorityGroups);
        return priorityGroupDTOs;
    }

    public List<PriorityGroupDTO> getPriorityGroupsByRuleTemplate(long unitId,BigInteger ruleTemplateId) {
        return priorityGroupRepository.findByUnitIdAndRuleTemplateIdAndDeletedFalse(unitId,ruleTemplateId);
    }
    public PriorityGroupWrapper getPriorityGroupsByRuleTemplateForUnit(Long unitId,BigInteger ruleTemplateId){
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.findByUnitIdAndRuleTemplateIdAndDeletedFalse(unitId,ruleTemplateId);
        PriorityGroupDefaultData priorityGroupDefaultData1=genericIntegrationService.getExpertiseAndEmploymentForUnit(unitId);
        PriorityGroupDefaultData priorityGroupDefaultData=new PriorityGroupDefaultData(priorityGroupDefaultData1.getEmploymentTypes(),priorityGroupDefaultData1.getExpertise());
        PriorityGroupWrapper priorityGroupWrapper=new PriorityGroupWrapper(priorityGroupDefaultData,priorityGroupDTOS);
        return priorityGroupWrapper;
    }
    public List<PriorityGroupDTO> getPriorityGroupsByOrderId(long unitId,BigInteger orderId) {
        return priorityGroupRepository.findByUnitIdAndOrderIdAndDeletedFalse(unitId,orderId);
    }

    public PriorityGroupWrapper getPriorityGroupsByOrderIdForUnit(Long unitId,BigInteger orderId){
        List<PriorityGroupDTO> priorityGroupDTOS=priorityGroupRepository.findByUnitIdAndOrderIdAndDeletedFalse(unitId,orderId);
        PriorityGroupDefaultData priorityGroupDefaultData1=genericIntegrationService.getExpertiseAndEmploymentForUnit(unitId);
        PriorityGroupDefaultData priorityGroupDefaultData=new PriorityGroupDefaultData(priorityGroupDefaultData1.getEmploymentTypes(),priorityGroupDefaultData1.getExpertise());
        PriorityGroupWrapper priorityGroupWrapper=new PriorityGroupWrapper(priorityGroupDefaultData,priorityGroupDTOS);
        return priorityGroupWrapper;
    }

}

