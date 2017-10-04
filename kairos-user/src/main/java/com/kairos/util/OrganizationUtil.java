package com.kairos.util;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.position.PositionName;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.position.PositionNameGraphRepository;
import com.kairos.service.organization.GroupService;
import com.kairos.service.organization.TeamService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.AppConstants.*;

/**
 * Created by vipul on 4/10/17.
 */
@Service
public class OrganizationUtil {
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private PositionNameGraphRepository positionNameGraphRepository;
    @Inject
    private GroupService groupService;
    @Inject
    private TeamService teamService;


    public Long getOrganization(Long id, String type) {
        Organization organization = null;
        List<PositionName> positionNames = new ArrayList<PositionName>();
        if (ORGANIZATION.equalsIgnoreCase(type)) {
            organization = organizationGraphRepository.findOne(id);
        } else if (GROUP.equalsIgnoreCase(type)) {
            organization = groupService.getUnitByGroupId(id);
        } else if (TEAM.equalsIgnoreCase(type)) {
            organization = teamService.getOrganizationByTeamId(id);
        } else {
            throw new InternalError("Type is not valid");
        }
        if (organization == null) {
            throw new DataNotFoundByIdException("Organization not found-" + id);
        }
        return organization.getId();
    }
}
