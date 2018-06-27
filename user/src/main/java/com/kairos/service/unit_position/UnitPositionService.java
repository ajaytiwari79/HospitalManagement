package com.kairos.service.unit_position;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kairos.enums.IntegrationOperation;
import com.kairos.dto.shift.StaffUnitPositionDetails;
import com.kairos.util.DateUtils;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.client.TimeBankRestClient;
import com.kairos.client.WorkingTimeAgreementRestClient;
import com.kairos.activity.time_bank.CTAIntervalDTO;
import com.kairos.activity.time_bank.CTARuleTemplateDTO;
import com.kairos.user.organization.Organization;
import com.kairos.user.agreement.cta.CTAListQueryResult;
import com.kairos.user.agreement.cta.CTARuleTemplateQueryResult;
import com.kairos.user.agreement.cta.CompensationTableInterval;
import com.kairos.user.agreement.cta.CostTimeAgreement;
import com.kairos.user.auth.User;
import com.kairos.user.client.ClientMinimumDTO;
import com.kairos.user.country.DayType;
import com.kairos.user.country.Function;
import com.kairos.user.country.FunctionDTO;
import com.kairos.user.country.ReasonCode;
import com.kairos.user.country.employment_type.EmploymentType;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.ExpertisePlannedTimeQueryResult;
import com.kairos.persistence.model.user.expertise.Response.SeniorityLevelQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.user.staff.*;
import com.kairos.persistence.model.user.unit_position.PositionCtaWtaQueryResult;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionEmploymentTypeRelationShip;
import com.kairos.persistence.model.user.unit_position.UnitPositionQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.cta.CollectiveTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.FunctionGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseEmploymentTypeRelationshipGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorityLevelGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.positionCode.PositionCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitPermissionGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionEmploymentTypeRelationShipGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionFunctionRelationshipRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;
import com.kairos.response.dto.web.PositionWrapper;
import com.kairos.response.dto.web.UnitPositionDTO;
import com.kairos.activity.wta.WTADTO;
import com.kairos.activity.wta.WTAResponseDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.position_code.PositionCodeService;
import com.kairos.service.staff.EmploymentService;
import com.kairos.service.staff.StaffService;
import com.kairos.util.DateConverter;
import com.kairos.util.DateUtil;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.util.DateUtils.ONLY_DATE;

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
    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private WorkingTimeAgreementRestClient workingTimeAgreementRestClient;
    @Inject
    private PlannerSyncService plannerSyncService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UnitPositionFunctionRelationshipRepository unitPositionFunctionRelationshipRepository;
    @Inject
    ExpertiseEmploymentTypeRelationshipGraphRepository expertiseEmploymentTypeRelationshipGraphRepository;


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

        Employment employment = employmentGraphRepository.findEmploymentByStaff(unitPositionDTO.getStaffId());
        if (!Optional.ofNullable(employment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.employment.notFound", unitPositionDTO.getStaffId());

        }
        if (employment.getStartDateMillis() != null) {

            if (unitPositionDTO.getStartLocalDate().isBefore(DateUtil.getDateFromEpoch(employment.getStartDateMillis()))) {
                exceptionService.actionNotPermittedException("message.staff.data.employmentdate.lessthan");

            }
        }

        if (!Optional.ofNullable(positionCode).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.position.name.notexist", unitPositionDTO.getPositionCodeId());

        }

        List<UnitPosition> oldUnitPositions = unitPositionGraphRepository.getStaffUnitPositionsByExpertise(organization.getId(), unitPositionDTO.getStaffId(), unitPositionDTO.getExpertiseId());
        validateUnitPositionWithExpertise(oldUnitPositions, unitPositionDTO);
        UnitPosition unitPosition = new UnitPosition();

        EmploymentType employmentType = organizationGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(parentOrganization.getId(), unitPositionDTO.getEmploymentTypeId(), false);
        if (!Optional.ofNullable(employmentType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.position.employmenttype.notexist", unitPositionDTO.getEmploymentTypeId());

        }

        preparePosition(unitPosition, unitPositionDTO, organization, parentOrganization, createFromTimeCare);
        WTAResponseDTO wtaResponseDTO = workingTimeAgreementRestClient.assignWTAToUnitPosition(unitPositionDTO.getWtaId());
        if (wtaResponseDTO == null) {
            exceptionService.dataNotFoundByIdException("message.wta.id");

        }
        unitPosition.setWorkingTimeAgreementId(wtaResponseDTO.getId());
        unitPosition.setPositionCode(positionCode);

        unitPosition.setUnit(organization);
        save(unitPosition);
        Employment employment1 = employmentService.updateEmploymentEndDate(organization, unitPositionDTO.getStaffId(), unitPositionDTO.getEndLocalDate() != null ? DateUtil.getDateFromEpoch(unitPositionDTO.getEndLocalDate()) : null, unitPositionDTO.getReasonCodeId(), unitPositionDTO.getAccessGroupIdOnEmploymentEnd());
        Long reasonCodeId = Optional.ofNullable(employment.getReasonCode()).isPresent() ? employment1.getReasonCode().getId() : null;

        UnitPositionEmploymentTypeRelationShip relationShip = new UnitPositionEmploymentTypeRelationShip(unitPosition, employmentType, unitPositionDTO.getEmploymentTypeCategory());
        unitPositionEmploymentTypeRelationShipGraphRepository.save(relationShip);

        UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPositionDTO, unitPosition, relationShip, parentOrganization.getId(), parentOrganization.getName(), wtaResponseDTO);
        PositionWrapper positionWrapper = new PositionWrapper(unitPositionQueryResult, new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), reasonCodeId, employment.getAccessGroupIdOnEmploymentEnd()));
//        timeBankRestClient.createBlankTimeBank(getUnitPositionCTA(unitPosition.getId(), organization.getId()));

        //      UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPosition);
        //timeBankRestClient.createBlankTimeBank(getUnitPositionCTA(unitPosition.getId(),id));

        //plannerSyncService.publishUnitPosition(id, unitPosition, employmentType, IntegrationOperation.CREATE);
        return positionWrapper;
    }


    public boolean validateUnitPositionWithExpertise(List<UnitPosition> unitPositions, UnitPositionDTO unitPositionDTO) {

        LocalDate newUPStartDate = unitPositionDTO.getStartLocalDate();
        LocalDate newUPEndDate = (unitPositionDTO.getEndLocalDate() != null) ? unitPositionDTO.getEndLocalDate() : null;
        unitPositions.forEach(unitPosition -> {
            // if null date is set
            if (unitPosition.getEndDateMillis() != null) {
                if (newUPStartDate.isBefore(DateUtil.getDateFromEpoch(unitPosition.getEndDateMillis())) && newUPStartDate.isAfter(DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()))) {
                    exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", newUPEndDate, DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()));
                }
                if (newUPEndDate != null) {
                    Interval previousInterval = new Interval(unitPosition.getStartDateMillis(), unitPosition.getEndDateMillis());
                    Interval interval = new Interval(DateUtil.getDateFromEpoch(newUPStartDate), DateUtil.getDateFromEpoch(newUPEndDate));
                    logger.info(" Interval of CURRENT UEP " + previousInterval + " Interval of going to create  " + interval);
                    if (previousInterval.overlaps(interval))
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist");
                } else {
                    if (newUPStartDate.isBefore(DateUtil.getDateFromEpoch(unitPosition.getEndDateMillis()))) {
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", newUPEndDate, DateUtil.getDateFromEpoch(unitPosition.getEndDateMillis()));
                    }
                }
            } else {
                // unitEmploymentEnd date is null
                if (newUPEndDate != null) {
                    if (newUPEndDate.isAfter(DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()))) {
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", newUPEndDate, DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()));
                    }
                } else {
                    exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist");
                }
            }
        });
        return true;
    }


    public PositionWrapper updateUnitPosition(long unitPositionId, UnitPositionDTO unitPositionDTO, Long unitId, String type) {

        Organization organization = organizationService.getOrganizationDetail(unitId, type);
        List<ClientMinimumDTO> clientMinimumDTO = clientGraphRepository.getCitizenListForThisContactPerson(unitPositionDTO.getStaffId());
        if (clientMinimumDTO.size() > 0) {
            return new PositionWrapper(clientMinimumDTO);
        }

        UnitPosition oldUnitPosition = unitPositionGraphRepository.findOne(unitPositionId);
        if (!Optional.ofNullable(oldUnitPosition).isPresent()) {

            exceptionService.dataNotFoundByIdException("message.positionid.notfound", unitPositionId);

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
                    exceptionService.dataNotFoundByIdException("message.employmentType.notnull", unitPositionDTO.getEmploymentTypeId());

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
                unitPositionDTO.getEndLocalDate() != null ? DateUtil.getDateFromEpoch(unitPositionDTO.getEndLocalDate()) : null, unitPositionDTO.getReasonCodeId(), unitPositionDTO.getAccessGroupIdOnEmploymentEnd());
        Long reasonCodeId = Optional.ofNullable(employment.getReasonCode()).isPresent() ? employment.getReasonCode().getId() : null;

        EmploymentQueryResult employmentQueryResult = new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), reasonCodeId, employment.getAccessGroupIdOnEmploymentEnd());

        // unitPositionDTO.getEndDate());
        //plannerSyncService.publishUnitPosition(unitId, oldUnitPosition, unitPositionEmploymentTypeRelationShip.getEmploymentType(), IntegrationOperation.UPDATE);
        return new PositionWrapper(getBasicDetails(unitPositionDTO, oldUnitPosition, unitPositionEmploymentTypeRelationShip, organization.getId(), organization.getName(), null), employmentQueryResult);

    }

    public EmploymentQueryResult removePosition(long positionId, Long unitId) {
        UnitPosition unitPosition = unitPositionGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(unitPosition).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitposition.id.notexist", positionId);

        }
        unitPosition.setDeleted(true);
        save(unitPosition);

        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        Long staffId = unitPositionGraphRepository.getStaffIdFromUnitPosition(positionId);
        Employment employment = employmentService.updateEmploymentEndDate(unit, staffId);
        //plannerSyncService.publishUnitPosition(unitId, unitPosition, null, IntegrationOperation.DELETE);
        return new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis());
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
                exceptionService.dataNotFoundByIdException("message.unitposition.union.notexist", unitPositionDTO.getUnionId());

            }
            unitPosition.setUnion(union);
        }
        Optional<Organization> unitPositionInOrganization = organizationGraphRepository.findById(unitPositionDTO.getUnitId(), 0);
        if (!unitPositionInOrganization.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.notfound");

        }
        unitPosition.setUnit(unitPositionInOrganization.get());

       /* Optional<WorkingTimeAgreement> wta = workingTimeAgreementGraphRepository.findById(unitPositionDTO.getWtaId());
        if (!wta.isPresent()) {
            throw new DataNotFoundByIdException("Invalid wta id ");
        }
        copyAndLinkNewWTA(unitPosition, wta.get());
*/


        CostTimeAgreement cta = (unitPositionDTO.getCtaId() == null) ? null :
                costTimeAgreementGraphRepository.findOne(unitPositionDTO.getCtaId());
        if (cta != null) {
            unitPosition.setCta(cta);
        }

        Optional<Expertise> expertise = expertiseGraphRepository.findById(unitPositionDTO.getExpertiseId(), 1);
        if (!expertise.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", unitPositionDTO.getExpertiseId());

        }
        unitPosition.setExpertise(expertise.get());
        unitPosition.setWorkingDaysInWeek(expertise.get().getNumberOfWorkingDaysInWeek());


        Staff staff = staffGraphRepository.findOne(unitPositionDTO.getStaffId());
        if (!Optional.ofNullable(staff).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitposition.staff.notfound", unitPositionDTO.getStaffId());

        }
        unitPosition.setStaff(staff);

        // UEP can be created for past dates from time care
        if (!createFromTimeCare && unitPositionDTO.getStartLocalDate().isBefore(LocalDate.now())) {
            exceptionService.actionNotPermittedException("message.startdate.notlessthan.currentdate");

        }
        unitPosition.setStartDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getStartLocalDate()));
        unitPosition.setWorkingDaysInWeek(expertise.get().getNumberOfWorkingDaysInWeek());

        if (Optional.ofNullable(unitPositionDTO.getEndLocalDate()).isPresent()) {
            if (unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getEndLocalDate())) {
                exceptionService.actionNotPermittedException("message.startdate.notlessthan.enddate");

            }
            if (!Optional.ofNullable(unitPositionDTO.getReasonCodeId()).isPresent()) {
                exceptionService.actionNotPermittedException("message.region.enddate");

            }

            Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(unitPositionDTO.getReasonCodeId(), 0);
            if (!Optional.ofNullable(reasonCode).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound", unitPositionDTO.getReasonCodeId());

            }
            unitPosition.setReasonCode(reasonCode.get());
            unitPosition.setEndDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getEndLocalDate()));
        }

        if (Optional.ofNullable(unitPositionDTO.getLastWorkingLocalDate()).isPresent()) {
            if (unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getLastWorkingLocalDate())) {
                exceptionService.actionNotPermittedException("message.lastdate.notlessthan.startdate");

            }
            unitPosition.setLastWorkingDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getLastWorkingLocalDate()));
        }

        SeniorityLevel seniorityLevel = getSeniorityLevelByStaffAndExpertise(staff.getId(), expertise.get());
        if (!Optional.ofNullable(seniorityLevel).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.seniorityLevel.id.notfound", unitPositionDTO.getReasonCodeId());

        }
        unitPosition.setSeniorityLevel(seniorityLevel);
        List<Function> functions = functionGraphRepository.findAllFunctionsById(unitPositionDTO.getFunctionIds());
        if (functions.size() != unitPositionDTO.getFunctionIds().size()) {
            exceptionService.actionNotPermittedException("message.unitposition.functions.unable");

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
                exceptionService.dataNotFoundByIdException("message.unitposition.union.notexist", unitPositionDTO.getUnionId());

            }
            oldUnitPosition.setUnion(union);
        }

// If already present and still present but a different

        else if (Optional.ofNullable(unitPositionDTO.getUnionId()).isPresent() && Optional.ofNullable(oldUnitPosition.getUnion()).isPresent()) {
            if (!unitPositionDTO.getUnionId().equals(oldUnitPosition.getUnion())) {
                Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitPositionDTO.getUnionId());
                if (!Optional.ofNullable(union).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.unitposition.union.notexist", unitPositionDTO.getUnionId());

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
            exceptionService.actionNotPermittedException("message.unitposition.expertise.notchanged", unitPositionDTO.getExpertiseId());

//            Expertise expertise = expertiseGraphRepository.findOne(unitPositionDTO.getExpertiseId());
//            if (!Optional.ofNullable(expertise).isPresent()) {
//                throw new DataNotFoundByIdException("Invalid expertise id");
//            }
//            oldUnitPosition.setParentExpertise(expertise);
        }
        if (!oldUnitPosition.getPositionCode().getId().equals(unitPositionDTO.getPositionCodeId())) {
            exceptionService.actionNotPermittedException("message.unitposition.positioncode.notchanged", unitPositionDTO.getPositionCodeId());

//            PositionCode positionCode = positionCodeGraphRepository.findOne(unitPositionDTO.getPositionCodeId());
//            if (!Optional.ofNullable(positionCode).isPresent()) {
//                throw new DataNotFoundByIdException("Position Code Cannot be null" + unitPositionDTO.getPositionCodeId());
//            }
//            oldUnitPosition.setPositionCode(positionCode);

        }
        Set<Long> olderFunctionsAddedInUnitPosition = oldUnitPosition.getFunctions() != null ? oldUnitPosition.getFunctions().stream().map(Function::getId).collect(Collectors.toSet()) : Collections.emptySet();
        if (!olderFunctionsAddedInUnitPosition.equals(unitPositionDTO.getFunctionIds())) {

            /*if (!olderFunctionsAddedInUnitPosition.isEmpty())
                unitPositionGraphRepository.removeOlderFunctionsFromUnitPosition(oldUnitPosition.getId());*/

            List<Function> functions = new ArrayList<>();
            if (!unitPositionDTO.getFunctionIds().isEmpty()) {
                functions = functionGraphRepository.findAllFunctionsById(unitPositionDTO.getFunctionIds());
                if (functions.size() != unitPositionDTO.getFunctionIds().size()) {
                    exceptionService.actionNotPermittedException("message.unitposition.functions.unable");

                }
            }
            oldUnitPosition.setFunctions(functions);
        }


        if (Optional.ofNullable(unitPositionDTO.getEndLocalDate()).isPresent()) {
            if (unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getEndLocalDate())) {
                exceptionService.actionNotPermittedException("message.startdate.notlessthan.enddate");

            }
        }
        if (Optional.ofNullable(unitPositionDTO.getLastWorkingLocalDate()).isPresent()) {
            if (unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getLastWorkingLocalDate())) {
                exceptionService.actionNotPermittedException("message.lastdate.notlessthan.enddate");

            }
            oldUnitPosition.setLastWorkingDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getLastWorkingLocalDate()));
        }
        Employment employment = employmentGraphRepository.findEmploymentByStaff(unitPositionDTO.getStaffId());
        if (!Optional.ofNullable(employment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.employment.notFound", unitPositionDTO.getStaffId());

        }
        if (employment.getStartDateMillis() != null) {
            if (unitPositionDTO.getStartLocalDate().isBefore(DateUtil.getDateFromEpoch(employment.getStartDateMillis()))) {
                exceptionService.actionNotPermittedException("message.staff.data.employmentdate.lessthan");

            }
        }
        if (unitPositionDTO.getStartLocalDate().isBefore(LocalDate.now())) {
            exceptionService.actionNotPermittedException("message.startdate.notlessthan.currentdate");

        }
        oldUnitPosition.setStartDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getStartLocalDate()));

        if (Optional.ofNullable(unitPositionDTO.getEndLocalDate()).isPresent()) {

            oldUnitPosition.setEndDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getEndLocalDate()));

            if (!Optional.ofNullable(unitPositionDTO.getReasonCodeId()).isPresent()) {
                exceptionService.actionNotPermittedException("message.region.enddate");

            }

            if (oldUnitPosition.getReasonCode() == null || !oldUnitPosition.getReasonCode().getId().equals(unitPositionDTO.getReasonCodeId())) {
                Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(unitPositionDTO.getReasonCodeId(), 0);
                if (!Optional.ofNullable(reasonCode).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound", unitPositionDTO.getReasonCodeId());

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
    public EmploymentUnitPositionDTO getUnitPositionsOfStaff(long id, long staffId, boolean allOrganization) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitposition.staff.notfound", staffId);

        }

        User user = userGraphRepository.getUserByStaffId(staffId);

        EmploymentReasonCodeQueryResult employmentReasonCode = employmentGraphRepository.findEmploymentreasonCodeByStaff(staffId);
        Employment employment = employmentReasonCode.getEmployment();

        Long reasonCodeId = Optional.ofNullable(employmentReasonCode.getReasonCode()).isPresent() ? employmentReasonCode.getReasonCode().getId() : null;
        EmploymentQueryResult employmentQueryResult = new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), reasonCodeId, employment.getAccessGroupIdOnEmploymentEnd(), employment.getMainEmploymentStartDate(), employment.getMainEmploymentEndDate(), employment.isMainEmployment());
        List<UnitPositionQueryResult> unitPositionQueryResults = (allOrganization) ? unitPositionGraphRepository.getAllUnitPositionsByUser(user.getId()) : unitPositionGraphRepository.getAllUnitPositionsForCurrentOrganization(staffId);
        List<WTAResponseDTO> wtaResponseDTOS = workingTimeAgreementRestClient.getWTAByIds(unitPositionQueryResults.stream().map(u -> u.getWorkingTimeAgreementId()).collect(Collectors.toList()));
        Map<BigInteger, WTAResponseDTO> wtaResponseDTOMap = wtaResponseDTOS.stream().collect(Collectors.toMap(w -> w.getId(), w -> w));
        unitPositionQueryResults.forEach(u -> {
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
        //SeniorityLevelQueryResult seniorityLevel = (appliedSeniorityLevel != null) ? seniorityLevelGraphRepository.getSeniorityLevelById(appliedSeniorityLevel.getId()) : null;
        //positionCtaWtaQueryResult.setApplicableSeniorityLevel(seniorityLevel);
        positionCtaWtaQueryResult.setUnion(currentExpertise.get().getUnion());

        SeniorityLevelQueryResult seniorityLevel = null;
        if (appliedSeniorityLevel != null) {
            seniorityLevel = seniorityLevelGraphRepository.getSeniorityLevelById(appliedSeniorityLevel.getId());

            List<FunctionDTO> functionDTOs = functionGraphRepository.getFunctionsByExpertiseAndSeniorityLevel(currentExpertise.get().getId(), appliedSeniorityLevel.getId());
            seniorityLevel.setFunctions(functionDTOs);
        }
        positionCtaWtaQueryResult.setApplicableSeniorityLevel(seniorityLevel);


        return positionCtaWtaQueryResult;
    }

    //TODO this must be moved to activity
    public UnitPositionQueryResult updateUnitPositionWTA(Long unitId, Long unitPositionId, BigInteger wtaId, WTADTO updateDTO) {
        UnitPosition unitPosition = unitPositionGraphRepository.findOne(unitPositionId);
        if (!Optional.ofNullable(unitPosition).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.InvalidEmploymentPostionId", unitPositionId);

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
        newWta.setExpertises(oldWta.getExpertise());
        newWta.setParentWTA(oldWta);
        newWta.setDisabled(false);
*/        //unitPosition.setWorkingTimeAgreement(newWta);
        updateDTO.setId(wtaId);
        WTAResponseDTO wtaResponseDTO = workingTimeAgreementRestClient.updateWTAOfUnitPosition(updateDTO);
        unitPosition.setWorkingTimeAgreementId(wtaResponseDTO.getId());
        save(unitPosition);
        UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPosition);
        //newWta.setParentWTA(oldWta.basicDetails());

        //newWta.setExpertises(newWta.getExpertise().retrieveBasicDetails());
        //unitPositionQueryResult.setWorkingTimeAgreement(newWta);
        plannerSyncService.publishWTA(unitId, unitPositionId, wtaResponseDTO, IntegrationOperation.UPDATE);
        return unitPositionQueryResult;
    }

    private UnitPositionQueryResult getBasicDetails(UnitPositionDTO unitPositionDTO, UnitPosition unitPosition, UnitPositionEmploymentTypeRelationShip relationShip, Long parentOrganizationId, String parentOrganizationName, WTAResponseDTO wtaResponseDTO) {


        UnitPositionQueryResult result = new UnitPositionQueryResult(unitPosition.getExpertise().retrieveBasicDetails(), unitPosition.getStartDateMillis(), unitPosition.getWorkingDaysInWeek(),
                unitPosition.getEndDateMillis(), unitPosition.getTotalWeeklyMinutes(), unitPosition.getAvgDailyWorkingHours(), unitPosition.getHourlyWages(),
                unitPosition.getId(), unitPosition.getSalary(), unitPosition.getPositionCode(), unitPosition.getUnion(),
                unitPosition.getLastWorkingDateMillis(), unitPosition.getCta(), wtaResponseDTO);
        result.setUnitId(unitPosition.getUnit().getId());
        result.setReasonCodeId(unitPosition.getReasonCode() != null ? unitPosition.getReasonCode().getId() : null);
        result.setParentUnitId(parentOrganizationId);

        // TODO Setting for compatibility
        Map<String, Object> unitInfo = new HashMap<>();
        unitInfo.put("id", parentOrganizationId);
        unitInfo.put("name", parentOrganizationName);
        result.setUnitInfo(unitInfo);
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
            exceptionService.dataNotFoundByIdException("message.InvalidEmploymentPostionId", unitEmploymentPositionId);

        }
        WTAResponseDTO workingTimeAgreement = workingTimeAgreementRestClient.getWTAById(unitPosition.getWorkingTimeAgreementId());
        return workingTimeAgreement;
    }

    public StaffUnitPositionDetails getUnitPositionCTA(Long unitPositionId, Long unitId) {

        com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails unitPosition = unitPositionGraphRepository.getUnitPositionById(unitPositionId);
        CTAListQueryResult ctaQueryResults = costTimeAgreementGraphRepository.getCTAByUnitPositionId(unitPositionId);
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        StaffUnitPositionDetails unitPositionWithCtaDetailsDTO = new StaffUnitPositionDetails();
        unitPositionWithCtaDetailsDTO.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.shift.Expertise.class));
//        unitPositionWithCtaDetailsDTO.setStaffId(unitPosition.getStaff().getId());
        unitPositionWithCtaDetailsDTO.setId(unitPosition.getId());
        unitPositionWithCtaDetailsDTO.setCountryId(countryId);
        unitPositionWithCtaDetailsDTO.setTotalWeeklyMinutes(unitPosition.getTotalWeeklyMinutes());
        unitPositionWithCtaDetailsDTO.setWorkingDaysInWeek(unitPosition.getWorkingDaysInWeek());
        unitPositionWithCtaDetailsDTO.setStartDateMillis(unitPosition.getStartDateMillis());
        unitPositionWithCtaDetailsDTO.setWorkingTimeAgreementId(unitPosition.getWorkingTimeAgreementId());
        unitPositionWithCtaDetailsDTO.setUnitPositionStartDate(DateUtils.asLocalDate(new Date(unitPosition.getStartDateMillis())));
        if (unitPosition.getEndDateMillis() != null) {
            unitPositionWithCtaDetailsDTO.setUnitPositionEndDate(DateUtils.asLocalDate(new Date(unitPosition.getEndDateMillis())));
            unitPositionWithCtaDetailsDTO.setEndDateMillis(unitPosition.getEndDateMillis());
        }
        Optional<Organization> organization = organizationGraphRepository.findById(unitId, 0);
        unitPositionWithCtaDetailsDTO.setUnitTimeZone(organization.get().getTimeZone());
        unitPositionWithCtaDetailsDTO.setCtaRuleTemplates(getCtaRuleTemplates(ctaQueryResults));
        com.kairos.dto.shift.EmploymentType employmentType=new com.kairos.dto.shift.EmploymentType();
        ObjectMapperUtils.copyProperties(unitPosition.getEmploymentType(),employmentType);
        unitPositionWithCtaDetailsDTO.setEmploymentType(employmentType);
        return unitPositionWithCtaDetailsDTO;
    }

    public StaffUnitPositionDetails getUnitPositionWithCTA(Long unitPositionId, Organization organization, Long countryId) {

        com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails unitPosition = unitPositionGraphRepository.getUnitPositionById(unitPositionId);
        CTAListQueryResult ctaQueryResults = costTimeAgreementGraphRepository.getCTAByUnitPositionId(unitPositionId);

        StaffUnitPositionDetails unitPositionDetails = new StaffUnitPositionDetails();
        unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.shift.Expertise.class));
        unitPositionDetails.setEmploymentType(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getEmploymentType(), com.kairos.dto.shift.EmploymentType.class));

        unitPositionDetails.setId(unitPosition.getId());
        unitPositionDetails.setCountryId(countryId);
        unitPositionDetails.setTotalWeeklyMinutes(unitPosition.getTotalWeeklyMinutes());
        unitPositionDetails.setWorkingDaysInWeek(unitPosition.getWorkingDaysInWeek());
        unitPositionDetails.setStartDateMillis(unitPosition.getStartDateMillis());
        unitPositionDetails.setWorkingTimeAgreementId(unitPosition.getWorkingTimeAgreementId());
        unitPositionDetails.setUnitPositionStartDate(DateUtils.asLocalDate(new Date(unitPosition.getStartDateMillis())));
        if (unitPosition.getEndDateMillis() != null) {
            unitPositionDetails.setUnitPositionEndDate(DateUtils.asLocalDate(new Date(unitPosition.getEndDateMillis())));
            unitPositionDetails.setEndDateMillis(unitPosition.getEndDateMillis());
        }
        ExpertisePlannedTimeQueryResult expertisePlannedTimeQueryResult = expertiseEmploymentTypeRelationshipGraphRepository.findPlannedTimeByExpertise(unitPositionDetails.getExpertise().getId(), unitPositionDetails.getEmploymentType().getId());
        if (Optional.ofNullable(expertisePlannedTimeQueryResult).isPresent()) {
            unitPositionDetails.setExcludedPlannedTime(expertisePlannedTimeQueryResult.getExcludedPlannedTime());
            unitPositionDetails.setIncludedPlannedTime(expertisePlannedTimeQueryResult.getIncludedPlannedTime());

        }
        unitPositionDetails.setUnitTimeZone(organization.getTimeZone());
        unitPositionDetails.setCtaRuleTemplates(getCtaRuleTemplates(ctaQueryResults));
        return unitPositionDetails;
    }

    public List<CTARuleTemplateDTO> getCtaRuleTemplates(CTAListQueryResult ctaListQueryResult) {
        List<CTARuleTemplateDTO> ctaRuleTemplateDTOS = new ArrayList<>(ctaListQueryResult.getRuleTemplates().size());
        List<CTARuleTemplateQueryResult> ruleTemplateQueryResults = getObjects(ctaListQueryResult.getRuleTemplates(), new TypeReference<List<CTARuleTemplateQueryResult>>() {
        });
        ruleTemplateQueryResults.forEach(ruleTemplateQueryResult -> {
            CTARuleTemplateDTO ctaRuleTemplateDTO = new CTARuleTemplateDTO();
            ctaRuleTemplateDTO.setPayrollSystem(ruleTemplateQueryResult.getPayrollSystem());
            ctaRuleTemplateDTO.setPayrollType(ruleTemplateQueryResult.getPayrollType());
            ctaRuleTemplateDTO.setGranularity((int) ruleTemplateQueryResult.getCompensationTable().get("granularityLevel"));
            ctaRuleTemplateDTO.setActivityIds(ruleTemplateQueryResult.getActivityIds().stream().map(ac -> new BigInteger(ac.toString())).collect(Collectors.toList()));
            ctaRuleTemplateDTO.setName(ruleTemplateQueryResult.getName());
            ctaRuleTemplateDTO.setId(ruleTemplateQueryResult.getId());
            if (ruleTemplateQueryResult.getDayTypeIds() != null && !ruleTemplateQueryResult.getDayTypeIds().isEmpty()) {
                List<DayType> dayTypes = dayTypeGraphRepository.getDayTypes(ruleTemplateQueryResult.getDayTypeIds());
                ctaRuleTemplateDTO.setDays(dayTypes.stream().filter(dt -> !dt.isHolidayType()).flatMap(dt -> dt.getValidDays().stream().map(day -> DayOfWeek.valueOf(day.name()).getValue())).collect(Collectors.toList()));
            }
            ctaRuleTemplateDTO.setTimeTypeIds(ruleTemplateQueryResult.getTimeTypeIds() != null ? ruleTemplateQueryResult.getTimeTypeIds().stream().map(t -> new BigInteger(t.toString())).collect(Collectors.toList()) : null);
            //ctaRuleTemplateDTO.setPublicHolidays();

            ctaRuleTemplateDTO.setPlannedTimeIds(ruleTemplateQueryResult.getPlannedTimeIds());

            ctaRuleTemplateDTO.setCtaIntervalDTOS(getCtaInterval((List<CompensationTableInterval>) ruleTemplateQueryResult.getCompensationTable().get("compensationTableInterval")));


            ctaRuleTemplateDTO.setCalculateScheduledHours(ruleTemplateQueryResult.isCalculateScheduledHours());
            ctaRuleTemplateDTO.setCalculationFor(ruleTemplateQueryResult.getCalculationFor());
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
            exceptionService.dataNotFoundByIdException("message.unitposition.organization.externalid", unitExternalId);

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
            exceptionService.dataNotFoundByIdException("message.unitposition.expertise.notfound", expertiseId);

        }

        //WorkingTimeAgreement wta = unitPositionGraphRepository.getOneDefaultWTA(organization.getId(), expertise.getId());
        CostTimeAgreement cta = unitPositionGraphRepository.getOneDefaultCTA(organization.getId(), expertise.getId());
        /*if (wta == null) {
            throw new DataNotFoundByIdException("NO WTA found for organization : " + organization.getId());
        }*/

        if (cta == null) {
            exceptionService.dataNotFoundByIdException("message.organization.cta.notfound", organization.getId());

        }
        List<WTAResponseDTO> wtaResponseDTOS = workingTimeAgreementRestClient.getWTAByExpertise(expertise.getId());
        PositionCode positionCode = positionCodeGraphRepository.getOneDefaultPositionCodeByUnitId(parentOrganization.getId());
        if (positionCode == null) {
            exceptionService.dataNotFoundByIdException("message.positioncode.organization.notexist", parentOrganization.getId());

        }

        for (TimeCareEmploymentDTO timeCareEmploymentDTO : timeCareEmploymentDTOs) {
            Staff staff = staffGraphRepository.findByExternalId(timeCareEmploymentDTO.getPersonID());
            if (staff == null) {
                exceptionService.dataNotFoundByIdException("message.staff.externalid.notexist", timeCareEmploymentDTO.getPersonID());

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
            exceptionService.dataNotFoundByIdException("message.staff.expertise.notassigned");


        }

        Integer experienceInMonth = (int) ChronoUnit.MONTHS.between(DateUtil.asLocalDate(staffSelectedExpertise.getExpertiseStartDate()), LocalDate.now());
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

                if (seniorityLevel.getFrom() * 12 <= experienceInMonth && seniorityLevel.getTo() * 12 > experienceInMonth) {
                    appliedSeniorityLevel = seniorityLevel;
                    break;
                }
            }
        }

        return appliedSeniorityLevel;
    }

    public EmploymentUnitPositionDTO updateUnitPositionEndDateFromEmployment(Long staffId, String employmentEndDate, Long unitId, Long reasonCodeId, Long accessGroupId) {

        Organization unit = organizationGraphRepository.findOne(unitId);
        Long endDateMillis = DateUtil.getIsoDateInLong(employmentEndDate);
        Long unitPositionStartDateMillisMax = unitPositionGraphRepository.getMaxUnitPositionStartDate(staffId);
        if (Optional.ofNullable(unitPositionStartDateMillisMax).isPresent() && endDateMillis < unitPositionStartDateMillisMax) {
            exceptionService.actionNotPermittedException("message.employmentdata.greaterthan.unitpositiondate", unitPositionStartDateMillisMax);

        }
        List<UnitPosition> unitPositions = unitPositionGraphRepository.getUnitPositionsFromEmploymentEndDate(staffId, endDateMillis);
        ReasonCode reasonCode = reasonCodeGraphRepository.findById(reasonCodeId, 0).get();
        if (!Optional.ofNullable(reasonCode).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound", reasonCodeId);

        }

        for (UnitPosition unitPosition : unitPositions) {
            unitPosition.setEndDateMillis(endDateMillis);
            if (!Optional.ofNullable(unitPosition.getReasonCode()).isPresent()) {
                unitPosition.setReasonCode(reasonCode);
            }
        }

        Employment employment = employmentGraphRepository.findEmploymentByStaff(staffId);
        if (employment.getMainEmploymentEndDate() != null) {
            Long mainEmploymentEndDate = DateUtil.getDateFromEpoch(employment.getMainEmploymentEndDate());
            if (endDateMillis > mainEmploymentEndDate) {
                exceptionService.invalidRequestException("message.employmentdata.lessthan.mainEmploymentEndDate");
            }
        }
        employment.setEndDateMillis(endDateMillis);
        employmentGraphRepository.deleteEmploymentReasonCodeRelation(staffId);

        employment.setReasonCode(reasonCode);
        employment.setAccessGroupIdOnEmploymentEnd(accessGroupId);
        unitPositionGraphRepository.saveAll(unitPositions);
        employmentGraphRepository.save(employment);
        if (Optional.ofNullable(employmentEndDate).isPresent() && (DateUtil.getDateFromEpoch(endDateMillis).compareTo(DateUtil.getTimezonedCurrentDate(unit.getTimeZone().toString())) == 0)) {
            //employment = employmentGraphRepository.findEmploymentByStaff(staffId);
            List<Long> employmentIds = Stream.of(employment.getId()).collect(Collectors.toList());
            employmentService.moveToReadOnlyAccessGroup(employmentIds);
        }
        User user = userGraphRepository.getUserByStaffId(staffId);
        EmploymentQueryResult employmentUpdated = new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), employment.getReasonCode().getId(), employment.getAccessGroupIdOnEmploymentEnd());
        EmploymentUnitPositionDTO employmentUnitPositionDTO = new EmploymentUnitPositionDTO(employmentUpdated, unitPositionGraphRepository.getAllUnitPositionsByUser(user.getId()));
        return employmentUnitPositionDTO;

    }

    public Long getUnitPositionIdByStaffAndExpertise(Long unitId, Long staffId, Long dateInMillis,Long expertiseId) {
        return unitPositionGraphRepository.getUnitPositionIdByStaffAndExpertise(unitId, staffId, expertiseId, dateInMillis);
    }

    public Map<Long, Long> getUnitPositionExpertiseMap(Long unitId) {
        List<Map<Long, Long>> listOfMap = unitPositionGraphRepository.getMapOfUnitPositionAndExpertiseId(unitId);
        Map<Long, Long> mapOfUnitPositionAndExpertise = new HashMap<>(listOfMap.size());
        listOfMap.stream().forEach(mapOfExpertise -> {
            mapOfUnitPositionAndExpertise.putAll(mapOfExpertise);
        });
        return mapOfUnitPositionAndExpertise;
    }

    public Boolean applyFunction(Long unitPositionId, Map<String, Object> payload) throws ParseException {

        String dateAsString = new ArrayList<>(payload.keySet()).get(0);

        Map<String, Object> functionMap = (Map<String, Object>) payload.get(dateAsString);
        Long functionId = new Long((Integer) functionMap.get("id"));

        Date date = DateUtils.convertToOnlyDate(dateAsString, ONLY_DATE);
        Boolean unitPositionFunctionRelationship = unitPositionFunctionRelationshipRepository.getUnitPositionFunctionRelationshipByUnitPositionAndFunction(unitPositionId, functionId, date.getTime());

        if (unitPositionFunctionRelationship == null) {
            unitPositionFunctionRelationshipRepository.createUnitPositionFunctionRelationship(unitPositionId, functionId, Arrays.asList(date.getTime()));
        } else if (unitPositionFunctionRelationship == true) {
            exceptionService.actionNotPermittedException("message.unitposition.function.alreadyApplied", dateAsString);
        }
        return true;
    }

    public Boolean removeFunction(Long unitPositionId, Date appliedDate) throws ParseException {
        unitPositionFunctionRelationshipRepository.removeDateFromUnitPositionFunctionRelationship(unitPositionId, appliedDate.getTime());
        return true;
    }

    public List<com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails> getStaffsUnitPosition(Long unitId, Long expertiseId, List<Long> staffId) {
        List<com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails> staffData =
                staffGraphRepository.getStaffInfoByUnitIdAndStaffId(unitId, expertiseId, staffId);
        return staffData;
    }

    public List<com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails> getStaffIdAndUnitPositionId(Long unitId, Long expertiseId, List<Long> staffId) {
        List<com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails> staffData =
                staffGraphRepository.getStaffIdAndUnitPositionId(unitId, expertiseId, staffId, System.currentTimeMillis());
        return staffData;
    }
}
