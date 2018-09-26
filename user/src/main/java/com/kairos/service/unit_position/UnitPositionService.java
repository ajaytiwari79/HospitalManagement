package com.kairos.service.unit_position;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTATableSettingWrapper;
import com.kairos.dto.activity.cta.CTAWTAWrapper;
import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.activity.wta.version.WTATableSettingWrapper;
import com.kairos.dto.scheduler.KairosSchedulerLogsDTO;
import com.kairos.dto.scheduler.kafka.producer.KafkaProducer;
import com.kairos.dto.user.employment.UnitPositionIdDTO;
import com.kairos.dto.user.organization.position_code.PositionCodeDTO;
import com.kairos.dto.user.staff.unit_position.UnitPositionDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.Result;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.query_results.ClientMinimumDTO;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.staff.TimeCareEmploymentDTO;
import com.kairos.persistence.model.staff.employment.Employment;
import com.kairos.persistence.model.staff.employment.EmploymentQueryResult;
import com.kairos.persistence.model.staff.employment.EmploymentReasonCodeQueryResult;
import com.kairos.persistence.model.staff.employment.EmploymentUnitPositionDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.ExpertisePlannedTimeQueryResult;
import com.kairos.persistence.model.user.expertise.Response.SeniorityLevelQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.persistence.model.user.unit_position.PositionLine;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionEmploymentTypeRelationShip;
import com.kairos.persistence.model.user.unit_position.query_result.*;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
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
import com.kairos.rest_client.TimeBankRestClient;
import com.kairos.rest_client.WorkingTimeAgreementRestClient;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.AsynchronousService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.position_code.PositionCodeService;
import com.kairos.service.scheduler.UserToSchedulerQueueService;
import com.kairos.service.staff.EmploymentService;
import com.kairos.service.staff.StaffService;
import com.kairos.utils.DateUtil;
import com.kairos.wrapper.PositionWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.ONLY_DATE;
import static com.kairos.constants.ApiConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@Transactional
@Service

public class UnitPositionService {
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
    //@Inject
    //private CollectiveTimeAgreementGraphRepository costTimeAgreementGraphRepository;
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
    private ExpertiseEmploymentTypeRelationshipGraphRepository expertiseEmploymentTypeRelationshipGraphRepository;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private UserToSchedulerQueueService userToSchedulerQueueService;
    @Inject
    private GenericRestClient genericRestClient;
    @Inject
    private KafkaProducer kafkaProducer;
    @Inject
    private AsynchronousService asynchronousService;

    public PositionWrapper createUnitPosition(Long id, String type, UnitPositionDTO unitPositionDTO, Boolean createFromTimeCare, Boolean saveAsDraft) throws InterruptedException, ExecutionException {
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
            if (unitPositionDTO.getStartLocalDate().isBefore(DateUtils.getDateFromEpoch(employment.getStartDateMillis()))) {
                exceptionService.actionNotPermittedException("message.staff.data.employmentdate.lessthan");
            }
        }
        if (!Optional.ofNullable(positionCode).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.position.name.notexist", unitPositionDTO.getPositionCodeId());
        }
        if (!saveAsDraft) {
            List<UnitPosition> oldUnitPositions = unitPositionGraphRepository.getStaffUnitPositionsByExpertise(organization.getId(), unitPositionDTO.getStaffId(), unitPositionDTO.getExpertiseId());
            validateUnitPositionWithExpertise(oldUnitPositions, unitPositionDTO);
        }


        EmploymentType employmentType = organizationGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(parentOrganization.getId(), unitPositionDTO.getEmploymentTypeId(), false);
        if (!Optional.ofNullable(employmentType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.position.employmenttype.notexist", unitPositionDTO.getEmploymentTypeId());
        }
        UnitPosition unitPosition =
                new UnitPosition(positionCode, organization, DateUtils.getLongFromLocalDate(unitPositionDTO.getStartLocalDate()), unitPositionDTO.getTimeCareExternalId(), !saveAsDraft);

        preparePosition(unitPosition, unitPositionDTO, createFromTimeCare);

        unitPositionGraphRepository.save(unitPosition);
        CTAWTAWrapper ctawtaWrapper = assignCTAAndWTAToUnitPosition(unitPosition, unitPositionDTO);
        Long reasonCodeId = updateEmploymentEndDate(parentOrganization, unitPositionDTO, employment);

        UnitPositionEmploymentTypeRelationShip relationShip = new UnitPositionEmploymentTypeRelationShip(unitPosition.getPositionLines().get(0), employmentType, unitPositionDTO.getEmploymentTypeCategory());
        unitPositionEmploymentTypeRelationShipGraphRepository.save(relationShip);

        UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPositionDTO, unitPosition, relationShip, parentOrganization.getId(), parentOrganization.getName(), ctawtaWrapper.getWta().get(0), unitPosition.getPositionLines().get(0));
        unitPositionQueryResult.setCostTimeAgreement(ctawtaWrapper.getCta().get(0));
        return new PositionWrapper(unitPositionQueryResult, new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), reasonCodeId, employment.getAccessGroupIdOnEmploymentEnd()));
    }

    private CTAWTAWrapper assignCTAAndWTAToUnitPosition(UnitPosition unitPosition, UnitPositionDTO unitPositionDTO) {
        CTAWTAWrapper ctawtaWrapper = workingTimeAgreementRestClient.assignWTAToUnitPosition(unitPosition.getId(), unitPositionDTO.getWtaId(), unitPositionDTO.getCtaId());
        if (ctawtaWrapper.getWta().isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.wta.id");
        }
        if (ctawtaWrapper.getCta().isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.cta.id");
        }
        return ctawtaWrapper;
    }

    private Long updateEmploymentEndDate(Organization organization, UnitPositionDTO unitPositionDTO, Employment employment) {
        Employment employment1 = employmentService.updateEmploymentEndDate(organization, unitPositionDTO.getStaffId(), unitPositionDTO.getEndLocalDate() != null ? DateUtil.getDateFromEpoch(unitPositionDTO.getEndLocalDate()) : null, unitPositionDTO.getReasonCodeId(), unitPositionDTO.getAccessGroupIdOnEmploymentEnd());
        return Optional.ofNullable(employment.getReasonCode()).isPresent() ? employment1.getReasonCode().getId() : null;

    }

    public boolean validateUnitPositionWithExpertise(List<UnitPosition> unitPositions, UnitPositionDTO unitPositionDTO) {

        LocalDate unitPositionStartDate = unitPositionDTO.getStartLocalDate();
        LocalDate unitPositionEndDate = unitPositionDTO.getEndLocalDate();
        unitPositions.forEach(unitPosition -> {
            // if null date is set
            if (unitPosition.getEndDateMillis() != null) {
                if (unitPositionStartDate.isBefore(DateUtil.getDateFromEpoch(unitPosition.getEndDateMillis())) && unitPositionStartDate.isAfter(DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()))) {
                    exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", unitPositionEndDate, DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()));
                }
                if (unitPositionEndDate != null) {
                    Interval previousInterval = new Interval(unitPosition.getStartDateMillis(), unitPosition.getEndDateMillis());
                    Interval interval = new Interval(DateUtil.getDateFromEpoch(unitPositionStartDate), DateUtil.getDateFromEpoch(unitPositionEndDate));
                    logger.info(" Interval of CURRENT UEP " + previousInterval + " Interval of going to create  " + interval);
                    if (previousInterval.overlaps(interval))
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist");
                } else {
                    if (unitPositionStartDate.isBefore(DateUtil.getDateFromEpoch(unitPosition.getEndDateMillis()))) {
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", unitPositionEndDate, DateUtil.getDateFromEpoch(unitPosition.getEndDateMillis()));
                    }
                }
            } else {
                // unitEmploymentEnd date is null
                if (unitPositionEndDate != null) {
                    if (unitPositionEndDate.isAfter(DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()))) {
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", unitPositionEndDate, DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()));
                    }
                } else {
                    exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist");
                }
            }
        });
        return true;
    }

    private PositionLine createPositionLine(UnitPosition oldUnitPosition, PositionLine oldPositionLine, UnitPositionDTO unitPositionDTO) {
        if (unitPositionDTO.getStartLocalDate().isBefore(LocalDate.now())) {
            exceptionService.actionNotPermittedException("message.startdate.notlessthan.currentdate");
        }
        if (Optional.ofNullable(unitPositionDTO.getEndLocalDate()).isPresent() && unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getEndLocalDate())) {
                exceptionService.actionNotPermittedException("message.startdate.notlessthan.enddate");
        }
        if (Optional.ofNullable(unitPositionDTO.getLastWorkingLocalDate()).isPresent() && unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getLastWorkingLocalDate())) {
                exceptionService.actionNotPermittedException("message.lastdate.notlessthan.enddate");
        }
        oldUnitPosition.setLastWorkingDateMillis(DateUtils.getLongFromLocalDate(unitPositionDTO.getLastWorkingLocalDate()));
        List<Function> functions = functionGraphRepository.findAllFunctionsById(unitPositionDTO.getFunctionIds());
        if (functions.size() != unitPositionDTO.getFunctionIds().size()) {
            exceptionService.actionNotPermittedException("message.unitposition.functions.unable");
        }
        PositionLine positionLine = new PositionLine.PositionLineBuilder()
                .setAvgDailyWorkingHours(unitPositionDTO.getAvgDailyWorkingHours())
                .setTotalWeeklyMinutes((unitPositionDTO.getTotalWeeklyHours()*60)+unitPositionDTO.getTotalWeeklyMinutes())
                .setHourlyWages(unitPositionDTO.getHourlyWages())
                .setStartDate(unitPositionDTO.getStartLocalDate())
                .setFunctions(functions)
                .setFullTimeWeeklyMinutes(oldPositionLine.getFullTimeWeeklyMinutes())
                .setWorkingDaysInWeek(oldPositionLine.getWorkingDaysInWeek())
                .setEndDate(unitPositionDTO.getEndLocalDate())
                .setSeniorityLevel(oldPositionLine.getSeniorityLevel())
                .build();

        oldPositionLine.setEndDate(unitPositionDTO.getStartLocalDate().minusDays(1));
        if (Optional.ofNullable(unitPositionDTO.getEndLocalDate()).isPresent()) {

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

        return positionLine;
    }


    private boolean calculativeValueChanged(UnitPosition oldUnitPosition, UnitPositionDTO unitPositionDTO, UnitPositionEmploymentTypeRelationShip oldUnitPositionEmploymentTypeRelationShip, PositionLine positionLine) {
        if (positionLine.getAvgDailyWorkingHours() != unitPositionDTO.getAvgDailyWorkingHours()
                || positionLine.getTotalWeeklyMinutes() != (unitPositionDTO.getTotalWeeklyMinutes()+(unitPositionDTO.getTotalWeeklyHours()*60))
                || (oldUnitPosition.getReasonCode() != null && !oldUnitPosition.getReasonCode().getId().equals(unitPositionDTO.getReasonCodeId()))
                || (positionLine.getFunctions() != null && !positionLine.getFunctions().stream().map(Function::getId).collect(Collectors.toSet()).equals(unitPositionDTO.getFunctionIds()))) {
            return true;
        } else if (!oldUnitPositionEmploymentTypeRelationShip.getEmploymentType().getId().equals(unitPositionDTO.getEmploymentTypeId()) || !oldUnitPositionEmploymentTypeRelationShip.getEmploymentTypeCategory().equals(unitPositionDTO.getEmploymentTypeCategory())) {
            return true;
        }
        return false;
    }

    private void linkUnitPositionWithEmploymentType(PositionLine positionLine, UnitPositionDTO unitPositionDTO) {
        EmploymentType employmentType = employmentTypeGraphRepository.findOne(unitPositionDTO.getEmploymentTypeId());
        if (!Optional.ofNullable(employmentType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.position.employmenttype.notexist", unitPositionDTO.getEmploymentTypeId());
        }

        UnitPositionEmploymentTypeRelationShip relationShip = new UnitPositionEmploymentTypeRelationShip(positionLine, employmentType, unitPositionDTO.getEmploymentTypeCategory());
        unitPositionEmploymentTypeRelationShipGraphRepository.save(relationShip);
    }

    private void assignWTACTA(Long oldUnitPositionId, Long newUnitPositionId) {
        activityIntegrationService.copyWTACTA(Collections.singletonList(new UnitPositionIdDTO(oldUnitPositionId, newUnitPositionId)));
    }

    public PositionWrapper updateUnitPosition(long unitPositionId, UnitPositionDTO unitPositionDTO, Long unitId, String type, Boolean saveAsDraft) {

        Organization organization = organizationService.getOrganizationDetail(unitId, type);
        List<ClientMinimumDTO> clientMinimumDTO = clientGraphRepository.getCitizenListForThisContactPerson(unitPositionDTO.getStaffId());
        if (clientMinimumDTO.size() > 0) {
            return new PositionWrapper(clientMinimumDTO);
        }

        UnitPosition oldUnitPosition = unitPositionGraphRepository.findOne(unitPositionId, 2);
        if (!Optional.ofNullable(oldUnitPosition).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.positionid.notfound", unitPositionId);
        }
        PositionLine currentPositionLine = oldUnitPosition.getPositionLines().stream().filter(positionLine -> positionLine.getId().equals(unitPositionDTO.getPositionLineId()))
                .findFirst().orElse(null);
        if (currentPositionLine == null) {
            exceptionService.dataNotFoundByIdException("message.positionid.notfound", unitPositionId);
        }
        UnitPositionEmploymentTypeRelationShip positionLineEmploymentTypeRelationShip = unitPositionGraphRepository.findEmploymentTypeByUnitPositionId(currentPositionLine.getId());
        EmploymentQueryResult employmentQueryResult;
        UnitPositionQueryResult unitPositionQueryResult;
        Boolean calculativeValueChanged = calculativeValueChanged(oldUnitPosition, unitPositionDTO, positionLineEmploymentTypeRelationShip, currentPositionLine);
        /**
         *  Old unit position is published and calculative values is changes and both options save and  published is selected
         *  Old unit position is published so need to create a new  position line
         **/
        if (calculativeValueChanged ) {
            List<UnitPosition> oldUnitPositions
                    = unitPositionGraphRepository.getAllUEPByExpertiseExcludingCurrent(unitPositionDTO.getUnitId(), unitPositionDTO.getStaffId(), unitPositionDTO.getExpertiseId(), unitPositionId);
            validateUnitPositionWithExpertise(oldUnitPositions, unitPositionDTO);
            PositionLine positionLine = createPositionLine(oldUnitPosition, currentPositionLine, unitPositionDTO);
            oldUnitPosition.getPositionLines().add(positionLine);
            unitPositionGraphRepository.save(oldUnitPosition);
            linkUnitPositionWithEmploymentType(positionLine, unitPositionDTO);
            unitPositionQueryResult = getBasicDetails(unitPositionDTO, oldUnitPosition, positionLineEmploymentTypeRelationShip, organization.getId(), organization.getName(), null, positionLine);
        }
        // calculative value is not changed it means only end date is updated.
        else {
            oldUnitPosition.setEndDateMillis(unitPositionDTO.getEndLocalDate() != null ? DateUtils.getLongFromLocalDate(unitPositionDTO.getEndLocalDate()) : null);
            oldUnitPosition.setLastWorkingDateMillis(unitPositionDTO.getLastWorkingLocalDate() != null ? DateUtils.getLongFromLocalDate(unitPositionDTO.getLastWorkingLocalDate()) : null);
            unitPositionGraphRepository.save(oldUnitPosition);
            unitPositionQueryResult = getBasicDetails(unitPositionDTO, oldUnitPosition, positionLineEmploymentTypeRelationShip, organization.getId(), organization.getName(), null, currentPositionLine);
        }


        Employment employment = employmentService.updateEmploymentEndDate(oldUnitPosition.getUnit(), unitPositionDTO.getStaffId(),
                unitPositionDTO.getEndLocalDate() != null ? DateUtil.getDateFromEpoch(unitPositionDTO.getEndLocalDate()) : null, unitPositionDTO.getReasonCodeId(), unitPositionDTO.getAccessGroupIdOnEmploymentEnd());
        Long reasonCodeId = Optional.ofNullable(employment.getReasonCode()).isPresent() ? employment.getReasonCode().getId() : null;
        employmentQueryResult = new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), reasonCodeId, employment.getAccessGroupIdOnEmploymentEnd());
        // Deleting All shifts after employment end date
        if (unitPositionDTO.getEndLocalDate() != null) {
            activityIntegrationService.deleteShiftsAfterEmploymentEndDate(unitId, unitPositionDTO.getEndLocalDate(), unitPositionDTO.getStaffId());
        }
        //TODO might remove -- FOR FE compactibility
        WTAResponseDTO wtaResponseDTO = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_WTA_BY_UNITPOSITION, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>>() {
        }, unitPositionId);
        unitPositionQueryResult.setWorkingTimeAgreement(wtaResponseDTO);
        //plannerSyncService.publishUnitPosition(unitId, oldUnitPosition, unitPositionEmploymentTypeRelationShip.getEmploymentType(), IntegrationOperation.UPDATE);
        return new PositionWrapper(unitPositionQueryResult, employmentQueryResult);

    }

    public EmploymentQueryResult removePosition(long positionId, Long unitId) {
        UnitPosition unitPosition = unitPositionGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(unitPosition).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitposition.id.notexist", positionId);

        }
        unitPosition.setDeleted(true);
        unitPositionGraphRepository.save(unitPosition);

        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        Long staffId = unitPositionGraphRepository.getStaffIdFromUnitPosition(positionId);
        Employment employment = employmentService.updateEmploymentEndDate(unit, staffId);
        //plannerSyncService.publishUnitPosition(unitId, unitPosition, null, IntegrationOperation.DELETE);
        return new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis());
    }


    public UnitPositionQueryResult getUnitPosition(Long unitPositionId) {
        UnitPositionQueryResult unitPositionQueryResult = unitPositionGraphRepository.findByUnitPositionId(unitPositionId);
        return unitPositionQueryResult;
    }

    @Async
    private CompletableFuture<Boolean> setDefaultData(UnitPositionDTO unitPositionDTO, UnitPosition unitPosition) throws InterruptedException, ExecutionException {
        Callable<Expertise> expertiseCallable = () -> {
            Optional<Expertise> expertise = expertiseGraphRepository.findById(unitPositionDTO.getExpertiseId(), 1);
            if (!expertise.isPresent()) {
                exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", unitPositionDTO.getExpertiseId());
            }
            return expertise.get();
        };
        Future<Expertise> expertiseFuture = asynchronousService.executeAsynchronously(expertiseCallable);
        unitPosition.setExpertise(expertiseFuture.get());
        if (Optional.ofNullable(unitPositionDTO.getUnionId()).isPresent()) {
            Callable<Organization> organizationCallable = () -> organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitPositionDTO.getUnionId());
            Future<Organization> organizationFuture = asynchronousService.executeAsynchronously(organizationCallable);
            if (!Optional.ofNullable(organizationFuture.get()).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.unitposition.union.notexist", unitPositionDTO.getUnionId());
            }
            unitPosition.setUnion(organizationFuture.get());
        }
        Callable<Staff> staffCallable = () -> staffGraphRepository.findOne(unitPositionDTO.getStaffId());
        Future<Staff> staffFuture = asynchronousService.executeAsynchronously(staffCallable);
        if (!Optional.ofNullable(staffFuture.get()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitposition.staff.notfound", unitPositionDTO.getStaffId());
        }
        unitPosition.setExpertise(expertiseFuture.get());
        unitPosition.setStaff(staffFuture.get());
        return CompletableFuture.completedFuture(true);
    }

    private UnitPosition preparePosition(UnitPosition unitPosition, UnitPositionDTO unitPositionDTO, Boolean createFromTimeCare) throws InterruptedException, ExecutionException {
        CompletableFuture<Boolean> done = setDefaultData(unitPositionDTO, unitPosition);
        CompletableFuture.allOf(done).join();
        // UEP can be created for past dates from time care
        if (!createFromTimeCare && unitPositionDTO.getStartLocalDate().isBefore(LocalDate.now())) {
            exceptionService.actionNotPermittedException("message.startdate.notlessthan.currentdate");
        }
        unitPosition.setStartDateMillis(DateUtils.getLongFromLocalDate(unitPositionDTO.getStartLocalDate()));

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
            unitPosition.setEndDateMillis(DateUtils.getLongFromLocalDate(unitPositionDTO.getEndLocalDate()));
        }

        if (Optional.ofNullable(unitPositionDTO.getLastWorkingLocalDate()).isPresent()) {
            if (unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getLastWorkingLocalDate())) {
                exceptionService.actionNotPermittedException("message.lastdate.notlessthan.startdate");
            }
            unitPosition.setLastWorkingDateMillis(DateUtils.getLongFromLocalDate(unitPositionDTO.getLastWorkingLocalDate()));
        }


        SeniorityLevel seniorityLevel = getSeniorityLevelByStaffAndExpertise(unitPosition.getStaff().getId(), unitPosition.getExpertise());

        if (!Optional.ofNullable(seniorityLevel).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.seniorityLevel.id.notfound", unitPositionDTO.getReasonCodeId());
        }
        //seniorityLevel.setPayGrade(payGradeGraphRepository.getPayGradeBySeniorityLevelId(seniorityLevel.getId()));

        List<Function> functions = functionGraphRepository.findAllFunctionsById(unitPositionDTO.getFunctionIds());
        if (functions.size() != unitPositionDTO.getFunctionIds().size()) {
            exceptionService.actionNotPermittedException("message.unitposition.functions.unable");
        }
        PositionLine positionLine = new PositionLine.PositionLineBuilder()
                .setSeniorityLevel(seniorityLevel)
                .setFunctions(functions)
                .setStartDate(unitPositionDTO.getStartLocalDate())
                .setEndDate(unitPositionDTO.getEndLocalDate())
                .setTotalWeeklyMinutes(unitPositionDTO.getTotalWeeklyMinutes() + (unitPositionDTO.getTotalWeeklyHours() * 60))
                .setFullTimeWeeklyMinutes(unitPosition.getExpertise().getFullTimeWeeklyMinutes())
                .setWorkingDaysInWeek(unitPosition.getExpertise().getNumberOfWorkingDaysInWeek())
                .setAvgDailyWorkingHours(unitPositionDTO.getAvgDailyWorkingHours())

                .setHourlyWages(unitPositionDTO.getHourlyWages())
                .build();
        unitPosition.setPositionLines(Collections.singletonList(positionLine));
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
            if (!unitPositionDTO.getUnionId().equals(oldUnitPosition.getUnion().getId())) {
                Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitPositionDTO.getUnionId());
                if (!Optional.ofNullable(union).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.unitposition.union.notexist", unitPositionDTO.getUnionId());

                }
                oldUnitPosition.setUnion(union);
            }
        }

    }

    // This is while update
    private void preparePosition(UnitPosition oldUnitPosition, UnitPositionDTO unitPositionDTO, UnitPositionEmploymentTypeRelationShip unitPositionEmploymentTypeRelationShip) {

        prepareUnion(oldUnitPosition, unitPositionDTO);
        Employment employment = employmentGraphRepository.findEmploymentByStaff(unitPositionDTO.getStaffId());
        if (!Optional.ofNullable(employment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.employment.notFound", unitPositionDTO.getStaffId());

        }
        if (employment.getStartDateMillis() != null) {
            if (unitPositionDTO.getStartLocalDate().isBefore(DateUtil.getDateFromEpoch(employment.getStartDateMillis()))) {
                exceptionService.actionNotPermittedException("message.staff.data.employmentdate.lessthan");
            }
        }
        oldUnitPosition.setStartDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getStartLocalDate()));
        }

    /*
     * @author vipul
     * used to get all positions of organization n by organization and staff Id
     * */
    public EmploymentUnitPositionDTO getUnitPositionsOfStaff(long unitId, long staffId, boolean allOrganization) {
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
        List<Long> unitPositionIds = unitPositionQueryResults.stream().map(UnitPositionQueryResult::getId).collect(Collectors.toList());
        List<NameValuePair> param = Collections.singletonList(new BasicNameValuePair("upIds", unitPositionIds.toString().replace("[", "").replace("]", "")));
        CTAWTAWrapper ctawtaWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_CTA_WTA_BY_UPIDS, param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAWrapper>>() {
        });
        Map<Long, WTAResponseDTO> wtaResponseDTOMap = ctawtaWrapper.getWta().stream().collect(Collectors.toMap(WTAResponseDTO::getUnitPositionId, w -> w));
        Map<Long, CTAResponseDTO> ctaResponseDTOMap = ctawtaWrapper.getCta().stream().collect(Collectors.toMap(CTAResponseDTO::getUnitPositionId, c -> c));
        List<PositionLinesQueryResult> positionLines = unitPositionGraphRepository.findAllPositionLines(unitPositionIds);
        Map<Long, List<PositionLinesQueryResult>> positionLinesMap = positionLines.stream().collect(Collectors.groupingBy(PositionLinesQueryResult::getUnitPositionId));
        unitPositionQueryResults.forEach(u -> {
            u.setWorkingTimeAgreement(wtaResponseDTOMap.get(u.getId()));
            u.setCostTimeAgreement(ctaResponseDTOMap.get(u.getId()));
            u.setPositionLines(positionLinesMap.get(u.getId()));
        });


        EmploymentUnitPositionDTO employmentUnitPositionDTO = new EmploymentUnitPositionDTO(employmentQueryResult, unitPositionQueryResults);
        return employmentUnitPositionDTO;
    }

    public PositionCtaWtaQueryResult getCtaAndWtaWithExpertiseDetailByExpertiseId(Long unitId, Long expertiseId, Long staffId) {
        PositionCtaWtaQueryResult positionCtaWtaQueryResult = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_CTA_WTA_BY_EXPERTISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<PositionCtaWtaQueryResult>>() {
        }, expertiseId);
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
        updateDTO.setId(wtaId);
        WTAResponseDTO wtaResponseDTO = workingTimeAgreementRestClient.updateWTAOfUnitPosition(updateDTO, unitPosition.isPublished());
        unitPositionGraphRepository.save(unitPosition);
        UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPosition, wtaResponseDTO, unitPosition.getPositionLines().get(0));
        return unitPositionQueryResult;
    }

    private UnitPositionQueryResult getBasicDetails(UnitPositionDTO unitPositionDTO, UnitPosition unitPosition, UnitPositionEmploymentTypeRelationShip relationShip,
                                                    Long parentOrganizationId, String parentOrganizationName, WTAResponseDTO wtaResponseDTO, PositionLine positionLine) {


        logger.info(unitPosition.toString());

        UnitPositionQueryResult result = new UnitPositionQueryResult(unitPosition.getExpertise().retrieveBasicDetails(), unitPosition.getStartDateMillis(),
                positionLine.getWorkingDaysInWeek(),
                unitPosition.getEndDateMillis(),
                positionLine.getTotalWeeklyMinutes(),
                positionLine.getAvgDailyWorkingHours(),
                positionLine.getHourlyWages(),
                unitPosition.getId(),
                0D,
                unitPosition.getPositionCode(),
                unitPosition.getUnion(),
                unitPosition.getLastWorkingDateMillis(),
                null, wtaResponseDTO);

        result.setUnitId(unitPosition.getUnit().getId());
        result.setReasonCodeId(unitPosition.getReasonCode() != null ? unitPosition.getReasonCode().getId() : null);
        result.setParentUnitId(parentOrganizationId);
        result.setEditable(unitPosition.isEditable());
        result.setHistory(unitPosition.isHistory());
        result.setPublished(unitPosition.isPublished());


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
        Map<String, Object> seniorityLevel = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        seniorityLevel = objectMapper.convertValue(positionLine.getSeniorityLevel(), Map.class);
        seniorityLevel.put("functions", unitPositionDTO.getFunctionIds());
        seniorityLevel.put("payGrade", positionLine.getSeniorityLevel().getPayGrade());
        result.setSeniorityLevel(seniorityLevel);
        return result;
    }

    private UnitPositionQueryResult getBasicDetails(UnitPosition unitPosition, WTAResponseDTO wtaResponseDTO, PositionLine positionLine) {
        UnitPositionQueryResult unitPositionQueryResult = unitPositionGraphRepository.getUnitIdAndParentUnitIdByUnitPositionId(unitPosition.getId());
        UnitPositionQueryResult result = new UnitPositionQueryResult(unitPosition.getExpertise().retrieveBasicDetails(), unitPosition.getStartDateMillis(), positionLine.getWorkingDaysInWeek(),
                unitPosition.getEndDateMillis(), positionLine.getTotalWeeklyMinutes(), positionLine.getAvgDailyWorkingHours(), positionLine.getHourlyWages(),
                unitPosition.getId(), 0D, unitPosition.getPositionCode(), unitPosition.getUnion(),
                unitPosition.getLastWorkingDateMillis(), null, null/*unitPosition.getWorkingTimeAgreement()*/);
        result.setReasonCodeId(unitPosition.getReasonCode() != null ? unitPosition.getReasonCode().getId() : null);
        result.setUnitId(unitPositionQueryResult.getUnitId());
        result.setEditable(unitPosition.isEditable());
        result.setHistory(unitPosition.isHistory());
        result.setPublished(unitPosition.isPublished());
        result.setWorkingTimeAgreement(wtaResponseDTO);
        result.setParentUnitId(unitPositionQueryResult.getParentUnitId());
        return result;
    }


    public com.kairos.dto.activity.shift.StaffUnitPositionDetails getUnitPositionCTA(Long unitPositionId, Long unitId) {

        StaffUnitPositionDetails unitPosition = unitPositionGraphRepository.getUnitPositionById(unitPositionId);
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionWithCtaDetailsDTO = new com.kairos.dto.activity.shift.StaffUnitPositionDetails();
        unitPositionWithCtaDetailsDTO.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
//        unitPositionWithCtaDetailsDTO.setStaffId(unitPosition.getStaff().getId());
        unitPositionWithCtaDetailsDTO.setId(unitPosition.getId());
        unitPositionWithCtaDetailsDTO.setCountryId(countryId);
        unitPositionWithCtaDetailsDTO.setTotalWeeklyMinutes(unitPosition.getTotalWeeklyMinutes());
        unitPositionWithCtaDetailsDTO.setWorkingDaysInWeek(unitPosition.getWorkingDaysInWeek());
        unitPositionWithCtaDetailsDTO.setStartDate(new Date(unitPosition.getStartDateMillis()));
        unitPositionWithCtaDetailsDTO.setWorkingTimeAgreementId(unitPosition.getWorkingTimeAgreementId());
        unitPositionWithCtaDetailsDTO.setUnitPositionStartDate(DateUtils.asLocalDate(new Date(unitPosition.getStartDateMillis())));
        if (unitPosition.getEndDateMillis() != null) {
            unitPositionWithCtaDetailsDTO.setUnitPositionEndDate(DateUtils.asLocalDate(new Date(unitPosition.getEndDateMillis())));
            unitPositionWithCtaDetailsDTO.setEndDate(new Date(unitPosition.getEndDateMillis()));
        }
        Optional<Organization> organization = organizationGraphRepository.findById(unitId, 0);
        unitPositionWithCtaDetailsDTO.setUnitTimeZone(organization.get().getTimeZone());
        com.kairos.dto.activity.shift.EmploymentType employmentType = new com.kairos.dto.activity.shift.EmploymentType();
        ObjectMapperUtils.copyProperties(unitPosition.getEmploymentType(), employmentType);
        unitPositionWithCtaDetailsDTO.setEmploymentType(employmentType);
        return unitPositionWithCtaDetailsDTO;
    }

    public void convertUnitPositionObject(UnitPositionQueryResult unitPosition, com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails) {


        unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        unitPositionDetails.setEmploymentType(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getEmploymentType(), com.kairos.dto.activity.shift.EmploymentType.class));
        unitPositionDetails.setId(unitPosition.getId());
        unitPositionDetails.setFullTimeWeeklyMinutes(unitPosition.getFullTimeWeeklyMinutes());
        unitPositionDetails.setTotalWeeklyMinutes(unitPosition.getTotalWeeklyMinutes());
        unitPositionDetails.setWorkingDaysInWeek(unitPosition.getWorkingDaysInWeek());
        unitPositionDetails.setStartDateMillis(unitPosition.getStartDateMillis());
        unitPositionDetails.setWorkingTimeAgreementId(unitPosition.getWorkingTimeAgreementId());
        unitPositionDetails.setUnitPositionStartDate(DateUtils.asLocalDate(new Date(unitPosition.getStartDateMillis())));
        unitPositionDetails.setCostTimeAgreementId(unitPosition.getCostTimeAgreementId());
        if (unitPosition.getEndDateMillis() != null) {
            unitPositionDetails.setUnitPositionEndDate(DateUtils.asLocalDate(new Date(unitPosition.getEndDateMillis())));
            unitPositionDetails.setEndDateMillis(unitPosition.getEndDateMillis());
        }
    }

    private void convertUnitPositionObject(StaffUnitPositionDetails unitPosition, com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails) {
        unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        unitPositionDetails.setEmploymentType(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getEmploymentType(), com.kairos.dto.activity.shift.EmploymentType.class));
        unitPositionDetails.setId(unitPosition.getId());
        unitPositionDetails.setFullTimeWeeklyMinutes(unitPosition.getFullTimeWeeklyMinutes());
        unitPositionDetails.setTotalWeeklyMinutes(unitPosition.getTotalWeeklyMinutes());
        unitPositionDetails.setWorkingDaysInWeek(unitPosition.getWorkingDaysInWeek());
        unitPositionDetails.setStartDateMillis(unitPosition.getStartDateMillis());
        unitPositionDetails.setWorkingTimeAgreementId(unitPosition.getWorkingTimeAgreementId());
        unitPositionDetails.setUnitPositionStartDate(DateUtils.asLocalDate(new Date(unitPosition.getStartDateMillis())));
        unitPositionDetails.setCostTimeAgreementId(unitPosition.getCostTimeAgreementId());
        if (unitPosition.getEndDateMillis() != null) {
            unitPositionDetails.setUnitPositionEndDate(DateUtils.asLocalDate(new Date(unitPosition.getEndDateMillis())));
            unitPositionDetails.setEndDateMillis(unitPosition.getEndDateMillis());
        }
    }

    public com.kairos.dto.activity.shift.StaffUnitPositionDetails getUnitPositionDetails(Long unitPositionId, Organization organization, Long countryId) {

        StaffUnitPositionDetails unitPosition = unitPositionGraphRepository.getUnitPositionById(unitPositionId);
        com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails = new com.kairos.dto.activity.shift.StaffUnitPositionDetails();
        convertUnitPositionObject(unitPosition, unitPositionDetails);
        unitPositionDetails.setCountryId(countryId);
        ExpertisePlannedTimeQueryResult expertisePlannedTimeQueryResult = expertiseEmploymentTypeRelationshipGraphRepository.findPlannedTimeByExpertise(unitPositionDetails.getExpertise().getId(), unitPositionDetails.getEmploymentType().getId());
        if (Optional.ofNullable(expertisePlannedTimeQueryResult).isPresent()) {
            unitPositionDetails.setExcludedPlannedTime(expertisePlannedTimeQueryResult.getExcludedPlannedTime());
            unitPositionDetails.setIncludedPlannedTime(expertisePlannedTimeQueryResult.getIncludedPlannedTime());

        }
        unitPositionDetails.setUnitTimeZone(organization.getTimeZone());
        return unitPositionDetails;
    }


    public UnitPositionDTO convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(TimeCareEmploymentDTO timeCareEmploymentDTO, Long expertiseId, Long staffId, Long employmentTypeId, Long positionCodeId, BigInteger wtaId, BigInteger ctaId, Long unitId) {
        LocalDate startDate = DateUtils.getLocalDateFromString(timeCareEmploymentDTO.getStartDate());
        LocalDate endDate = null;
        if (!timeCareEmploymentDTO.getEndDate().equals("0001-01-01T00:00:00")) {
            endDate = DateUtils.getLocalDateFromString(timeCareEmploymentDTO.getEndDate());
        }
        UnitPositionDTO unitPositionDTO = new UnitPositionDTO(positionCodeId, expertiseId, startDate, endDate, Integer.parseInt(timeCareEmploymentDTO.getWeeklyHours()), employmentTypeId, staffId, wtaId, ctaId, unitId, new Long(timeCareEmploymentDTO.getId()));
        return unitPositionDTO;
    }

    public boolean addEmploymentToUnitByExternalId(List<TimeCareEmploymentDTO> timeCareEmploymentDTOs, String unitExternalId, Long expertiseId) throws InterruptedException, ExecutionException {
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


        CTAWTAWrapper ctawtaWrapper = workingTimeAgreementRestClient.getWTAByExpertise(expertise.getId());
        if (!CollectionUtils.isNotEmpty(ctawtaWrapper.getCta())) {
            exceptionService.dataNotFoundByIdException("message.organization.cta.notfound", organization.getId());

        }
        if (!CollectionUtils.isNotEmpty(ctawtaWrapper.getWta())) {
            exceptionService.dataNotFoundByIdException("message.wta.notFound", organization.getId());

        }
        PositionCode positionCode = positionCodeGraphRepository.getOneDefaultPositionCodeByUnitId(parentOrganization.getId());
        if (positionCode == null) {
            exceptionService.dataNotFoundByIdException("message.positioncode.organization.notexist", parentOrganization.getId());

        }

        for (TimeCareEmploymentDTO timeCareEmploymentDTO : timeCareEmploymentDTOs) {
            Staff staff = staffGraphRepository.findByExternalId(timeCareEmploymentDTO.getPersonID());
            if (staff == null) {
                exceptionService.dataNotFoundByIdException("message.staff.externalid.notexist", timeCareEmploymentDTO.getPersonID());

            }
            UnitPositionDTO unitEmploymentPosition = convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(timeCareEmploymentDTO, expertise.getId(), staff.getId(), employmentType.getId(), positionCode.getId(), ctawtaWrapper.getWta().get(0).getId(), ctawtaWrapper.getCta().get(0).getId(), organization.getId());
            createUnitPosition(organization.getId(), "Organization", unitEmploymentPosition, true, true);
        }
        return true;
    }

    public boolean importAllEmploymentsFromTimeCare(List<TimeCareEmploymentDTO> timeCareEmploymentsDTOs, Long expertiseId) throws InterruptedException, ExecutionException {

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

                if (seniorityLevel.getFrom() * 12 <= experienceInMonth && seniorityLevel.getTo() * 12 >= experienceInMonth) {
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
        if (!Optional.of(reasonCode).isPresent()) {
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

        userToSchedulerQueueService.pushToJobQueueOnEmploymentEnd(endDateMillis, employment.getEndDateMillis(), unit.getId(), employment.getId(),
                unit.getTimeZone());

        employment.setEndDateMillis(endDateMillis);
        employmentGraphRepository.deleteEmploymentReasonCodeRelation(staffId);

        employment.setReasonCode(reasonCode);
        employment.setAccessGroupIdOnEmploymentEnd(accessGroupId);
        unitPositionGraphRepository.saveAll(unitPositions);
        employmentGraphRepository.save(employment);
        User user = userGraphRepository.getUserByStaffId(staffId);
        EmploymentQueryResult employmentUpdated = new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), employment.getReasonCode().getId(), employment.getAccessGroupIdOnEmploymentEnd());
        EmploymentUnitPositionDTO employmentUnitPositionDTO = new EmploymentUnitPositionDTO(employmentUpdated, unitPositionGraphRepository.getAllUnitPositionsByUser(user.getId()));
        return employmentUnitPositionDTO;

    }

    public Long getUnitPositionIdByStaffAndExpertise(Long unitId, Long staffId, Long dateInMillis, Long expertiseId) {
        return unitPositionGraphRepository.getUnitPositionIdByStaffAndExpertise(unitId, staffId, expertiseId, dateInMillis);
    }

    public Map<Long, Long> getUnitPositionExpertiseMap(Long unitId) {
        List<Map<Long, Long>> listOfMap = unitPositionGraphRepository.getMapOfUnitPositionAndExpertiseId(unitId);
        Map<Long, Long> mapOfUnitPositionAndExpertise = new HashMap<>(listOfMap.size());
        listOfMap.forEach(mapOfUnitPositionAndExpertise::putAll);
        return mapOfUnitPositionAndExpertise;
    }
// TODO FIX
    public Boolean applyFunction(Long unitPositionId, Map<String, Object> payload) throws ParseException {

        String dateAsString = new ArrayList<>(payload.keySet()).get(0);

        Map<String, Object> functionMap = (Map<String, Object>) payload.get(dateAsString);
        Long functionId = new Long((Integer) functionMap.get("id"));

        Date date = DateUtils.convertToOnlyDate(dateAsString, ONLY_DATE);
        Boolean unitPositionFunctionRelationship = unitPositionFunctionRelationshipRepository.getUnitPositionFunctionRelationshipByUnitPositionAndFunction(unitPositionId, functionId, date.getTime());

        if (unitPositionFunctionRelationship == null) {
            unitPositionFunctionRelationshipRepository.createUnitPositionFunctionRelationship(unitPositionId, functionId, Collections.singletonList(date.getTime()));
        } else if (unitPositionFunctionRelationship) {
            exceptionService.actionNotPermittedException("message.unitposition.function.alreadyApplied", dateAsString);
        }
        return true;
    }

    public Boolean removeFunction(Long unitPositionId, Date appliedDate) {
        unitPositionFunctionRelationshipRepository.removeDateFromUnitPositionFunctionRelationship(unitPositionId, appliedDate.getTime());
        return true;
    }

    public List<StaffUnitPositionDetails> getStaffsUnitPosition(Long unitId, Long expertiseId, List<Long> staffId) {
        List<StaffUnitPositionDetails> staffData =
                staffGraphRepository.getStaffInfoByUnitIdAndStaffId(unitId, expertiseId, staffId);
        return staffData;
    }

    public List<StaffUnitPositionDetails> getStaffIdAndUnitPositionId(Long unitId, Long expertiseId, List<Long> staffId) {
        List<StaffUnitPositionDetails> staffData =
                staffGraphRepository.getStaffIdAndUnitPositionId(unitId, expertiseId, staffId, System.currentTimeMillis());
        return staffData;
    }

    public WTATableSettingWrapper getAllWTAOfStaff(Long unitId, Long staffId) {
        User user = userGraphRepository.getUserByStaffId(staffId);
        List<UnitPositionQueryResult> unitPositionQueryResults = unitPositionGraphRepository.getAllUnitPositionsBasicDetailsAndWTAByUser(user.getId());
        List<Long> unitpositionIds = unitPositionQueryResults.stream().map(UnitPositionQueryResult::getId).collect(Collectors.toList());

        List<NameValuePair> param = Collections.singletonList(new BasicNameValuePair("upIds", unitpositionIds.toString().replace("[", "").replace("]", "")));
        WTATableSettingWrapper wtaWithTableSettings = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_VERSION_WTA, param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTATableSettingWrapper>>() {
        });
        Map<Long, UnitPositionQueryResult> unitPositionQueryResultMap = unitPositionQueryResults.stream().filter(u -> u.getHistory().equals(false)).collect(Collectors.toMap(UnitPositionQueryResult::getId, v -> v));
        wtaWithTableSettings.getAgreements().forEach(currentWTA -> {
            UnitPositionQueryResult unitPositionQueryResult = unitPositionQueryResultMap.get(currentWTA.getUnitPositionId());
            if (unitPositionQueryResult != null) {
                currentWTA.setUnitInfo(unitPositionQueryResult.getUnitInfo());
                currentWTA.setUnitPositionId(unitPositionQueryResult.getId());
                currentWTA.setPositionCode(ObjectMapperUtils.copyPropertiesByMapper(unitPositionQueryResult.getPositionCode(), PositionCodeDTO.class));
            }
        });
        return wtaWithTableSettings;
    }

    public CTATableSettingWrapper getAllCTAOfStaff(Long unitId, Long staffId) {
        User user = userGraphRepository.getUserByStaffId(staffId);
        List<UnitPositionQueryResult> unitPositionQueryResults = unitPositionGraphRepository.getAllUnitPositionsBasicDetailsAndWTAByUser(user.getId());
        List<Long> upIds = unitPositionQueryResults.stream().map(UnitPositionQueryResult::getId).collect(Collectors.toList());
        List<NameValuePair> requestParam = Collections.singletonList(new BasicNameValuePair("upIds", upIds.toString().replace("[", "").replace("]", "")));
        CTATableSettingWrapper ctaTableSettingWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_VERSION_CTA, requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTATableSettingWrapper>>() {
        });
        Map<Long, UnitPositionQueryResult> unitPositionQueryResultMap = unitPositionQueryResults.stream().collect(Collectors.toMap(UnitPositionQueryResult::getId, v -> v));
        ctaTableSettingWrapper.getAgreements().forEach(currentCTA -> {
            if (unitPositionQueryResultMap.containsKey(currentCTA.getUnitPositionId())) {
                UnitPositionQueryResult currentActiveUnitPosition = unitPositionQueryResultMap.get(currentCTA.getUnitPositionId());
                currentCTA.setUnitInfo(currentActiveUnitPosition.getUnitInfo());
                currentCTA.setUnitPositionId(currentActiveUnitPosition.getId());
                currentCTA.setPositionCode(ObjectMapperUtils.copyPropertiesByMapper(currentActiveUnitPosition.getPositionCode(), PositionCodeDTO.class));
            }
        });
        return ctaTableSettingWrapper;
    }


    public void updateSeniorityLevelOnJobTrigger(BigInteger schedulerPanelId) {

        LocalDateTime started = LocalDateTime.now();
        KairosSchedulerLogsDTO schedulerLogsDTO;
        LocalDateTime stopped;
        String log = null;
        Result result = Result.SUCCESS;

        try {

            List<UnitPositionSeniorityLevelQueryResult> unitPositionSeniorityLevelQueryResults = unitPositionGraphRepository.findUnitPositionSeniorityLeveltoUpdate();
            if (!unitPositionSeniorityLevelQueryResults.isEmpty()) {
                List<Long> unitPositionIds = unitPositionSeniorityLevelQueryResults.stream().map(unitPositionQueryResult -> unitPositionQueryResult.getUnitPosition().getId()).
                        collect(Collectors.toList());

                List<UnitPositionCompleteQueryResult> unitPositionsComplete = unitPositionGraphRepository.findUnitPositionCompleteObject(unitPositionIds);
                Map<Long, UnitPosition> unitPositionMap = new HashMap<>();
                for (UnitPositionCompleteQueryResult unitPositionCompleteQueryResult : unitPositionsComplete) {
                    UnitPosition unitPositionComplete = unitPositionCompleteQueryResult.getUnitPosition();
                    unitPositionComplete.setExpertise(unitPositionCompleteQueryResult.getExpertise());
                    //unitPositionComplete.setFunctions(unitPositionCompleteQueryResult.getFunctions());
                    unitPositionComplete.setReasonCode(unitPositionCompleteQueryResult.getReasonCode());
                    unitPositionComplete.setStaff(unitPositionCompleteQueryResult.getStaff());
                    unitPositionComplete.setPositionCode(unitPositionCompleteQueryResult.getPositionCode());
                    unitPositionComplete.setUnit(unitPositionCompleteQueryResult.getUnit());
                    unitPositionComplete.setUnion(unitPositionCompleteQueryResult.getUnionOrg());
                    unitPositionMap.put(unitPositionComplete.getId(), unitPositionComplete);
                }

                List<UnitPositionEmploymentTypeRelationShip> unitPositionEmploymentTypeRelationShips = new ArrayList<>();
                List<UnitPosition> unitPositions = new ArrayList<>();
                for (UnitPositionSeniorityLevelQueryResult unitPositionSeniorityLevelQueryResult : unitPositionSeniorityLevelQueryResults) {
                    UnitPosition unitPosition = new UnitPosition();
                    UnitPositionEmploymentTypeRelationShip unitPositionEmploymentTypeRelationShip;
                    UnitPosition oldUnitPosition = unitPositionMap.get(unitPositionSeniorityLevelQueryResult.getUnitPosition().getId());
                    ObjectMapperUtils.copyProperties(oldUnitPosition, unitPosition);
                    oldUnitPosition.setEndDateMillis(DateUtils.getOneDayBeforeMillis());
                    oldUnitPosition.setHistory(true);
                    oldUnitPosition.setEditable(false);
                    unitPosition.setStartDateMillis(DateUtils.getCurrentDayStartMillis());
                    //unitPosition.setSeniorityLevel(unitPositionSeniorityLevelQueryResult.getSeniorityLevel());
                    //unitPosition.setParentUnitPosition(oldUnitPosition);
                    unitPositionEmploymentTypeRelationShip = new UnitPositionEmploymentTypeRelationShip(unitPosition.getPositionLines().get(0), unitPositionSeniorityLevelQueryResult.getEmploymentType(),
                            unitPositionSeniorityLevelQueryResult.getUnitPositionEmploymentTypeRelationShip().getEmploymentTypeCategory());
                    unitPositionEmploymentTypeRelationShips.add(unitPositionEmploymentTypeRelationShip);
                    unitPosition.setId(null);
                    unitPositions.add(unitPosition);
                }
                List<UnitPositionIdDTO> unitPositionNewOldIds = new ArrayList<>();
                unitPositionGraphRepository.saveAll(unitPositions);
                int i = 0;
                for (UnitPositionSeniorityLevelQueryResult unitPositionSeniorityLevelQueryResult : unitPositionSeniorityLevelQueryResults) {
                    unitPositionNewOldIds.add(new UnitPositionIdDTO(unitPositionSeniorityLevelQueryResult.getUnitPosition().getId(), unitPositions.get(i++).getId()));
                }
                unitPositionEmploymentTypeRelationShipGraphRepository.saveAll(unitPositionEmploymentTypeRelationShips);
                activityIntegrationService.copyWTACTA(unitPositionNewOldIds);

            }

        } catch (Exception ex) {
            log = ex.getMessage();
            result = Result.ERROR;
        }

        stopped = LocalDateTime.now();
        schedulerLogsDTO = new KairosSchedulerLogsDTO(result, log, schedulerPanelId, null, DateUtils.getMillisFromLocalDateTime(started), DateUtils.getMillisFromLocalDateTime(stopped), JobSubType.SENIORITY_LEVEL);

        kafkaProducer.pushToSchedulerLogsQueue(schedulerLogsDTO);

        // List<CTAWTAResponseDTO> ctaWTAs =  activityIntegrationService.copyWTACTA(unitPositionNewOldIds);

    }
}
