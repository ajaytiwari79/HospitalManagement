package com.kairos.service.organization;

import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.organization.GroupGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.AppConstants.GROUP_LABEL;


/**
 * Created by oodles on 7/10/16.
 */
@Transactional
@Service
public class GroupService  {
    @Inject
    private GroupGraphRepository groupGraphRepository;

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private SkillGraphRepository skillGraphRepository;

    /**
     * Get List of Group of an Organization
     *
     * @param organizationId
     * @return
     */
    public List<Map<String, Object>> getGroups(Long organizationId) {
        return organizationGraphRepository.getGroups(organizationId);
    }


    public Group getGroupOfOrganizationById(Long organizationId, Long groupId) {
        return organizationGraphRepository.getGroups(organizationId, groupId);
    }

    public QueryResult createGroup(Group group, long unitId) {
        Organization currentOrganization = organizationGraphRepository.findOne(unitId);
        if (currentOrganization == null) {
            return null;
        }
        currentOrganization.getGroupList().add(group);
        organizationGraphRepository.save(currentOrganization);

        QueryResult queryResult = new QueryResult();
        queryResult.setId(group.getId());
        queryResult.setType(GROUP_LABEL);
        queryResult.setName(group.getName());
        return queryResult;
    }

    public List<Map<String, Object>> getGroupAvailableService(Long groupId) {
        return organizationGraphRepository.getGroupOrganizationServices(groupId);
    }

    public List<Map<String, Object>> getGroupSelectedService(Long groupId) {
        return organizationGraphRepository.getGroupAllSelectedServices(groupId);
    }

    public List<OrganizationService> addGroupSelectedService(Long groupId, Long[] service) {
        return groupGraphRepository.addSelectedService(groupId, service);

    }


    public List<Map<String, Object>> getGroupAvailableSkills(Long groupId) {
        return groupGraphRepository.getGroupOrganizationSkills(groupId);
    }

    public List<Map<String, Object>> getGroupSelectedSkills(Long groupId) {
        return groupGraphRepository.getGroupSelectedSkills(groupId);
    }

    public List<Skill> addGroupSelectedSkills(Long groupId, Long[] skill) {
        return groupGraphRepository.saveSkill(groupId, skill);
    }

    public Group updateGroupGeneralDetails(long groupId, Group group) {

        Group objectToUpdate = groupGraphRepository.findOne(groupId);
        if (objectToUpdate == null) {
            throw new InternalError("Group can not be null");
        }
        objectToUpdate.setName(group.getName());
        objectToUpdate.setDescription(group.getDescription());
        groupGraphRepository.save(group);
        return group;
    }

    public Organization getUnitByGroupId(Long groupId) {
        return groupGraphRepository.getUnitByGroupId(groupId);
    }


}
