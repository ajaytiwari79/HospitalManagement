package com.kairos.service.position;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WTADTO;
import com.kairos.persistence.model.user.agreement.wta.WTAResponseDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.country.EmploymentType;

import com.kairos.persistence.model.user.expertise.Expertise;

import com.kairos.persistence.model.user.position.PositionCode;
import com.kairos.persistence.model.user.position.PositionCtaWtaQueryResult;

import com.kairos.persistence.model.user.position.UnitEmploymentPosition;

import com.kairos.persistence.model.user.position.UnitEmploymentPositionQueryResult;

import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.staff.UnitEmployment;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.cta.CollectiveTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;

import com.kairos.persistence.repository.user.position.PositionCodeGraphRepository;
import com.kairos.persistence.repository.user.position.UnitEmploymentPositionGraphRepository;

import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitEmploymentGraphRepository;
import com.kairos.response.dto.web.UnitEmploymentPositionDTO;
import com.kairos.response.dto.web.PositionWrapper;
import com.kairos.service.UserBaseService;
import com.kairos.service.agreement.wta.WTAService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.staff.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Created by pawanmandhan on 26/7/17.
 */


@Transactional
@Service

public class UnitEmploymentPositionService extends UserBaseService {
    private final Logger logger = LoggerFactory.getLogger(UnitEmploymentPositionService.class);

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private UnitEmploymentPositionGraphRepository unitEmploymentPositionGraphRepository;
    @Inject
    private PositionCodeGraphRepository positionCodeGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private UnitEmploymentGraphRepository unitEmploymentGraphRepository;
    @Inject
    private WorkingTimeAgreementGraphRepository workingTimeAgreementGraphRepository;
    @Inject
    private CollectiveTimeAgreementGraphRepository costTimeAgreementGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private StaffService staffService;
    @Inject
    private EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private PositionCodeService positionCodeService;
    @Inject
    private WTAService wtaService;
    @Inject
    private ClientGraphRepository clientGraphRepository;


    public UnitEmploymentPositionQueryResult createUnitEmploymentPosition(Long id, long unitEmploymentId, UnitEmploymentPositionDTO unitEmploymentPositionDTO, String type) {
        UnitEmployment unitEmployment = unitEmploymentGraphRepository.findOne(unitEmploymentId);
        if (!Optional.ofNullable(unitEmployment).isPresent()) {
            throw new DataNotFoundByIdException("Invalid UnitEmployment id" + unitEmploymentId);
        }

        Organization organization = organizationService.getOrganizationDetail(id, type);
        if (!organization.isParentOrganization()) {
            organization = organizationService.getParentOfOrganization(organization.getId());

        }
        UnitEmploymentPosition unitEmploymentPosition = preparePosition(unitEmploymentPositionDTO, organization, id);
        List<UnitEmploymentPosition> unitEmploymentPositions = unitEmployment.getUnitEmploymentPositions();

        unitEmploymentPositions.add(unitEmploymentPosition);
        unitEmployment.setUnitEmploymentPositions(unitEmploymentPositions);
        save(unitEmployment);
        UnitEmploymentPositionQueryResult unitEmploymentPositionQueryResult = unitEmploymentPosition.getBasicDetails();
        return unitEmploymentPositionQueryResult;
    }


    public PositionWrapper updateUnitEmploymentPosition(long positionId, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {

        List<ClientMinimumDTO> clientMinimumDTO = clientGraphRepository.getCitizenListForThisContactPerson(unitEmploymentPositionDTO.getStaffId());
        if (clientMinimumDTO.size() > 0) {
            return new PositionWrapper(clientMinimumDTO);
        }

        UnitEmploymentPosition oldUnitEmploymentPosition = unitEmploymentPositionGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(oldUnitEmploymentPosition).isPresent()) {
            throw new DataNotFoundByIdException("Invalid positionId id " + positionId + " while updating the position");
        }
        preparePosition(oldUnitEmploymentPosition, unitEmploymentPositionDTO);
        save(oldUnitEmploymentPosition);
        return new PositionWrapper(oldUnitEmploymentPosition);

    }

    public boolean removePosition(long positionId) {

        UnitEmploymentPosition unitEmploymentPosition = unitEmploymentPositionGraphRepository.findOne(positionId);
        if (unitEmploymentPosition == null) {
            return false;
        }
        unitEmploymentPosition.setDeleted(false);
        save(unitEmploymentPosition);
        if (unitEmploymentPositionGraphRepository.findOne(positionId).isDeleted()) {
            return false;
        }
        return true;
    }


    public UnitEmploymentPosition getUnitEmploymentPosition(long positionId) {
        return unitEmploymentPositionGraphRepository.findOne(positionId);
    }
    /*
    * Created by vipul
    * 4-august-17
    * used to get all positions based on unitEmployment
    * */

    public List<UnitEmploymentPositionQueryResult> getAllUnitEmploymentPositions(long unitEmploymentId) {
        UnitEmployment unitEmployment = unitEmploymentGraphRepository.findOne(unitEmploymentId);

        if (unitEmployment == null) {
            throw new DataNotFoundByIdException("Invalid UnitEmployment id" + unitEmploymentId);
        }
        return unitEmploymentPositionGraphRepository.findAllUnitEmploymentPositions(unitEmploymentId);

    }

    private UnitEmploymentPosition preparePosition(UnitEmploymentPositionDTO unitEmploymentPositionDTO, Organization organization, Long unitId) {
        UnitEmploymentPosition unitEmploymentPosition = new UnitEmploymentPosition();
        Optional<WorkingTimeAgreement> wta = workingTimeAgreementGraphRepository.findById(unitEmploymentPositionDTO.getWtaId());
        if (!wta.isPresent()) {
            throw new DataNotFoundByIdException("Invalid wta id ");
        }
        unitEmploymentPosition.setWorkingTimeAgreement(wta.get());


        CostTimeAgreement cta = (unitEmploymentPositionDTO.getCtaId() == null) ? null :
                costTimeAgreementGraphRepository.findOne(unitEmploymentPositionDTO.getCtaId());
        if (cta != null) {
            unitEmploymentPosition.setCta(cta);
        }

        Optional<Expertise> expertise = expertiseGraphRepository.findById(unitEmploymentPositionDTO.getExpertiseId());
        if (!expertise.isPresent()) {
            throw new DataNotFoundByIdException("Invalid expertise id");
        }


       /* if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getExpertise()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Expertize" + positionDTO.getExpertiseId());
        }*/
        unitEmploymentPosition.setExpertise(expertise.get());

        WTAResponseDTO wtaWithRuleTemplateDTO = workingTimeAgreementGraphRepository.getWTAByExpertiseAndCountry(unitEmploymentPositionDTO.getExpertiseId());

        if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getExpertise()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Expertize" + unitEmploymentPositionDTO.getExpertiseId());
        }
        unitEmploymentPosition.setExpertise(wtaWithRuleTemplateDTO.getExpertise());

        EmploymentType employmentType = organizationGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(organization.getId(), unitEmploymentPositionDTO.getEmploymentTypeId(), false);
        if (employmentType == null) {
            throw new DataNotFoundByIdException("Employment Type does not exist in unit " + employmentType.getId() + " AND " + unitEmploymentPositionDTO.getEmploymentTypeId());
        }
        unitEmploymentPosition.setEmploymentType(employmentType);
        PositionCode positionCode = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(organization.getId(), unitEmploymentPositionDTO.getPositionCodeId());
        if (!Optional.ofNullable(positionCode).isPresent()) {
            throw new DataNotFoundByIdException("position Name does not exist in unit " + unitEmploymentPositionDTO.getPositionCodeId());
        }
        unitEmploymentPosition.setPositionCode(positionCode);

        /*CostTimeAgreement cta = costTimeAgreementGraphRepository.findOne(positionDTO.getCtaId());
         if (cta == null) {
            throw new DataNotFoundByIdException("Invalid CTA");
        }
        position.setParent(cta);<String, Object>

        */

        Staff staff = staffGraphRepository.findOne(unitEmploymentPositionDTO.getStaffId());
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + unitEmploymentPositionDTO.getStaffId());
        }
        unitEmploymentPosition.setStaff(staff);

        if (unitEmploymentPositionDTO.getStartDateMillis() < System.currentTimeMillis()) {
            throw new ActionNotPermittedException("Start date can't be less than current Date ");
        }
        unitEmploymentPosition.setStartDateMillis(unitEmploymentPositionDTO.getStartDateMillis());
        if (unitEmploymentPositionDTO.getStartDateMillis() > unitEmploymentPositionDTO.getEndDateMillis()) {
            throw new ActionNotPermittedException("Start date can't be less than End Date ");
        }
        unitEmploymentPosition.setEndDateMillis(unitEmploymentPositionDTO.getEndDateMillis());

        unitEmploymentPosition.setTotalWeeklyHours(unitEmploymentPositionDTO.getTotalWeeklyHours());
        unitEmploymentPosition.setAvgDailyWorkingHours(unitEmploymentPositionDTO.getAvgDailyWorkingHours());
        unitEmploymentPosition.setHourlyWages(unitEmploymentPositionDTO.getHourlyWages());
        unitEmploymentPosition.setSalary(unitEmploymentPositionDTO.getSalary());
        unitEmploymentPosition.setWorkingDaysInWeek(unitEmploymentPositionDTO.getWorkingDaysInWeek());

        return unitEmploymentPosition;
    }


    private void preparePosition(UnitEmploymentPosition oldUnitEmploymentPosition, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {
        if (!oldUnitEmploymentPosition.getExpertise().getId().equals(unitEmploymentPositionDTO.getExpertiseId())) {
            WTAResponseDTO wtaWithRuleTemplateDTO = workingTimeAgreementGraphRepository.getWTAByExpertiseAndCountry(unitEmploymentPositionDTO.getExpertiseId());

            Optional<WorkingTimeAgreement> wta = workingTimeAgreementGraphRepository.findById(unitEmploymentPositionDTO.getWtaId());
            if (!wta.isPresent()) {
                throw new DataNotFoundByIdException("Invalid wta id ");
            }
            oldUnitEmploymentPosition.setWorkingTimeAgreement(wta.get());


            CostTimeAgreement cta = (unitEmploymentPositionDTO.getCtaId() == null) ? null :
                    costTimeAgreementGraphRepository.findOne(unitEmploymentPositionDTO.getCtaId());
            if (cta != null) {
                oldUnitEmploymentPosition.setCta(cta);
            }

            Optional<Expertise> expertise = expertiseGraphRepository.findById(unitEmploymentPositionDTO.getExpertiseId());
            if (!expertise.isPresent()) {
                throw new DataNotFoundByIdException("Invalid expertise id");
            }


            if (!oldUnitEmploymentPosition.getPositionCode().getId().equals(unitEmploymentPositionDTO.getPositionCodeId())) {
                PositionCode positionCode = positionCodeGraphRepository.findOne(unitEmploymentPositionDTO.getPositionCodeId());
                if (!Optional.ofNullable(positionCode).isPresent()) {
                    throw new DataNotFoundByIdException("PositionCode Cannot be null" + unitEmploymentPositionDTO.getPositionCodeId());
                }
                oldUnitEmploymentPosition.setPositionCode(positionCode);
            }


            if (!oldUnitEmploymentPosition.getPositionCode().getId().equals(unitEmploymentPositionDTO.getPositionCodeId())) {
                PositionCode positionCode = positionCodeGraphRepository.findOne(unitEmploymentPositionDTO.getPositionCodeId());
                if (!Optional.ofNullable(positionCode).isPresent()) {
                    throw new DataNotFoundByIdException("PositionCode Cannot be null" + unitEmploymentPositionDTO.getPositionCodeId());
                }
                oldUnitEmploymentPosition.setPositionCode(positionCode);

            }

            if (!oldUnitEmploymentPosition.getEmploymentType().getId().equals(unitEmploymentPositionDTO.getEmploymentTypeId())) {
                EmploymentType employmentType = employmentTypeGraphRepository.findOne(unitEmploymentPositionDTO.getEmploymentTypeId());
                if (!Optional.ofNullable(employmentType).isPresent()) {
                    throw new DataNotFoundByIdException("employmentType Cannot be null" + unitEmploymentPositionDTO.getEmploymentTypeId());
                }
                oldUnitEmploymentPosition.setEmploymentType(employmentType);
            }


            if (unitEmploymentPositionDTO.getStartDateMillis() > unitEmploymentPositionDTO.getEndDateMillis()) {
                throw new ActionNotPermittedException("Start date can't be less than End Date ");
            }
            oldUnitEmploymentPosition.setEndDateMillis(unitEmploymentPositionDTO.getEndDateMillis());


            oldUnitEmploymentPosition.setStartDateMillis(unitEmploymentPositionDTO.getStartDateMillis());

            oldUnitEmploymentPosition.setWorkingDaysInWeek(unitEmploymentPositionDTO.getWorkingDaysInWeek());
            oldUnitEmploymentPosition.setTotalWeeklyHours(unitEmploymentPositionDTO.getTotalWeeklyHours());
            oldUnitEmploymentPosition.setAvgDailyWorkingHours(unitEmploymentPositionDTO.getAvgDailyWorkingHours());
            oldUnitEmploymentPosition.setHourlyWages(unitEmploymentPositionDTO.getHourlyWages());
            oldUnitEmploymentPosition.setSalary(unitEmploymentPositionDTO.getSalary());

        }
    }

    /*
     * @auth vipul
     * used to get all positions of organization n buy organization and staff Id
     * */
    public List<UnitEmploymentPositionQueryResult> getAllUnitEmploymentPositionsOfStaff(long id, long unitEmploymentId, long staffId, String type) {


        Long unitId = organizationService.getOrganization(id, type);

        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + staffId);
        }

        return unitEmploymentPositionGraphRepository.getAllUnitEmploymentPositionByStaff(unitId, unitEmploymentId, staffId);
    }

    public PositionCtaWtaQueryResult getCtaAndWtaByExpertiseId(Long unitId, Long expertiseId) {
        return unitEmploymentPositionGraphRepository.getCtaAndWtaByExpertise(unitId, expertiseId);
    }

    public WorkingTimeAgreement updateUnitEmploymentPositionWTA(Long unitId, Long unitEmploymentPositionId, Long wtaId, WTADTO updateDTO) {
        UnitEmploymentPosition unitEmploymentPosition = unitEmploymentPositionGraphRepository.findOne(unitEmploymentPositionId);
        if (!Optional.ofNullable(unitEmploymentPosition).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit Employment Position id" + unitEmploymentPositionId);
        }
        WorkingTimeAgreement oldWta = workingTimeAgreementGraphRepository.findOne(wtaId, 2);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating unit Employment Position for staff %d", unitEmploymentPositionId);
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }

        WorkingTimeAgreement newWta = new WorkingTimeAgreement();

        if (oldWta.getExpertise().getId() != updateDTO.getExpertiseId()) {
            logger.info("Expertise cant be changed :", wtaId);
            throw new ActionNotPermittedException("Expertise can't be changed");
        }
        logger.info(updateDTO.getName());
        newWta = wtaService.copyWta(oldWta, updateDTO);
        newWta.setExpertise(oldWta.getExpertise());
        newWta.setParentWTA(oldWta);
        newWta.setDisabled(false);
        unitEmploymentPosition.setWorkingTimeAgreement(newWta);
        save(unitEmploymentPosition);
        newWta.setParentWTA(oldWta.basicDetails());
        newWta.getExpertise().setCountry(null);
        return newWta;
    }

    public WTAResponseDTO getUnitEmploymentPositionWTA(Long unitId, Long unitEmploymentPositionId) {
        UnitEmploymentPosition unitEmploymentPosition = unitEmploymentPositionGraphRepository.findOne(unitEmploymentPositionId);
        if (!Optional.ofNullable(unitEmploymentPosition).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit Employment Position id" + unitEmploymentPositionId);
        }
        WTAResponseDTO workingTimeAgreement = workingTimeAgreementGraphRepository.findWtaByUnitEmploymentPosition(unitEmploymentPositionId);
        return workingTimeAgreement;
    }
}
