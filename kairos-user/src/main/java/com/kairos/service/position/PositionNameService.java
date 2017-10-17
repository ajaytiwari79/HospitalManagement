package com.kairos.service.position;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.position.PositionName;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.position.PositionNameGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.organization.GroupService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.AppConstants.GROUP;
import static com.kairos.constants.AppConstants.ORGANIZATION;
import static com.kairos.constants.AppConstants.TEAM;

/**
 * Created by pawanmandhan on 27/7/17.
 */

@Transactional
@Service
public class PositionNameService extends UserBaseService {

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private PositionNameGraphRepository positionNameGraphRepository;
    @Inject
    private GroupService groupService;
    @Inject
    private TeamService teamService;
    @Inject private OrganizationService organizationService;

    public PositionName createPositionName(Long id, PositionName positionName, String type) {
        Long unitId=organizationService.getOrganization(id,type);
        PositionName position = null;
        String name = "(?i)" + positionName.getName();
        //check if duplicate
        position = positionNameGraphRepository.checkDuplicatePositionName(unitId, name);
        if (position != null) {
            throw new DuplicateDataException("PositionName already exist");
        }

        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Organization not found");
        }
        if (!organization.isParentOrganization()) {
            throw new ActionNotPermittedException("Can only create PositionName in Parent organization");
        }


        List<PositionName> positionNameList = organization.getPositionNameList();
        positionNameList = (positionNameList == null) ? new ArrayList<PositionName>() : positionNameList;
        positionNameList.add(positionName);
        organization.setPositionNameList(positionNameList);
        save(organization);

        return positionName;
    }


    public PositionName updatePositionName(Long id, Long positionNameId, PositionName positionName, String type) {
        Long unitId=organizationService.getOrganization(id,type);
        PositionName oldPositionName = positionNameGraphRepository.findOne(positionNameId);

        if (oldPositionName == null) {
            return null;
        }

        //check if new Name already exist
        if (!(oldPositionName.getName().equalsIgnoreCase(positionName.getName())) &&
                (positionNameGraphRepository.checkDuplicatePositionName(unitId, positionName.getName()) != null)) {
            throw new DuplicateDataException("PositionName can't be updated");
        }
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Organization not found");
        }
        if (!organization.isParentOrganization()) {
            throw new ActionNotPermittedException("Can only update PositionName in Parent organization");
        }

        oldPositionName.setName(positionName.getName());
        oldPositionName.setDescription(positionName.getDescription());
        oldPositionName.setEnabled(positionName.isEnabled());

        save(oldPositionName);

        return oldPositionName;
    }


    public boolean deletePositionName(Long id, Long positionId, String type) {
        Long unitId=organizationService.getOrganization(id,type);
        PositionName position = positionNameGraphRepository.findOne(positionId);
        if (position == null) {
            throw new DataNotFoundByIdException("position_name  not found " + positionId);
        }
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Organization not found " + unitId);
        }
        if (!organization.isParentOrganization()) {
            throw new ActionNotPermittedException("Only parent Organization can remove/edit position names.");
        }


        position.setEnabled(false);
        save(position);
        return true;
    }


    public PositionName getPositionName(Long positionId) {
        return positionNameGraphRepository.findOne(positionId);
    }

    public PositionName getPositionNameByUnitIdAndId(Long unitId, long  positionNameId){
        return  positionNameGraphRepository.getPositionNameByUnitIdAndId(unitId,positionNameId);
    }
    public List<PositionName> getAllPositionName(Long id, String type) {
        Long unitId;
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
        unitId = organization.getId();
        if (organization.isParentOrganization()) {
            //return parents(its own)
            positionNames = organizationGraphRepository.getPositionNames(unitId);
        } else {
            positionNames = organizationGraphRepository.getPositionNamesOfParentOrganization(unitId);
        }
        return positionNames;
    }


}
