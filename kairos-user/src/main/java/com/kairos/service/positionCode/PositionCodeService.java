package com.kairos.service.positionCode;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.position.PositionCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.positionCode.PositionCodeGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.organization.GroupService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.AppConstants.GROUP;
import static com.kairos.constants.AppConstants.ORGANIZATION;
import static com.kairos.constants.AppConstants.TEAM;

/**
 * Created by pawanmandhan on 27/7/17.
 */

@Transactional
@Service

public class PositionCodeService extends UserBaseService {
     private final Logger logger = LoggerFactory.getLogger(PositionCodeService.class);


    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private PositionCodeGraphRepository positionCodeGraphRepository;
    @Inject
    private GroupService groupService;
    @Inject
    private TeamService teamService;

    @Inject
    private OrganizationService organizationService;


    public PositionCode createPositionCode(Long id, PositionCode positionCode, String type) {
        Long unitId = organizationService.getOrganization(id, type);
        PositionCode position = null;
        String name = "(?i)" + positionCode.getName().trim();
        //check if duplicate
        position = positionCodeGraphRepository.checkDuplicatePositionCode(unitId, name);
        if (position != null) {
            throw new DuplicateDataException("PositionCode already exist");
        }


        Organization organization = organizationService.getOrganizationDetail(id, type);
        if (!organization.isParentOrganization()) {
            throw new ActionNotPermittedException("Can only create PositionCode in Parent organization");
        }


        List<PositionCode> positionCodeList = organization.getPositionCodeList();
        positionCodeList = (positionCodeList == null) ? new ArrayList<PositionCode>() : positionCodeList;
        positionCodeList.add(positionCode);
        organization.setPositionCodeList(positionCodeList);
        save(organization);

        return positionCode;
    }

    public PositionCode updatePositionCode(Long id, Long positionCodeId, PositionCode positionCode, String type) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        if (!organization.isParentOrganization()) {
            throw new ActionNotPermittedException("Can only update PositionCode in Parent organization");
        }

        PositionCode oldPositionCode = positionCodeGraphRepository.findOne(positionCodeId);
        if (!Optional.ofNullable(oldPositionCode).isPresent()) {
            logger.info("positionCode code not found,{}", positionCode.getName());
            throw new DataNotFoundByIdException("PositionCode doesn't exist");
        }

        if (!(oldPositionCode.getName().equalsIgnoreCase(positionCode.getName())) &&
                (positionCodeGraphRepository.checkDuplicatePositionCode(organization.getId(), positionCode.getName()) != null)) {
            throw new DuplicateDataException("PositionCode can't be updated");
        }


        oldPositionCode.setName(positionCode.getName());
        oldPositionCode.setDescription(positionCode.getDescription());
        save(oldPositionCode);
        return oldPositionCode;
    }


    public boolean deletePositionCode(Long id, Long positionId, String type) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        PositionCode positionCode = positionCodeGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(positionCode).isPresent()) {
            throw new DataNotFoundByIdException("positionCode code  not found " + positionId);

        }
        if (!organization.isParentOrganization()) {
            throw new ActionNotPermittedException("Only parent Organization can remove/edit positionCode names.");
        }


        positionCode.setDeleted(true);
        save(positionCode);

        return true;
    }

    public List<PositionCode> getAllPositionCode(Long id, String type) {
        Long unitId;
        Organization organization = null;
        List<PositionCode> positionCodes = new ArrayList<PositionCode>();
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
            positionCodes = organizationGraphRepository.getPositionCodes(unitId);
        } else {
            positionCodes = organizationGraphRepository.getPositionCodesOfParentOrganization(unitId);
        }
        return positionCodes;
    }

    public PositionCode getPositionCode(Long positionId) {
        return positionCodeGraphRepository.findOne(positionId);
    }

    public List<PositionCode> getAllPositionCodes(Long id, String type) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        List<PositionCode> positionCodes = (organization.isParentOrganization()) ? organizationGraphRepository.getPositionCodes(organization.getId()) : organizationGraphRepository.getPositionCodesOfParentOrganization(organization.getId());
        return positionCodes;

    }


}
