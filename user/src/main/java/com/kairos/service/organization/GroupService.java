package com.kairos.service.organization;

import com.kairos.persistence.model.organization.group.GroupDTO;
import com.kairos.persistence.repository.organization.GroupGraphRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Created By G.P.Ranjan on 19/11/19
 **/
public class GroupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);

    @Inject
    private GroupGraphRepository groupGraphRepository;

    public GroupDTO createGroup(Long unitId, GroupDTO groupDTO) {

        return null;
    }

    public GroupDTO updateGroup(Long unitId, Long groupId, GroupDTO groupDTO) {
        return null;
    }

    public GroupDTO getGroupDetails(Long groupId) {
        return groupGraphRepository.getGroupDetailsById(groupId);
    }

    public List<GroupDTO> getAllGroupsOfUnit(Long unitId) {
        return null;
    }
}
