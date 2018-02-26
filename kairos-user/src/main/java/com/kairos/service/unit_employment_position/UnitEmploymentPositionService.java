package com.kairos.service.unit_employment_position;

import com.kairos.client.dto.timeBank.CostTimeAgreementDTO;
import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.WTADTO;
import com.kairos.persistence.model.user.agreement.wta.WTAResponseDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.country.EmploymentType;

import com.kairos.persistence.model.user.expertise.Expertise;

import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.persistence.model.user.unitEmploymentPosition.PositionCtaWtaQueryResult;

import com.kairos.persistence.model.user.unitEmploymentPosition.UnitPosition;

import com.kairos.persistence.model.user.unitEmploymentPosition.UnitEmploymentPositionQueryResult;

import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.staff.TimeCareEmploymentDTO;
import com.kairos.persistence.model.user.staff.UnitPermission;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.cta.CollectiveTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;

import com.kairos.persistence.repository.user.positionCode.PositionCodeGraphRepository;
import com.kairos.persistence.repository.user.unitEmploymentPosition.UnitEmploymentPositionGraphRepository;

import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitPermissionGraphRepository;
import com.kairos.response.dto.web.UnitEmploymentPositionDTO;
import com.kairos.response.dto.web.PositionWrapper;
import com.kairos.service.UserBaseService;
import com.kairos.service.agreement.wta.WTAService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.position_code.PositionCodeService;
import com.kairos.service.staff.StaffService;
import com.kairos.util.DateConverter;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

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
    private UnitPermissionGraphRepository unitPermissionGraphRepository;
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


    public UnitEmploymentPositionQueryResult createUnitEmploymentPosition(Long id, String type, UnitEmploymentPositionDTO unitEmploymentPositionDTO, Boolean createFromTimeCare) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        Organization parentOrganization;
        UnitPermission unitPermission;
        PositionCode positionCode = null;
        if (!organization.isParentOrganization()) {
            parentOrganization = organizationService.getParentOfOrganization(organization.getId());
            unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(parentOrganization.getId(), organization.getId(), unitEmploymentPositionDTO.getStaffId());
            positionCode = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(parentOrganization.getId(), unitEmploymentPositionDTO.getPositionCodeId());
        } else {
            unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(organization.getId(), unitEmploymentPositionDTO.getStaffId());
            positionCode = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(organization.getId(), unitEmploymentPositionDTO.getPositionCodeId());
        }
        if (!Optional.ofNullable(unitPermission).isPresent()) {
            logger.info("Unable to get Unit employment of this staff ,{} in organization,{}", unitEmploymentPositionDTO.getStaffId(), organization.getId());
            throw new DataNotFoundByIdException("unable to create position_code of staff");
        }

        if (!Optional.ofNullable(positionCode).isPresent()) {
            throw new DataNotFoundByIdException("position_code Name does not exist in unit " + unitEmploymentPositionDTO.getPositionCodeId());
        }

        List<UnitPosition> oldUnitPositions = unitEmploymentPositionGraphRepository.getAllUEPByExpertise(unitEmploymentPositionDTO.getExpertiseId(), unitPermission.getId());
        validateUnitEmploymentPositionWithExpertise(oldUnitPositions, unitEmploymentPositionDTO);
        UnitPosition unitPosition = preparePosition(unitEmploymentPositionDTO, organization, id, createFromTimeCare);

        unitPosition.setPositionCode(positionCode);

        List<UnitPosition> unitPositions = unitPermission.getUnitPositions();

        unitPositions.add(unitPosition);
        unitPermission.setUnitPositions(unitPositions);
        save(unitPermission);
        UnitEmploymentPositionQueryResult unitEmploymentPositionQueryResult = unitPosition.getBasicDetails();

        //unitEmploymentPositionQueryResult.setUnion();
        return unitEmploymentPositionQueryResult;
    }

    public boolean validateUnitEmploymentPositionWithExpertise(List<UnitPosition> unitPositions, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {

        Long newUEPStartDateMillis = unitEmploymentPositionDTO.getStartDateMillis();
        Long newUEPEndDateMillis = (unitEmploymentPositionDTO.getEndDateMillis() != null) ? unitEmploymentPositionDTO.getEndDateMillis() : null;
        unitPositions.forEach(unitEmploymentPosition -> {
            // if null date is set
            if (unitEmploymentPosition.getEndDateMillis() != null) {
                logger.info("new UEP start {} " + new DateTime(newUEPStartDateMillis).toLocalDate() + " new UEP End date " + (new DateTime(newUEPEndDateMillis)).toLocalDate(),
                        " current Employment  " + new DateTime(unitEmploymentPosition.getStartDateMillis()).toLocalDate() + " unitEmployment End date   " + (new DateTime(unitEmploymentPosition.getEndDateMillis())).toLocalDate());

                if (new DateTime(newUEPStartDateMillis).isBefore(new DateTime(unitEmploymentPosition.getEndDateMillis()))) {
                    throw new ActionNotPermittedException("Already a unit employment position_code is active with same expertise on this period(End date overlap with start Date)" + new DateTime(newUEPEndDateMillis).toDate() + " --> " + new DateTime(unitEmploymentPosition.getStartDateMillis()).toDate());
                }
                if (newUEPEndDateMillis != null) {
                    Interval previousInterval = new Interval(unitEmploymentPosition.getStartDateMillis(), unitEmploymentPosition.getEndDateMillis());
                    Interval interval = new Interval(newUEPStartDateMillis, newUEPEndDateMillis);
                    logger.info(" Interval of CURRENT UEP " + previousInterval + " Interval of going to create  " + interval);
                    if (previousInterval.overlaps(interval))
                        throw new ActionNotPermittedException("Already a unit employment position_code is active with same expertise on this period(End date overlap with start Date)");

                } else {
                    logger.info("new UEP EndDate {}", new DateTime(newUEPStartDateMillis) + " unitEmployment End date " + (new DateTime(unitEmploymentPosition.getEndDateMillis())));
                    if (new DateTime(newUEPStartDateMillis).isBefore(new DateTime(unitEmploymentPosition.getEndDateMillis()))) {
                        throw new ActionNotPermittedException("Already a unit employment position_code is active with same expertise on this period(End date overlap with start Date)." + new DateTime(newUEPEndDateMillis).toDate() + " --> " + new DateTime(unitEmploymentPosition.getEndDateMillis()).toDate());
                    }
                }
            } else {
                // unitEmploymentEnd date is null
                if (newUEPEndDateMillis != null) {
                    logger.info("new UEP EndDate " + new DateTime(newUEPEndDateMillis) + " running  UEP Start date " + (new DateTime(unitEmploymentPosition.getStartDateMillis())));
                    if (new DateTime(newUEPEndDateMillis).isAfter(new DateTime(unitEmploymentPosition.getStartDateMillis()))) {
                        throw new ActionNotPermittedException("Already a unit employment position_code is active with same expertise on this period(End date overlap with start Date)" + new DateTime(newUEPEndDateMillis).toDate() + " --> " + new DateTime(unitEmploymentPosition.getStartDateMillis()).toDate());
                    }
                } else {
                    logger.info("new UEP start date " + new DateTime(newUEPStartDateMillis) + " new UEP End date ", new DateTime(newUEPEndDateMillis));
                    throw new ActionNotPermittedException("Already a unit employment position_code is active with same expertise on this period.");
                }
            }
        });
        return true;
    }


    public PositionWrapper updateUnitEmploymentPosition(long unitEmploymentPositionId, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {

        List<ClientMinimumDTO> clientMinimumDTO = clientGraphRepository.getCitizenListForThisContactPerson(unitEmploymentPositionDTO.getStaffId());
        if (clientMinimumDTO.size() > 0) {
            return new PositionWrapper(clientMinimumDTO);
        }

        UnitPosition oldUnitPosition = unitEmploymentPositionGraphRepository.findOne(unitEmploymentPositionId);
        if (!Optional.ofNullable(oldUnitPosition).isPresent()) {
            throw new DataNotFoundByIdException("Invalid positionId id " + unitEmploymentPositionId + " while updating the position_code");
        }
        // findEmployment by UnitEmployment Id
        Long unitEmploymentId = unitEmploymentPositionGraphRepository.findEmploymentByUnitEmploymentPosition(unitEmploymentPositionId);

        List<UnitPosition> oldUnitPositions;
        oldUnitPositions = unitEmploymentPositionGraphRepository.getAllUEPByExpertiseExcludingCurrent(unitEmploymentPositionDTO.getExpertiseId(), unitEmploymentId, unitEmploymentPositionId);
        validateUnitEmploymentPositionWithExpertise(oldUnitPositions, unitEmploymentPositionDTO);

        preparePosition(oldUnitPosition, unitEmploymentPositionDTO);
        save(oldUnitPosition);
        return new PositionWrapper(oldUnitPosition);

    }

    public boolean removePosition(long positionId) {
        UnitPosition unitPosition = unitEmploymentPositionGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(unitPosition).isPresent()) {
            return false;
        }
        unitPosition.setDeleted(true);
        save(unitPosition);
        return true;
    }


    public UnitPosition getUnitEmploymentPosition(long positionId) {
        return unitEmploymentPositionGraphRepository.findOne(positionId);
    }

    /*
    * Created by vipul
    * 4-august-17
    * used to get all positions based on unitEmployment
    *

    public List<UnitEmploymentPositionQueryResult> getAllUnitEmploymentPositions(long unitEmploymentId) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        Organization parentOrganization;
        UnitEmployment unitEmployment;
        PositionCode position_code = null;
        if (!organization.isParentOrganization()) {
            parentOrganization = organizationService.getParentOfOrganization(organization.getId());
            unitEmployment = unitEmploymentGraphRepository.checkUnitPermissionOfStaff(parentOrganization.getId(), organization.getId(), unitEmploymentPositionDTO.getStaffId());
            position_code = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(parentOrganization.getId(), unitEmploymentPositionDTO.getPositionCodeId());
        } else {
            unitEmployment = unitEmploymentGraphRepository.checkUnitPermissionOfStaff(organization.getId(), unitEmploymentPositionDTO.getStaffId());
            position_code = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(organization.getId(), unitEmploymentPositionDTO.getPositionCodeId());
        }
        if (!Optional.ofNullable(unitEmployment).isPresent()) {
            logger.info("Unable to get Unit employment of this staff ,{} in organization,{}", unitEmploymentPositionDTO.getStaffId(), organization.getId());
            throw new DataNotFoundByIdException("unable to create position_code of staff");
        }


        if (unitEmployment == null) {
            throw new DataNotFoundByIdException("Invalid UnitEmployment id" + unitEmploymentId);
        }
        return unitEmploymentPositionGraphRepository.findAllUnitEmploymentPositions(unitEmploymentId);

    }
 */
    private UnitPosition preparePosition(UnitEmploymentPositionDTO unitEmploymentPositionDTO, Organization organization, Long unitId, Boolean createFromTimeCare) {
        UnitPosition unitPosition = new UnitPosition();

        if (Optional.ofNullable(unitEmploymentPositionDTO.getUnionId()).isPresent()) {
            Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitEmploymentPositionDTO.getUnionId());
            if (!Optional.ofNullable(union).isPresent()) {
                throw new DataNotFoundByIdException(" union does not exist in unit " + unitEmploymentPositionDTO.getUnionId());
            }
            unitPosition.setUnion(union);
        }

        Optional<WorkingTimeAgreement> wta = workingTimeAgreementGraphRepository.findById(unitEmploymentPositionDTO.getWtaId());
        if (!wta.isPresent()) {
            throw new DataNotFoundByIdException("Invalid wta id ");
        }
        unitPosition.setWorkingTimeAgreement(wta.get());


        CostTimeAgreement cta = (unitEmploymentPositionDTO.getCtaId() == null) ? null :
                costTimeAgreementGraphRepository.findOne(unitEmploymentPositionDTO.getCtaId());
        if (cta != null) {
            unitPosition.setCta(cta);
        }

        Optional<Expertise> expertise = expertiseGraphRepository.findById(unitEmploymentPositionDTO.getExpertiseId());
        if (!expertise.isPresent()) {
            throw new DataNotFoundByIdException("Invalid expertise id");
        }
        unitPosition.setExpertise(expertise.get());

        EmploymentType employmentType = organizationGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(organization.getId(), unitEmploymentPositionDTO.getEmploymentTypeId(), false);
        if (!Optional.ofNullable(employmentType).isPresent()) {
            throw new DataNotFoundByIdException("Employment Type does not exist in unit " + employmentType.getId() + " AND " + unitEmploymentPositionDTO.getEmploymentTypeId());
        }
        unitPosition.setEmploymentType(employmentType);


        Staff staff = staffGraphRepository.findOne(unitEmploymentPositionDTO.getStaffId());
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + unitEmploymentPositionDTO.getStaffId());
        }
        unitPosition.setStaff(staff);

        // UEP can be created for past dates from time care
        if (!createFromTimeCare && unitEmploymentPositionDTO.getStartDateMillis() < System.currentTimeMillis()) {
            throw new ActionNotPermittedException("Start date can't be less than current Date ");
        }
        unitPosition.setStartDateMillis(unitEmploymentPositionDTO.getStartDateMillis());


        if (Optional.ofNullable(unitEmploymentPositionDTO.getEndDateMillis()).isPresent()) {
            if (unitEmploymentPositionDTO.getStartDateMillis() > unitEmploymentPositionDTO.getEndDateMillis()) {
                throw new ActionNotPermittedException("Start date can't be less than End Date ");

            }
            unitPosition.setEndDateMillis(unitEmploymentPositionDTO.getEndDateMillis());
        }
        unitPosition.setTotalWeeklyMinutes(unitEmploymentPositionDTO.getTotalWeeklyMinutes() + (unitEmploymentPositionDTO.getTotalWeeklyHours() * 60));
        unitPosition.setAvgDailyWorkingHours(unitEmploymentPositionDTO.getAvgDailyWorkingHours());
        unitPosition.setHourlyWages(unitEmploymentPositionDTO.getHourlyWages());
        unitPosition.setSalary(unitEmploymentPositionDTO.getSalary());
        unitPosition.setWorkingDaysInWeek(unitEmploymentPositionDTO.getWorkingDaysInWeek());

        return unitPosition;
    }

    private void prepareUnion(UnitPosition oldUnitPosition, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {

        // If already selected but now no value so we are removing
        if (!Optional.ofNullable(unitEmploymentPositionDTO.getUnionId()).isPresent() && Optional.ofNullable(oldUnitPosition.getUnion()).isPresent()) {
            oldUnitPosition.setUnion(null);

        }

// If already not present now its present    Previous its absent
       else if (Optional.ofNullable(unitEmploymentPositionDTO.getUnionId()).isPresent() && !Optional.ofNullable(oldUnitPosition.getUnion()).isPresent()) {
            Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitEmploymentPositionDTO.getUnionId());
            if (!Optional.ofNullable(union).isPresent()) {
                throw new DataNotFoundByIdException(" union does not exist in unit " + unitEmploymentPositionDTO.getUnionId());
            }
            oldUnitPosition.setUnion(union);

        }

// If already present and still present but a different

        else if (Optional.ofNullable(unitEmploymentPositionDTO.getUnionId()).isPresent() && Optional.ofNullable(oldUnitPosition.getUnion()).isPresent()) {
            if (!unitEmploymentPositionDTO.getUnionId().equals(oldUnitPosition.getUnion())) {
                Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitEmploymentPositionDTO.getUnionId());
                if (!Optional.ofNullable(union).isPresent()) {
                    throw new DataNotFoundByIdException(" union does not exist in unit " + unitEmploymentPositionDTO.getUnionId());
                }
                oldUnitPosition.setUnion(union);
            }
        }

    }

    private void preparePosition(UnitPosition oldUnitPosition, UnitEmploymentPositionDTO unitEmploymentPositionDTO) {

        prepareUnion(oldUnitPosition, unitEmploymentPositionDTO);

        CostTimeAgreement cta = (unitEmploymentPositionDTO.getCtaId() == null) ? null :
                costTimeAgreementGraphRepository.findOne(unitEmploymentPositionDTO.getCtaId());
        if (cta != null) {
            oldUnitPosition.setCta(cta);
        }
        if (!oldUnitPosition.getExpertise().getId().equals(unitEmploymentPositionDTO.getExpertiseId())) {
            Expertise expertise = expertiseGraphRepository.findOne(unitEmploymentPositionDTO.getExpertiseId());
            if (!Optional.ofNullable(expertise).isPresent()) {
                throw new DataNotFoundByIdException("Invalid expertise id");
            }
            oldUnitPosition.setExpertise(expertise);
        }
        if (!oldUnitPosition.getPositionCode().getId().equals(unitEmploymentPositionDTO.getPositionCodeId())) {
            PositionCode positionCode = positionCodeGraphRepository.findOne(unitEmploymentPositionDTO.getPositionCodeId());
            if (!Optional.ofNullable(positionCode).isPresent()) {
                throw new DataNotFoundByIdException("PositionCode Cannot be null" + unitEmploymentPositionDTO.getPositionCodeId());
            }
            oldUnitPosition.setPositionCode(positionCode);

        }

        if (!oldUnitPosition.getEmploymentType().getId().equals(unitEmploymentPositionDTO.getEmploymentTypeId())) {
            EmploymentType employmentType = employmentTypeGraphRepository.findOne(unitEmploymentPositionDTO.getEmploymentTypeId());
            if (!Optional.ofNullable(employmentType).isPresent()) {
                throw new DataNotFoundByIdException("employmentType Cannot be null" + unitEmploymentPositionDTO.getEmploymentTypeId());
            }
            oldUnitPosition.setEmploymentType(employmentType);
        }

        if (Optional.ofNullable(unitEmploymentPositionDTO.getEndDateMillis()).isPresent()) {

            if (unitEmploymentPositionDTO.getStartDateMillis() > unitEmploymentPositionDTO.getEndDateMillis()) {
                throw new ActionNotPermittedException("Start date can't be less than End Date ");
            }
            oldUnitPosition.setEndDateMillis(unitEmploymentPositionDTO.getEndDateMillis());
        }
        oldUnitPosition.setStartDateMillis(unitEmploymentPositionDTO.getStartDateMillis());


        oldUnitPosition.setWorkingDaysInWeek(unitEmploymentPositionDTO.getWorkingDaysInWeek());
        oldUnitPosition.setTotalWeeklyMinutes(unitEmploymentPositionDTO.getTotalWeeklyMinutes() + (unitEmploymentPositionDTO.getTotalWeeklyHours() * 60));
        oldUnitPosition.setAvgDailyWorkingHours(unitEmploymentPositionDTO.getAvgDailyWorkingHours());
        oldUnitPosition.setHourlyWages(unitEmploymentPositionDTO.getHourlyWages());
        oldUnitPosition.setSalary(unitEmploymentPositionDTO.getSalary());


    }

    /*
     * @auth vipul
     * used to get all positions of organization n buy organization and staff Id
     * */
    public List<UnitEmploymentPositionQueryResult> getAllUnitEmploymentPositionsOfStaff(long id, long staffId, String type) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + staffId);
        }

        Organization organization = organizationService.getOrganizationDetail(id, type);
        Organization parentOrganization;
        UnitPermission unitPermission;
        if (!organization.isParentOrganization()) {
            parentOrganization = organizationService.getParentOfOrganization(organization.getId());
            unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(parentOrganization.getId(), organization.getId(), staffId);
        } else {
            unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(organization.getId(), staffId);
        }
        if (!Optional.ofNullable(unitPermission).isPresent()) {
            logger.info("Unable to get Unit employment of this staff ,{} in organization,{}", staffId, organization.getId());
            throw new DataNotFoundByIdException("unable to get unit employment  of staff");
        }

        return unitEmploymentPositionGraphRepository.getAllUnitEmploymentPositionByStaff(unitPermission.getId(), staffId);
    }

    public PositionCtaWtaQueryResult getCtaAndWtaByExpertiseId(Long unitId, Long expertiseId) {
        PositionCtaWtaQueryResult positionCtaWtaQueryResult = new PositionCtaWtaQueryResult();
        positionCtaWtaQueryResult.setCta(unitEmploymentPositionGraphRepository.getCtaByExpertise(unitId, expertiseId));
        positionCtaWtaQueryResult.setWta(unitEmploymentPositionGraphRepository.getWtaByExpertise(unitId, expertiseId));
        return positionCtaWtaQueryResult;
    }

    public UnitEmploymentPositionQueryResult updateUnitEmploymentPositionWTA(Long unitId, Long unitEmploymentPositionId, Long wtaId, WTADTO updateDTO) {
        UnitPosition unitPosition = unitEmploymentPositionGraphRepository.findOne(unitEmploymentPositionId);
        if (!Optional.ofNullable(unitPosition).isPresent()) {
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
        unitPosition.setWorkingTimeAgreement(newWta);
        save(unitPosition);
        UnitEmploymentPositionQueryResult unitEmploymentPositionQueryResult = unitPosition.getBasicDetails();
        newWta.setParentWTA(oldWta.basicDetails());

        newWta.setExpertise(newWta.getExpertise().retrieveBasicDetails());
        unitEmploymentPositionQueryResult.setWorkingTimeAgreement(newWta);
        return unitEmploymentPositionQueryResult;
    }

    public WTAResponseDTO getUnitEmploymentPositionWTA(Long unitId, Long unitEmploymentPositionId) {
        UnitPosition unitPosition = unitEmploymentPositionGraphRepository.findOne(unitEmploymentPositionId);
        if (!Optional.ofNullable(unitPosition).isPresent() || unitPosition.isDeleted() == true) {
            throw new DataNotFoundByIdException("Invalid unit Employment Position id" + unitEmploymentPositionId);
        }
        WTAResponseDTO workingTimeAgreement = workingTimeAgreementGraphRepository.findWtaByUnitEmploymentPosition(unitEmploymentPositionId);
        return workingTimeAgreement;
    }

    public CostTimeAgreementDTO getUnitEmploymentPositionCTA(Long unitEmploymentPositionId) {
        UnitPosition unitPosition = unitEmploymentPositionGraphRepository.findOne(unitEmploymentPositionId);
        unitEmploymentPositionGraphRepository.getCtaByUnitEmploymentId(unitEmploymentPositionId);
        CostTimeAgreementDTO costTimeAgreementDTO = new CostTimeAgreementDTO(unitEmploymentPositionId);
        costTimeAgreementDTO.setStaffId(unitPosition.getStaff().getId());
        costTimeAgreementDTO.setContractedMinByWeek(unitPosition.getTotalWeeklyMinutes());
        costTimeAgreementDTO.setWorkingDays(unitPosition.getWorkingDaysInWeek());
        costTimeAgreementDTO.setCtaRuleTemplateDTOS(getCtaRuleTemplateDtos(unitPosition.getCta().getRuleTemplates()));
        return costTimeAgreementDTO;
    }

    public List<com.kairos.client.dto.timeBank.CTARuleTemplateDTO> getCtaRuleTemplateDtos(List<RuleTemplate> ruleTemplates){
        List<com.kairos.client.dto.timeBank.CTARuleTemplateDTO> ctaRuleTemplateDTOS = new ArrayList<>(ruleTemplates.size());
        ruleTemplates.forEach(rt->{
            com.kairos.client.dto.timeBank.CTARuleTemplateDTO ctaRuleTemplateDTO = new com.kairos.client.dto.timeBank.CTARuleTemplateDTO();
            ctaRuleTemplateDTOS.add(ctaRuleTemplateDTO);

        });
        return ctaRuleTemplateDTOS;
    }

    public UnitEmploymentPositionDTO convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(TimeCareEmploymentDTO timeCareEmploymentDTO,  Long expertiseId, Long staffId, Long employmentTypeId, Long positionCodeId, Long wtaId, Long ctaId){
        Long startDateMillis = DateConverter.convertInUTCTimestamp(timeCareEmploymentDTO.getStartDate());
        Long endDateMillis = null;
        if (!timeCareEmploymentDTO.getEndDate().equals("0001-01-01T00:00:00")) {
            endDateMillis = DateConverter.convertInUTCTimestamp(timeCareEmploymentDTO.getEndDate());
        }
        UnitEmploymentPositionDTO unitEmploymentPositionDTO = new UnitEmploymentPositionDTO(positionCodeId, expertiseId, startDateMillis, endDateMillis, Integer.parseInt(timeCareEmploymentDTO.getWeeklyHours()), employmentTypeId, staffId, wtaId, ctaId);
        return unitEmploymentPositionDTO;
    }

    public boolean addEmploymentToUnitByExternalId(List<TimeCareEmploymentDTO> timeCareEmploymentDTOs, String unitExternalId, Long expertiseId) {
        Organization organization = organizationGraphRepository.findByExternalId(unitExternalId);
        if (organization == null) {
            throw new DataNotFoundByIdException("Invalid organization external id" + unitExternalId);
        }
        Organization parentOrganization = organizationService.fetchParentOrganization(organization.getId());

        Long countryId = organizationService.getCountryIdOfOrganization(parentOrganization.getId());
        EmploymentType employmentType = employmentTypeGraphRepository.getOneEmploymentTypeByCountryId(countryId, false);

        Expertise expertise = null;
        if (expertiseId == null) {
            expertise = expertiseGraphRepository.getOneDefaultExpertiseByCountry(countryId);
        } else {
            expertise = expertiseGraphRepository.getExpertiesOfCountry(countryId, expertiseId);
        }

        if (expertise == null) {
            throw new DataNotFoundByIdException("NO expertise found or Invalid expertise Id : " + expertiseId);
        }

        WorkingTimeAgreement wta = unitEmploymentPositionGraphRepository.getOneDefaultWTA(organization.getId(), expertise.getId());
        CostTimeAgreement cta = unitEmploymentPositionGraphRepository.getOneDefaultCTA(organization.getId(), expertise.getId());
        if (wta == null) {
            throw new DataNotFoundByIdException("NO WTA found for organization : " + organization.getId());
        }
        if (cta == null) {
            throw new DataNotFoundByIdException("NO CTA found for organization : " + organization.getId());
        }

        PositionCode positionCode = positionCodeGraphRepository.getOneDefaultPositionCodeByUnitId(parentOrganization.getId());
        if (positionCode == null) {
            throw new DataNotFoundByIdException("NO Position code exist in organization : " + parentOrganization.getId());
        }

        for ( TimeCareEmploymentDTO timeCareEmploymentDTO : timeCareEmploymentDTOs) {
            Staff staff = staffGraphRepository.findStaffByExternalId(timeCareEmploymentDTO.getPersonID(), organization.getId());
            if(staff == null){
                throw new DataNotFoundByIdException("NO staff exist with External Id : " + timeCareEmploymentDTO.getPersonID());
            }
            UnitEmploymentPositionDTO unitEmploymentPosition = convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(timeCareEmploymentDTO, expertise.getId(), staff.getId(), employmentType.getId(), positionCode.getId(), wta.getId(), cta.getId());
            createUnitEmploymentPosition(organization.getId(), "Organization", unitEmploymentPosition, true);
        }
        return true;
    }

    public boolean importAllEmploymentsFromTimeCare(List<TimeCareEmploymentDTO> timeCareEmploymentsDTOs, Long expertiseId) {

        // To prepare list of organization's external Id
        Set<String> listOfWorkPlaceIds = new HashSet<>();
        for (TimeCareEmploymentDTO timeCareStaffDTO : timeCareEmploymentsDTOs) {
            listOfWorkPlaceIds.add(timeCareStaffDTO.getWorkPlaceID());
        }

        for (String workPlaceId : listOfWorkPlaceIds) {
            List<TimeCareEmploymentDTO> timeCareEmploymentsByWorkPlace = timeCareEmploymentsDTOs.stream().filter(timeCareEmploymentDTO -> timeCareEmploymentDTO.getWorkPlaceID().equals(workPlaceId)).
                    collect(Collectors.toList());
            addEmploymentToUnitByExternalId(timeCareEmploymentsByWorkPlace, workPlaceId, expertiseId);
        }

        return true;
    }


}
