package com.kairos.activity.service.priority_group;

import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.enums.PriorityGroup.PriorityGroupName;
import com.kairos.activity.persistence.model.priority_group.*;
import com.kairos.activity.persistence.repository.priority_group.PriorityGroupRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PriorityGroupService extends MongoBaseService {
    @Inject
    PriorityGroupRepository priorityGroupRepository;

    public boolean createPriorityGroupForCountry(long countryId) {
        if(priorityGroupRepository.existsByCountryId(countryId)){
            throw new ActionNotPermittedException("Priority Groups are already created");
        }
        List<PriorityGroup> priorityGroups = new ArrayList<>();
        OpenShiftCancelProcess openShiftCancelProcess = new OpenShiftCancelProcess(true, false, true, true, false);
        RoundRules roundRules = new RoundRules(10, 10, 10, 10);
        StaffExcludeFilter staffExcludeFilter = new StaffExcludeFilter(false, 10, 10, 10, 10, 10, 10,
                10, 10, false, 10, 10, 10, false, false, false);
        StaffIncludeFilter staffIncludeFilter = new StaffIncludeFilter(false, false, false, false, false, new ArrayList<Long>(),5000,78.5f,2000);
        PriorityGroup priorityGroup1 = new PriorityGroup(PriorityGroupName.PRIORITY_GROUP1,false, openShiftCancelProcess, roundRules, staffExcludeFilter, staffIncludeFilter, countryId, -1L);
        PriorityGroup priorityGroup2 = new PriorityGroup(PriorityGroupName.PRIORITY_GROUP2,false, openShiftCancelProcess, roundRules, staffExcludeFilter, staffIncludeFilter, countryId, -1L);
        PriorityGroup priorityGroup3 = new PriorityGroup(PriorityGroupName.PRIORITY_GROUP3,false, openShiftCancelProcess, roundRules, staffExcludeFilter, staffIncludeFilter, countryId, -1L);
        PriorityGroup priorityGroup4 = new PriorityGroup(PriorityGroupName.PRIORITY_GROUP4,false, openShiftCancelProcess, roundRules, staffExcludeFilter, staffIncludeFilter, countryId, -1L);
        priorityGroups.add(priorityGroup1);
        priorityGroups.add(priorityGroup2);
        priorityGroups.add(priorityGroup3);
        priorityGroups.add(priorityGroup4);
        save(priorityGroups);

        return true;
    }

    public List<PriorityGroupDTO> findAllPriorityGroups(long countryId) {
        return priorityGroupRepository.findByCountryIdAndDeletedFalse(countryId);
    }

    public PriorityGroupDTO updatePriorityGroup(long countryId, BigInteger priorityGroupId, PriorityGroupDTO priorityGroupDTO) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndCountryIdAndDeletedFalse(priorityGroupId, countryId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            throw new DataNotFoundByIdException("Priority group not found" + priorityGroupId);
        }
        ObjectMapperUtils.copyProperties(priorityGroupDTO, priorityGroup);
        save(priorityGroup);
        ObjectMapperUtils.copyProperties(priorityGroup,priorityGroupDTO);
        return priorityGroupDTO;
//        return new PriorityGroupDTO(priorityGroup.getPriority(), priorityGroup.getId(), priorityGroup.isDeActivated(),
//                priorityGroup.getOpenShiftCancelProcess(), priorityGroup.getRoundRules(), priorityGroup.getStaffExcludeFilter(), priorityGroup.getStaffIncludeFilter(), priorityGroup.getCountryId(), priorityGroup.getUnitId());
    }

    public boolean deletePriorityGroup(long countryId, BigInteger priorityGroupId) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndCountryIdAndDeletedFalse(priorityGroupId, countryId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            throw new DataNotFoundByIdException("Priority group not found" + priorityGroupId);
        }
        priorityGroup.setDeleted(true);
        save(priorityGroup);
        return true;
    }

    public boolean copyPriorityGroupsForUnit(long unitId,long countryId){
        List<PriorityGroup> priorityGroups = priorityGroupRepository.findAllByCountryIdAndDeActivatedFalseAndDeletedFalse(countryId);
        priorityGroups.forEach(priorityGroup -> {
            priorityGroup.setCountryParentId(priorityGroup.getId());
            priorityGroup.setUnitId(unitId);
            priorityGroup.setId(null);
            priorityGroup.setCountryId(null);
            });
        save(priorityGroups);
        return true;
    }

    public List<PriorityGroupDTO> getPriorityGroupsOfUnit(long unitId) {
        return priorityGroupRepository.findByUnitIdAndDeletedFalse(unitId);
    }

    public PriorityGroupDTO updatePriorityGroupOfUnit(long unitId, BigInteger priorityGroupId, PriorityGroupDTO priorityGroupDTO) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndUnitIdAndDeletedFalse(priorityGroupId, unitId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            throw new DataNotFoundByIdException("Priority group not found" + priorityGroupId);
        }
        ObjectMapperUtils.copyProperties(priorityGroupDTO, priorityGroup);
        save(priorityGroup);
        ObjectMapperUtils.copyProperties(priorityGroup,priorityGroupDTO);
        return priorityGroupDTO;
//        return new PriorityGroupDTO(priorityGroup.getPriority(), priorityGroup.getId(), priorityGroup.isDeActivated(),
//                priorityGroup.getOpenShiftCancelProcess(), priorityGroup.getRoundRules(), priorityGroup.getStaffExcludeFilter(), priorityGroup.getStaffIncludeFilter(), priorityGroup.getCountryId(), priorityGroup.getUnitId());
    }

    public boolean deletePriorityGroupOfUnit(long unitId, BigInteger priorityGroupId) {
        PriorityGroup priorityGroup = priorityGroupRepository.findByIdAndUnitIdAndDeletedFalse(priorityGroupId, unitId);
        if (!Optional.ofNullable(priorityGroup).isPresent()) {
            throw new DataNotFoundByIdException("Priority group not found" + priorityGroupId);
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




}
