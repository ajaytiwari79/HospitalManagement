package com.kairos.service.organization;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.group.GroupDTO;
import com.kairos.persistence.model.user.filter.FilterGroup;
import com.kairos.persistence.repository.organization.GroupGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_GROUP_ALREADY_EXISTS_IN_UNIT;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_GROUP_NOT_FOUND;

/**
 * Created By G.P.Ranjan on 19/11/19
 **/
@Service
public class GroupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);
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

    public GroupDTO createGroup(Long unitId, GroupDTO groupDTO) {
        Unit unit = unitGraphRepository.getUnitWithGroupsByUnitId(unitId);
        if (groupGraphRepository.existsByName(unitId,-1L, groupDTO.getName())){
            exceptionService.duplicateDataException(MESSAGE_GROUP_ALREADY_EXISTS_IN_UNIT, groupDTO.getName(), unitId);
        }
        Group group = new Group(groupDTO.getName(),groupDTO.getDescription());
        groupGraphRepository.save(group);
        unit.getGroups().add(group);
        unitGraphRepository.save(unit);
        groupDTO.setId(group.getId());
        return groupDTO;
    }

    public GroupDTO updateGroup(Long unitId, Long groupId, GroupDTO groupDTO) {
        if (groupGraphRepository.existsByName(unitId,groupId, groupDTO.getName())){
            exceptionService.duplicateDataException(MESSAGE_GROUP_ALREADY_EXISTS_IN_UNIT, groupDTO.getName(), unitId);
        }
        Group group = groupGraphRepository.findGroupByIdAndDeletedFalse(groupId);
        if(isNull(group)){
            exceptionService.dataNotFoundByIdException(MESSAGE_GROUP_NOT_FOUND,groupId);
        }
        if(isNotNull(groupDTO.getName())){
            group.setName(groupDTO.getName());
            group.setDescription(groupDTO.getDescription());
        } else {
            group.setFiltersData(groupDTO.getFiltersData());
            group.setExcludedStaffs(groupDTO.getExcludedStaffs());
        }
        groupGraphRepository.save(group);
        return groupDTO;
    }

    public GroupDTO getGroupDetails(Long groupId) {
        return ObjectMapperUtils.copyPropertiesByMapper(groupGraphRepository.findGroupByIdAndDeletedFalse(groupId), GroupDTO.class);
    }

    public List<GroupDTO> getAllGroupsOfUnit(Long unitId) {
        Unit unit = unitGraphRepository.getUnitWithGroupsByUnitId(unitId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(unit.getGroups(), GroupDTO.class);
    }

    public Boolean deleteGroup(Long groupId) {
        Group group = groupGraphRepository.findGroupByIdAndDeletedFalse(groupId);
        if(isNull(group)){
            exceptionService.dataNotFoundByIdException(MESSAGE_GROUP_NOT_FOUND,groupId);
        }
        group.setDeleted(false);
        groupGraphRepository.save(group);
        return true;
    }

    public List<Map> getStaffListByGroupFilter(Long unitId, List<FilterSelectionDTO> filterSelectionDTOS){
        Map<FilterType, Set<String>> mapOfFilters = new HashMap<>();
        Map ageRange = null;
        Map joiningRange = null;
        for(FilterSelectionDTO filterSelection : filterSelectionDTOS){
            if(FilterType.AGE.equals(filterSelection.getName())){
                ageRange = (Map) filterSelection.getValue().iterator().next();
            }else if(FilterType.NEW_TO_ORGANISATION.equals(filterSelection.getName())){
                joiningRange = (Map) filterSelection.getValue().iterator().next();
            }else {
                mapOfFilters.put(filterSelection.getName(), filterSelection.getValue());
            }
        }
        Organization organization=organizationService.fetchParentOrganization(unitId);
        List<Map> staffs = unitGraphRepository.getStaffWithFilters(unitId, Arrays.asList(organization.getId()), null,mapOfFilters, "",envConfig.getServerHost() + AppConstants.FORWARD_SLASH + envConfig.getImagesPath());
        if(isNotNull(ageRange)) {
            final AgeRangeDTO age = new AgeRangeDTO(Integer.parseInt(ageRange.get("from").toString()), isNotNull(ageRange.get("to")) ? Integer.parseInt(ageRange.get("to").toString()) : null,DurationType.valueOf(ageRange.get("durationType").toString()));
            staffs = staffs.stream().filter(map -> validate(Integer.parseInt(map.get("age").toString()), age)).collect(Collectors.toList());
        }
        if(isNotNull(joiningRange)){
            final AgeRangeDTO joining = new AgeRangeDTO(Integer.parseInt(joiningRange.get("from").toString()), isNotNull(joiningRange.get("to")) ? Integer.parseInt(joiningRange.get("to").toString()) : null,DurationType.valueOf(joiningRange.get("durationType").toString()));
            staffs = staffs.stream().filter(map -> validate(Integer.parseInt(map.get("experienceInYears").toString()), joining)).collect(Collectors.toList());
        }
        List<Map> filteredStaff = new ArrayList<>();
        for(Map staff : staffs){
            Map<String, Object> fStaff = new HashMap<>();
            fStaff.put("id",staff.get("id"));
            fStaff.put("firstName",staff.get("firstName"));
            fStaff.put("lastName",staff.get("lastName"));
            fStaff.put("profilePic",staff.get("profilePic"));
            fStaff.put("userName",staff.get("userName"));
            fStaff.put("user_id",staff.get("user_id"));
            fStaff.put("access_token",staff.get("access_token"));
            filteredStaff.add(fStaff);
        }
        return filteredStaff;
    }

    private boolean validate(int inYears, AgeRangeDTO ageRangeDTO){
        long inDays = Math.round(inYears *  DAYS_IN_ONE_YEAR);
        long from = getDataInDays(ageRangeDTO.getFrom(), ageRangeDTO.getDurationType());
        long to = isNotNull(ageRangeDTO.getTo()) ? getDataInDays(ageRangeDTO.getTo(), ageRangeDTO.getDurationType()) : MAX_LONG_VALUE ;
        return from <= inDays && to >= inDays;
    }

    private long getDataInDays(long value, DurationType durationType){
        switch (durationType){
            case YEAR :
                return Math.round(value *  DAYS_IN_ONE_YEAR);
            case MONTHS:
                return Math.round(value *  DAYS_IN_ONE_MONTH);
            default:
                return value;
        }
    }
}
