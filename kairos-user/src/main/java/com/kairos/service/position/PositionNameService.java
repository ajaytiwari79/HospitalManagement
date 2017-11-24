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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by pawanmandhan on 27/7/17.
 */

@Transactional
@Service
<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionNameService.java
public class PositionNameService extends UserBaseService {
=======
public class PositionCodeService extends UserBaseService {
    transient private final Logger logger = LoggerFactory.getLogger(PositionCode.class);
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/PositionCodeService.java

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private PositionNameGraphRepository positionNameGraphRepository;
    @Inject
    private GroupService groupService;
    @Inject
    private TeamService teamService;
<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionNameService.java
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
=======
    @Inject
    private OrganizationService organizationService;
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/PositionCodeService.java

    public PositionCode createPositionCode(Long id, PositionCode positionCode, String type) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        if (!organization.isParentOrganization()) {
            throw new ActionNotPermittedException("Can only create PositionCode in Parent organization");
        }

<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionNameService.java

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
=======
        String name = "(?i)" + positionCode.getName();
        //check if duplicate
        PositionCode positionCodeObj = positionCodeGraphRepository.checkDuplicatePositionName(organization.getId(), name);
        if (!Optional.ofNullable(positionCodeObj).isPresent()) {
            logger.info("positionCode already exist,{}", positionCode.getName());
            throw new DuplicateDataException("PositionCode already exist");
        }
        List<PositionCode> positionCodeList = organization.getPositionCodeList();
        positionCodeList = (positionCodeList == null) ? new ArrayList<PositionCode>() : positionCodeList;
        positionCodeList.add(positionCode);
        organization.setPositionCodeList(positionCodeList);
        save(organization);
        return positionCode;
    }


    public PositionCode updatePositionCode(Long id, Long positionNameId, PositionCode positionCode, String type) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        if (!organization.isParentOrganization()) {
            throw new ActionNotPermittedException("Can only update PositionCode in Parent organization");
        }

        PositionCode oldPositionCode = positionCodeGraphRepository.findOne(positionNameId);
        if (!Optional.ofNullable(oldPositionCode).isPresent()) {
            logger.info("position code not found,{}", positionCode.getName());
            throw new DataNotFoundByIdException("PositionCode doesn't exist");
        }

        //check if new Name already exist
        if (!(oldPositionCode.getName().equalsIgnoreCase(positionCode.getName())) &&
                (positionCodeGraphRepository.checkDuplicatePositionName(organization.getId(), positionCode.getName()) != null)) {
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
            throw new DataNotFoundByIdException("position code  not found " + positionId);
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/PositionCodeService.java
        }
        if (!organization.isParentOrganization()) {
            throw new ActionNotPermittedException("Only parent Organization can remove/edit position names.");
        }
<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionNameService.java


        position.setEnabled(false);
        save(position);
=======
        positionCode.setDeleted(true);
        save(positionCode);
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/PositionCodeService.java
        return true;
    }


<<<<<<< HEAD:kairos-user/src/main/java/com/kairos/service/position/PositionNameService.java
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
=======
    public PositionCode getPositionCode(Long positionId) {
        return positionCodeGraphRepository.findOne(positionId);
    }

    public PositionCode getPositionCodeByUnitIdAndId(Long unitId, long positionNameId) {
        return positionCodeGraphRepository.getPositionCodeByUnitIdAndId(unitId, positionNameId);
    }

    public List<PositionCode> getAllPositionCodes(Long id, String type) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        List<PositionCode> positionCodes = (organization.isParentOrganization()) ? organizationGraphRepository.getPositionNames(organization.getId()) : organizationGraphRepository.getPositionCodesOfParentOrganization(organization.getId());
        return positionCodes;
>>>>>>> b503068... changed position to UEP:kairos-user/src/main/java/com/kairos/service/position/PositionCodeService.java
    }


}
