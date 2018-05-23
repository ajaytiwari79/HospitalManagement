package com.kairos.activity.service.priority_group;

import com.kairos.activity.persistence.model.priority_group.*;
import com.kairos.activity.persistence.repository.priority_group.PriorityGroupRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.ObjectMapperUtils;
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


    public boolean createPriorityGroupForCountry(long countryId,PriorityGroupDTO priorityGroupDTO) {
        PriorityGroup priorityGroup=new PriorityGroup();
        ObjectMapperUtils.copyProperties(priorityGroupDTO, priorityGroup);
        save(priorityGroup);
        return true;
    }

    public List<PriorityGroupDTO> findAllPriorityGroups(long countryId) {
        return priorityGroupRepository.findByCountryIdAndDeletedFalse(countryId);
    }

    public PriorityGroupDTO updatePriorityGroup(long countryId, BigInteger priorityGroupId, PriorityGroupDTO priorityGroupDTO) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndCountryIdAndDeletedFalse(priorityGroupId, countryId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound","priority-group",priorityGroupId);
        }
        priorityGroupDTO.setName(priorityGroup.getName());
        ObjectMapperUtils.copyProperties(priorityGroupDTO, priorityGroup);
        priorityGroup.setId(priorityGroupId);
        priorityGroup.setCountryId(countryId);
        save(priorityGroup);
        ObjectMapperUtils.copyProperties(priorityGroup,priorityGroupDTO);
        return priorityGroupDTO;
//        return new PriorityGroupDTO(priorityGroup.getPriority(), priorityGroup.getId(), priorityGroup.isDeActivated(),
//                priorityGroup.getOpenShiftCancelProcess(), priorityGroup.getRoundRules(), priorityGroup.getStaffExcludeFilter(), priorityGroup.getStaffIncludeFilter(), priorityGroup.getCountryId(), priorityGroup.getUnitId());
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
        List<PriorityGroup> priorityGroups = priorityGroupRepository.findAllByCountryIdAndDeActivatedFalseAndDeletedFalse(countryId);
        if(!priorityGroups.isEmpty()) {
            priorityGroups.forEach(priorityGroup -> {
                priorityGroup.setCountryParentId(priorityGroup.getId());
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
        return priorityGroupRepository.findByUnitIdAndDeletedFalse(unitId);
    }

    public PriorityGroupDTO updatePriorityGroupOfUnit(long unitId, BigInteger priorityGroupId, PriorityGroupDTO priorityGroupDTO) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndUnitIdAndDeletedFalse(priorityGroupId, unitId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound","priority-group",priorityGroupId);
        }
        ObjectMapperUtils.copyProperties(priorityGroupDTO, priorityGroup);
        priorityGroup.setId(priorityGroupId);
        priorityGroup.setUnitId(unitId);
        save(priorityGroup);
        ObjectMapperUtils.copyProperties(priorityGroup,priorityGroupDTO);
        return priorityGroupDTO;
//        return new PriorityGroupDTO(priorityGroup.getPriority(), priorityGroup.getId(), priorityGroup.isDeActivated(),
//                priorityGroup.getOpenShiftCancelProcess(), priorityGroup.getRoundRules(), priorityGroup.getStaffExcludeFilter(), priorityGroup.getStaffIncludeFilter(), priorityGroup.getCountryId(), priorityGroup.getUnitId());
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
        List<PriorityGroup> priorityGroups = priorityGroupRepository.findAllByUnitIdAndDeActivatedFalseAndDeletedFalse(unitId);

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

    public List<PriorityGroupDTO> getPriorityGroupsByRuleTemplate(long unitId) {
        return priorityGroupRepository.findByUnitIdAndDeletedFalse(unitId);
    }


}
