package com.kairos.service.position_code;

import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBasicResponse;
import com.kairos.persistence.model.organization.OrganizationHierarchyData;
import com.kairos.persistence.model.organization.union.UnionResponseDTO;
import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.FunctionGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.positionCode.PositionCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.wrapper.PositionCodeUnionWrapper;
import com.kairos.dto.user.organization.position_code.PositionCodeDTO;
import com.kairos.service.exception.ExceptionService;
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

public class PositionCodeService {
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
    @Inject
    private StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    public PositionCode createPositionCode(Long id, PositionCodeDTO positionCodeDTO, String type) {
        Long unitId = organizationService.getOrganization(id, type);
        PositionCode position = null;
        String name = "(?i)" + positionCodeDTO.getName().trim();
        //check if duplicate
        position = positionCodeGraphRepository.checkDuplicatePositionCode(unitId, name);
        if (position != null) {
            exceptionService.duplicateDataException("message.positioncode.alreadyexist");

        }


        Organization organization = organizationService.getOrganizationDetail(id, type);
        if (!organization.isParentOrganization()) {
            exceptionService.actionNotPermittedException("message.positioncode.create.parentorganization");

        }


        List<PositionCode> positionCodeList = organization.getPositionCodeList();
        positionCodeList = (positionCodeList == null) ? new ArrayList<PositionCode>() : positionCodeList;
        PositionCode positionCode = new PositionCode(positionCodeDTO.getName(), positionCodeDTO.getDescription(), positionCodeDTO.getTimeCareId());
        positionCodeList.add(positionCode);
        organization.setPositionCodeList(positionCodeList);
        organizationGraphRepository.save(organization);

        return positionCode;
    }

    public PositionCode updatePositionCode(Long id, Long positionCodeId, PositionCode positionCode, String type) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        if (!organization.isParentOrganization()) {
            exceptionService.actionNotPermittedException("message.positioncode.update.parentorganization");

        }

        PositionCode oldPositionCode = positionCodeGraphRepository.findOne(positionCodeId);
        if (!Optional.ofNullable(oldPositionCode).isPresent()) {
            logger.info("position_code code not found,{}", positionCode.getName());
            exceptionService.dataNotFoundByIdException("message.positioncode.notexist");

        }

        if (!(oldPositionCode.getName().equalsIgnoreCase(positionCode.getName().trim())) &&
                (positionCodeGraphRepository.checkDuplicatePositionCode(organization.getId() ,"(?i)"+positionCode.getName().trim()) != null)) {
            exceptionService.duplicateDataException("message.positioncode.notupdated");

        }


        oldPositionCode.setName(positionCode.getName());
        oldPositionCode.setDescription(positionCode.getDescription());
        positionCodeGraphRepository.save(oldPositionCode);
        return oldPositionCode;
    }


    public boolean deletePositionCode(Long id, Long positionId, String type) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        PositionCode positionCode = positionCodeGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(positionCode).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.positioncode.id.notfound",positionId);


        }
        if (!organization.isParentOrganization()) {
            exceptionService.actionNotPermittedException("message.positioncode.parentorganization");

        }


        positionCode.setDeleted(true);
        positionCodeGraphRepository.save(positionCode);

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
            exceptionService.internalServerError("error.type.notvalid");

        }

        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound",id);

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

    public PositionCodeUnionWrapper getUnionsAndPositionCodes(Long id, String type, Long staffId) {
        Optional<Staff> staff = staffGraphRepository.findById(staffId);
        if (!staff.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.unitid.notfound");

        }

        List<StaffExperienceInExpertiseDTO> staffSelectedExpertise = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffId(staffId);

        Organization organization = organizationService.getOrganizationDetail(id, type);
        if (!Optional.ofNullable(organization).isPresent() || !Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent()) {
           exceptionService.dataNotFoundByIdException("message.positioncode.organization.notfound");

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

        List<ReasonCodeResponseDTO> reasonCodeType = reasonCodeGraphRepository.findReasonCodesByUnitIdAndReasonCodeType(organization.getId(), ReasonCodeType.EMPLOYMENT);
        PositionCodeUnionWrapper positionCodeUnionWrapper = new PositionCodeUnionWrapper(positionCodes, unions, organizationHierarchy, reasonCodeType, staffSelectedExpertise);
        return positionCodeUnionWrapper;
    }


}
