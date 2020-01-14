package com.kairos.service.employment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.cta.CTAWTAAndAccumulatedTimebankWrapper;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.dto.user.country.experties.FunctionsDTO;
import com.kairos.dto.user.staff.employment.EmploymentDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.query_results.ClientMinimumDTO;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.functions.FunctionWithAmountQueryResult;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.pay_table.PayTable;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.staff.TimeCareEmploymentDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffAdditionalInfoQueryResult;
import com.kairos.persistence.model.staff.position.EmploymentAndPositionDTO;
import com.kairos.persistence.model.staff.position.Position;
import com.kairos.persistence.model.staff.position.PositionQueryResult;
import com.kairos.persistence.model.staff.position.PositionReasonCodeQueryResult;
import com.kairos.persistence.model.user.employment.*;
import com.kairos.persistence.model.user.employment.query_result.EmploymentLinesQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.model.user.employment.query_result.StaffEmploymentDetails;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseLine;
import com.kairos.persistence.model.user.expertise.ProtectedDaysOffSetting;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.expertise.response.ExpertisePlannedTimeQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentAndEmploymentTypeRelationShipGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentLineFunctionRelationShipGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseEmploymentTypeRelationshipGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableGraphRepository;
import com.kairos.persistence.repository.user.staff.PositionGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.rest_client.WorkingTimeAgreementRestClient;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.AsynchronousService;
import com.kairos.service.country.CountryService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.initial_time_bank_log.InitialTimeBankLogService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.staff.PositionService;
import com.kairos.service.staff.StaffRetrievalService;
import com.kairos.wrapper.PositionWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.startDateIsEqualsOrBeforeEndDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ApiConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION;
import static com.kairos.service.employment.EmploymentUtility.convertEmploymentObject;
import static com.kairos.service.employment.EmploymentUtility.convertStaffEmploymentObject;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@Transactional
@Service

public class EmploymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmploymentService.class);
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private PayTableGraphRepository payTableGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private StaffRetrievalService staffRetrievalService;
    @Inject
    private EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private CountryService countryService;
    @Inject
    private ClientGraphRepository clientGraphRepository;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private EmploymentAndEmploymentTypeRelationShipGraphRepository employmentAndEmploymentTypeRelationShipGraphRepository;
    @Inject
    private ReasonCodeGraphRepository reasonCodeGraphRepository;
    @Inject
    private PayGradeGraphRepository payGradeGraphRepository;
    @Inject
    private FunctionGraphRepository functionGraphRepository;
    @Inject
    private StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Inject
    private PositionService positionService;
    @Inject
    private PositionGraphRepository positionGraphRepository;
    @Inject
    private WorkingTimeAgreementRestClient workingTimeAgreementRestClient;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ExpertiseEmploymentTypeRelationshipGraphRepository expertiseEmploymentTypeRelationshipGraphRepository;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private GenericRestClient genericRestClient;
    @Inject
    private AsynchronousService asynchronousService;
    @Inject
    private EmploymentLineFunctionRelationShipGraphRepository employmentLineFunctionRelationRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private InitialTimeBankLogService initialTimeBankLogService;


    public PositionWrapper createEmployment(EmploymentDTO employmentDTO, boolean saveAsDraft) throws Exception {
        Unit unit = unitGraphRepository.findOne(employmentDTO.getUnitId());
        Organization parentUnit = organizationService.fetchParentOrganization(unit.getId());

        Position position = positionGraphRepository.findByStaffId(employmentDTO.getStaffId());
        if (!Optional.ofNullable(position).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_EMPLOYMENT_NOTFOUND, employmentDTO.getStaffId());
        }
        if (position.getStartDateMillis() != null) {
            if (employmentDTO.getStartDate().isBefore(DateUtils.getDateFromEpoch(position.getStartDateMillis()))) {
                exceptionService.actionNotPermittedException(MESSAGE_STAFF_DATA_EMPLOYMENTDATE_LESSTHAN);
            }
        }

        if (!saveAsDraft) {
            List<Employment> oldEmployments = employmentGraphRepository.getStaffEmploymentsByExpertise(unit.getId(), employmentDTO.getStaffId(), employmentDTO.getExpertiseId());
            validateEmploymentWithExpertise(oldEmployments, employmentDTO);
        }


        EmploymentType employmentType = unitGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(parentUnit.getId(), employmentDTO.getEmploymentTypeId(), false);
        if (!Optional.ofNullable(employmentType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_POSITION_EMPLOYMENTTYPE_NOTEXIST, employmentDTO.getEmploymentTypeId());
        }
        List<FunctionWithAmountQueryResult> functions = findAndValidateFunction(employmentDTO);
        Employment employment = new Employment(unit, employmentDTO.getStartDate(), employmentDTO.getTimeCareExternalId(), !saveAsDraft, employmentDTO.getTaxDeductionPercentage(), employmentDTO.getAccumulatedTimebankMinutes(), employmentDTO.getAccumulatedTimebankDate());

        preparePosition(employment, employmentDTO);
        if (EmploymentSubType.MAIN.equals(employmentDTO.getEmploymentSubType()) && positionService.eligibleForMainEmployment(employmentDTO, -1)) {
            employment.setEmploymentSubType(EmploymentSubType.MAIN);
        }
        employmentGraphRepository.save(employment);
        initialTimeBankLogService.saveInitialTimeBankLog(employment.getId(), employment.getAccumulatedTimebankMinutes());
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = assignCTAAndWTAToEmployment(employment, employmentDTO);
        Long reasonCodeId = updateEmploymentEndDate(parentUnit, employmentDTO, position);
        List<EmploymentLineEmploymentTypeRelationShip> employmentLineEmploymentTypeRelationShips = new ArrayList<>();
        employment.getEmploymentLines().forEach(line -> {
            employmentLineEmploymentTypeRelationShips.add(new EmploymentLineEmploymentTypeRelationShip(line, employmentType, employmentDTO.getEmploymentTypeCategory()));
        });
        employmentAndEmploymentTypeRelationShipGraphRepository.saveAll(employmentLineEmploymentTypeRelationShips);
        linkFunctions(functions, employment.getEmploymentLines().get(0), false, employmentDTO.getFunctions());

        EmploymentQueryResult employmentQueryResult = getBasicDetails(employmentType, employmentDTO, employment, employmentLineEmploymentTypeRelationShips.get(0), parentUnit.getId(), parentUnit.getName(), ctawtaAndAccumulatedTimebankWrapper.getWta().get(0), employment.getEmploymentLines().get(0));
        employmentQueryResult.getEmploymentLines().get(0).setCostTimeAgreement(ctawtaAndAccumulatedTimebankWrapper.getCta().get(0));
        employmentQueryResult.getEmploymentLines().get(0).setWorkingTimeAgreement(ctawtaAndAccumulatedTimebankWrapper.getWta().get(0));
        setHourlyCost(employmentQueryResult);
        return new PositionWrapper(employmentQueryResult, new PositionQueryResult(position.getId(), position.getStartDateMillis(), position.getEndDateMillis(), reasonCodeId, position.getAccessGroupIdOnPositionEnd()));
    }

    private void linkFunctions(List<FunctionWithAmountQueryResult> functions, EmploymentLine employmentLine, boolean update, Set<FunctionsDTO> functionDTOS) {
        if (update) {
            // need to delete the current applied functions
            employmentGraphRepository.removeAllAppliedFunctionOnEmploymentLines(employmentLine.getId());
        }
        Map<Long, BigDecimal> functionAmountMap = functionDTOS.stream().collect(Collectors.toMap(FunctionsDTO::getId, FunctionsDTO::getAmount));
        List<EmploymentLineFunctionRelationShip> functionsEmploymentLines = new ArrayList<>(functions.size());
        functions.forEach(currentFunction ->
            functionsEmploymentLines.add(new EmploymentLineFunctionRelationShip(employmentLine, currentFunction.getFunction(), functionAmountMap.get(currentFunction.getFunction().getId())))
        );
        employmentLineFunctionRelationRepository.saveAll(functionsEmploymentLines);
    }

    private CTAWTAAndAccumulatedTimebankWrapper assignCTAAndWTAToEmployment(Employment employment, EmploymentDTO employmentDTO) {
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = workingTimeAgreementRestClient.assignWTAToEmployment(employment.getId(), employmentDTO.getWtaId(), employmentDTO.getCtaId(), employmentDTO.getStartDate());
        if (ctawtaAndAccumulatedTimebankWrapper.getWta().isEmpty()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_ID);
        }
        if (ctawtaAndAccumulatedTimebankWrapper.getCta().isEmpty()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_CTA_ID);
        }
        return ctawtaAndAccumulatedTimebankWrapper;
    }

    private Long updateEmploymentEndDate(Organization organization, EmploymentDTO employmentDTO, Position position) throws Exception {
        Position position1 = positionService.updatePositionEndDate(organization, employmentDTO.getStaffId(), employmentDTO.getEndDate() != null ? DateUtils.getDateFromEpoch(employmentDTO.getEndDate()) : null, employmentDTO.getReasonCodeId(), employmentDTO.getAccessGroupId());
        return Optional.ofNullable(position.getReasonCode()).isPresent() ? position1.getReasonCode().getId() : null;

    }

    public boolean validateEmploymentWithExpertise(List<Employment> employments, EmploymentDTO employmentDTO) {

        LocalDate employmentStartDate = employmentDTO.getStartDate();
        LocalDate employmentEndDate = employmentDTO.getEndDate();

        employments.forEach(employment -> {
            // if null date is set
            if (employment.getEndDate() != null) {
                if (employmentStartDate.isBefore(employment.getEndDate()) && employmentStartDate.isAfter(employment.getStartDate())) {
                    exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_POSITIONCODE_ALREADYEXIST_WITHVALUE, employmentEndDate, employment.getStartDate());
                }
                if (employmentEndDate != null) {
                    Interval previousInterval = new Interval(DateUtils.getDateFromEpoch(employment.getStartDate()), DateUtils.getDateFromEpoch(employment.getEndDate()));
                    Interval interval = new Interval(DateUtils.getDateFromEpoch(employmentStartDate), DateUtils.getDateFromEpoch(employmentEndDate));
                    LOGGER.info(" Interval of CURRENT UEP " + previousInterval + " Interval of going to create  " + interval);
                    if (previousInterval.overlaps(interval))
                        exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_POSITIONCODE_ALREADYEXIST);
                } else {
                    if (employmentStartDate.isBefore(employment.getEndDate())) {
                        exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_POSITIONCODE_ALREADYEXIST_WITHVALUE, employmentEndDate, employment.getEndDate());
                    }
                }
            } else {
                // unitEmploymentEnd date is null
                if (employmentEndDate != null) {
                    if (employmentEndDate.isAfter(employment.getStartDate())) {
                        exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_POSITIONCODE_ALREADYEXIST_WITHVALUE, employmentEndDate, employment.getStartDate());

                    }
                } else {
                    exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_POSITIONCODE_ALREADYEXIST);
                }
            }
        });

        return true;
    }


    private List<FunctionWithAmountQueryResult> findAndValidateFunction(EmploymentDTO employmentDTO) {
        List<Long> funIds = employmentDTO.getFunctions().stream().map(FunctionsDTO::getId).collect(Collectors.toList());
        List<FunctionWithAmountQueryResult> functions = functionGraphRepository.getFunctionsByExpertiseAndSeniorityLevelAndIds
                (employmentDTO.getUnitId(), employmentDTO.getExpertiseId(), employmentDTO.getSeniorityLevelId(), employmentDTO.getStartDate().toString(),
                        funIds);
//        if (functions.size() != employmentDTO.getFunctions().size()) {
//            exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_FUNCTIONS_UNABLE);
//        }
        return functions;
    }

    private EmploymentLine createEmploymentLine(Employment oldEmployment, EmploymentLine oldEmploymentLine, EmploymentDTO employmentDTO) {
        if (Optional.ofNullable(employmentDTO.getEndDate()).isPresent() && employmentDTO.getStartDate().isAfter(employmentDTO.getEndDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_STARTDATE_NOTLESSTHAN_ENDDATE);
        }
        if (Optional.ofNullable(employmentDTO.getLastWorkingDate()).isPresent() && employmentDTO.getStartDate().isAfter(employmentDTO.getLastWorkingDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_LASTDATE_NOTLESSTHAN_ENDDATE);
        }
        oldEmployment.setLastWorkingDate(employmentDTO.getLastWorkingDate());
        EmploymentLine employmentLine = new EmploymentLine.EmploymentLineBuilder()
                .setAvgDailyWorkingHours(employmentDTO.getAvgDailyWorkingHours())
                .setTotalWeeklyMinutes((employmentDTO.getTotalWeeklyHours() * 60) + employmentDTO.getTotalWeeklyMinutes())
                .setHourlyCost(employmentDTO.getHourlyCost())
                .setStartDate(employmentDTO.getStartDate())
                .setFullTimeWeeklyMinutes(oldEmploymentLine.getFullTimeWeeklyMinutes())
                .setWorkingDaysInWeek(oldEmploymentLine.getWorkingDaysInWeek())
                .setEndDate(employmentDTO.getEndDate())
                .setSeniorityLevel(oldEmploymentLine.getSeniorityLevel())
                .build();

        oldEmploymentLine.setEndDate(employmentDTO.getStartDate().minusDays(1));
        if (Optional.ofNullable(employmentDTO.getEndDate()).isPresent()) {

            if (!Optional.ofNullable(employmentDTO.getReasonCodeId()).isPresent()) {
                exceptionService.actionNotPermittedException(MESSAGE_REGION_ENDDATE);
            }
            if (oldEmployment.getReasonCode() == null || !oldEmployment.getReasonCode().getId().equals(employmentDTO.getReasonCodeId())) {
                Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(employmentDTO.getReasonCodeId(), 0);
                if (!Optional.ofNullable(reasonCode).isPresent()) {
                    exceptionService.dataNotFoundByIdException(MESSAGE_REASONCODE_ID_NOTFOUND, employmentDTO.getReasonCodeId());
                }
                oldEmployment.setReasonCode(reasonCode.get());
            }
        }

        return employmentLine;
    }


    private EmploymentLineChangeResultDTO calculativeValueChanged(EmploymentDTO employmentDTO, EmploymentLineEmploymentTypeRelationShip oldEmploymentLineEmploymentTypeRelationShip, EmploymentLine employmentLine,
                                                                  CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper, List<NameValuePair> changedParams) {
        EmploymentLineChangeResultDTO changeResultDTO = new EmploymentLineChangeResultDTO(false);

        if (!employmentDTO.getCtaId().equals(ctawtaAndAccumulatedTimebankWrapper.getCta().get(0).getId())) {
            // CTA is changed
            changeResultDTO.setCtaId(employmentDTO.getCtaId());
            changeResultDTO.setOldctaId(ctawtaAndAccumulatedTimebankWrapper.getCta().get(0).getId());
            changedParams.add(new BasicNameValuePair("ctaId", employmentDTO.getCtaId() + ""));
            changedParams.add(new BasicNameValuePair("oldctaId", ctawtaAndAccumulatedTimebankWrapper.getCta().get(0).getId() + ""));
            changeResultDTO.setCalculativeChanged(true);
        }
        if (!employmentDTO.getWtaId().equals(ctawtaAndAccumulatedTimebankWrapper.getWta().get(0).getId())) {
            // wta is changed
            changeResultDTO.setWtaId(employmentDTO.getWtaId());
            changeResultDTO.setOldwtaId(ctawtaAndAccumulatedTimebankWrapper.getWta().get(0).getId());
            changeResultDTO.setCalculativeChanged(true);
            changedParams.add(new BasicNameValuePair("wtaId", employmentDTO.getWtaId() + ""));
            changedParams.add(new BasicNameValuePair("oldwtaId", ctawtaAndAccumulatedTimebankWrapper.getWta().get(0).getId() + ""));
        }
        if (employmentLine.getAvgDailyWorkingHours() != employmentDTO.getAvgDailyWorkingHours()
                || employmentLine.getTotalWeeklyMinutes() != (employmentDTO.getTotalWeeklyMinutes() + (employmentDTO.getTotalWeeklyHours() * 60))) {
            changeResultDTO.setCalculativeChanged(true);
        }
        if (!oldEmploymentLineEmploymentTypeRelationShip.getEmploymentType().getId().equals(employmentDTO.getEmploymentTypeId()) || !oldEmploymentLineEmploymentTypeRelationShip.getEmploymentTypeCategory().equals(employmentDTO.getEmploymentTypeCategory())) {
            changeResultDTO.setCalculativeChanged(true);
            changeResultDTO.setEmploymentTypeChanged(true);
        }

        List<FunctionWithAmountQueryResult> newAppliedFunctions = findAndValidateFunction(employmentDTO);
        List<FunctionWithAmountQueryResult> olderAppliesFunctions = employmentGraphRepository.findAllAppliedFunctionOnEmploymentLines(employmentDTO.getEmploymentLineId());
        Map<Long, BigDecimal> functionAmountMap = employmentDTO.getFunctions().stream().collect(Collectors.toMap(FunctionsDTO::getId, FunctionsDTO::getAmount, (first, second) -> first));
        // if earlier there were 3 applied function and new its 2 or 4 then simply we need to set calculative value change and
        // return it without checking its objects or values
        if (newAppliedFunctions.size() != olderAppliesFunctions.size()) {
            changeResultDTO.setCalculativeChanged(true);
            changeResultDTO.setFunctionsChanged(true);
        } else {  // earlier appilied function 4 amount 5 new applied 4 but amount 6
            olderAppliesFunctions.forEach(currentOldFunction -> {
                AtomicBoolean currentMatched = new AtomicBoolean(false);
                newAppliedFunctions.forEach(newCurrentFunction -> {
                    if (currentOldFunction.getFunction().getId().equals(newCurrentFunction.getFunction().getId()) && functionAmountMap.get(currentOldFunction.getFunction().getId()).equals(newCurrentFunction.getAmount())) {
                        currentMatched.getAndSet(true);
                        return; // break inner loop
                    }
                });
                // flag based matching
                if (!currentMatched.get()) {
                    changeResultDTO.setCalculativeChanged(true);
                    changeResultDTO.setFunctionsChanged(true);
                    return; // this is used to break from outer loop.
                }
            });
        }
        //TODO add outside if statement becouse if function size is same not sent setCalculativeChanged true
        changeResultDTO.setFunctions(newAppliedFunctions);
        return changeResultDTO;
    }

    private void linkEmploymentLineWithEmploymentType(EmploymentLine employmentLine, EmploymentDTO employmentDTO) {
        EmploymentType employmentType = employmentTypeGraphRepository.findOne(employmentDTO.getEmploymentTypeId());
        if (!Optional.ofNullable(employmentType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_POSITION_EMPLOYMENTTYPE_NOTEXIST, employmentDTO.getEmploymentTypeId());
        }

        EmploymentLineEmploymentTypeRelationShip relationShip = new EmploymentLineEmploymentTypeRelationShip(employmentLine, employmentType, employmentDTO.getEmploymentTypeCategory());
        employmentAndEmploymentTypeRelationShipGraphRepository.save(relationShip);
    }


    public PositionWrapper updateEmployment(long employmentId, EmploymentDTO employmentDTO, Long unitId, boolean saveAsDraft) throws Exception {

        Unit unit = unitGraphRepository.findOne(unitId);
        List<ClientMinimumDTO> clientMinimumDTO = clientGraphRepository.getCitizenListForThisContactPerson(employmentDTO.getStaffId());
        if (isCollectionNotEmpty(clientMinimumDTO)) {
            return new PositionWrapper(clientMinimumDTO);
        }
        Employment oldEmployment = employmentGraphRepository.findOne(employmentId, 2);
        if (!Optional.ofNullable(oldEmployment).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_POSITIONID_NOTFOUND, employmentId);
        }
        EmploymentLine currentEmploymentLine = oldEmployment.getEmploymentLines().stream().filter(employmentLine -> employmentLine.getId().equals(employmentDTO.getEmploymentLineId()))
                .findFirst().orElse(null);
        if (currentEmploymentLine == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_POSITION_LINE_NOTFOUND, employmentId);
        }

        List<NameValuePair> param = Arrays.asList(new BasicNameValuePair("employmentId", employmentId + ""), new BasicNameValuePair("startDate", currentEmploymentLine.getStartDate().toString()));
        CTAWTAAndAccumulatedTimebankWrapper existingCtaWtaAndAccumulatedTimebankWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, APPLICABLE_CTA_WTA, param,
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>>() {
                });
        if (existingCtaWtaAndAccumulatedTimebankWrapper.getCta().isEmpty()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENT_CTAMISSING, employmentDTO.getStartDate(), employmentId);
        }
        if (existingCtaWtaAndAccumulatedTimebankWrapper.getWta().isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.employment.wtamissing", employmentDTO.getStartDate(), employmentId);
        }

        EmploymentType employmentType = employmentTypeGraphRepository.findById(employmentDTO.getEmploymentTypeId(), 0).orElse(null);
        if (EmploymentSubType.MAIN.equals(employmentDTO.getEmploymentSubType()) && positionService.eligibleForMainEmployment(employmentDTO, employmentId)) {
            oldEmployment.setEmploymentSubType(EmploymentSubType.MAIN);
        }

        EmploymentLineEmploymentTypeRelationShip employmentLineEmploymentTypeRelationShip = employmentGraphRepository.findEmploymentTypeByEmploymentId(currentEmploymentLine.getId());
        PositionQueryResult positionQueryResult;
        EmploymentQueryResult employmentQueryResult;
        List<NameValuePair> changedParams = new ArrayList<>();
        oldEmployment.setPublished(!saveAsDraft);
        oldEmployment.setAccumulatedTimebankMinutes(employmentDTO.getAccumulatedTimebankMinutes());
        oldEmployment.setAccumulatedTimebankDate(employmentDTO.getAccumulatedTimebankDate());
        oldEmployment.setTaxDeductionPercentage(employmentDTO.getTaxDeductionPercentage());
        EmploymentLineChangeResultDTO changeResultDTO = calculativeValueChanged(employmentDTO, employmentLineEmploymentTypeRelationShip, currentEmploymentLine, existingCtaWtaAndAccumulatedTimebankWrapper, changedParams);
        /**
         *  Old employment's calculative values is changed
         *  Old employment is published so need to create a new  position line
         **/
        if (changeResultDTO.isCalculativeChanged()) {

            if (currentEmploymentLine.getStartDate().isEqual(employmentDTO.getStartDate())) {
                //both are of same start Date only set  data
                updateCurrentEmploymentLine(currentEmploymentLine, employmentDTO);
                if (changeResultDTO.isEmploymentTypeChanged()) {
                    employmentAndEmploymentTypeRelationShipGraphRepository.updateEmploymentTypeInCurrentEmploymentLine(currentEmploymentLine.getId(), employmentDTO.getEmploymentTypeId(), employmentDTO.getEmploymentTypeCategory());
                }
                //TODO uncomment if function setting is changed currently function not add in employmentLine KP-6010
                // if (changeResultDTO.isFunctionsChanged()) {
                linkFunctions(changeResultDTO.getFunctions(), currentEmploymentLine, true, employmentDTO.getFunctions());
                //}
                setEndDateToEmployment(oldEmployment, employmentDTO);
                employmentGraphRepository.save(oldEmployment);
                employmentQueryResult = getBasicDetails(employmentType, employmentDTO, oldEmployment, employmentLineEmploymentTypeRelationShip, unit.getId(), unit.getName(), null, currentEmploymentLine);
            } else {
                EmploymentLine employmentLine = createEmploymentLine(oldEmployment, currentEmploymentLine, employmentDTO);
                oldEmployment.getEmploymentLines().add(employmentLine);
                setEndDateToEmployment(oldEmployment, employmentDTO);
                employmentGraphRepository.save(oldEmployment);
                linkEmploymentLineWithEmploymentType(employmentLine, employmentDTO);
                // if (changeResultDTO.isFunctionsChanged()) {
                linkFunctions(changeResultDTO.getFunctions(), employmentLine, false, employmentDTO.getFunctions());
                //}
                employmentQueryResult = getBasicDetails(employmentType, employmentDTO, oldEmployment, employmentLineEmploymentTypeRelationShip, unit.getId(), unit.getName(), null, employmentLine);
            }

            CTAWTAAndAccumulatedTimebankWrapper newCTAWTAAndAccumulatedTimebankWrapper = null;
            if (changeResultDTO.getCtaId() != null || changeResultDTO.getWtaId() != null) {
                changedParams.add(new BasicNameValuePair("startDate", employmentDTO.getStartDate() + ""));
                newCTAWTAAndAccumulatedTimebankWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.CREATE, APPLY_CTA_WTA, changedParams,
                        new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>>() {
                        }, employmentId);
            }


            if (changeResultDTO.getWtaId() != null) {
                employmentQueryResult.getEmploymentLines().get(0).setWorkingTimeAgreement(newCTAWTAAndAccumulatedTimebankWrapper.getWta().get(0));
            } else {
                employmentQueryResult.getEmploymentLines().get(0).setWorkingTimeAgreement(existingCtaWtaAndAccumulatedTimebankWrapper.getWta().get(0));
            }
            if (changeResultDTO.getCtaId() != null) {
                employmentQueryResult.getEmploymentLines().get(0).setCostTimeAgreement(newCTAWTAAndAccumulatedTimebankWrapper.getCta().get(0));
            } else {
                employmentQueryResult.getEmploymentLines().get(0).setCostTimeAgreement(existingCtaWtaAndAccumulatedTimebankWrapper.getCta().get(0));
            }
            if (newCTAWTAAndAccumulatedTimebankWrapper != null && isCollectionNotEmpty(newCTAWTAAndAccumulatedTimebankWrapper.getCta())) {
                updateTimeBank(newCTAWTAAndAccumulatedTimebankWrapper.getCta().get(0).getId(), employmentId, employmentQueryResult.getEmploymentLines().get(0).getStartDate(), employmentQueryResult.getEmploymentLines().get(0).getEndDate(), unitId);
            }

        }
        // calculative value is not changed it means only end date is updated.
        else {
            currentEmploymentLine.setEndDate(employmentDTO.getEndDate());
            oldEmployment.setEndDate(employmentDTO.getEndDate());
            if (saveAsDraft) {
                currentEmploymentLine.setStartDate(employmentDTO.getStartDate());
                oldEmployment.setStartDate(employmentDTO.getStartDate());
            }
            setEndDateToEmployment(oldEmployment, employmentDTO);
            oldEmployment.setLastWorkingDate(employmentDTO.getLastWorkingDate());
            employmentGraphRepository.save(oldEmployment);
            employmentQueryResult = getBasicDetails(employmentType, employmentDTO, oldEmployment, employmentLineEmploymentTypeRelationShip, unit.getId(), unit.getName(), null, currentEmploymentLine);
            employmentQueryResult.getEmploymentLines().get(0).setWorkingTimeAgreement(existingCtaWtaAndAccumulatedTimebankWrapper.getWta().get(0));
            employmentQueryResult.getEmploymentLines().get(0).setCostTimeAgreement(existingCtaWtaAndAccumulatedTimebankWrapper.getCta().get(0));
        }

        Organization organization = organizationService.fetchParentOrganization(unitId);
        initialTimeBankLogService.saveInitialTimeBankLog(oldEmployment.getId(), oldEmployment.getAccumulatedTimebankMinutes());
        Position position = positionService.updatePositionEndDate(organization, employmentDTO.getStaffId(),
                employmentDTO.getEndDate() != null ? DateUtils.getDateFromEpoch(employmentDTO.getEndDate()) : null, employmentDTO.getReasonCodeId(), employmentDTO.getAccessGroupId());
        Long reasonCodeId = Optional.ofNullable(position.getReasonCode()).isPresent() ? position.getReasonCode().getId() : null;
        positionQueryResult = new PositionQueryResult(position.getId(), position.getStartDateMillis(), position.getEndDateMillis(), reasonCodeId, position.getAccessGroupIdOnPositionEnd());
        // Deleting All shifts after position end date
        if (employmentDTO.getEndDate() != null) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRetrievalService.getStaffEmploymentDataByEmploymentId(employmentDTO.getEndDate(), employmentDTO.getId(), employmentDTO.getUnitId(), null);
            activityIntegrationService.deleteShiftsAfterEmploymentEndDate(unitId, employmentDTO.getEndDate(), employmentDTO.getStaffId(), staffAdditionalInfoDTO);
        }
        setHourlyCost(employmentQueryResult);
        return new PositionWrapper(employmentQueryResult, positionQueryResult);

    }


    /**
     * @param employmentId
     * @param employmentLineStartDate
     * @param employmentLineEndDate
     * @param unitId
     */
    private void updateTimeBank(BigInteger ctaId, long employmentId, LocalDate employmentLineStartDate, LocalDate employmentLineEndDate, Long unitId) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRetrievalService.getStaffEmploymentDataByEmploymentIdAndStaffId(employmentLineStartDate, employmentGraphRepository.getStaffIdFromEmployment(employmentId), employmentId, unitId, Collections.emptySet());
        activityIntegrationService.updateTimeBankOnEmploymentUpdation(ctaId, employmentId, employmentLineStartDate, employmentLineEndDate, staffAdditionalInfoDTO);
    }

    private void setEndDateToEmployment(Employment employment, EmploymentDTO employmentDTO) {
        PlanningPeriodDTO planningPeriod = activityIntegrationService.getPlanningPeriodIntervalByUnitId(employment.getUnit().getId());
        if(isNotNull(employmentDTO.getEndDate()) && planningPeriod.getEndDate().isAfter(employmentDTO.getEndDate())){
            exceptionService.actionNotPermittedException(MESSAGE_ENDDATE_NOTGREATERTHAN_PLANNING_PERIOD_ENDDATE);
        }
        if (isNull(employmentDTO.getEndDate())) {
            employment.setEndDate(null);
        } else if (isNotNull(employmentDTO.getEndDate()) && isNull(employment.getEndDate())) {
            employment.setEndDate(employmentDTO.getEndDate());
            setEndDateToCTAWTA(employment.getUnit().getId(), employment.getId(), employmentDTO.getEndDate());
        } else if (isNotNull(employmentDTO.getEndDate()) && isNotNull(employment.getEndDate()) && employment.getEndDate().isBefore(employmentDTO.getEndDate())) {
            employment.setEndDate(employmentDTO.getEndDate());
            setEndDateToCTAWTA(employment.getUnit().getId(), employment.getId(), employmentDTO.getEndDate());
        }
    }

    private void setEndDateToCTAWTA(Long unitId, Long employmentId, LocalDate endDate) {

        genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.UPDATE, APPLY_CTA_WTA_END_DATE,
                Collections.singletonList(new BasicNameValuePair("endDate", endDate + "")), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                }, employmentId);
    }

    private void updateCurrentEmploymentLine(EmploymentLine employmentLine, EmploymentDTO employmentDTO) {
        employmentLine.setAvgDailyWorkingHours(employmentDTO.getAvgDailyWorkingHours());
        employmentLine.setTotalWeeklyMinutes((employmentDTO.getTotalWeeklyHours() * 60) + employmentDTO.getTotalWeeklyMinutes());
        employmentLine.setHourlyCost(employmentDTO.getHourlyCost());
        employmentLine.setStartDate(employmentDTO.getStartDate());
        employmentLine.setEndDate(employmentDTO.getEndDate());
    }

    public PositionQueryResult removeEmployment(long positionId, Long unitId) throws Exception {
        Employment employment = employmentGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(employment).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENT_ID_NOTEXIST, positionId);
        }
        Long shiftcount = activityIntegrationService.publishShiftCountWithEmploymentId(positionId);
        if (shiftcount > 0) {
            exceptionService.actionNotPermittedException(MESSAGE_EMPLOYMENT_CONTAIN_SHIFT, positionId);
        }
        employment.setDeleted(true);
        employmentGraphRepository.save(employment);
        Long staffId = employmentGraphRepository.getStaffIdFromEmployment(positionId);
        Organization organization = organizationService.fetchParentOrganization(unitId);
        Position position = positionService.updatePositionEndDate(organization, staffId);
        return new PositionQueryResult(position.getId(), position.getStartDateMillis(), position.getEndDateMillis());
    }


    public EmploymentQueryResult getEmployment(Long employmentId) {
        return employmentGraphRepository.findByEmploymentId(employmentId);
    }

    @Async
    private CompletableFuture<Boolean> setDefaultData(EmploymentDTO employmentDTO, Employment employment) throws InterruptedException, ExecutionException {


        if (Optional.ofNullable(employmentDTO.getUnionId()).isPresent()) {
            Callable<Organization> organizationCallable = () -> organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(employmentDTO.getUnionId());
            Future<Organization> organizationFuture = asynchronousService.executeAsynchronously(organizationCallable);
            if (!Optional.ofNullable(organizationFuture.get()).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_UNION_NOTEXIST, employmentDTO.getUnionId());
            }
            employment.setUnion(organizationFuture.get());
        }

        Callable<Staff> staffCallable = () -> staffGraphRepository.findOne(employmentDTO.getStaffId());
        Future<Staff> staffFuture = asynchronousService.executeAsynchronously(staffCallable);
        if (!Optional.ofNullable(staffFuture.get()).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENT_STAFF_NOTFOUND, employmentDTO.getStaffId());
        }
        employment.setStaff(staffFuture.get());
        return CompletableFuture.completedFuture(true);
    }


    private Employment preparePosition(Employment employment, EmploymentDTO employmentDTO) throws Exception {
        CompletableFuture<Boolean> done = setDefaultData(employmentDTO, employment);
        CompletableFuture.allOf(done).join();
        // UEP can be created for past dates from time care
        PlanningPeriodDTO planningPeriod = activityIntegrationService.getPlanningPeriodIntervalByUnitId(employment.getUnit().getId());
        if(planningPeriod.getStartDate().isBefore(employmentDTO.getStartDate())){
            exceptionService.actionNotPermittedException(MESSAGE_STARTDATE_NOTLESSTHAN_PLANNING_PERIOD_STARTDATE);
        }
        employment.setStartDate(employmentDTO.getStartDate());
        if (Optional.ofNullable(employmentDTO.getEndDate()).isPresent()) {
            if (employmentDTO.getStartDate().isAfter(employmentDTO.getEndDate())) {
                exceptionService.actionNotPermittedException(MESSAGE_STARTDATE_NOTLESSTHAN_ENDDATE);
            }
            if (!Optional.ofNullable(employmentDTO.getReasonCodeId()).isPresent()) {
                exceptionService.actionNotPermittedException(MESSAGE_REGION_ENDDATE);
            }
            Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(employmentDTO.getReasonCodeId(), 0);
            if (!Optional.ofNullable(reasonCode).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_REASONCODE_ID_NOTFOUND, employmentDTO.getReasonCodeId());
            }
            if(planningPeriod.getEndDate().isAfter(employmentDTO.getEndDate())){
                exceptionService.actionNotPermittedException(MESSAGE_ENDDATE_NOTGREATERTHAN_PLANNING_PERIOD_ENDDATE);
            }
            employment.setReasonCode(reasonCode.get());
            employment.setEndDate(employmentDTO.getEndDate());
        }

        if (Optional.ofNullable(employmentDTO.getLastWorkingDate()).isPresent()) {
            if (employmentDTO.getStartDate().isAfter(employmentDTO.getLastWorkingDate())) {
                exceptionService.actionNotPermittedException(MESSAGE_LASTDATE_NOTLESSTHAN_STARTDATE);
            }
            employment.setLastWorkingDate(employmentDTO.getLastWorkingDate());
        }

        employment.setEmploymentLines(getEmploymentLines(employmentDTO, employment));

        return employment;
    }

    private List<EmploymentLine> getEmploymentLines(EmploymentDTO employmentDTO, Employment employment) {
        List<EmploymentLine> employmentLines = new ArrayList<>();
        Expertise expertise = expertiseGraphRepository.findOne(employmentDTO.getExpertiseId(), 2);
        expertise.getExpertiseLines().sort(Comparator.comparing(ExpertiseLine::getStartDate));
        LocalDate startDateForLine = employmentDTO.getStartDate();
        LocalDate endDateForLine;
        for (ExpertiseLine expertiseLine : expertise.getExpertiseLines()) {
            DateTimeInterval expertiseLineInterval = new DateTimeInterval(expertiseLine.getStartDate(), expertiseLine.getEndDate());
            DateTimeInterval employmentInterval = new DateTimeInterval(employmentDTO.getStartDate(), employmentDTO.getEndDate());
            if (expertiseLineInterval.overlaps(employmentInterval)) {
                List<PayTable> payTables = payTableGraphRepository.findAllActivePayTable(expertise.getOrganizationLevel().getId(), expertiseLine.getStartDate().toString(), expertiseLine.getEndDate() == null ? null : expertiseLine.getEndDate().toString(),employmentDTO.getStartDate().toString());
                if (isCollectionEmpty(payTables)) {
                    addEmploymentLines(employmentDTO, employmentLines, expertiseLine, employmentDTO.getStartDate().isAfter(expertiseLine.getStartDate()) ? employmentDTO.getStartDate() : expertiseLine.getStartDate(), expertiseLine.getEndDate());
                } else {
                    payTables.sort(Comparator.comparing(PayTable::getStartDateMillis));
                    for (PayTable payTable : payTables) {
                        startDateForLine = getStartDate(startDateForLine, expertiseLine, payTable);
                        endDateForLine = getEndDate(expertiseLine, payTable);
                        addEmploymentLines(employmentDTO, employmentLines, expertiseLine, startDateForLine, endDateForLine);
                        if (endDateForLine != null) {
                            startDateForLine = endDateForLine.plusDays(1);
                        }
                    }
                }
            }
        }
        employment.setExpertise(expertise);
        return employmentLines;
    }

    private LocalDate getStartDate(LocalDate startDateForLine, ExpertiseLine expertiseLine, PayTable payTable) {
        return expertiseLine.getStartDate().isBefore(startDateForLine) ? startDateForLine : payTable.getStartDateMillis().isAfter(expertiseLine.getStartDate()) ? payTable.getStartDateMillis() : expertiseLine.getStartDate();
    }

    private LocalDate getEndDate(ExpertiseLine expertiseLine, PayTable payTable) {
        if (expertiseLine.getEndDate() == null && payTable.getEndDateMillis() != null) {
            return payTable.getEndDateMillis();
        } else if (expertiseLine.getEndDate() != null && payTable.getEndDateMillis() != null) {
            return payTable.getEndDateMillis().isBefore(expertiseLine.getEndDate()) ? payTable.getEndDateMillis() : expertiseLine.getEndDate();
        }
        return expertiseLine.getEndDate();
    }

    private void addEmploymentLines(EmploymentDTO employmentDTO, List<EmploymentLine> employmentLines, ExpertiseLine expertiseLine, LocalDate startDate, LocalDate endDate) {
        employmentLines.add(new EmploymentLine.EmploymentLineBuilder()
                .setSeniorityLevel(getSeniorityLevelByStaffAndExpertise(employmentDTO.getStaffId(), expertiseLine, employmentDTO.getExpertiseId()))
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setTotalWeeklyMinutes(employmentDTO.getTotalWeeklyMinutes() + (employmentDTO.getTotalWeeklyHours() * 60))
                .setFullTimeWeeklyMinutes(expertiseLine.getFullTimeWeeklyMinutes())
                .setWorkingDaysInWeek(expertiseLine.getNumberOfWorkingDaysInWeek())
                .setAvgDailyWorkingHours(employmentDTO.getAvgDailyWorkingHours())
                .setHourlyCost(employmentDTO.getHourlyCost())
                .build());
    }

    /*
     * @author vipul
     * used to get all positions of organization n by organization and staff Id
     * */
    public EmploymentAndPositionDTO getEmploymentsOfStaff(long unitId, long staffId, boolean allOrganization) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENT_STAFF_NOTFOUND, staffId);
        }

        User user = userGraphRepository.getUserByStaffId(staffId);

        PositionReasonCodeQueryResult employmentReasonCode = positionGraphRepository.findEmploymentreasonCodeByStaff(staffId);
        Position position = employmentReasonCode.getPosition();

        Long reasonCodeId = Optional.ofNullable(employmentReasonCode.getReasonCode()).isPresent() ? employmentReasonCode.getReasonCode().getId() : null;
        PositionQueryResult positionQueryResult = new PositionQueryResult(position.getId(), position.getStartDateMillis(), position.getEndDateMillis(), reasonCodeId, position.getAccessGroupIdOnPositionEnd());

        List<EmploymentQueryResult> employmentQueryResults = (allOrganization) ? employmentGraphRepository.getAllEmploymentsByUser(user.getId()) : employmentGraphRepository.getAllEmploymentsForCurrentOrganization(staffId, unitId);

        List<Long> employmentIds = employmentQueryResults.stream().map(EmploymentQueryResult::getId).collect(Collectors.toList());

        List<EmploymentLinesQueryResult> employmentLines = employmentGraphRepository.findAllEmploymentLines(employmentIds);
        List<EmploymentLinesQueryResult> hourlyCostPerLine = employmentGraphRepository.findFunctionalHourlyCost(employmentIds);
        Map<Long, BigDecimal> hourlyCostMap = hourlyCostPerLine.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getHourlyCost, (previous, current) -> current));
        Map<Long, List<EmploymentLinesQueryResult>> employmentLinesMap = employmentLines.stream().collect(Collectors.groupingBy(EmploymentLinesQueryResult::getEmploymentId));
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = activityIntegrationService.getCTAWTAAndAccumulatedTimebankByEmployment(employmentLinesMap, unitId);
        employmentQueryResults.forEach(employment -> {
            employment.setEmploymentLines(employmentLinesMap.get(employment.getId()));
            employment.setTotalShifts(activityIntegrationService.publishShiftCountWithEmploymentId(employment.getId()));
            employment.getEmploymentLines().forEach(employmentLine -> {
                BigDecimal hourlyCost = employmentLine.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
                employmentLine.setHourlyCost(hourlyCost);

                ctawtaAndAccumulatedTimebankWrapper.getCta().forEach(cta -> {

                    if (employment.getId().equals(cta.getEmploymentId()) && employmentLine.getEndDate() == null && (cta.getEndDate() == null || cta.getEndDate().plusDays(1).isAfter(employmentLine.getStartDate())) ||
                            employmentLine.getEndDate() != null && (cta.getStartDate().isBefore(employmentLine.getEndDate().plusDays(1))) && (cta.getEndDate() == null || cta.getEndDate().isAfter(employmentLine.getStartDate()) || cta.getEndDate().equals(employmentLine.getStartDate()))) {
                        employmentLine.setCostTimeAgreement(cta);
                    }
                    //This is the Map of employmentLineId and accumulated timebank in minutes map
                    Map<Long, Long> employmentLineAndTimebankMinutes = ctawtaAndAccumulatedTimebankWrapper.getEmploymentLineAndTimebankMinuteMap().getOrDefault(employment.getId(), new HashMap<>());
                    employmentLine.setAccumulatedTimebankMinutes(employmentLineAndTimebankMinutes.getOrDefault(employmentLine.getId(), 0l));
                });

                ctawtaAndAccumulatedTimebankWrapper.getWta().forEach(wta -> {
                    LocalDate wtaStartDate = wta.getStartDate();
                    LocalDate wtaEndDate = wta.getEndDate();
                    if (employment.getId().equals(wta.getEmploymentId()) && employmentLine.getEndDate() == null && (wtaEndDate == null || wtaEndDate.plusDays(1).isAfter(employmentLine.getStartDate())) ||
                            employmentLine.getEndDate() != null && (wtaStartDate.isBefore(employmentLine.getEndDate().plusDays(1))) && (wtaEndDate == null || wtaEndDate.isAfter(employmentLine.getStartDate()) || wtaEndDate.equals(employmentLine.getStartDate()))) {
                        employmentLine.setWorkingTimeAgreement(wta);
                    }
                });
                if (employment.getEndDate() != null && employmentLine.getEndDate() != null) {
                    employment.setEndDate(employmentLine.getEndDate());
                    employment.setEditable(!employmentLine.getEndDate().isBefore(DateUtils.getCurrentLocalDate()));
                } else {
                    employment.setEditable(true);
                }
            });
        });
        return new EmploymentAndPositionDTO(positionQueryResult, employmentQueryResults);

    }

    private EmploymentQueryResult getBasicDetails(EmploymentType employmentType, EmploymentDTO employmentDTO, Employment employment, EmploymentLineEmploymentTypeRelationShip relationShip,
                                                  Long parentOrganizationId, String parentOrganizationName, WTAResponseDTO wtaResponseDTO, EmploymentLine employmentLine) {

        Map<String, Object> reasonCode = null;
        if (Optional.ofNullable(employment.getReasonCode()).isPresent()) {
            reasonCode = new HashMap();
            reasonCode.put("name", employment.getReasonCode().getName());
            reasonCode.put("id", employment.getReasonCode().getId());
        }
        Map<String, Object> employmentTypes = new HashMap();
        employmentTypes.put("name", relationShip.getEmploymentType().getName());
        employmentTypes.put("id", employmentDTO.getEmploymentTypeId());
        employmentTypes.put("employmentTypeCategory", employmentDTO.getEmploymentTypeCategory());
        employmentTypes.put("editableAtEmployment", employmentType.isEditableAtEmployment());
        employmentTypes.put("weeklyMinutes", employmentType.getWeeklyMinutes());
        Map<String, Object> unitInfo = new HashMap<>();
        unitInfo.put("id", employment.getUnit().getId());
        unitInfo.put("name", employment.getUnit().getName());

        Map<String, Object> seniorityLevel;
        ObjectMapper objectMapper = new ObjectMapper();
        seniorityLevel = objectMapper.convertValue(employmentLine.getSeniorityLevel(), Map.class);

        seniorityLevel.put("functions", employmentDTO.getFunctions());
        seniorityLevel.put("payGrade", Optional.ofNullable(employmentLine.getSeniorityLevel().getPayGrade()).isPresent() ? employmentLine.getSeniorityLevel().getPayGrade() : payGradeGraphRepository.getPayGradeBySeniorityLevelId(employmentLine.getSeniorityLevel().getId()));
        EmploymentLinesQueryResult employmentLinesQueryResult = new EmploymentLinesQueryResult(employmentLine.getId(), employmentLine.getStartDate(), employmentLine.getEndDate()
                , employmentLine.getWorkingDaysInWeek(), employmentLine.getTotalWeeklyMinutes() / 60, employmentLine.getAvgDailyWorkingHours(), employmentLine.getFullTimeWeeklyMinutes(), 0D,
                employmentLine.getTotalWeeklyMinutes() % 60, employmentLine.getHourlyCost(), employmentTypes, seniorityLevel, employment.getId(), employment.getAccumulatedTimebankMinutes());
        ExpertiseDTO expertiseDTO = ObjectMapperUtils.copyPropertiesByMapper(employment.getExpertise(), ExpertiseDTO.class);
        ExpertiseLine expertiseLine = expertiseGraphRepository.getCurrentlyActiveExpertiseLineByDate(expertiseDTO.getId(), employment.getStartDate().toString());
        expertiseDTO.setNumberOfWorkingDaysInWeek(expertiseLine.getNumberOfWorkingDaysInWeek());
        expertiseDTO.setFullTimeWeeklyMinutes(expertiseLine.getFullTimeWeeklyMinutes());
        return new EmploymentQueryResult(employment.getExpertise(), employment.getStartDate(),
                employment.getEndDate(), employment.getId(), employment.getUnion(), employment.getLastWorkingDate()
                , wtaResponseDTO, employment.getUnit().getId(), parentOrganizationId, employment.isPublished(), reasonCode, unitInfo, employment.getEmploymentSubType(),
                Collections.singletonList(employmentLinesQueryResult), employmentDTO.getTaxDeductionPercentage(), employment.getAccumulatedTimebankMinutes(), employment.getAccumulatedTimebankDate());

    }

    protected EmploymentQueryResult getBasicDetails(Employment employment, WTAResponseDTO wtaResponseDTO, EmploymentLine employmentLine) {
        EmploymentQueryResult employmentQueryResult = employmentGraphRepository.getUnitIdAndParentUnitIdByEmploymentId(employment.getId());
        ExpertiseDTO expertiseDTO = ObjectMapperUtils.copyPropertiesByMapper(employment.getExpertise(), ExpertiseDTO.class);
        ExpertiseLine expertiseLine = expertiseGraphRepository.getCurrentlyActiveExpertiseLineByDate(expertiseDTO.getId(), employment.getStartDate().toString());
        expertiseDTO.setFullTimeWeeklyMinutes(expertiseLine.getFullTimeWeeklyMinutes());
        expertiseDTO.setNumberOfWorkingDaysInWeek(expertiseLine.getNumberOfWorkingDaysInWeek());
        return new EmploymentQueryResult(employmentQueryResult.getExpertise(), employment.getStartDate(), employment.getEndDate(), employment.getId(), employment.getUnion(),
                employment.getLastWorkingDate(), wtaResponseDTO, employmentQueryResult.getUnitId(), employment.isPublished(), employmentQueryResult.getParentUnitId());

    }

    public List<com.kairos.dto.activity.shift.StaffEmploymentDetails> getEmploymentDetails(List<Long> employmentIds, OrganizationBaseEntity organizationBaseEntity, Long countryId) {
        List<EmploymentQueryResult> employments = employmentGraphRepository.getEmploymentByIds(employmentIds);
        List<com.kairos.dto.activity.shift.StaffEmploymentDetails> employmentDetailsList = new ArrayList<>();
        employments.forEach(employment -> {
            com.kairos.dto.activity.shift.StaffEmploymentDetails employmentDetail = convertEmploymentObject(employment);
            List<EmploymentLinesQueryResult> employmentLinesQueryResults = employmentGraphRepository.findFunctionalHourlyCost(Arrays.asList(employment.getId()));
            Map<Long, BigDecimal> hourlyCostMap = employmentLinesQueryResults.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getHourlyCost, (previous, current) -> current));
            employmentDetail.setStaffId(employment.getStaffId());
            employmentDetail.setCountryId(countryId);
            employmentDetail.setUnitTimeZone(organizationBaseEntity.getTimeZone());
            EmploymentLinesQueryResult employmentLinesQueryResult = ObjectMapperUtils.copyPropertiesByMapper(employment.getEmploymentLines().get(0), EmploymentLinesQueryResult.class);
            BigDecimal hourlyCost = employmentLinesQueryResult.getStartDate().isLeapYear() ? hourlyCostMap.getOrDefault(employmentLinesQueryResult.getId(), new BigDecimal(0)).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.getOrDefault(employmentLinesQueryResult.getId(), new BigDecimal(0)).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
            employmentDetail.setHourlyCost(hourlyCost);
            employmentDetailsList.add(employmentDetail);
        });

        return employmentDetailsList;
    }

    // since we have employmentLine are on date so we are matching and might we wont have any active position line on date.
    public com.kairos.dto.activity.shift.StaffEmploymentDetails getEmploymentDetails(Long employmentId) {
        EmploymentQueryResult employment = employmentGraphRepository.getEmploymentById(employmentId);
        com.kairos.dto.activity.shift.StaffEmploymentDetails employmentDetails = null;
        if (employment != null) {
            List<ProtectedDaysOffSetting> protectedDaysOffSettings = expertiseGraphRepository.findProtectedDaysOffSettingByExpertiseId(employment.getExpertise().getId());
            employment.getExpertise().setProtectedDaysOffSettings(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(protectedDaysOffSettings, com.kairos.dto.activity.shift.ProtectedDaysOffSetting.class));
            employmentDetails = convertEmploymentObject(employment);
            List<EmploymentLinesQueryResult> employmentLinesQueryResults = employmentGraphRepository.findFunctionalHourlyCost(Arrays.asList(employmentId));
            Map<Long, BigDecimal> hourlyCostMap = employmentLinesQueryResults.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getHourlyCost, (previous, current) -> current));
            ExpertisePlannedTimeQueryResult expertisePlannedTimeQueryResult = expertiseEmploymentTypeRelationshipGraphRepository.findPlannedTimeByExpertise(employmentDetails.getExpertise().getId(),
                    employmentDetails.getEmploymentType().getId());
            if (Optional.ofNullable(expertisePlannedTimeQueryResult).isPresent()) {
                employmentDetails.setExcludedPlannedTime(expertisePlannedTimeQueryResult.getExcludedPlannedTime());
                employmentDetails.setIncludedPlannedTime(expertisePlannedTimeQueryResult.getIncludedPlannedTime());
            }
            employmentDetails.getEmploymentLines().forEach(employmentLinesDTO -> {
                if (hourlyCostMap.containsKey(employmentLinesDTO.getId())) {
                    BigDecimal hourlyCost = employmentLinesDTO.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLinesDTO.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLinesDTO.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
                    employmentLinesDTO.setHourlyCost(hourlyCost);
                }
            });
            EmploymentLinesQueryResult employmentLinesQueryResult = ObjectMapperUtils.copyPropertiesByMapper(employment.getEmploymentLines().get(0), EmploymentLinesQueryResult.class);
            BigDecimal hourlyCost = employmentLinesQueryResult.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLinesQueryResult.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLinesQueryResult.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
            employmentDetails.setHourlyCost(hourlyCost);
        }
        return employmentDetails;
    }

    private EmploymentDTO convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(TimeCareEmploymentDTO timeCareEmploymentDTO, Long expertiseId, Long staffId, Long employmentTypeId, BigInteger wtaId, BigInteger ctaId, Long unitId) {
        LocalDate startDate = DateUtils.getLocalDateFromString(timeCareEmploymentDTO.getStartDate());
        LocalDate endDate = null;
        if (!timeCareEmploymentDTO.getEndDate().equals("0001-01-01T00:00:00")) {
            endDate = DateUtils.getLocalDateFromString(timeCareEmploymentDTO.getEndDate());
        }
        return new EmploymentDTO(expertiseId, startDate, endDate, Integer.parseInt(timeCareEmploymentDTO.getWeeklyHours()), employmentTypeId, staffId, wtaId, ctaId, unitId, new Long(timeCareEmploymentDTO.getId()));
    }

    private boolean addEmploymentToUnitByExternalId(List<TimeCareEmploymentDTO> timeCareEmploymentDTOs, String unitExternalId, Long expertiseId) throws Exception {
        Unit unit = unitGraphRepository.findByExternalId(unitExternalId);
        if (unit == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENT_ORGANIZATION_EXTERNALID, unitExternalId);
        }
        Long countryId = countryService.getCountryIdByUnitId(new Long(unitExternalId));
        EmploymentType employmentType = employmentTypeGraphRepository.getOneEmploymentTypeByCountryId(countryId, false);

        Expertise expertise;
        if (expertiseId == null) {
            expertise = expertiseGraphRepository.getOneDefaultExpertiseByCountry(countryId);
        } else {
            expertise = expertiseGraphRepository.getExpertiesOfCountry(countryId, expertiseId);
        }
        if (expertise == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENT_EXPERTISE_NOTFOUND, expertiseId);
        }
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = workingTimeAgreementRestClient.getWTAByExpertise(expertise.getId());
        if (!CollectionUtils.isNotEmpty(ctawtaAndAccumulatedTimebankWrapper.getCta())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_CTA_NOTFOUND, unit.getId());
        }
        if (!CollectionUtils.isNotEmpty(ctawtaAndAccumulatedTimebankWrapper.getWta())) {
            exceptionService.dataNotFoundByIdException("message.wta.notFound", unit.getId());
        }
        for (TimeCareEmploymentDTO timeCareEmploymentDTO : timeCareEmploymentDTOs) {
            Staff staff = staffGraphRepository.findByExternalId(timeCareEmploymentDTO.getPersonID());
            if (staff == null) {
                exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_EXTERNALID_NOTEXIST, timeCareEmploymentDTO.getPersonID());
            }
            EmploymentDTO unitEmploymentPosition = convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(timeCareEmploymentDTO, expertise.getId(), staff.getId(), employmentType.getId(), ctawtaAndAccumulatedTimebankWrapper.getWta().get(0).getId(), ctawtaAndAccumulatedTimebankWrapper.getCta().get(0).getId(), unit.getId());
            createEmployment(unitEmploymentPosition,true);
        }
        return true;
    }

    public boolean importAllEmploymentsFromTimeCare(List<TimeCareEmploymentDTO> timeCareEmploymentsDTOs, Long expertiseId) throws Exception {
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


    public SeniorityLevel getSeniorityLevelByStaffAndExpertise(Long staffId, ExpertiseLine expertiseLine, Long expertiseId) {
        StaffExperienceInExpertiseDTO staffSelectedExpertise = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndExpertiseId(staffId, expertiseId);
        if (!Optional.ofNullable(staffSelectedExpertise).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_EXPERTISE_NOTASSIGNED);
        }
        Integer experienceInMonth = (int) ChronoUnit.MONTHS.between(DateUtils.asLocalDate(staffSelectedExpertise.getExpertiseStartDate()), LocalDate.now());
        LOGGER.info("user has current experience in months :{}", experienceInMonth);
        SeniorityLevel appliedSeniorityLevel = null;
        for (SeniorityLevel seniorityLevel : expertiseLine.getSeniorityLevel()) {
            if (seniorityLevel.getTo() == null) {
                // more than  is set if
                if (experienceInMonth >= seniorityLevel.getFrom() * 12) {
                    appliedSeniorityLevel = seniorityLevel;
                    break;
                }
            } else {
                // to and from is present
                LOGGER.info("user has current experience in months :{} ,{},{},{}", seniorityLevel.getFrom(), experienceInMonth, seniorityLevel.getTo(), experienceInMonth);

                if (seniorityLevel.getFrom() * 12 <= experienceInMonth && seniorityLevel.getTo() * 12 >= experienceInMonth) {
                    appliedSeniorityLevel = seniorityLevel;
                    break;
                }
            }
        }
        if (appliedSeniorityLevel == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_SENIORITYLEVEL_ID_NOTFOUND);
        }

        return appliedSeniorityLevel;
    }

    /**
     * @param unitId
     * @param staffId
     * @param expertiseId
     * @return
     * @Desc This method is used to veify the employment of staff while copy shift
     */
    public Long getEmploymentIdByStaffAndExpertise(Long unitId, Long staffId, Long expertiseId) {
        return employmentGraphRepository.getEmploymentIdByStaffAndExpertise(unitId, staffId, expertiseId);
    }

    public Map<Long, Long> getEmploymentExpertiseMap(Long unitId) {
        List<Map<Long, Long>> listOfMap = employmentGraphRepository.getMapOfEmploymentAndExpertiseId(unitId);
        Map<Long, Long> mapOfEmploymentAndExpertise = new HashMap<>(listOfMap.size());
        listOfMap.forEach(mapOfEmploymentAndExpertise::putAll);
        return mapOfEmploymentAndExpertise;
    }

    public StaffEmploymentUnitDataWrapper getStaffsEmployment(Long unitId, Long expertiseId, List<Long> staffIds) {
        Unit unit = unitGraphRepository.findOne(unitId);
        Long countryId = countryService.getCountryIdByUnitId(unitId);
        // TODO MIght We dont need these details I(vipul) will verify and remove
        List<StaffAdditionalInfoQueryResult> staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndStaffIds(unit.getId(), staffIds, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        List<com.kairos.dto.activity.shift.StaffEmploymentDetails> staffAdditionalInfoDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffAdditionalInfoQueryResult, com.kairos.dto.activity.shift.StaffEmploymentDetails.class);
        List<StaffEmploymentDetails> staffData = employmentGraphRepository.getStaffInfoByUnitIdAndStaffId(unitId, expertiseId, staffIds);
        Map<Long, StaffEmploymentDetails> staffEmploymentDetailsMap = staffData.stream().collect(Collectors.toMap(StaffEmploymentDetails::getStaffId, Function.identity()));
        List<String> invalidStaffs = staffAdditionalInfoQueryResult.stream().filter(staffAdditionalInfoQueryResult1 -> !staffEmploymentDetailsMap.containsKey(staffAdditionalInfoQueryResult1.getId())).map(StaffAdditionalInfoQueryResult::getName).collect(Collectors.toList());
        if (isCollectionNotEmpty(invalidStaffs)) {
            exceptionService.dataNotMatchedException(EMPLOYMENT_ABSENT, invalidStaffs);
        }
        Map<Long, StaffEmploymentDetails> employmentDetailsMap = staffData.stream().collect(Collectors.toMap(o -> o.getStaffId(), v -> v));
        List<ExpertisePlannedTimeQueryResult> expertisePlannedTimes = expertiseEmploymentTypeRelationshipGraphRepository.findPlannedTimeByExpertise(expertiseId);
        staffAdditionalInfoDTOS.forEach(currentData -> convertStaffEmploymentObject(employmentDetailsMap.get(currentData.getId()), currentData, expertisePlannedTimes));
        StaffEmploymentUnitDataWrapper staffEmploymentUnitDataWrapper = new StaffEmploymentUnitDataWrapper(staffAdditionalInfoDTOS);
        staffRetrievalService.setRequiredDataForShiftCreationInWrapper(staffEmploymentUnitDataWrapper, unit, countryId, expertiseId);
        return staffEmploymentUnitDataWrapper;
    }

    public List<StaffEmploymentDetails> getStaffIdAndEmploymentId(Long unitId, Long expertiseId, List<Long> staffId) {
        return staffGraphRepository.getStaffIdAndEmploymentId(unitId, expertiseId, staffId);
    }

    /**
     * @param unitId
     * @param staffId
     * @return
     */
    public List<EmploymentDTO> getEmploymentsByStaffId(Long unitId, Long staffId) {
        Object object = employmentGraphRepository.getEmploymentsByUnitIdAndStaffId(unitId, staffId);
        List<EmploymentDTO> employmentDTOList = new ArrayList<>();
        if (object instanceof String) {
            if (ORGANIZATION.equals(object)) {
                exceptionService.unitNotFoundException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
            } else if (STAFF.equals(object)) {
                exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, "Staff", staffId);
            }
        } else {
            List<Map<Object, Object>> employments = (List<Map<Object, Object>>) object;
            employmentDTOList = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(employments, EmploymentDTO.class);
        }
        return employmentDTOList;
    }

    private void setHourlyCost(EmploymentQueryResult employmentQueryResult) {
        List<EmploymentLinesQueryResult> hourlyCostPerLine = employmentGraphRepository.findFunctionalHourlyCost(Collections.singletonList(employmentQueryResult.getId()));
        Map<Long, BigDecimal> hourlyCostMap = hourlyCostPerLine.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getHourlyCost, (previous, current) -> current));
        employmentQueryResult.getEmploymentLines().forEach(employmentLine -> {
            BigDecimal hourlyCost = employmentLine.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
            employmentLine.setHourlyCost(hourlyCost);
        });
    }

    public Long getUnitByEmploymentId(Long employmentId) {
        Employment employment = employmentGraphRepository.findOne(employmentId);
        return isNotNull(employment) ? isNotNull(employment.getUnit()) ? employment.getUnit().getId() : null : null;
    }

    public List<EmploymentQueryResult> getMainEmploymentOfStaffs() {
        List<EmploymentQueryResult> employments =  employmentGraphRepository.getMainEmploymentOfStaffs(EmploymentSubType.MAIN);
        return setHourlyCostInEmployments(employments);
    }

    private List<EmploymentQueryResult> setHourlyCostInEmployments(List<EmploymentQueryResult> employments) {
        List<EmploymentLinesQueryResult> hourlyCostPerLine = employmentGraphRepository.findFunctionalHourlyCost(employments.stream().map(employmentQueryResult -> employmentQueryResult.getId()).collect(Collectors.toList()));
        Map<Long, BigDecimal> hourlyCostMap = hourlyCostPerLine.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getHourlyCost, (previous, current) -> current));
        employments = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(employments, EmploymentQueryResult.class);
        for (EmploymentQueryResult employmentQueryResult : employments) {
            for (EmploymentLinesQueryResult employmentLine : employmentQueryResult.getEmploymentLines()) {
                BigDecimal hourlyCost = employmentLine.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
                employmentLine.setHourlyCost(hourlyCost);
            }
        }
        return employments;
    }

    public List<EmploymentQueryResult> findEmploymentByUnitId(Long unitId) {
        List<EmploymentQueryResult> employments = employmentGraphRepository.findEmploymentByUnitId(unitId);
        return setHourlyCostInEmployments(employments);
    }

    public void setEndDateInEmploymentOfExpertise(ExpertiseDTO expertiseDTO) {
        List<Employment> employments = employmentGraphRepository.findAllEmploymentByExpertiseId(expertiseDTO.getId());
        employments.forEach(employment -> {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRetrievalService.getStaffEmploymentDataByEmploymentId(employment.getEndDate(), employment.getId(), employment.getUnit().getId(), null);
            employment.getEmploymentLines().forEach(employmentLine -> {
                if (employmentLine.getEndDate() != null && startDateIsEqualsOrBeforeEndDate(employmentLine.getEndDate(), expertiseDTO.getEndDate())) {
                    return;
                }
                if (employmentLine.getStartDate().isAfter(expertiseDTO.getEndDate())) {
                    employmentLine.setDeleted(true);
                }
                employmentLine.setEndDate(expertiseDTO.getEndDate());
            });
            employment.setEndDate(expertiseDTO.getEndDate());
            activityIntegrationService.deleteShiftsAfterEmploymentEndDate(employment.getUnit().getId(), expertiseDTO.getEndDate(), employment.getId(), staffAdditionalInfoDTO);
        });
        employmentGraphRepository.saveAll(employments);

    }

    public void triggerEmploymentLine(Long expertiseId, ExpertiseLine expertiseLine) {
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId, 2);
        List<Employment> employmentsList = employmentGraphRepository.findAllEmploymentByExpertiseId(expertise.getId());
        List<Employment> employments = new CopyOnWriteArrayList<>(employmentsList);
        DateTimeInterval expertiseLineInterval = new DateTimeInterval(expertiseLine.getStartDate(), expertiseLine.getEndDate());
        List<Employment> employmentList = new ArrayList<>();
        for (Employment employment : employments) {
            List<EmploymentLine> employmentLines = new CopyOnWriteArrayList<>(employment.getEmploymentLines());
            for (EmploymentLine employmentLine : employmentLines) {
                DateTimeInterval employmentLineInterval = new DateTimeInterval(employmentLine.getStartDate(), employmentLine.getEndDate());
                if (expertiseLineInterval.overlaps(employmentLineInterval)) {
                    if (employmentLine.getStartDate().isBefore(expertiseLine.getStartDate())) {
                        employmentLine.setEndDate(expertiseLine.getStartDate().minusDays(1));
                        EmploymentLine employmentLineToBeCreated = getEmploymentLine(expertiseLine, employment, expertiseLine.getStartDate(), employmentLine.getEndDate(), employmentLine, expertiseId);
                        employment.getEmploymentLines().add(employmentLineToBeCreated);
                        linkExistingRelations(employmentLineToBeCreated, employmentLine);
                    } else {
                        employmentLine.setFullTimeWeeklyMinutes(expertiseLine.getFullTimeWeeklyMinutes());
                        //employmentLine.setSeniorityLevel(getSeniorityLevelByStaffAndExpertise(employment.getStaff().getId(), expertiseLine, expertiseId));
                        employmentLine.setWorkingDaysInWeek(expertiseLine.getNumberOfWorkingDaysInWeek());
                    }
                }
            }
            employmentList.add(employment);
        }
        employmentGraphRepository.saveAll(employmentList);
    }

    public void linkExistingRelations(EmploymentLine employmentLineToBeCreated, EmploymentLine existingLine) {
        EmploymentLineEmploymentTypeRelationShip employmentLineEmploymentTypeRelationShip = employmentAndEmploymentTypeRelationShipGraphRepository.findByEmploymentLineId(existingLine.getId());
        EmploymentLineEmploymentTypeRelationShip relationShip = new EmploymentLineEmploymentTypeRelationShip(employmentLineToBeCreated, employmentLineEmploymentTypeRelationShip.getEmploymentType(), employmentLineEmploymentTypeRelationShip.getEmploymentTypeCategory());
        employmentAndEmploymentTypeRelationShipGraphRepository.save(relationShip);
        linkExistingFunctions(employmentLineToBeCreated, existingLine);
    }

    private void linkExistingFunctions(EmploymentLine employmentLineToBeCreated, EmploymentLine existingLine) {
        List<EmploymentLineFunctionRelationShip> employmentLineFunctionRelationShips = employmentLineFunctionRelationRepository.findAllByEmploymentLineId(existingLine.getId());
        List<EmploymentLineFunctionRelationShip> employmentLineFunctionRelationShipsList = new ArrayList<>();
        employmentLineFunctionRelationShips.forEach(employmentLineFunctionRelationShip -> {
            employmentLineFunctionRelationShipsList.add(new EmploymentLineFunctionRelationShip(employmentLineToBeCreated, employmentLineFunctionRelationShip.getFunction(), employmentLineFunctionRelationShip.getAmount()));
        });
        employmentLineFunctionRelationRepository.saveAll(employmentLineFunctionRelationShipsList);
    }

    private EmploymentLine getEmploymentLine(ExpertiseLine expertiseLine, Employment employment, LocalDate startDate, LocalDate endDate, EmploymentLine employmentLine, Long expertiseId) {
        return new EmploymentLine.EmploymentLineBuilder()
                .setSeniorityLevel(expertiseLine == null ? employmentLine.getSeniorityLevel() : getSeniorityLevelByStaffAndExpertise(employment.getStaff().getId(), expertiseLine, expertiseId))
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setTotalWeeklyMinutes(employmentLine.getTotalWeeklyMinutes())
                .setFullTimeWeeklyMinutes(expertiseLine == null ? employmentLine.getFullTimeWeeklyMinutes() : expertiseLine.getFullTimeWeeklyMinutes())
                .setWorkingDaysInWeek(expertiseLine == null ? employmentLine.getWorkingDaysInWeek() : expertiseLine.getNumberOfWorkingDaysInWeek())
                .setAvgDailyWorkingHours(employmentLine.getAvgDailyWorkingHours())
                .setHourlyCost(employmentLine.getHourlyCost())
                .build();
    }


    public void createEmploymentLineOnPayTableChanges(PayTable payTable) {
        List<Employment> employments = employmentGraphRepository.getAllEmploymentByLevel(payTable.getLevel().getId(), payTable.getStartDateMillis().toString(), payTable.getEndDateMillis() == null ? null : payTable.getEndDateMillis().toString());
        DateTimeInterval expertiseLineInterval = new DateTimeInterval(payTable.getStartDateMillis(), payTable.getEndDateMillis());
        List<Employment> employmentList = new ArrayList<>();
        for (Employment employment : employments) {
            List<EmploymentLine> employmentLines = new CopyOnWriteArrayList<>(employment.getEmploymentLines());
            for (EmploymentLine employmentLine : employmentLines) {
                DateTimeInterval employmentLineInterval = new DateTimeInterval(employmentLine.getStartDate(), employmentLine.getEndDate());
                if (expertiseLineInterval.overlaps(employmentLineInterval)) {
                    if (employmentLine.getStartDate().isBefore(payTable.getStartDateMillis())) {
                        employmentLine.setEndDate(payTable.getStartDateMillis().minusDays(1));
                        EmploymentLine employmentLineToBeCreated = getEmploymentLine(null, employment, payTable.getStartDateMillis(), payTable.getEndDateMillis(), employmentLine, null);
                        employment.getEmploymentLines().add(employmentLineToBeCreated);
                        linkExistingRelations(employmentLineToBeCreated, employmentLine);
                    } else {
                        employmentLine.setFullTimeWeeklyMinutes(employmentLine.getFullTimeWeeklyMinutes());
                        employmentLine.setSeniorityLevel(getSeniorityLevelByStaffAndExpertise(employment.getStaff().getId(), null, employment.getExpertise().getId()));
                        employmentLine.setWorkingDaysInWeek(employmentLine.getWorkingDaysInWeek());
                    }
                }
            }
            employmentList.add(employment);
        }
        employmentGraphRepository.saveAll(employmentList);
    }
}


