package com.kairos.service.unit_position;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta.CTATableSettingWrapper;
import com.kairos.dto.activity.cta.CTAWTAWrapper;
import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.activity.wta.version.WTATableSettingWrapper;
import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import com.kairos.dto.user.employment.UnitPositionIdDTO;
import com.kairos.dto.user.organization.position_code.PositionCodeDTO;
import com.kairos.dto.user.staff.unit_position.PositionLineChangeResultDTO;
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
import com.kairos.persistence.model.user.unit_position.*;
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
import com.kairos.scheduler.queue.producer.KafkaProducer;
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
import org.apache.poi.ss.formula.functions.T;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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

        PositionCode positionCode;
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
            if (unitPositionDTO.getStartDate().isBefore(DateUtils.getDateFromEpoch(employment.getStartDateMillis()))) {
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
                new UnitPosition(positionCode, organization, unitPositionDTO.getStartDate(), unitPositionDTO.getTimeCareExternalId(), !saveAsDraft);

        preparePosition(unitPosition, unitPositionDTO, createFromTimeCare);

        unitPositionGraphRepository.save(unitPosition);
        CTAWTAWrapper ctawtaWrapper = assignCTAAndWTAToUnitPosition(unitPosition, unitPositionDTO);
        Long reasonCodeId = null;
        if (unitPositionDTO.getEndDate() != null) {
            reasonCodeId = updateEmploymentEndDate(parentOrganization, unitPositionDTO, employment);
        }
        UnitPositionEmploymentTypeRelationShip relationShip = new UnitPositionEmploymentTypeRelationShip(unitPosition.getUnitPositionLines().get(0), employmentType, unitPositionDTO.getEmploymentTypeCategory());
        unitPositionEmploymentTypeRelationShipGraphRepository.save(relationShip);

        UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPositionDTO, unitPosition, relationShip, parentOrganization.getId(), parentOrganization.getName(), ctawtaWrapper.getWta().get(0), unitPosition.getUnitPositionLines().get(0));
        unitPositionQueryResult.getPositionLines().get(0).setCostTimeAgreement(ctawtaWrapper.getCta().get(0));
        unitPositionQueryResult.getPositionLines().get(0).setWorkingTimeAgreement(ctawtaWrapper.getWta().get(0));
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
        Employment employment1 = employmentService.updateEmploymentEndDate(organization, unitPositionDTO.getStaffId(), unitPositionDTO.getEndDate() != null ? DateUtil.getDateFromEpoch(unitPositionDTO.getEndDate()) : null, unitPositionDTO.getReasonCodeId(), unitPositionDTO.getAccessGroupId());
        return Optional.ofNullable(employment.getReasonCode()).isPresent() ? employment1.getReasonCode().getId() : null;

    }

    public boolean validateUnitPositionWithExpertise(List<UnitPosition> unitPositions, UnitPositionDTO unitPositionDTO) {

        LocalDate unitPositionStartDate = unitPositionDTO.getStartDate();
        LocalDate unitPositionEndDate = unitPositionDTO.getEndDate();
        unitPositions.forEach(unitPosition -> {
            // if null date is set
            if (unitPosition.getEndDate() != null) {
                if (unitPositionStartDate.isBefore(unitPosition.getEndDate()) && unitPositionStartDate.isAfter(unitPosition.getStartDate())) {
                    exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", unitPositionEndDate, unitPosition.getStartDate());
                }
                if (unitPositionEndDate != null) {
                    Interval previousInterval = new Interval(DateUtil.getDateFromEpoch(unitPosition.getStartDate()), DateUtil.getDateFromEpoch(unitPosition.getEndDate()));
                    Interval interval = new Interval(DateUtil.getDateFromEpoch(unitPositionStartDate), DateUtil.getDateFromEpoch(unitPositionEndDate));
                    logger.info(" Interval of CURRENT UEP " + previousInterval + " Interval of going to create  " + interval);
                    if (previousInterval.overlaps(interval))
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist");
                } else {
                    if (unitPositionStartDate.isBefore(unitPosition.getEndDate())) {
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", unitPositionEndDate, unitPosition.getEndDate());
                    }
                }
            } else {
                // unitEmploymentEnd date is null
                if (unitPositionEndDate != null) {
                    if (unitPositionEndDate.isAfter(unitPosition.getStartDate())) {
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", unitPositionEndDate, unitPosition.getStartDate());
                    }
                } else {
                    exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist");
                }
            }
        });
        return true;
    }

    private UnitPositionLine createPositionLine(UnitPosition oldUnitPosition, UnitPositionLine oldUnitPositionLine, UnitPositionDTO unitPositionDTO) {
        if (unitPositionDTO.getStartDate().isBefore(LocalDate.now())) {
            exceptionService.actionNotPermittedException("message.startdate.notlessthan.currentdate");
        }
        if (Optional.ofNullable(unitPositionDTO.getEndDate()).isPresent() && unitPositionDTO.getStartDate().isAfter(unitPositionDTO.getEndDate())) {
            exceptionService.actionNotPermittedException("message.startdate.notlessthan.enddate");
        }
        if (Optional.ofNullable(unitPositionDTO.getLastWorkingDate()).isPresent() && unitPositionDTO.getStartDate().isAfter(unitPositionDTO.getLastWorkingDate())) {
            exceptionService.actionNotPermittedException("message.lastdate.notlessthan.enddate");
        }
        oldUnitPosition.setLastWorkingDate(unitPositionDTO.getLastWorkingDate());
        List<Function> functions = functionGraphRepository.findAllFunctionsById(unitPositionDTO.getFunctionIds());
        if (functions.size() != unitPositionDTO.getFunctionIds().size()) {
            exceptionService.actionNotPermittedException("message.unitposition.functions.unable");
        }
        UnitPositionLine unitPositionLine = new UnitPositionLine.UnitPositionLineBuilder()
                .setAvgDailyWorkingHours(unitPositionDTO.getAvgDailyWorkingHours())
                .setTotalWeeklyMinutes((unitPositionDTO.getTotalWeeklyHours() * 60) + unitPositionDTO.getTotalWeeklyMinutes())
                .setHourlyWages(unitPositionDTO.getHourlyWages())
                .setStartDate(unitPositionDTO.getStartDate())
                .setFunctions(functions)
                .setFullTimeWeeklyMinutes(oldUnitPositionLine.getFullTimeWeeklyMinutes())
                .setWorkingDaysInWeek(oldUnitPositionLine.getWorkingDaysInWeek())
                .setEndDate(unitPositionDTO.getEndDate())
                .setSeniorityLevel(oldUnitPositionLine.getSeniorityLevel())
                .build();

        oldUnitPositionLine.setEndDate(unitPositionDTO.getStartDate().minusDays(1));
        if (Optional.ofNullable(unitPositionDTO.getEndDate()).isPresent()) {

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

        return unitPositionLine;
    }


    private PositionLineChangeResultDTO calculativeValueChanged(UnitPosition oldUnitPosition, UnitPositionDTO unitPositionDTO, UnitPositionEmploymentTypeRelationShip oldUnitPositionEmploymentTypeRelationShip, UnitPositionLine unitPositionLine,
                                                                CTAWTAWrapper ctawtaWrapper, List<NameValuePair> changedParams) {
        PositionLineChangeResultDTO changeResultDTO = new PositionLineChangeResultDTO(false);

        if (!unitPositionDTO.getCtaId().equals(ctawtaWrapper.getCta().get(0).getId())) {
            // CTA is changed
            changeResultDTO.setCtaId(unitPositionDTO.getCtaId());
            changeResultDTO.setOldctaId(ctawtaWrapper.getCta().get(0).getId());
            changedParams.add(new BasicNameValuePair("ctaId", unitPositionDTO.getCtaId() + ""));
            changedParams.add(new BasicNameValuePair("oldctaId", ctawtaWrapper.getCta().get(0).getId() + ""));
            changeResultDTO.setCalculativeChanged(true);
        }
        if (!unitPositionDTO.getWtaId().equals(ctawtaWrapper.getWta().get(0).getId())) {
            // wta is changed
            changeResultDTO.setWtaId(unitPositionDTO.getWtaId());
            changeResultDTO.setOldwtaId(ctawtaWrapper.getWta().get(0).getId());
            changeResultDTO.setCalculativeChanged(true);
            changedParams.add(new BasicNameValuePair("wtaId", unitPositionDTO.getWtaId() + ""));
            changedParams.add(new BasicNameValuePair("oldwtaId", ctawtaWrapper.getWta().get(0).getId() + ""));
        }
        if (unitPositionLine.getAvgDailyWorkingHours() != unitPositionDTO.getAvgDailyWorkingHours()
                || unitPositionLine.getTotalWeeklyMinutes() != (unitPositionDTO.getTotalWeeklyMinutes() + (unitPositionDTO.getTotalWeeklyHours() * 60))
                || (oldUnitPosition.getReasonCode() != null && !oldUnitPosition.getReasonCode().getId().equals(unitPositionDTO.getReasonCodeId()))
                || (unitPositionLine.getFunctions() != null && !unitPositionLine.getFunctions().stream().map(Function::getId).collect(Collectors.toSet()).equals(unitPositionDTO.getFunctionIds()))) {
            changeResultDTO.setCalculativeChanged(true);
        } else if (!oldUnitPositionEmploymentTypeRelationShip.getEmploymentType().getId().equals(unitPositionDTO.getEmploymentTypeId()) || !oldUnitPositionEmploymentTypeRelationShip.getEmploymentTypeCategory().equals(unitPositionDTO.getEmploymentTypeCategory())) {
            changeResultDTO.setCalculativeChanged(true);
        }
        return changeResultDTO;
    }

    private void linkPositionLineWithEmploymentType(UnitPositionLine unitPositionLine, UnitPositionDTO unitPositionDTO) {
        EmploymentType employmentType = employmentTypeGraphRepository.findOne(unitPositionDTO.getEmploymentTypeId());
        if (!Optional.ofNullable(employmentType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.position.employmenttype.notexist", unitPositionDTO.getEmploymentTypeId());
        }

        UnitPositionEmploymentTypeRelationShip relationShip = new UnitPositionEmploymentTypeRelationShip(unitPositionLine, employmentType, unitPositionDTO.getEmploymentTypeCategory());
        unitPositionEmploymentTypeRelationShipGraphRepository.save(relationShip);
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
        UnitPositionLine currentUnitPositionLine = oldUnitPosition.getUnitPositionLines().stream().filter(positionLine -> positionLine.getId().equals(unitPositionDTO.getPositionLineId()))
                .findFirst().orElse(null);
        if (currentUnitPositionLine == null) {
            exceptionService.dataNotFoundByIdException("message.positionid.notfound", unitPositionId);
        }
        List<NameValuePair> param = Collections.singletonList(new BasicNameValuePair("unitPositionId", unitPositionId + ""));
        CTAWTAWrapper existingCtaWtaWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, APPLICABLE_CTA_WTA, param,
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAWrapper>>() {
                });
        if (existingCtaWtaWrapper.getCta().isEmpty() || existingCtaWtaWrapper.getWta().isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.unitPosition.ctawtamissing", existingCtaWtaWrapper.getCta().isEmpty(), existingCtaWtaWrapper.getWta().isEmpty(), unitPositionId);
        }

        UnitPositionEmploymentTypeRelationShip positionLineEmploymentTypeRelationShip = unitPositionGraphRepository.findEmploymentTypeByUnitPositionId(currentUnitPositionLine.getId());
        EmploymentQueryResult employmentQueryResult;
        UnitPositionQueryResult unitPositionQueryResult = new UnitPositionQueryResult();
        List<NameValuePair> changedParams = new ArrayList<>();
        oldUnitPosition.setPublished(!saveAsDraft);
        PositionLineChangeResultDTO changeResultDTO = calculativeValueChanged(oldUnitPosition, unitPositionDTO, positionLineEmploymentTypeRelationShip, currentUnitPositionLine, existingCtaWtaWrapper, changedParams);
        /**
         *  Old unit position's calculative values is changed
         *  Old unit position is published so need to create a new  position line
         **/
        if (changeResultDTO.isCalculativeChanged()) {
            List<UnitPosition> oldUnitPositions
                    = unitPositionGraphRepository.getAllUEPByExpertiseExcludingCurrent(unitPositionDTO.getUnitId(), unitPositionDTO.getStaffId(), unitPositionDTO.getExpertiseId(), unitPositionId);
            validateUnitPositionWithExpertise(oldUnitPositions, unitPositionDTO);
            UnitPositionLine unitPositionLine = createPositionLine(oldUnitPosition, currentUnitPositionLine, unitPositionDTO);
            CTAWTAWrapper newCTAWTAWrapper = null;
            if (changeResultDTO.getCtaId() != null || changeResultDTO.getWtaId() != null) {
                changedParams.add(new BasicNameValuePair("startDate", unitPositionDTO.getStartDate() + ""));
                newCTAWTAWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.CREATE, APPLY_CTA_WTA, changedParams,
                        new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAWrapper>>() {
                        }, unitPositionId);

            }
            oldUnitPosition.getUnitPositionLines().add(unitPositionLine);
            unitPositionGraphRepository.save(oldUnitPosition);
            linkPositionLineWithEmploymentType(unitPositionLine, unitPositionDTO);
            unitPositionQueryResult = getBasicDetails(unitPositionDTO, oldUnitPosition, positionLineEmploymentTypeRelationShip, organization.getId(), organization.getName(), null, unitPositionLine);

            if (changeResultDTO.getWtaId() != null) {
                unitPositionQueryResult.getPositionLines().get(0).setWorkingTimeAgreement(newCTAWTAWrapper.getWta().get(0));
            } else {
                unitPositionQueryResult.getPositionLines().get(0).setWorkingTimeAgreement(existingCtaWtaWrapper.getWta().get(0));
            }
            if (changeResultDTO.getCtaId() != null) {
                unitPositionQueryResult.getPositionLines().get(0).setCostTimeAgreement(newCTAWTAWrapper.getCta().get(0));
            } else {
                unitPositionQueryResult.getPositionLines().get(0).setCostTimeAgreement(existingCtaWtaWrapper.getCta().get(0));
            }
        }
        // calculative value is not changed it means only end date is updated.
        else {
            oldUnitPosition.setEndDate(unitPositionDTO.getEndDate());
            oldUnitPosition.setLastWorkingDate(unitPositionDTO.getLastWorkingDate());
            unitPositionGraphRepository.save(oldUnitPosition);
            unitPositionQueryResult = getBasicDetails(unitPositionDTO, oldUnitPosition, positionLineEmploymentTypeRelationShip, organization.getId(), organization.getName(), null, currentUnitPositionLine);
            unitPositionQueryResult.getPositionLines().get(0).setWorkingTimeAgreement(existingCtaWtaWrapper.getWta().get(0));
            unitPositionQueryResult.getPositionLines().get(0).setCostTimeAgreement(existingCtaWtaWrapper.getCta().get(0));
        }


        Employment employment = employmentService.updateEmploymentEndDate(oldUnitPosition.getUnit(), unitPositionDTO.getStaffId(),
                unitPositionDTO.getEndDate() != null ? DateUtil.getDateFromEpoch(unitPositionDTO.getEndDate()) : null, unitPositionDTO.getReasonCodeId(), unitPositionDTO.getAccessGroupId());
        Long reasonCodeId = Optional.ofNullable(employment.getReasonCode()).isPresent() ? employment.getReasonCode().getId() : null;
        employmentQueryResult = new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), reasonCodeId, employment.getAccessGroupIdOnEmploymentEnd());
        // Deleting All shifts after employment end date
        if (unitPositionDTO.getEndDate() != null) {
            activityIntegrationService.deleteShiftsAfterEmploymentEndDate(unitId, unitPositionDTO.getEndDate(), unitPositionDTO.getStaffId());
        }
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
        return unitPositionGraphRepository.findByUnitPositionId(unitPositionId);
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
        if (!createFromTimeCare && unitPositionDTO.getStartDate().isBefore(LocalDate.now())) {
            exceptionService.actionNotPermittedException("message.startdate.notlessthan.currentdate");
        }
        unitPosition.setStartDate(unitPositionDTO.getStartDate());
        if (Optional.ofNullable(unitPositionDTO.getEndDate()).isPresent()) {
            if (unitPositionDTO.getStartDate().isAfter(unitPositionDTO.getEndDate())) {
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
            unitPosition.setEndDate(unitPositionDTO.getEndDate());
        }

        if (Optional.ofNullable(unitPositionDTO.getLastWorkingDate()).isPresent()) {
            if (unitPositionDTO.getStartDate().isAfter(unitPositionDTO.getLastWorkingDate())) {
                exceptionService.actionNotPermittedException("message.lastdate.notlessthan.startdate");
            }
            unitPosition.setLastWorkingDate(unitPositionDTO.getLastWorkingDate());
        }


        SeniorityLevel seniorityLevel = getSeniorityLevelByStaffAndExpertise(unitPosition.getStaff().getId(), unitPosition.getExpertise());

        if (!Optional.ofNullable(seniorityLevel).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.seniorityLevel.id.notfound", unitPositionDTO.getReasonCodeId());
        }


        List<Function> functions = functionGraphRepository.findAllFunctionsById(unitPositionDTO.getFunctionIds());
        if (functions.size() != unitPositionDTO.getFunctionIds().size()) {
            exceptionService.actionNotPermittedException("message.unitposition.functions.unable");
        }
        UnitPositionLine unitPositionLine = new UnitPositionLine.UnitPositionLineBuilder()
                .setSeniorityLevel(seniorityLevel)
                .setFunctions(functions)
                .setStartDate(unitPositionDTO.getStartDate())
                .setEndDate(unitPositionDTO.getEndDate())
                .setTotalWeeklyMinutes(unitPositionDTO.getTotalWeeklyMinutes() + (unitPositionDTO.getTotalWeeklyHours() * 60))
                .setFullTimeWeeklyMinutes(unitPosition.getExpertise().getFullTimeWeeklyMinutes())
                .setWorkingDaysInWeek(unitPosition.getExpertise().getNumberOfWorkingDaysInWeek())
                .setAvgDailyWorkingHours(unitPositionDTO.getAvgDailyWorkingHours())
                .setHourlyWages(unitPositionDTO.getHourlyWages())
                .build();
        unitPosition.setUnitPositionLines(Collections.singletonList(unitPositionLine));
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

        List<UnitPositionLinesQueryResult> positionLines = unitPositionGraphRepository.findAllPositionLines(unitPositionIds);
        Map<Long, List<UnitPositionLinesQueryResult>> positionLinesMap = positionLines.stream().collect(Collectors.groupingBy(UnitPositionLinesQueryResult::getUnitPositionId));
        unitPositionQueryResults.forEach(u -> {
            u.setPositionLines(positionLinesMap.get(u.getId()));
            u.getPositionLines().forEach(positionLine -> {

                ctawtaWrapper.getCta().forEach(cta -> {
                    if ((cta.getStartDate().isBefore(positionLine.getStartDate()) || positionLine.getStartDate().isEqual(cta.getStartDate()))
                            && (cta.getEndDate() == null || positionLine.getEndDate() == null || cta.getEndDate().isAfter(positionLine.getEndDate()) || cta.getEndDate().isEqual(positionLine.getEndDate()))) {
                        positionLine.setCostTimeAgreement(cta);
                    }
                });
                ctawtaWrapper.getWta().forEach(wta -> {
                    if ((DateUtils.asLocalDate(wta.getStartDate()).isBefore(positionLine.getStartDate()) || positionLine.getStartDate().isEqual(DateUtils.asLocalDate(wta.getStartDate()))) &&
                            ((positionLine.getEndDate() == null || wta.getEndDate() == null) || (DateUtils.asLocalDate(wta.getEndDate()).isAfter(positionLine.getEndDate())
                                    || DateUtils.asLocalDate(wta.getEndDate()).isEqual(positionLine.getEndDate())))) {
                        positionLine.setWorkingTimeAgreement(wta);
                    }
                });
                if (u.getEndDate() != null) {
                    u.setEndDate(positionLine.getEndDate());
                }
                });
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
        UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPosition, wtaResponseDTO, unitPosition.getUnitPositionLines().get(0));
        return unitPositionQueryResult;
    }

    private UnitPositionQueryResult getBasicDetails(UnitPositionDTO unitPositionDTO, UnitPosition unitPosition, UnitPositionEmploymentTypeRelationShip relationShip,
                                                    Long parentOrganizationId, String parentOrganizationName, WTAResponseDTO wtaResponseDTO, UnitPositionLine unitPositionLine) {

        UnitPositionQueryResult result = new UnitPositionQueryResult(unitPosition.getExpertise().retrieveBasicDetails(), unitPosition.getStartDate(),
                unitPosition.getEndDate(), unitPosition.getId(), unitPosition.getPositionCode(), unitPosition.getUnion(), unitPosition.getLastWorkingDate()
                , wtaResponseDTO);

        result.setUnitId(unitPosition.getUnit().getId());

        result.setParentUnitId(parentOrganizationId);
        result.setEditable(unitPosition.isEditable());
        result.setHistory(unitPosition.isHistory());
        result.setPublished(unitPosition.isPublished());
        Map<String, Object> reasonCode = null;
        if (Optional.ofNullable(unitPosition.getReasonCode()).isPresent()) {
            reasonCode = new HashMap();
            reasonCode.put("name", unitPosition.getReasonCode().getName());
            reasonCode.put("id", unitPosition.getReasonCode().getId());
        }
        result.setReasonCode(reasonCode);

        Map<String, Object> employmentTypes = new HashMap();
        employmentTypes.put("name", relationShip.getEmploymentType().getName());
        employmentTypes.put("id", relationShip.getEmploymentType().getId());
        employmentTypes.put("employmentTypeCategory", relationShip.getEmploymentTypeCategory());

        Map<String, Object> seniorityLevel = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

        seniorityLevel = objectMapper.convertValue(unitPositionLine.getSeniorityLevel(), Map.class);
        seniorityLevel.put("functions", unitPositionDTO.getFunctionIds());
        seniorityLevel.put("payGrade", Optional.ofNullable(unitPositionLine.getSeniorityLevel().getPayGrade()).isPresent() ? unitPositionLine.getSeniorityLevel().getPayGrade() : payGradeGraphRepository.getPayGradeBySeniorityLevelId(unitPositionLine.getSeniorityLevel().getId()));

        UnitPositionLinesQueryResult unitPositionLinesQueryResult = new UnitPositionLinesQueryResult(unitPositionLine.getId(), unitPositionLine.getStartDate(), unitPositionLine.getEndDate()
                , unitPositionLine.getWorkingDaysInWeek(), unitPositionLine.getTotalWeeklyMinutes() / 60, unitPositionLine.getAvgDailyWorkingHours(), unitPositionLine.getFullTimeWeeklyMinutes(), 0D,
                unitPositionLine.getTotalWeeklyMinutes() % 60, unitPositionLine.getHourlyWages(), employmentTypes, seniorityLevel);

        unitPositionLinesQueryResult.setUnitPositionId(unitPosition.getId());
        // TODO Setting for compatibility
        Map<String, Object> unitInfo = new HashMap<>();
        unitInfo.put("id", parentOrganizationId);
        unitInfo.put("name", parentOrganizationName);
        result.setUnitInfo(unitInfo);

        result.setPositionLines(Collections.singletonList(unitPositionLinesQueryResult));
        return result;
    }

    private UnitPositionQueryResult getBasicDetails(UnitPosition unitPosition, WTAResponseDTO wtaResponseDTO, UnitPositionLine unitPositionLine) {
        UnitPositionQueryResult unitPositionQueryResult = unitPositionGraphRepository.getUnitIdAndParentUnitIdByUnitPositionId(unitPosition.getId());
        UnitPositionQueryResult result = new UnitPositionQueryResult(unitPosition.getExpertise().retrieveBasicDetails(), unitPosition.getStartDate(),
                unitPosition.getEndDate(),
                unitPosition.getId(), unitPosition.getPositionCode(), unitPosition.getUnion(),
                unitPosition.getLastWorkingDate(),  wtaResponseDTO);
        result.setUnitId(unitPositionQueryResult.getUnitId());

        result.setEditable(unitPosition.isEditable());
        result.setHistory(unitPosition.isHistory());
        result.setPublished(unitPosition.isPublished());
        result.setParentUnitId(unitPositionQueryResult.getParentUnitId());
        return result;
    }


    public void convertUnitPositionObject(UnitPositionQueryResult unitPosition, com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails) {
        unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        UnitPositionLinesQueryResult currentPositionLine = ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getPositionLines().get(0), UnitPositionLinesQueryResult.class);
        unitPositionDetails.setEmploymentType(ObjectMapperUtils.copyPropertiesByMapper(currentPositionLine.getEmploymentType(), com.kairos.dto.activity.shift.EmploymentType.class));
        unitPositionDetails.setId(unitPosition.getId());

        unitPositionDetails.setStartDate(unitPosition.getStartDate());
        unitPositionDetails.setAppliedFunctions(unitPosition.getAppliedFunctions());
        unitPositionDetails.setEndDate(unitPosition.getEndDate());
        unitPositionDetails.setFullTimeWeeklyMinutes(currentPositionLine.getFullTimeWeeklyMinutes());
        unitPositionDetails.setTotalWeeklyMinutes(currentPositionLine.getTotalWeeklyMinutes());
        unitPositionDetails.setWorkingDaysInWeek(currentPositionLine.getWorkingDaysInWeek());
        unitPositionDetails.setAvgDailyWorkingHours(currentPositionLine.getAvgDailyWorkingHours());
        unitPositionDetails.setHourlyWages(currentPositionLine.getHourlyWages());
    }


    public com.kairos.dto.activity.shift.StaffUnitPositionDetails getUnitPositionDetails(Long unitPositionId, Organization organization, Long countryId) {

        UnitPositionQueryResult unitPosition = unitPositionGraphRepository.getUnitPositionById(unitPositionId);
        com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails = new com.kairos.dto.activity.shift.StaffUnitPositionDetails();
        convertUnitPositionObject(unitPosition, unitPositionDetails);
        unitPositionDetails.setCountryId(countryId);
        ExpertisePlannedTimeQueryResult expertisePlannedTimeQueryResult = expertiseEmploymentTypeRelationshipGraphRepository.findPlannedTimeByExpertise(unitPositionDetails.getExpertise().getId(),
                unitPositionDetails.getEmploymentType().getId());
        if (Optional.ofNullable(expertisePlannedTimeQueryResult).isPresent()) {
            unitPositionDetails.setExcludedPlannedTime(expertisePlannedTimeQueryResult.getExcludedPlannedTime());
            unitPositionDetails.setIncludedPlannedTime(expertisePlannedTimeQueryResult.getIncludedPlannedTime());
        }
        unitPositionDetails.setUnitTimeZone(organization.getTimeZone());
        return unitPositionDetails;
    }


    private UnitPositionDTO convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(TimeCareEmploymentDTO timeCareEmploymentDTO, Long expertiseId, Long staffId, Long employmentTypeId, Long positionCodeId, BigInteger wtaId, BigInteger ctaId, Long unitId) {
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

        Expertise expertise;
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

        Integer experienceInMonth = (int) ChronoUnit.MONTHS.between(DateUtils.asLocalDate(staffSelectedExpertise.getExpertiseStartDate()), LocalDate.now());
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
        LocalDate unitPositionStartDateMax = unitPositionGraphRepository.getMaxUnitPositionStartDate(staffId);
        if (Optional.ofNullable(unitPositionStartDateMax).isPresent() && DateUtils.getDateFromEpoch(endDateMillis).isBefore(unitPositionStartDateMax)) {
            exceptionService.actionNotPermittedException("message.employmentdata.greaterthan.unitpositiondate", unitPositionStartDateMax);

        }
        List<UnitPosition> unitPositions = unitPositionGraphRepository.getUnitPositionsFromEmploymentEndDate(staffId, DateUtils.getDateFromEpoch(endDateMillis));
        Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(reasonCodeId, 0);
        if (!reasonCode.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound", reasonCodeId);

        }

        for (UnitPosition unitPosition : unitPositions) {
            unitPosition.setEndDate(DateUtils.getLocalDate(endDateMillis));
            if (!Optional.ofNullable(unitPosition.getReasonCode()).isPresent()) {
                unitPosition.setReasonCode(reasonCode.get());
            }
        }

        Employment employment = employmentGraphRepository.findEmploymentByStaff(staffId);
        if (employment.getMainEmploymentEndDate() != null) {
            Long mainEmploymentEndDate = DateUtils.getLongFromLocalDate(employment.getMainEmploymentEndDate());
            if (endDateMillis > mainEmploymentEndDate) {
                exceptionService.invalidRequestException("message.employmentdata.lessthan.mainEmploymentEndDate");
            }
        }

        userToSchedulerQueueService.pushToJobQueueOnEmploymentEnd(endDateMillis, employment.getEndDateMillis(), unit.getId(), employment.getId(),
                unit.getTimeZone());

        employment.setEndDateMillis(endDateMillis);
        employmentGraphRepository.deleteEmploymentReasonCodeRelation(staffId);

        employment.setReasonCode(reasonCode.get());
        employment.setAccessGroupIdOnEmploymentEnd(accessGroupId);
        unitPositionGraphRepository.saveAll(unitPositions);
        employmentGraphRepository.save(employment);
        User user = userGraphRepository.getUserByStaffId(staffId);
        EmploymentQueryResult employmentUpdated = new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), employment.getReasonCode().getId(), employment.getAccessGroupIdOnEmploymentEnd());
        EmploymentUnitPositionDTO employmentUnitPositionDTO = new EmploymentUnitPositionDTO(employmentUpdated, unitPositionGraphRepository.getAllUnitPositionsByUser(user.getId()));
        return employmentUnitPositionDTO;

    }

    /**
     * @Desc This method is used to veify the unit position of staff while copy shift
     * @param unitId
     * @param staffId
     * @param dateInMillis
     * @param expertiseId
     * @return
     */
    public Long getUnitPositionIdByStaffAndExpertise(Long unitId, Long staffId, Long dateInMillis, Long expertiseId) {
        return unitPositionGraphRepository.getUnitPositionIdByStaffAndExpertise(unitId, staffId, expertiseId, DateUtils.getLocalDate(dateInMillis));
    }

    public Map<Long, Long> getUnitPositionExpertiseMap(Long unitId) {
        List<Map<Long, Long>> listOfMap = unitPositionGraphRepository.getMapOfUnitPositionAndExpertiseId(unitId);
        Map<Long, Long> mapOfUnitPositionAndExpertise = new HashMap<>(listOfMap.size());
        listOfMap.forEach(mapOfUnitPositionAndExpertise::putAll);
        return mapOfUnitPositionAndExpertise;
    }

    // TODO FIX
    public Boolean applyFunction(Long unitPositionId, Map<String, Object> payload) {

        String dateAsString = new ArrayList<>(payload.keySet()).get(0);

        Map<String, Object> functionMap = (Map<String, Object>) payload.get(dateAsString);
        Long functionId = new Long((Integer) functionMap.get("id"));

        Boolean unitPositionFunctionRelationship = unitPositionFunctionRelationshipRepository.getUnitPositionFunctionRelationshipByUnitPositionAndFunction(unitPositionId, functionId, dateAsString);

        if (unitPositionFunctionRelationship == null) {
            unitPositionFunctionRelationshipRepository.createUnitPositionFunctionRelationship(unitPositionId, functionId, Collections.singletonList(dateAsString));
        } else if (unitPositionFunctionRelationship) {
            exceptionService.actionNotPermittedException("message.unitposition.function.alreadyApplied", dateAsString);
        }
        return true;
    }

    public Long removeFunction(Long unitPositionId, Date appliedDate) {
        return unitPositionFunctionRelationshipRepository.removeDateFromUnitPositionFunctionRelationship(unitPositionId, DateUtils.asLocalDate(appliedDate).toString());
    }


    /**
     * @param unitPositionId
     * @param appliedDates
     * @return
     * @Desc this method will remove applied functions for multiple dates
     */
    public Map<LocalDate, Long> removeFunctions(Long unitPositionId, Set<LocalDate> appliedDates) {
        Map<LocalDate, Long> localDateAndFunctionIdMap = new HashMap<>();
        List<UnitPositionFunctionRelationship> unitPositionFunctionRelationships = new ArrayList<>();
        Set<String> localDatesAsString = ObjectMapperUtils.copyPropertiesOfSetByMapper(appliedDates, String.class);
        List<UnitPositionFunctionRelationshipQueryResult> unitPositionFunctionRelationshipQueryResults = unitPositionFunctionRelationshipRepository.findAllByAppliedDatesIn(unitPositionId, localDatesAsString);
        for (UnitPositionFunctionRelationshipQueryResult unitPositionFunctionRelationshipQueryResult : unitPositionFunctionRelationshipQueryResults) {
            Set<LocalDate> dateToRemove = getIntersectedDates(unitPositionFunctionRelationshipQueryResult.getAppliedDates(), appliedDates);
            unitPositionFunctionRelationshipQueryResult.getAppliedDates().removeAll(dateToRemove);
            unitPositionFunctionRelationships.add(new UnitPositionFunctionRelationship(unitPositionFunctionRelationshipQueryResult.getId(), unitPositionFunctionRelationshipQueryResult.getUnitPosition(), unitPositionFunctionRelationshipQueryResult.getFunction(), unitPositionFunctionRelationshipQueryResult.getAppliedDates()));
            for (LocalDate localDate : dateToRemove) {
                localDateAndFunctionIdMap.put(localDate, unitPositionFunctionRelationshipQueryResult.getFunction().getId());
            }

        }
        unitPositionFunctionRelationshipRepository.saveAll(unitPositionFunctionRelationships);
        return localDateAndFunctionIdMap;
    }

    public List<StaffUnitPositionDetails> getStaffsUnitPosition(Long unitId, Long expertiseId, List<Long> staffId) {
        List<StaffUnitPositionDetails> staffData = staffGraphRepository.getStaffInfoByUnitIdAndStaffId(unitId, expertiseId, staffId);
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


    public void updateSeniorityLevelOnJobTrigger(BigInteger schedulerPanelId, Long unitId) {

        LocalDateTime started = LocalDateTime.now();
        LocalDate todaysDate = DateUtils.getCurrentLocalDate();
        KairosSchedulerLogsDTO schedulerLogsDTO;
        LocalDateTime stopped;
        String log = null;
        Result result = Result.SUCCESS;
        try {
            List<UnitPositionSeniorityLevelQueryResult> unitPositionSeniorityLevelQueryResults = unitPositionGraphRepository.findUnitPositionSeniorityLeveltoUpdate();
            if (!unitPositionSeniorityLevelQueryResults.isEmpty()) {

                Map<Long, UnitPositionSeniorityLevelQueryResult> unitPositionSeniorityLevelQueryResultMap
                        = unitPositionSeniorityLevelQueryResults.stream().collect(Collectors.toMap(t -> t.getUnitPositionId(), java.util.function.Function.identity()));

                Set<Long> unitPositionIds = unitPositionSeniorityLevelQueryResultMap.keySet();
                Iterable<UnitPosition> unitPositions = unitPositionGraphRepository.findAllById(unitPositionIds, 2);

                Map<UnitPositionIdDTO, UnitPositionLine> newPositionLineWithParentId = new HashMap<>();

                for (UnitPosition currentUnitPosition : unitPositions) {
                    Optional<UnitPositionLine> positionLine = currentUnitPosition.getUnitPositionLines().stream()
                            .filter(pl -> (todaysDate.isAfter(pl.getStartDate()) || todaysDate.isEqual(pl.getStartDate()) && (pl.getEndDate() == null || pl.getEndDate().isBefore(todaysDate) || pl.getEndDate().isEqual(todaysDate))))
                            .findAny();
                    if (positionLine.isPresent()) {
                        UnitPositionLine newUnitPositionLine = new UnitPositionLine.UnitPositionLineBuilder()
                                .setAvgDailyWorkingHours(positionLine.get().getAvgDailyWorkingHours())
                                .setTotalWeeklyMinutes(positionLine.get().getTotalWeeklyMinutes())
                                .setHourlyWages(positionLine.get().getHourlyWages())
                                .setStartDate(todaysDate.plusDays(1))
                                .setFunctions(positionLine.get().getFunctions())
                                .setFullTimeWeeklyMinutes(positionLine.get().getFullTimeWeeklyMinutes())
                                .setWorkingDaysInWeek(positionLine.get().getWorkingDaysInWeek())
                                .setEndDate(positionLine.get().getEndDate())
                                .setSeniorityLevel(unitPositionSeniorityLevelQueryResultMap.get(currentUnitPosition.getId()).getSeniorityLevel())
                                .build();
                        positionLine.get().setEndDate(todaysDate);
                        currentUnitPosition.getUnitPositionLines().add(newUnitPositionLine);
                        newPositionLineWithParentId.put(new UnitPositionIdDTO(currentUnitPosition.getId(), null, positionLine.get().getId()), newUnitPositionLine);
                    }

                }
                List<UnitPositionEmploymentTypeRelationShip> unitPositionEmploymentTypeRelationShips = new ArrayList<>();

                for (Map.Entry<UnitPositionIdDTO, UnitPositionLine> currentMap : newPositionLineWithParentId.entrySet()) {
                    UnitPositionSeniorityLevelQueryResult currentObject = unitPositionSeniorityLevelQueryResultMap.get(currentMap.getKey());
                    if (currentObject != null) {
                        UnitPositionEmploymentTypeRelationShip unitPositionEmploymentTypeRelationShip =
                                new UnitPositionEmploymentTypeRelationShip(currentMap.getValue(), currentObject.getEmploymentType(),
                                        currentObject.getUnitPositionEmploymentTypeRelationShip().getEmploymentTypeCategory());
                        unitPositionEmploymentTypeRelationShips.add(unitPositionEmploymentTypeRelationShip);
                    }
                }

                unitPositionGraphRepository.saveAll(unitPositions);
                unitPositionEmploymentTypeRelationShipGraphRepository.saveAll(unitPositionEmploymentTypeRelationShips);

            }

        } catch (Exception ex) {

            log = ex.getMessage();
            result = Result.ERROR;
        }

        stopped = LocalDateTime.now();

        schedulerLogsDTO = new KairosSchedulerLogsDTO(result, log, schedulerPanelId, unitId, DateUtils.getMillisFromLocalDateTime(started), DateUtils.getMillisFromLocalDateTime(stopped), JobSubType.SENIORITY_LEVEL);

        kafkaProducer.pushToSchedulerLogsQueue(schedulerLogsDTO);

        // List<CTAWTAResponseDTO> ctaWTAs =  activityIntegrationService.copyWTACTA(unitPositionNewOldIds);

    }

    public Boolean restoreFunctions(Long unitPositionId, Map<Long, Set<LocalDate>> payload) {
        List<UnitPositionFunctionRelationshipQueryResult> unitPositionFunctionRelationshipQueryResults = unitPositionFunctionRelationshipRepository.findAllByFunctionIdAndUnitPositionId(unitPositionId, payload.keySet());
        List<UnitPositionFunctionRelationship> unitPositionFunctionRelationships = new ArrayList<>();

        for (UnitPositionFunctionRelationshipQueryResult current : unitPositionFunctionRelationshipQueryResults) {
            if (payload.get(current.getFunction().getId()) != null) {
                current.getAppliedDates().addAll(payload.get(current.getFunction().getId()));
                unitPositionFunctionRelationships.add(new UnitPositionFunctionRelationship(current.getId(), current.getUnitPosition(), current.getFunction(), current.getAppliedDates()));
            }
        }
        unitPositionFunctionRelationshipRepository.saveAll(unitPositionFunctionRelationships);
        return true;
    }

    /**
     * @param first
     * @param second
     * @return matchedDates
     * @Auther Pavan
     * @Desc This method will return the Matched or common dates from two sets
     */

    private Set<LocalDate> getIntersectedDates(Set<LocalDate> first, Set<LocalDate> second) {
        Set<LocalDate> matchedDates = new HashSet<>();
        if (CollectionUtils.isEmpty(first) || CollectionUtils.isEmpty(second)) {
            return matchedDates;
        }
        for (LocalDate currentLocalDate : second) {
            if (first.contains(currentLocalDate)) {
                matchedDates.add(currentLocalDate);
            }

        }
        return matchedDates;
    }


    public com.kairos.dto.activity.shift.StaffUnitPositionDetails getUnitPositionCTA(Long unitPositionId, Long unitId) {
        UnitPositionQueryResult unitPosition = unitPositionGraphRepository.getUnitPositionById(unitPositionId);
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        Optional<Organization> organization = organizationGraphRepository.findById(unitId, 0);
        com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails = new com.kairos.dto.activity.shift.StaffUnitPositionDetails();
        unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        unitPositionDetails.setCountryId(countryId);
        convertUnitPositionObject(unitPosition, unitPositionDetails);
        unitPositionDetails.setCountryId(countryId);
        unitPositionDetails.setUnitTimeZone(organization.get().getTimeZone());
        return unitPositionDetails;

    }
}
