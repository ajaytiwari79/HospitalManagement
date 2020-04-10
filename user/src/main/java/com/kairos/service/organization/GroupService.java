package com.kairos.service.organization;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.enums.ModuleId;
import com.kairos.enums.StaffStatusEnum;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.group.GroupDTO;
import com.kairos.persistence.model.user.filter.FilterSelection;
import com.kairos.persistence.repository.organization.GroupGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.organization.filter_group.FilterSelectionGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.staff.StaffFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_GROUP_ALREADY_EXISTS_IN_UNIT;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_GROUP_NOT_FOUND;

/**
 * Created By G.P.Ranjan on 19/11/19
 **/
@Service
public class GroupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

    @Inject
    private ExceptionService exceptionService;
    @Inject
    private GroupGraphRepository groupGraphRepository;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private StaffFilterService staffFilterService;

    @Inject
    private FilterSelectionGraphRepository filterSelectionGraphRepository;

    public GroupDTO createGroup(Long unitId, GroupDTO groupDTO) {
        Unit unit = unitGraphRepository.findOne(unitId);
        if (groupGraphRepository.existsByName(unitId, -1L, groupDTO.getName())) {
            exceptionService.duplicateDataException(MESSAGE_GROUP_ALREADY_EXISTS_IN_UNIT, groupDTO.getName(), unitId);
        }
        Group group = new Group(groupDTO.getName(), groupDTO.getDescription());
        unit.getGroups().add(group);
        unitGraphRepository.save(unit);
        groupDTO.setId(group.getId());
        return groupDTO;
    }


    public GroupDTO updateGroup(Long unitId, Long groupId, GroupDTO groupDTO) {
        if (isNotNull(groupDTO.getName()) && groupGraphRepository.existsByName(unitId,groupId, groupDTO.getName())){
            exceptionService.duplicateDataException(MESSAGE_GROUP_ALREADY_EXISTS_IN_UNIT, groupDTO.getName(), unitId);
        }
        Group group = groupGraphRepository.findById(groupId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_GROUP_NOT_FOUND,groupId)));
        if(isNotNull(groupDTO.getName())){
            group.setName(groupDTO.getName());
            group.setDescription(groupDTO.getDescription());
            group.setRoomId(groupDTO.getRoomId());
        } else {
            groupGraphRepository.deleteAllFiltersByGroupId(groupId);
            List<FilterSelection> filterSelections = new ArrayList<>();
            groupDTO.getFiltersData().forEach(k->{
                List<String> values = new ArrayList<>();
                k.getValue().forEach(val->values.add(ObjectMapperUtils.objectToJsonString(val)));
                filterSelections.add(new FilterSelection(k.getName(),values));
            });
            group.setFiltersData(filterSelections);
            group.setExcludedStaffIds(groupDTO.getExcludedStaffs());
        }
        groupGraphRepository.save(group);
        return groupDTO;
    }

    public GroupDTO getGroupDetail(Long groupId) {
        Group group = groupGraphRepository.findOne(groupId);
        return getGroupDTOFromGroup(group);
    }

    public List<GroupDTO> getAllGroupsOfUnit(Long unitId) {
        Unit unit = unitGraphRepository.findOne(unitId);
        List<Group> groups = isNull(unit) ? new ArrayList<>() : groupGraphRepository.findAllGroupsByIdSAndDeletedFalse(unit.getGroups().stream().map(UserBaseEntity::getId).collect(Collectors.toList()));
        List<GroupDTO> groupDTOS = new ArrayList<>();
        for(Group group : groups){
            groupDTOS.add(getGroupDTOFromGroup(group));
        }
        return groupDTOS;
    }


    public List<FilterSelection> getFilterGroupsOfUnit(Long unitId,boolean isDeleted){
        return filterSelectionGraphRepository.findAllByUnitAndDeleted(unitId,isDeleted);
    }

    public Set<FilterSelection> getSelectedFilterGroupsOfUnit(final Long unitId,final Set<Long> filterGroupIds,final boolean isGroupDeleted){
        return filterSelectionGraphRepository.findAllByUnitIdAndSelectedGroupIdsAndGroupDeleted(unitId,filterGroupIds,isGroupDeleted);
    }

    private GroupDTO getGroupDTOFromGroup(Group group) {
        GroupDTO groupDTO = new GroupDTO(group.getId(), group.getName(), group.getDescription(), group.getExcludedStaffIds(), group.getRoomId());
        List<FilterSelectionDTO> filterSelectionDTOS = new ArrayList<>();
        for(FilterSelection filterSelection : group.getFiltersData()){
            Set<Object> values = new HashSet<>();
            filterSelection.getValue().forEach(val-> values.add(ObjectMapperUtils.jsonStringToObject((String) val,Object.class)));
            filterSelectionDTOS.add(new FilterSelectionDTO(filterSelection.getName(), values));
        }
        groupDTO.setFiltersData(filterSelectionDTOS);
        return groupDTO;
    }

    public Boolean deleteGroup(Long groupId) {
        Group group = groupGraphRepository.findOne(groupId,0);
        if(isNull(group)){
            exceptionService.dataNotFoundByIdException(MESSAGE_GROUP_NOT_FOUND,groupId);
        }
        group.setDeleted(true);
        groupGraphRepository.save(group);
        return true;
    }

    public List<Map> getStaffListByGroupFilter(Long unitId, List<FilterSelectionDTO> filterSelectionDTOS){
        List<Map> filteredStaff = new ArrayList<>();
        for(Map staff : staffFilterService.getAllStaffByUnitId(unitId, new StaffFilterDTO(ModuleId.Group_TAB_ID.value,filterSelectionDTOS),  ModuleId.Group_TAB_ID.value, null, null,false,null).getStaffList()){
            if(StaffStatusEnum.ACTIVE.toString().equals(staff.get("currentStatus"))) {
                Map<String, Object> fStaff = new HashMap<>();
                fStaff.put("id", staff.get("id"));
                fStaff.put("firstName", staff.get("firstName"));
                fStaff.put("lastName", staff.get("lastName"));
                fStaff.put("profilePic", staff.get("profilePic"));
                fStaff.put("userName", staff.get("userName"));
                fStaff.put("user_id", staff.get("user_id"));
                fStaff.put("access_token", staff.get("access_token"));
                filteredStaff.add(fStaff);
            }
        }
        return filteredStaff;
    }

    public Set<Long> getAllStaffIdsByGroupIds(Long unitId, List<Long> groupIds){
        Set<Long> staffIds = new HashSet<>();
        Set<Long> excludedStaffs = new HashSet<>();
        List<Group> groups = groupGraphRepository.findAllGroupsByIdSAndDeletedFalse(groupIds);
        List<GroupDTO> groupDTOS = new ArrayList<>();

        for(Group group : groups){
            GroupDTO groupDTO = getGroupDTOFromGroup(group);
            List<FilterSelectionDTO> filterSelectionDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(groupDTO.getFiltersData(), FilterSelectionDTO.class);
            List<Map> staffs = staffFilterService.getAllStaffByUnitId(unitId, new StaffFilterDTO(ModuleId.Group_TAB_ID.value, filterSelectionDTOS),  ModuleId.Group_TAB_ID.value, null, null,false,null).getStaffList();
            staffIds.addAll(staffs.stream().map(map-> Long.valueOf(map.get("id").toString())).collect(Collectors.toSet()));
            excludedStaffs.addAll(groupDTO.getExcludedStaffs());
            groupDTOS.add(groupDTO);
        }

        staffIds.removeAll(excludedStaffs);
        return staffIds;
    }
}
