package com.kairos.service.position_code;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.enums.ReasonCodeType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBasicResponse;
import com.kairos.persistence.model.organization.OrganizationHierarchyData;
import com.kairos.persistence.model.organization.union.UnionResponseDTO;
import com.kairos.persistence.model.user.country.FunctionDTO;
import com.kairos.persistence.model.user.country.ReasonCodeResponseDTO;
import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.FunctionGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.positionCode.PositionCodeGraphRepository;
import com.kairos.response.dto.web.PositionCodeUnionWrapper;
import com.kairos.response.dto.web.organization.position_code.PositionCodeDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.organization.GroupService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.organization.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

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
    @Inject
    private ReasonCodeGraphRepository reasonCodeGraphRepository;
    @Inject
    private FunctionGraphRepository functionGraphRepository;


    public PositionCode createPositionCode(Long id, PositionCodeDTO positionCodeDTO, String type) {
        Long unitId = organizationService.getOrganization(id, type);
        PositionCode position = null;
        String name = "(?i)" + positionCodeDTO.getName().trim();
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
        PositionCode positionCode = new PositionCode(positionCodeDTO.getName(), positionCodeDTO.getDescription(), positionCodeDTO.getTimeCareId());
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
            logger.info("position_code code not found,{}", positionCode.getName());
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
            throw new DataNotFoundByIdException("position_code code  not found " + positionId);

        }
        if (!organization.isParentOrganization()) {
            throw new ActionNotPermittedException("Only parent Organization can remove/edit position_code names.");
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

    public PositionCodeUnionWrapper getUnionsAndPositionCodes(Long id, String type) {

        Organization organization = organizationService.getOrganizationDetail(id, type);
        if (!Optional.ofNullable(organization).isPresent() || !Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent()) {
            throw new DataNotFoundByIdException("Can't find Organization with provided Id");
        }
        List<Long> organizationSubTypeIds = organization.getOrganizationSubTypes().parallelStream().map(organizationType -> organizationType.getId()).collect(Collectors.toList());
        List<UnionResponseDTO> unions = organizationGraphRepository.getAllUnionsByOrganizationSubType(organizationSubTypeIds);

        List<PositionCode> positionCodes = (organization.isParentOrganization()) ? organizationGraphRepository.getPositionCodes(organization.getId()) : organizationGraphRepository.getPositionCodesOfParentOrganization(organization.getId());
        List<OrganizationBasicResponse> organizationHierarchy = new ArrayList<>();
        if (organization.isParentOrganization()) {
            organizationHierarchy = organizationGraphRepository.getOrganizationHierarchy(organization.getId());
            OrganizationBasicResponse currentOrganization = new OrganizationBasicResponse(organization.getId(), organization.getName());
            organizationHierarchy.add(currentOrganization);

        } else {
            OrganizationHierarchyData data = organizationGraphRepository.getChildHierarchyByChildUnit(organization.getId());
            logger.debug(data.getParent().getId() + "" + data.getParent().getName());

            OrganizationBasicResponse parentOrganization = new OrganizationBasicResponse(data.getParent().getId(), data.getParent().getName());
            organizationHierarchy.add(parentOrganization);
            Iterator itr = data.getChildUnits().listIterator();
            while (itr.hasNext()) {
                Organization thisOrganization = (Organization) itr.next();
                organizationHierarchy.add(new OrganizationBasicResponse(thisOrganization.getId(), thisOrganization.getName()));
            }
            logger.info(data.toString());
        }

        List<ReasonCodeResponseDTO> reasonCodeType = reasonCodeGraphRepository.findReasonCodesByOrganizationAndReasonCodeType(organization.getId(), ReasonCodeType.EMPLOYMENT);
        List<FunctionDTO> functions = functionGraphRepository.findFunctionsByOrganization(organization.getId());
        PositionCodeUnionWrapper positionCodeUnionWrapper = new PositionCodeUnionWrapper(positionCodes, unions, organizationHierarchy, reasonCodeType, functions);
        return positionCodeUnionWrapper;
    }


}
