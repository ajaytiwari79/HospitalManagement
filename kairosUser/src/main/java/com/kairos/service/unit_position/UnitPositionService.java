package com.kairos.service.unit_position;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kairos.activity.enums.IntegrationOperation;
import com.kairos.client.WorkingTimeAgreementRestClient;
import com.kairos.client.dto.time_bank.CTAIntervalDTO;
import com.kairos.client.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.client.planner.PlannerRestClient;
import com.kairos.persistence.model.user.country.DayType;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.persistence.model.user.staff.*;
import com.kairos.persistence.repository.user.staff.EmploymentGraphRepository;

import com.kairos.response.dto.web.UnitPositionDTO;
import com.kairos.client.TimeBankRestClient;

import com.kairos.client.dto.time_bank.CTARuleTemplateDTO;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.*;
import com.kairos.response.dto.web.wta.WTADTO;
import com.kairos.response.dto.web.wta.WTAResponseDTO;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.country.EmploymentType;

import com.kairos.persistence.model.user.country.Function;
import com.kairos.persistence.model.user.country.ReasonCode;
import com.kairos.persistence.model.user.expertise.Expertise;

import com.kairos.persistence.model.user.expertise.FunctionAndSeniorityLevelQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.persistence.model.user.unit_position.*;

import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.cta.CollectiveTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.FunctionGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;

import com.kairos.persistence.repository.user.expertise.SeniorityLevelGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.positionCode.PositionCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionEmploymentTypeRelationShipGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;

import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitPermissionGraphRepository;

import com.kairos.response.dto.web.PositionWrapper;

import com.kairos.service.UserBaseService;
import com.kairos.service.agreement.wta.WTAService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.position_code.PositionCodeService;
import com.kairos.service.staff.EmploymentService;
import com.kairos.service.staff.StaffService;
import com.kairos.util.DateConverter;
import com.kairos.util.DateUtil;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Months;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.time.DayOfWeek;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@Transactional
@Service

public class UnitPositionService extends UserBaseService {
    private final Logger logger = LoggerFactory.getLogger(UnitPositionService.class);
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private UnitPositionGraphRepository unitPositionGraphRepository;
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
    @Inject
    private TimeBankRestClient timeBankRestClient;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private UnitPositionEmploymentTypeRelationShipGraphRepository unitPositionEmploymentTypeRelationShipGraphRepository;
    @Inject
    private ReasonCodeGraphRepository reasonCodeGraphRepository;
    @Inject
    private SeniorityLevelGraphRepository seniorityLevelGraphRepository;
    @Inject
    private PayGradeGraphRepository payGradeGraphRepository;
    @Inject
    private FunctionGraphRepository functionGraphRepository;
    @Inject
    private StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Inject
    private EmploymentService employmentService;
    @Inject private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject private WorkingTimeAgreementRestClient workingTimeAgreementRestClient;
    @Inject
    private PlannerSyncService plannerSyncService;

    public PositionWrapper createUnitPosition(Long id, String type, UnitPositionDTO unitPositionDTO, Boolean createFromTimeCare) {

        Organization organization = organizationService.getOrganizationDetail(id, type);
        Organization parentOrganization;

        PositionCode positionCode = null;
        if (!organization.isParentOrganization()) {
            parentOrganization = organizationService.getParentOfOrganization(organization.getId());
            positionCode = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(parentOrganization.getId(), unitPositionDTO.getPositionCodeId());
        } else {
            parentOrganization = organization;
            positionCode = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(organization.getId(), unitPositionDTO.getPositionCodeId());
        }


        if (!Optional.ofNullable(positionCode).isPresent()) {
            throw new DataNotFoundByIdException("position_code Name does not exist in unit " + unitPositionDTO.getPositionCodeId());
        }

        List<UnitPosition> oldUnitPositions = unitPositionGraphRepository.getAllUEPByExpertise(organization.getId(), unitPositionDTO.getStaffId(), unitPositionDTO.getExpertiseId());
        validateUnitPositionWithExpertise(oldUnitPositions, unitPositionDTO);
        UnitPosition unitPosition = new UnitPosition();

        EmploymentType employmentType = organizationGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(parentOrganization.getId(), unitPositionDTO.getEmploymentTypeId(), false);
        if (!Optional.ofNullable(employmentType).isPresent()) {
            throw new DataNotFoundByIdException("Employment Type does not exist in unit " + unitPositionDTO.getEmploymentTypeId());
        }

        preparePosition(unitPosition, unitPositionDTO, organization, parentOrganization, createFromTimeCare);

        unitPosition.setPositionCode(positionCode);

        unitPosition.setUnit(organization);
        save(unitPosition);
        Employment employment = employmentService.updateEmploymentEndDate(organization, unitPositionDTO.getStaffId(),unitPositionDTO.getEndDateMillis());

        UnitPositionEmploymentTypeRelationShip relationShip = new UnitPositionEmploymentTypeRelationShip(unitPosition, employmentType, unitPositionDTO.getEmploymentTypeCategory());
        unitPositionEmploymentTypeRelationShipGraphRepository.save(relationShip);

        UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPositionDTO, unitPosition, relationShip, parentOrganization.getId());
        PositionWrapper positionWrapper = new PositionWrapper(unitPositionQueryResult,new EmploymentQueryResult(employment.getId(),employment.getStartDateMillis(),employment.getEndDateMillis()));
//        timeBankRestClient.createBlankTimeBank(getUnitPositionCTA(unitPosition.getId(), organization.getId()));

        //      UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPosition);
        //timeBankRestClient.createBlankTimeBank(getUnitPositionCTA(unitPosition.getId(),id));

        plannerSyncService.publishUnitPosition(id,unitPosition,employmentType,IntegrationOperation.CREATE);
        return positionWrapper;
    }


    public boolean validateUnitPositionWithExpertise(List<UnitPosition> unitPositions, UnitPositionDTO unitPositionDTO) {

        Long newUEPStartDateMillis = unitPositionDTO.getStartDateMillis();
        Long newUEPEndDateMillis = (unitPositionDTO.getEndDateMillis() != null) ? unitPositionDTO.getEndDateMillis() : null;
        unitPositions.forEach(unitEmploymentPosition -> {
            // if null date is set
            if (unitEmploymentPosition.getEndDateMillis() != null) {
                logger.info("new UEP start {} " + new DateTime(newUEPStartDateMillis).toLocalDate() + " new UEP End date " + (new DateTime(newUEPEndDateMillis)).toLocalDate(),
                        " current Employment  " + new DateTime(unitEmploymentPosition.getStartDateMillis()).toLocalDate() + " unitEmployment End date   " + (new DateTime(unitEmploymentPosition.getEndDateMillis())).toLocalDate());

                if (new DateTime(newUEPStartDateMillis).isBefore(new DateTime(unitEmploymentPosition.getEndDateMillis()))&&new DateTime(newUEPStartDateMillis).isAfter(new DateTime(unitEmploymentPosition.getStartDateMillis()))) {
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


    public PositionWrapper updateUnitPosition(long unitPositionId, UnitPositionDTO unitPositionDTO, Long unitId) {

        List<ClientMinimumDTO> clientMinimumDTO = clientGraphRepository.getCitizenListForThisContactPerson(unitPositionDTO.getStaffId());
        if (clientMinimumDTO.size() > 0) {
            return new PositionWrapper(clientMinimumDTO);
        }

        UnitPosition oldUnitPosition = unitPositionGraphRepository.findOne(unitPositionId);
        if (!Optional.ofNullable(oldUnitPosition).isPresent()) {
            throw new DataNotFoundByIdException("Invalid positionId id " + unitPositionId + " while updating the position_code");
        }

        List<UnitPosition> oldUnitPositions
                = unitPositionGraphRepository.getAllUEPByExpertiseExcludingCurrent(unitPositionDTO.getUnitId(), unitPositionDTO.getStaffId(), unitPositionDTO.getExpertiseId(), unitPositionId);
        validateUnitPositionWithExpertise(oldUnitPositions, unitPositionDTO);

        UnitPositionEmploymentTypeRelationShip unitPositionEmploymentTypeRelationShip = unitPositionGraphRepository.findEmploymentTypeByUnitPositionId(unitPositionId);
        if (unitPositionEmploymentTypeRelationShip != null) {
            // user has changed need to remove previous and add new
            if (unitPositionEmploymentTypeRelationShip.getEmploymentType().getId() != unitPositionDTO.getEmploymentTypeId()) {
                EmploymentType employmentType = employmentTypeGraphRepository.findOne(unitPositionDTO.getEmploymentTypeId());
                if (!Optional.ofNullable(employmentType).isPresent()) {
                    throw new DataNotFoundByIdException("employmentType Cannot be null" + unitPositionDTO.getEmploymentTypeId());
                }
                unitPositionEmploymentTypeRelationShipGraphRepository.delete(unitPositionEmploymentTypeRelationShip);
                unitPositionEmploymentTypeRelationShip = new UnitPositionEmploymentTypeRelationShip(oldUnitPosition, employmentType, unitPositionDTO.getEmploymentTypeCategory());
            }
            // user has changed the type
            else if (!unitPositionDTO.getEmploymentTypeCategory().equals(unitPositionEmploymentTypeRelationShip.getEmploymentTypeCategory())) {
                unitPositionEmploymentTypeRelationShip.setEmploymentTypeCategory(unitPositionDTO.getEmploymentTypeCategory());
            }
            unitPositionEmploymentTypeRelationShipGraphRepository.save(unitPositionEmploymentTypeRelationShip);
            logger.info(unitPositionEmploymentTypeRelationShip.toString());
        }
        preparePosition(oldUnitPosition, unitPositionDTO);
        save(oldUnitPosition);
        Employment employment = employmentService.updateEmploymentEndDate(oldUnitPosition.getUnit(), unitPositionDTO.getStaffId(),
                unitPositionDTO.getEndDateMillis());
        EmploymentQueryResult employmentQueryResult = new EmploymentQueryResult(employment.getId(),employment.getStartDateMillis(),employment.getEndDateMillis());
        plannerSyncService.publishUnitPosition(unitId,oldUnitPosition,unitPositionEmploymentTypeRelationShip.getEmploymentType(),IntegrationOperation.UPDATE);
        return new PositionWrapper(getBasicDetails(unitPositionDTO, oldUnitPosition, unitPositionEmploymentTypeRelationShip, null),employmentQueryResult);

    }

    public EmploymentQueryResult removePosition(long positionId, Long unitId) {
        UnitPosition unitPosition = unitPositionGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(unitPosition).isPresent()) {
            throw new DataNotFoundByIdException("UnitPosition doesn't exist with id: "+ positionId);
        }
        unitPosition.setDeleted(true);
        save(unitPosition);

        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        Long staffId = unitPositionGraphRepository.getStaffIdFromUnitPosition(positionId);
        Employment employment = employmentService.updateEmploymentEndDate(unit,staffId);
        plannerSyncService.publishUnitPosition(unitId,unitPosition,null,IntegrationOperation.DELETE);
        return new EmploymentQueryResult(employment.getId(),employment.getStartDateMillis(),employment.getEndDateMillis());
    }

   /* private void copyAndLinkNewWTA(UnitPosition unitPosition, WorkingTimeAgreement workingTimeAgreement) {
        WorkingTimeAgreement newWta = new WorkingTimeAgreement();
        wtaService.copyWta(workingTimeAgreement, newWta);
        if (workingTimeAgreement.getRuleTemplates().size() > 0) {
            List<WTABaseRuleTemplate> ruleTemplates = wtaService.copyRuleTemplate(workingTimeAgreement.getRuleTemplates());
            newWta.setRuleTemplates(ruleTemplates);
        }
        //unitPosition.setWorkingTimeAgreement(newWta);
    }*/

    private UnitPosition preparePosition(UnitPosition unitPosition, UnitPositionDTO unitPositionDTO, Organization organization, Organization parentOrganization, Boolean createFromTimeCare) {


        if (Optional.ofNullable(unitPositionDTO.getUnionId()).isPresent()) {
            Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitPositionDTO.getUnionId());
            if (!Optional.ofNullable(union).isPresent()) {
                throw new DataNotFoundByIdException(" union does not exist in unit " + unitPositionDTO.getUnionId());
            }
            unitPosition.setUnion(union);
        }
        Optional<Organization> unitPositionInOrganization = organizationGraphRepository.findById(unitPositionDTO.getUnitId(), 0);
        if (!unitPositionInOrganization.isPresent()) {
            throw new DataNotFoundByIdException("Invalid Organization id ");
        }
        unitPosition.setUnit(unitPositionInOrganization.get());

       /* Optional<WorkingTimeAgreement> wta = workingTimeAgreementGraphRepository.findById(unitPositionDTO.getWtaId());
        if (!wta.isPresent()) {
            throw new DataNotFoundByIdException("Invalid wta id ");
        }
        copyAndLinkNewWTA(unitPosition, wta.get());
*/
       WTAResponseDTO wtaResponseDTO = workingTimeAgreementRestClient.assignWTAToUnitPosition(unitPositionDTO.getWtaId());
        if(wtaResponseDTO==null){
            throw new DataNotFoundByIdException("Invalid wta id ");
        }
        unitPosition.setWorkingTimeAgreementId(wtaResponseDTO.getId());
        CostTimeAgreement cta = (unitPositionDTO.getCtaId() == null) ? null :
                costTimeAgreementGraphRepository.findOne(unitPositionDTO.getCtaId());
        if (cta != null) {
            unitPosition.setCta(cta);
        }

        Optional<Expertise> expertise = expertiseGraphRepository.findById(unitPositionDTO.getExpertiseId(), 0);
        if (!expertise.isPresent()) {
            throw new DataNotFoundByIdException("Invalid expertise id");
        }
        unitPosition.setExpertise(expertise.get());
        unitPosition.setWorkingDaysInWeek(expertise.get().getNumberOfWorkingDaysInWeek());


        Staff staff = staffGraphRepository.findOne(unitPositionDTO.getStaffId());
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + unitPositionDTO.getStaffId());
        }
        unitPosition.setStaff(staff);

        // UEP can be created for past dates from time care
        if (!createFromTimeCare && unitPositionDTO.getStartDateMillis() < System.currentTimeMillis()) {
            throw new ActionNotPermittedException("Start date can't be less than current Date ");
        }
        unitPosition.setStartDateMillis(unitPositionDTO.getStartDateMillis());
        unitPosition.setWorkingDaysInWeek(expertise.get().getNumberOfWorkingDaysInWeek());

        if (Optional.ofNullable(unitPositionDTO.getEndDateMillis()).isPresent()) {
            if (unitPositionDTO.getStartDateMillis() > unitPositionDTO.getEndDateMillis()) {
                throw new ActionNotPermittedException("Start date can't be less than End Date ");
            }
            if (!Optional.ofNullable(unitPositionDTO.getReasonCodeId()).isPresent()) {
                throw new ActionNotPermittedException("Please select region with end date");
            }

            Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(unitPositionDTO.getReasonCodeId(), 0);
            if (!Optional.ofNullable(reasonCode).isPresent()) {
                throw new DataNotFoundByIdException("Invalid reasonCode Id" + unitPositionDTO.getReasonCodeId());
            }
            unitPosition.setReasonCode(reasonCode.get());
            unitPosition.setEndDateMillis(unitPositionDTO.getEndDateMillis());
        }

        if (Optional.ofNullable(unitPositionDTO.getLastWorkingDateMillis()).isPresent()) {
            if (unitPositionDTO.getStartDateMillis() > unitPositionDTO.getLastWorkingDateMillis()) {
                throw new ActionNotPermittedException("Last date can't be less than start Date ");
            }
            unitPosition.setLastWorkingDateMillis(unitPositionDTO.getLastWorkingDateMillis());
        }
        Optional<Expertise> currentExpertise = expertiseGraphRepository.findById(unitPositionDTO.getExpertiseId());
        SeniorityLevel seniorityLevel = getSeniorityLevelByStaffAndExpertise(staff.getId(), currentExpertise.get());
        if (!Optional.ofNullable(seniorityLevel).isPresent()) {
            throw new DataNotFoundByIdException("Invalid seniorityLevel Id" + unitPositionDTO.getReasonCodeId());
        }
        unitPosition.setSeniorityLevel(seniorityLevel);
        List<Function> functions = functionGraphRepository.findAllFunctionsById(unitPositionDTO.getFunctionIds());
        if (functions.size() != unitPositionDTO.getFunctionIds().size()) {
            throw new ActionNotPermittedException("unable to get all functions");
        }
        unitPosition.setFunctions(functions);
        unitPosition.setFullTimeWeeklyMinutes(expertise.get().getFullTimeWeeklyMinutes());
        unitPosition.setTotalWeeklyMinutes(unitPositionDTO.getTotalWeeklyMinutes() + (unitPositionDTO.getTotalWeeklyHours() * 60));
        unitPosition.setAvgDailyWorkingHours(unitPositionDTO.getAvgDailyWorkingHours());
        unitPosition.setHourlyWages(unitPositionDTO.getHourlyWages());
        unitPosition.setSalary(unitPositionDTO.getSalary());
        //unitPosition.setWorkingDaysInWeek(unitPositionDTO.getWorkingDaysInWeek());
        if (createFromTimeCare) {
            unitPosition.setTimeCareExternalId(unitPositionDTO.getTimeCareExternalId());
        }

        return unitPosition;
    }

    private void prepareUnion(UnitPosition oldUnitPosition, UnitPositionDTO unitPositionDTO) {

        // If already selected but now no value so we are removing
        if (!Optional.ofNullable(unitPositionDTO.getUnionId()).isPresent() && Optional.ofNullable(oldUnitPosition.getUnion()).isPresent()) {
            oldUnitPosition.setUnion(null);

        }

// If already not present now its present    Previous its absent
        else if (Optional.ofNullable(unitPositionDTO.getUnionId()).isPresent() && !Optional.ofNullable(oldUnitPosition.getUnion()).isPresent()) {
            Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitPositionDTO.getUnionId());
            if (!Optional.ofNullable(union).isPresent()) {
                throw new DataNotFoundByIdException(" union does not exist in unit " + unitPositionDTO.getUnionId());
            }
            oldUnitPosition.setUnion(union);

        }

// If already present and still present but a different

        else if (Optional.ofNullable(unitPositionDTO.getUnionId()).isPresent() && Optional.ofNullable(oldUnitPosition.getUnion()).isPresent()) {
            if (!unitPositionDTO.getUnionId().equals(oldUnitPosition.getUnion())) {
                Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitPositionDTO.getUnionId());
                if (!Optional.ofNullable(union).isPresent()) {
                    throw new DataNotFoundByIdException(" union does not exist in unit " + unitPositionDTO.getUnionId());
                }
                oldUnitPosition.setUnion(union);
            }
        }

    }

    private void preparePosition(UnitPosition oldUnitPosition, UnitPositionDTO unitPositionDTO) {

        prepareUnion(oldUnitPosition, unitPositionDTO);

        CostTimeAgreement cta = (unitPositionDTO.getCtaId() == null) ? null :
                costTimeAgreementGraphRepository.findOne(unitPositionDTO.getCtaId());
        if (cta != null) {
            oldUnitPosition.setCta(cta);
        }
        if (!oldUnitPosition.getExpertise().getId().equals(unitPositionDTO.getExpertiseId())) {
            throw new ActionNotPermittedException("expertise cant be changed" + unitPositionDTO.getExpertiseId());
//            Expertise expertise = expertiseGraphRepository.findOne(unitPositionDTO.getExpertiseId());
//            if (!Optional.ofNullable(expertise).isPresent()) {
//                throw new DataNotFoundByIdException("Invalid expertise id");
//            }
//            oldUnitPosition.setParentExpertise(expertise);
        }
        if (!oldUnitPosition.getPositionCode().getId().equals(unitPositionDTO.getPositionCodeId())) {
            throw new ActionNotPermittedException("Position Code cant be changed" + unitPositionDTO.getPositionCodeId());
//            PositionCode positionCode = positionCodeGraphRepository.findOne(unitPositionDTO.getPositionCodeId());
//            if (!Optional.ofNullable(positionCode).isPresent()) {
//                throw new DataNotFoundByIdException("Position Code Cannot be null" + unitPositionDTO.getPositionCodeId());
//            }
//            oldUnitPosition.setPositionCode(positionCode);

        }
        Set<Long> olderFunctionsAddedInUnitPosition = oldUnitPosition.getFunctions() != null ? oldUnitPosition.getFunctions().stream().map(Function::getId).collect(Collectors.toSet()) : Collections.emptySet();
        if (olderFunctionsAddedInUnitPosition.equals(unitPositionDTO.getFunctionIds())) {
            if (!olderFunctionsAddedInUnitPosition.isEmpty())
                unitPositionGraphRepository.removeOlderFunctionsFromUnitPosition(oldUnitPosition.getId());
            List<Function> functions = functionGraphRepository.findAllFunctionsById(unitPositionDTO.getFunctionIds());
            if (functions.size() != unitPositionDTO.getFunctionIds().size()) {
                throw new ActionNotPermittedException("unable to get all functions");
            }
            oldUnitPosition.setFunctions(functions);
        }


        if (Optional.ofNullable(unitPositionDTO.getEndDateMillis()).isPresent()) {
            if (unitPositionDTO.getStartDateMillis() > unitPositionDTO.getEndDateMillis()) {
                throw new ActionNotPermittedException("Start date can't be less than End Date ");
            }
        }
        if (Optional.ofNullable(unitPositionDTO.getLastWorkingDateMillis()).isPresent()) {
            if (unitPositionDTO.getStartDateMillis() > unitPositionDTO.getLastWorkingDateMillis()) {
                throw new ActionNotPermittedException("Last  date can't be less than End Date ");
            }
            oldUnitPosition.setLastWorkingDateMillis(unitPositionDTO.getLastWorkingDateMillis());
        }
        oldUnitPosition.setStartDateMillis(unitPositionDTO.getStartDateMillis());
        oldUnitPosition.setEndDateMillis(unitPositionDTO.getEndDateMillis());

        if (Optional.ofNullable(unitPositionDTO.getEndDateMillis()).isPresent()) {

            oldUnitPosition.setEndDateMillis(unitPositionDTO.getEndDateMillis());

            if (!Optional.ofNullable(unitPositionDTO.getReasonCodeId()).isPresent()) {
                throw new ActionNotPermittedException("Please select region with end date");
            }

            if (oldUnitPosition.getReasonCode()==null  || !oldUnitPosition.getReasonCode().getId().equals(unitPositionDTO.getReasonCodeId())) {
                Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(unitPositionDTO.getReasonCodeId(), 0);
                if (!Optional.ofNullable(reasonCode).isPresent()) {
                    throw new DataNotFoundByIdException("Invalid reasonCode Id" + unitPositionDTO.getReasonCodeId());
                }
                oldUnitPosition.setReasonCode(reasonCode.get());
            }
        }
        //oldUnitPosition.setWorkingDaysInWeek(unitPositionDTO.getWorkingDaysInWeek());
        oldUnitPosition.setTotalWeeklyMinutes((unitPositionDTO.getTotalWeeklyHours() * 60));
        oldUnitPosition.setAvgDailyWorkingHours(unitPositionDTO.getAvgDailyWorkingHours());
        oldUnitPosition.setHourlyWages(unitPositionDTO.getHourlyWages());
        oldUnitPosition.setSalary(unitPositionDTO.getSalary());


    }

    /*
     * @author vipul
     * used to get all positions of organization n by organization and staff Id
     * */
    public EmploymentUnitPositionDTO getUnitPositionsOfStaff(long id, long staffId, String type) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + staffId);
        }

        User user = userGraphRepository.getUserByStaffId(staffId);
        Employment employment = employmentGraphRepository.findEmploymentByStaff(staffId);
        EmploymentQueryResult employmentQueryResult = new EmploymentQueryResult(employment.getId(),employment.getStartDateMillis(),employment.getEndDateMillis());
        List<UnitPositionQueryResult> unitPositionQueryResults = unitPositionGraphRepository.getAllUnitPositionsByUser(user.getId());
        List<WTAResponseDTO> wtaResponseDTOS = workingTimeAgreementRestClient.getWTAByIds(unitPositionQueryResults.stream().map(u->u.getWorkingTimeAgreementId()).collect(Collectors.toList()));
        Map<BigInteger,WTAResponseDTO> wtaResponseDTOMap = wtaResponseDTOS.stream().collect(Collectors.toMap(w->w.getId(),w->w));
        unitPositionQueryResults.forEach(u->{
            u.setWorkingTimeAgreement(wtaResponseDTOMap.get(u.getWorkingTimeAgreementId()));
        });
        EmploymentUnitPositionDTO employmentUnitPositionDTO = new EmploymentUnitPositionDTO(employmentQueryResult, unitPositionQueryResults);
        return employmentUnitPositionDTO;

        // TODO  Organization organization = organizationService.getOrganizationDetail(id, type);
//        Organization parentOrganization;
//        UnitPermission unitPermission;
//        if (!organization.isParentOrganization()) {
//            parentOrganization = organizationService.getParentOfOrganization(organization.getId());
//            unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(parentOrganization.getId(), organization.getId(), staffId);
//        } else {
//            unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(organization.getId(), staffId);
//        }
//        if (!Optional.ofNullable(unitPermission).isPresent()) {
//            logger.info("Unable to get Unit employment of this staff ,{} in organization,{}", staffId, organization.getId());
//            throw new DataNotFoundByIdException("unable to get unit employment  of staff");
//        }
        //TODO  return unitPositionGraphRepository.getAllUnitPositionsByStaff(organization.getId(), staffId);
    }

    public PositionCtaWtaQueryResult getCtaAndWtaWithExpertiseDetailByExpertiseId(Long unitId, Long expertiseId, Long staffId) {
        PositionCtaWtaQueryResult positionCtaWtaQueryResult = new PositionCtaWtaQueryResult();
        positionCtaWtaQueryResult.setCta(unitPositionGraphRepository.getCtaByExpertise(unitId, expertiseId));
        List<WTAResponseDTO> wtaResponseDTOS = workingTimeAgreementRestClient.getWTAByExpertise(expertiseId);
        positionCtaWtaQueryResult.setWta(wtaResponseDTOS);

        Optional<Expertise> currentExpertise = expertiseGraphRepository.findById(expertiseId);
        SeniorityLevel appliedSeniorityLevel = getSeniorityLevelByStaffAndExpertise(staffId, currentExpertise.get());
        positionCtaWtaQueryResult.setExpertise(currentExpertise.get().retrieveBasicDetails());
        FunctionAndSeniorityLevelQueryResult seniorityLevel = (appliedSeniorityLevel != null) ? seniorityLevelGraphRepository.getSeniorityLevelById(appliedSeniorityLevel.getId()) : null;
        positionCtaWtaQueryResult.setApplicableSeniorityLevel(seniorityLevel);
        positionCtaWtaQueryResult.setUnion(currentExpertise.get().getUnion());

        return positionCtaWtaQueryResult;
    }

    //TODO this must be moved to activity
    public UnitPositionQueryResult updateUnitPositionWTA(Long unitId, Long unitPositionId, BigInteger wtaId, WTADTO updateDTO) {
        UnitPosition unitPosition = unitPositionGraphRepository.findOne(unitPositionId);
        if (!Optional.ofNullable(unitPosition).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit Employment Position id" + unitPositionId);
        }
       /* WorkingTimeAgreement oldWta = workingTimeAgreementGraphRepository.findOne(wtaId, 2);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating unit Employment Position for staff %d", unitPositionId);
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
*/        //unitPosition.setWorkingTimeAgreement(newWta);
        updateDTO.setId(wtaId);
        WTAResponseDTO wtaResponseDTO = workingTimeAgreementRestClient.updateWTAOfUnitPosition(updateDTO);
        unitPosition.setWorkingTimeAgreementId(wtaResponseDTO.getId());
        save(unitPosition);
        UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPosition);
        //newWta.setParentWTA(oldWta.basicDetails());

        //newWta.setExpertise(newWta.getExpertise().retrieveBasicDetails());
        //unitPositionQueryResult.setWorkingTimeAgreement(newWta);
        plannerSyncService.publishWTA(unitId,unitPositionId,wtaResponseDTO,IntegrationOperation.UPDATE);
        return unitPositionQueryResult;
    }

    private UnitPositionQueryResult getBasicDetails(UnitPositionDTO unitPositionDTO, UnitPosition unitPosition, UnitPositionEmploymentTypeRelationShip relationShip, Long parentOrganizationId) {

        WTAResponseDTO wtaResponseDTO = workingTimeAgreementRestClient.getWTAById(unitPosition.getWorkingTimeAgreementId());
        UnitPositionQueryResult result = new UnitPositionQueryResult(unitPosition.getExpertise().retrieveBasicDetails(), unitPosition.getStartDateMillis(), unitPosition.getWorkingDaysInWeek(),
                unitPosition.getEndDateMillis(), unitPosition.getTotalWeeklyMinutes(), unitPosition.getAvgDailyWorkingHours(), unitPosition.getHourlyWages(),
                unitPosition.getId(), unitPosition.getSalary(), unitPosition.getPositionCode(), unitPosition.getUnion(),
                unitPosition.getLastWorkingDateMillis(), unitPosition.getCta(),wtaResponseDTO);
        result.setUnitId(unitPosition.getUnit().getId());
        result.setReasonCodeId(unitPosition.getReasonCode() != null ? unitPosition.getReasonCode().getId() : null);
        result.setParentUnitId(parentOrganizationId);
        // TODO Setting for compatibility

        Map<String, Object> employmentTypes = new HashMap();
        employmentTypes.put("name", relationShip.getEmploymentType().getName());
        employmentTypes.put("id", relationShip.getEmploymentType().getId());
        employmentTypes.put("employmentTypeCategory", relationShip.getEmploymentTypeCategory());
        result.setEmploymentType(employmentTypes);


        Map<String, Object> seniorityLevel;
        ObjectMapper objectMapper = new ObjectMapper();
        seniorityLevel = objectMapper.convertValue(unitPosition.getSeniorityLevel(), Map.class);
        seniorityLevel.put("functions", unitPositionDTO.getFunctionIds());
        seniorityLevel.put("payGrade", unitPosition.getSeniorityLevel().getPayGrade());
        result.setSeniorityLevel(seniorityLevel);
        return result;
    }

    public UnitPositionQueryResult getBasicDetails(UnitPosition unitPosition) {
        UnitPositionQueryResult unitPositionQueryResult = unitPositionGraphRepository.getUnitIdAndParentUnitIdByUnitPositionId(unitPosition.getId());
        UnitPositionQueryResult result = new UnitPositionQueryResult(unitPosition.getExpertise().retrieveBasicDetails(), unitPosition.getStartDateMillis(), unitPosition.getWorkingDaysInWeek(),
                unitPosition.getEndDateMillis(), unitPosition.getTotalWeeklyMinutes(), unitPosition.getAvgDailyWorkingHours(), unitPosition.getHourlyWages(),
                unitPosition.getId(), unitPosition.getSalary(), unitPosition.getPositionCode(), unitPosition.getUnion(),
                unitPosition.getLastWorkingDateMillis(), unitPosition.getCta(), null/*unitPosition.getWorkingTimeAgreement()*/);
        result.setReasonCodeId(unitPosition.getReasonCode() != null ? unitPosition.getReasonCode().getId() : null);
        result.setUnitId(unitPositionQueryResult.getUnitId());
        result.setParentUnitId(unitPositionQueryResult.getParentUnitId());


        return result;
    }

    public WTAResponseDTO getUnitPositionWTA(Long unitId, Long unitEmploymentPositionId) {
        UnitPosition unitPosition = unitPositionGraphRepository.findOne(unitEmploymentPositionId);
        if (!Optional.ofNullable(unitPosition).isPresent() || unitPosition.isDeleted() == true) {
            throw new DataNotFoundByIdException("Invalid unit Employment Position id" + unitEmploymentPositionId);
        }
        WTAResponseDTO workingTimeAgreement = workingTimeAgreementRestClient.getWTAById(unitPosition.getWorkingTimeAgreementId());
        return workingTimeAgreement;
    }

    public UnitPositionWithCtaDetailsDTO getUnitPositionCTA(Long unitPositionId, Long unitId) {
        BeanUtils.copyProperties(new Object(),new Object(),"id");
        UnitPosition unitPosition = unitPositionGraphRepository.findOne(unitPositionId);
        CTAListQueryResult ctaQueryResults = costTimeAgreementGraphRepository.getCTAByUnitPositionId(unitPositionId);
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = new UnitPositionWithCtaDetailsDTO(unitPositionId);
        unitPositionWithCtaDetailsDTO.setStaffId(unitPosition.getStaff().getId());
        unitPositionWithCtaDetailsDTO.setCountryId(countryId);
        unitPositionWithCtaDetailsDTO.setContractedMinByWeek(unitPosition.getTotalWeeklyMinutes());
        unitPositionWithCtaDetailsDTO.setWorkingDaysPerWeek(unitPosition.getWorkingDaysInWeek());
        unitPositionWithCtaDetailsDTO.setUnitPositionStartDate(DateUtil.asLocalDate(new Date(unitPosition.getStartDateMillis())));
        if (unitPosition.getEndDateMillis() != null) {
            unitPositionWithCtaDetailsDTO.setUnitPositionEndDate(DateUtil.asLocalDate(new Date(unitPosition.getEndDateMillis())));
        }
        Optional<Organization> organization = organizationGraphRepository.findById(unitId, 0);
        unitPositionWithCtaDetailsDTO.setUnitTimeZone(organization.get().getTimeZone());
        unitPositionWithCtaDetailsDTO.setCtaRuleTemplates(getCtaRuleTemplates(ctaQueryResults));
        return unitPositionWithCtaDetailsDTO;
    }

    public List<CTARuleTemplateDTO> getCtaRuleTemplates(CTAListQueryResult ctaListQueryResult) {
        List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = new ArrayList<>(ctaListQueryResult.getRuleTemplates().size());
        List<CTARuleTemplateQueryResult> ruleTemplateQueryResults = getObjects(ctaListQueryResult.getRuleTemplates(), new TypeReference<List<CTARuleTemplateQueryResult>>() {
        });
        ruleTemplateQueryResults.forEach(ruleTemplateQueryResult -> {
            CTARuleTemplateDTO ctaRuleTemplateDTO = new CTARuleTemplateDTO();
            ctaRuleTemplateDTO.setGranularity((int) ruleTemplateQueryResult.getCompensationTable().get("granularityLevel"));
            ctaRuleTemplateDTO.setActivityIds(ruleTemplateQueryResult.getActivityIds().stream().map(ac -> new BigInteger(ac.toString())).collect(Collectors.toList()));
            ctaRuleTemplateDTO.setName(ruleTemplateQueryResult.getName());
            ctaRuleTemplateDTO.setId(ruleTemplateQueryResult.getId());
            if(ruleTemplateQueryResult.getDayTypeIds()!=null && !ruleTemplateQueryResult.getDayTypeIds().isEmpty()) {
                List<DayType> dayTypes = dayTypeGraphRepository.getDayTypes(ruleTemplateQueryResult.getDayTypeIds());
                ctaRuleTemplateDTO.setDays(dayTypes.stream().filter(dt->!dt.isHolidayType()).flatMap(dt -> dt.getValidDays().stream().map(day -> DayOfWeek.valueOf(day.name()).getValue())).collect(Collectors.toList()));
            }
            ctaRuleTemplateDTO.setTimeTypeIds(ruleTemplateQueryResult.getTimeTypeIds() != null ? ruleTemplateQueryResult.getTimeTypeIds().stream().map(t -> new BigInteger(t.toString())).collect(Collectors.toList()) : null);
            //ctaRuleTemplateDTO.setPublicHolidays();

            ctaRuleTemplateDTO.setPlannedTimeIds(ruleTemplateQueryResult.getPlannedTimeIds());

            ctaRuleTemplateDTO.setCtaIntervalDTOS(getCtaInterval((List<CompensationTableInterval>) ruleTemplateQueryResult.getCompensationTable().get("compensationTableInterval")));


            ctaRuleTemplateDTO.setCalculateScheduledHours(ruleTemplateQueryResult.isCalculateScheduledHours());
            ctaRuleTemplateDTO.setEmploymentTypes(ruleTemplateQueryResult.getEmploymentTypes());
            if (ruleTemplateQueryResult.getPlannedTimeWithFactor().getAccountType() != null) {
                ctaRuleTemplateDTO.setAccountType(ruleTemplateQueryResult.getPlannedTimeWithFactor().getAccountType().name());
            }
            ctaRuleTemplateDTOS.add(ctaRuleTemplateDTO);
        });
        return ctaRuleTemplateDTOS;
    }

    private List<CTAIntervalDTO> getCtaInterval(List<CompensationTableInterval> compensationTableIntervals) {
        List<CTAIntervalDTO> ctaIntervalDTOS = new ArrayList<>(compensationTableIntervals.size());
        compensationTableIntervals = getObjects(compensationTableIntervals, new TypeReference<List<CompensationTableInterval>>() {
        });
        compensationTableIntervals.forEach(cti -> {
            CTAIntervalDTO ctaIntervalDTO = new CTAIntervalDTO(cti.getCompensationMeasurementType().toString(), cti.getValue());
            ctaIntervalDTO.setStartTime(cti.getFrom().getHour() * 60 + cti.getFrom().getMinute());
            ctaIntervalDTO.setEndTime(cti.getTo().getHour() * 60 + cti.getTo().getMinute());
            ctaIntervalDTOS.add(ctaIntervalDTO);
        });
        return ctaIntervalDTOS;
    }

    private <T> List getObjects(List<T> object, TypeReference typeReference) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(mapper.writeValueAsBytes(object), typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UnitPositionDTO convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(TimeCareEmploymentDTO timeCareEmploymentDTO, Long expertiseId, Long staffId, Long employmentTypeId, Long positionCodeId, BigInteger wtaId, Long ctaId, Long unitId) {
        Long startDateMillis = DateConverter.convertInUTCTimestamp(timeCareEmploymentDTO.getStartDate());
        Long endDateMillis = null;
        if (!timeCareEmploymentDTO.getEndDate().equals("0001-01-01T00:00:00")) {
            endDateMillis = DateConverter.convertInUTCTimestamp(timeCareEmploymentDTO.getEndDate());
        }
        UnitPositionDTO unitPositionDTO = new UnitPositionDTO(positionCodeId, expertiseId, startDateMillis, endDateMillis, Integer.parseInt(timeCareEmploymentDTO.getWeeklyHours()), employmentTypeId, staffId, wtaId, ctaId, unitId, new Long(timeCareEmploymentDTO.getId()));
        return unitPositionDTO;
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

        //WorkingTimeAgreement wta = unitPositionGraphRepository.getOneDefaultWTA(organization.getId(), expertise.getId());
        CostTimeAgreement cta = unitPositionGraphRepository.getOneDefaultCTA(organization.getId(), expertise.getId());
        /*if (wta == null) {
            throw new DataNotFoundByIdException("NO WTA found for organization : " + organization.getId());
        }*/

        if (cta == null) {
            throw new DataNotFoundByIdException("NO CTA found for organization : " + organization.getId());
        }
        List<WTAResponseDTO> wtaResponseDTOS = workingTimeAgreementRestClient.getWTAByExpertise(expertise.getId());
        PositionCode positionCode = positionCodeGraphRepository.getOneDefaultPositionCodeByUnitId(parentOrganization.getId());
        if (positionCode == null) {
            throw new DataNotFoundByIdException("NO Position code exist in organization : " + parentOrganization.getId());
        }

        for (TimeCareEmploymentDTO timeCareEmploymentDTO : timeCareEmploymentDTOs) {
            Staff staff = staffGraphRepository.findByExternalId(timeCareEmploymentDTO.getPersonID());
            if (staff == null) {
                throw new DataNotFoundByIdException("NO staff exist with External Id : " + timeCareEmploymentDTO.getPersonID());
            }
            UnitPositionDTO unitEmploymentPosition = convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(timeCareEmploymentDTO, expertise.getId(), staff.getId(), employmentType.getId(), positionCode.getId(), wtaResponseDTOS.get(0).getId(), cta.getId(), organization.getId());
            createUnitPosition(organization.getId(), "Organization", unitEmploymentPosition, true);
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


    // For Test Cases
    public UnitPosition getDefaultUnitPositionByOrg(Long orgId) {
        return unitPositionGraphRepository.getDefaultUnitPositionByOrg(orgId);
    }

    public SeniorityLevel getSeniorityLevelByStaffAndExpertise(Long staffId, Expertise currentExpertise) {

        StaffExperienceInExpertiseDTO staffSelectedExpertise = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndExpertiseId(staffId, currentExpertise.getId());

        if (!Optional.ofNullable(staffSelectedExpertise).isPresent() || !Optional.ofNullable(currentExpertise).isPresent()) {
            throw new DataNotFoundByIdException("Expertise is not assigned to staff or unavailable");

        }

        DateTime expertiseStartDate = new DateTime(staffSelectedExpertise.getExpertiseStartDate());
        DateTime currentDate = new DateTime(DateUtil.getCurrentDateMillis());

        Integer experienceInMonth = Months.monthsBetween(expertiseStartDate, currentDate).getMonths() + staffSelectedExpertise.getRelevantExperienceInMonths();
        logger.info("user has current experience in months :{}", experienceInMonth);
        SeniorityLevel appliedSeniorityLevel = null;
        for (SeniorityLevel seniorityLevel : currentExpertise.getSeniorityLevel()) {
            if (seniorityLevel.getTo() == null) {
                // more than  is set if
                if (experienceInMonth >= seniorityLevel.getFrom() * 12) {
                    appliedSeniorityLevel = seniorityLevel;
                    break;
                }
            } else {
                // to and from is present
                logger.info("user has current experience in months :{} ,{},{},{}", seniorityLevel.getFrom(), experienceInMonth, seniorityLevel.getTo(), experienceInMonth);

                if (seniorityLevel.getFrom() * 12 < experienceInMonth && seniorityLevel.getTo() * 12 >= experienceInMonth) {
                    appliedSeniorityLevel = seniorityLevel;
                    break;
                }
            }
        }

        return appliedSeniorityLevel;
    }
    public EmploymentUnitPositionDTO updateUnitPositionEndDateFromEmployment(Long staffId, String employmentEndDate, Long unitId) throws ParseException {

        Organization unit = organizationGraphRepository.findOne(unitId);
        Long endDateMillis = DateUtil.getIsoDateInLong(employmentEndDate);
        List<UnitPosition> unitPositions =  unitPositionGraphRepository.getUnitPositionsFromEmploymentEndDate(staffId,endDateMillis);
        for(UnitPosition unitPosition:unitPositions) {
            unitPosition.setEndDateMillis(endDateMillis);
        }
        unitPositionGraphRepository.saveAll(unitPositions);
        Employment employment = employmentGraphRepository.findEmploymentByStaff(staffId);
        employment.setEndDateMillis(endDateMillis);
        employmentGraphRepository.save(employment);
        if(Optional.ofNullable(employmentEndDate).isPresent()&&(DateUtil.getDateFromEpoch(endDateMillis).compareTo(DateUtil.getTimezonedCurrentDate(unit.getTimeZone().toString()))==0)) {
            //employment = employmentGraphRepository.findEmploymentByStaff(staffId);
            List<Long> employmentIds = Stream.of(employment.getId()).collect(Collectors.toList());
            employmentService.moveToReadOnlyAccessGroup(employmentIds);
        }
        User user = userGraphRepository.getUserByStaffId(staffId);
        EmploymentQueryResult employmentUpdated = new EmploymentQueryResult(employment.getId(),employment.getStartDateMillis(),employment.getEndDateMillis());
        EmploymentUnitPositionDTO employmentUnitPositionDTO = new EmploymentUnitPositionDTO(employmentUpdated,unitPositionGraphRepository.getAllUnitPositionsByUser(user.getId()) );
        return employmentUnitPositionDTO;

    }


}
