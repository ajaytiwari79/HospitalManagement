package com.kairos.service.employment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.*;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTAWTAAndAccumulatedTimebankWrapper;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.dto.user.country.experties.FunctionsDTO;
import com.kairos.dto.user.staff.employment.EmploymentDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.functions.FunctionWithAmountQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.pay_table.PayTable;
import com.kairos.persistence.model.staff.TimeCareEmploymentDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffAdditionalInfoQueryResult;
import com.kairos.persistence.model.staff.position.EmploymentAndPositionDTO;
import com.kairos.persistence.model.staff.position.Position;
import com.kairos.persistence.model.staff.position.PositionQueryResult;
import com.kairos.persistence.model.user.employment.*;
import com.kairos.persistence.model.user.employment.query_result.EmploymentLinesQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.model.user.employment.query_result.StaffEmploymentDetails;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseLine;
import com.kairos.persistence.model.user.expertise.FunctionalPayment;
import com.kairos.persistence.model.user.expertise.response.ExpertisePlannedTimeQueryResult;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentAndEmploymentTypeRelationShipGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentLineFunctionRelationShipGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseEmploymentTypeRelationshipGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.staff.PositionGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.rest_client.WorkingTimeAgreementRestClient;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.country.CountryService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.initial_time_bank_log.InitialTimeBankLogService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.staff.PositionService;
import com.kairos.service.staff.StaffRetrievalService;
import com.kairos.wrapper.PositionWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
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

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private EmploymentDetailsValidatorService employmentDetailsValidatorService;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
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
    private UserGraphRepository userGraphRepository;
    @Inject
    private EmploymentAndEmploymentTypeRelationShipGraphRepository employmentAndEmploymentTypeRelationShipGraphRepository;
    @Inject
    private PayGradeGraphRepository payGradeGraphRepository;
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
    private EmploymentLineFunctionRelationShipGraphRepository employmentLineFunctionRelationRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private InitialTimeBankLogService initialTimeBankLogService;
    @Inject
    private SeniorityLevelService seniorityLevelService;
    @Inject
    private ExpertiseService expertiseService;


    public PositionWrapper createEmployment(EmploymentDTO employmentDTO, boolean saveAsDraft) throws Exception {
        Unit unit = unitGraphRepository.findOne(employmentDTO.getUnitId());
        if(isNull(unit)){
            exceptionService.actionNotPermittedException(UNIT_IS_MANDATORY);
        }
        Organization parentUnit = organizationService.fetchParentOrganization(unit.getId());
        Position position = positionGraphRepository.findByStaffId(employmentDTO.getStaffId());
        EmploymentType employmentType = validateDetails(employmentDTO, parentUnit, position,saveAsDraft);
        if (!saveAsDraft) {
            List<Employment> oldEmployments = employmentGraphRepository.getStaffEmploymentsByExpertise(unit.getId(), employmentDTO.getStaffId(), employmentDTO.getExpertiseId());
            employmentDetailsValidatorService.validateEmploymentWithExpertise(oldEmployments, employmentDTO);
        }
        List<FunctionWithAmountQueryResult> functions = employmentDetailsValidatorService.findAndValidateFunction(employmentDTO);
        Employment employment = new Employment(unit, employmentDTO.getStartDate(), employmentDTO.getTimeCareExternalId(), !saveAsDraft, employmentDTO.getTaxDeductionPercentage(), employmentDTO.getAccumulatedTimebankMinutes(), employmentDTO.getAccumulatedTimebankDate());;

        employmentDetailsValidatorService.prepareAndValidateEmployment(employment, employmentDTO);

        if (EmploymentSubType.MAIN.equals(employmentDTO.getEmploymentSubType()) && positionService.eligibleForMainEmployment(employmentDTO, -1)) {
            employment.setEmploymentSubType(EmploymentSubType.MAIN);
        }
        employmentGraphRepository.save(employment);
        initialTimeBankLogService.saveInitialTimeBankLog(employment.getId(), employment.getAccumulatedTimebankMinutes());
        assignCTAAndWTAToEmployment(employment, employmentDTO);
        BigInteger reasonCodeId = updateEmploymentEndDate(parentUnit, employmentDTO, position,saveAsDraft);
        List<EmploymentLineEmploymentTypeRelationShip> employmentLineEmploymentTypeRelationShips = new ArrayList<>();
        employment.getEmploymentLines().forEach(line -> employmentLineEmploymentTypeRelationShips.add(new EmploymentLineEmploymentTypeRelationShip(line, employmentType, employmentDTO.getEmploymentTypeCategory())));
        employmentAndEmploymentTypeRelationShipGraphRepository.saveAll(employmentLineEmploymentTypeRelationShips);
        linkFunctions(functions, employment.getEmploymentLines().get(0), false, employmentDTO.getFunctions());
        EmploymentAndPositionDTO employmentAndPositionDTO = getEmploymentsOfStaff(employmentDTO.getUnitId(), employmentDTO.getStaffId(), false);
        EmploymentQueryResult employmentQueryResult = employmentAndPositionDTO.getEmployments().stream().filter(e -> employment.getId().equals(e.getId())).findAny().orElse(new EmploymentQueryResult());
        setHourlyCost(employmentQueryResult);
        return new PositionWrapper(employmentQueryResult, new PositionQueryResult(position.getId(), position.getStartDateMillis(), position.getEndDateMillis(), reasonCodeId, position.getAccessGroupIdOnPositionEnd()));
    }

    private EmploymentType validateDetails(EmploymentDTO employmentDTO, Organization parentUnit, Position position,Boolean saveAsDraft) {
        if (!Optional.ofNullable(position).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_EMPLOYMENT_NOTFOUND, employmentDTO.getStaffId());
        }
        if (position.getStartDateMillis() != null) {
            if (employmentDTO.getStartDate().isBefore(DateUtils.getDateFromEpoch(position.getStartDateMillis()))) {
                exceptionService.actionNotPermittedException(MESSAGE_STAFF_DATA_EMPLOYMENTDATE_LESSTHAN);
            }
        }
        if(position.getEndDateMillis() !=null){
            if (employmentDTO.getStartDate().isAfter(DateUtils.getDateFromEpoch(position.getEndDateMillis()))) {
                exceptionService.actionNotPermittedException(MESSAGE_STAFF_DATA_EMPLOYMENTDATE_GREATERTHAN);
            }
        }
        EmploymentType employmentType = unitGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(parentUnit.getId(), employmentDTO.getEmploymentTypeId(), false);
        if (!Optional.ofNullable(employmentType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_POSITION_EMPLOYMENTTYPE_NOTEXIST, employmentDTO.getEmploymentTypeId());
        }
        if(position.getEndDateMillis()!=null){
            if(!saveAsDraft) {
                employmentDTO.setEndDate(getDateFromEpoch(position.getEndDateMillis()));
            }
            if(employmentDTO.getAccessGroupId()==null){
                employmentDTO.setAccessGroupId(position.getAccessGroupIdOnPositionEnd());
            }
            if(employmentDTO.getReasonCodeId()==null){
                if(isNotNull(position.getReasonCodeId())) {
                    employmentDTO.setReasonCodeId(position.getReasonCodeId());
                }
            }
        }
        return employmentType;
    }

    private void linkFunctions(List<FunctionWithAmountQueryResult> functions, EmploymentLine employmentLine, boolean update, Set<FunctionsDTO> functionDTOS) {
        if (update) {
            // need to delete the current applied functions
            employmentGraphRepository.removeAllAppliedFunctionOnEmploymentLines(employmentLine.getId());
        }
        Map<Long, BigDecimal> functionAmountMap = new HashMap<>();
        functionDTOS.forEach(functionsDTO -> functionAmountMap.put(functionsDTO.getId(), functionsDTO.getAmount()));
        List<EmploymentLineFunctionRelationShip> functionsEmploymentLines = new ArrayList<>(functions.size());
        functions.forEach(currentFunction -> functionsEmploymentLines.add(new EmploymentLineFunctionRelationShip(employmentLine, currentFunction.getFunction(), functionAmountMap.get(currentFunction.getFunction().getId()))));
        employmentLineFunctionRelationRepository.saveAll(functionsEmploymentLines);
    }

    private void assignCTAAndWTAToEmployment(Employment employment, EmploymentDTO employmentDTO) {
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = workingTimeAgreementRestClient.assignWTAToEmployment(employment.getId(), employmentDTO.getWtaId(), employmentDTO.getCtaId(), employmentDTO.getStartDate());
        if (ctawtaAndAccumulatedTimebankWrapper.getWta().isEmpty()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_WTA_ID);
        }
        if (ctawtaAndAccumulatedTimebankWrapper.getCta().isEmpty()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_CTA_ID);
        }
    }

    private BigInteger updateEmploymentEndDate(Organization organization, EmploymentDTO employmentDTO, Position position,Boolean saveAsDraft) throws Exception {
        Long endDateMillis = saveAsDraft?position.getEndDateMillis():employmentDTO.getEndDate() != null ? DateUtils.getDateFromEpoch(employmentDTO.getEndDate()) : null;
        Position position1 = positionService.updatePositionEndDate(organization, employmentDTO.getStaffId(), endDateMillis, employmentDTO.getReasonCodeId(), employmentDTO.getAccessGroupId(),saveAsDraft);
        return Optional.ofNullable(position.getReasonCodeId()).isPresent() ? position1.getReasonCodeId() : null;

    }

    private EmploymentLine createEmploymentLine(Employment oldEmployment, EmploymentLine oldEmploymentLine, EmploymentDTO employmentDTO) {
        if (Optional.ofNullable(employmentDTO.getEndDate()).isPresent() && employmentDTO.getStartDate().isAfter(employmentDTO.getEndDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_STARTDATE_NOTLESSTHAN_ENDDATE);
        }
        if (Optional.ofNullable(employmentDTO.getLastWorkingDate()).isPresent() && employmentDTO.getStartDate().isAfter(employmentDTO.getLastWorkingDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_LASTDATE_NOTLESSTHAN_ENDDATE);
        }
        oldEmployment.setLastWorkingDate(employmentDTO.getLastWorkingDate());
        EmploymentLine employmentLine = EmploymentLine.builder().avgDailyWorkingHours(employmentDTO.getAvgDailyWorkingHours())
                .totalWeeklyMinutes((employmentDTO.getTotalWeeklyHours() * 60) + employmentDTO.getTotalWeeklyMinutes())
                .hourlyCost(employmentDTO.getHourlyCost())
                .startDate(employmentDTO.getStartDate())
                .fullTimeWeeklyMinutes(oldEmploymentLine.getFullTimeWeeklyMinutes()).workingDaysInWeek(oldEmploymentLine.getWorkingDaysInWeek()).endDate(employmentDTO.getEndDate()).seniorityLevel(oldEmploymentLine.getSeniorityLevel()).build();
        oldEmploymentLine.setEndDate(employmentDTO.getStartDate().minusDays(1));
        if (Optional.ofNullable(employmentDTO.getEndDate()).isPresent()) {
            if (!Optional.ofNullable(employmentDTO.getReasonCodeId()).isPresent()) {
                exceptionService.actionNotPermittedException(MESSAGE_REGION_ENDDATE);
            }
            if (oldEmployment.getReasonCodeId() == null || !oldEmployment.getReasonCodeId().equals(employmentDTO.getReasonCodeId())) {
                oldEmployment.setReasonCodeId(employmentDTO.getReasonCodeId());
            }
        }
        return employmentLine;
    }


    private EmploymentLineChangeResultDTO calculativeValueChanged(EmploymentDTO employmentDTO, EmploymentLineEmploymentTypeRelationShip oldEmploymentLineEmploymentTypeRelationShip, EmploymentLine employmentLine, CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper, List<NameValuePair> changedParams,Boolean saveAsDraft) {
        EmploymentLineChangeResultDTO changeResultDTO = new EmploymentLineChangeResultDTO(false);
        setCTAAndWTADetails(employmentDTO, ctawtaAndAccumulatedTimebankWrapper, changedParams, changeResultDTO,saveAsDraft);
        checkWorkingHoursIfChnaged(employmentDTO, employmentLine, changeResultDTO, saveAsDraft);
        checkEmploymentTypeIfChanged(employmentDTO, oldEmploymentLineEmploymentTypeRelationShip, changeResultDTO);
        List<FunctionWithAmountQueryResult> newAppliedFunctions = employmentDetailsValidatorService.findAndValidateFunction(employmentDTO);
        List<FunctionWithAmountQueryResult> olderAppliesFunctions = employmentGraphRepository.findAllAppliedFunctionOnEmploymentLines(employmentDTO.getEmploymentLineId());
        Map<Long, BigDecimal> functionAmountMap = employmentDTO.getFunctions().stream().collect(Collectors.toMap(FunctionsDTO::getId, FunctionsDTO::getAmount, (first, second) -> first));
        if (newAppliedFunctions.size() != olderAppliesFunctions.size()) {
            changeResultDTO.setCalculativeChanged(true);
            changeResultDTO.setFunctionsChanged(true);
        } else {
            olderAppliesFunctions.forEach(currentOldFunction -> {
                AtomicBoolean currentMatched = new AtomicBoolean(false);
                newAppliedFunctions.forEach(newCurrentFunction -> {
                    if (currentOldFunction.getFunction().getId().equals(newCurrentFunction.getFunction().getId()) && functionAmountMap.get(currentOldFunction.getFunction().getId()).equals(newCurrentFunction.getAmount())) {
                        currentMatched.getAndSet(true);
                        return; // break inner loop
                    }
                });
                if (!currentMatched.get()) {
                    changeResultDTO.setCalculativeChanged(true);
                    changeResultDTO.setFunctionsChanged(true);
                    return; // this is used to break from outer loop.
                }
            });
        }
        changeResultDTO.setFunctions(newAppliedFunctions);
        return changeResultDTO;
    }

    private void checkWorkingHoursIfChnaged(EmploymentDTO employmentDTO, EmploymentLine employmentLine, EmploymentLineChangeResultDTO changeResultDTO, boolean saveAsDraft) {
        if (employmentLine.getAvgDailyWorkingHours() != employmentDTO.getAvgDailyWorkingHours() || employmentLine.getTotalWeeklyMinutes() != (employmentDTO.getTotalWeeklyMinutes() + (employmentDTO.getTotalWeeklyHours() * 60))) {
            changeResultDTO.setCalculativeChanged(true);
            if(!saveAsDraft) {
                activityIntegrationService.createNewWTALine(employmentDTO.getUnitId(), employmentDTO.getId(), asDate(employmentDTO.getStartDate()));
            }
        }
    }

    private void checkEmploymentTypeIfChanged(EmploymentDTO employmentDTO, EmploymentLineEmploymentTypeRelationShip oldEmploymentLineEmploymentTypeRelationShip, EmploymentLineChangeResultDTO changeResultDTO) {
        if (!oldEmploymentLineEmploymentTypeRelationShip.getEmploymentType().getId().equals(employmentDTO.getEmploymentTypeId()) || !oldEmploymentLineEmploymentTypeRelationShip.getEmploymentTypeCategory().equals(employmentDTO.getEmploymentTypeCategory())) {
            changeResultDTO.setCalculativeChanged(true);
            changeResultDTO.setEmploymentTypeChanged(true);
        }
    }

    private void setCTAAndWTADetails(EmploymentDTO employmentDTO, CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper, List<NameValuePair> changedParams, EmploymentLineChangeResultDTO changeResultDTO,Boolean saveAsDraft) {
        if (!ctawtaAndAccumulatedTimebankWrapper.getCtaIds().contains(employmentDTO.getCtaId()) ||saveAsDraft ) {
            // CTA is changed
            changeResultDTO.setCtaId(employmentDTO.getCtaId());
            changeResultDTO.setOldctaId(ctawtaAndAccumulatedTimebankWrapper.getCta().get(0).getId());
            changedParams.add(new BasicNameValuePair("ctaId", employmentDTO.getCtaId() + ""));
            changedParams.add(new BasicNameValuePair("oldctaId", ctawtaAndAccumulatedTimebankWrapper.getCta().get(0).getId() + ""));
            changeResultDTO.setCalculativeChanged(true);
        }
        if (!ctawtaAndAccumulatedTimebankWrapper.getWtaIds().contains(employmentDTO.getWtaId())|| saveAsDraft) {
            // wta is changed
            changeResultDTO.setWtaId(employmentDTO.getWtaId());
            changeResultDTO.setOldwtaId(ctawtaAndAccumulatedTimebankWrapper.getWta().get(0).getId());
            changeResultDTO.setCalculativeChanged(true);
            changedParams.add(new BasicNameValuePair("wtaId", employmentDTO.getWtaId() + ""));
            changedParams.add(new BasicNameValuePair("oldwtaId", ctawtaAndAccumulatedTimebankWrapper.getWta().get(0).getId() + ""));
        }
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
        Employment oldEmployment = employmentGraphRepository.findById(employmentId, 2).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_POSITIONID_NOTFOUND, employmentId)));
        EmploymentLine currentEmploymentLine = oldEmployment.getEmploymentLines().stream().filter(employmentLine -> employmentLine.getId().equals(employmentDTO.getEmploymentLineId())).findFirst().orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_POSITION_LINE_NOTFOUND, employmentId)));
        CTAWTAAndAccumulatedTimebankWrapper existingCtaWtaAndAccumulatedTimebankWrapper = getCtawtaAndAccumulatedTimebankWrapper(employmentId, employmentDTO, unitId, currentEmploymentLine);
        EmploymentType employmentType = employmentTypeGraphRepository.findById(employmentDTO.getEmploymentTypeId(), 0).orElse(null);
        setEmploymentSubType(employmentId, employmentDTO, oldEmployment);
        EmploymentLineEmploymentTypeRelationShip employmentLineEmploymentTypeRelationShip = employmentGraphRepository.findEmploymentTypeByEmploymentId(currentEmploymentLine.getId());
        EmploymentQueryResult employmentQueryResult;
        List<NameValuePair> changedParams = new ArrayList<>();
        setDataInExistingEmployment(employmentDTO, saveAsDraft, oldEmployment);
        EmploymentLineChangeResultDTO changeResultDTO = calculativeValueChanged(employmentDTO, employmentLineEmploymentTypeRelationShip, currentEmploymentLine, existingCtaWtaAndAccumulatedTimebankWrapper, changedParams,saveAsDraft);
        if (changeResultDTO.isCalculativeChanged()) {
            employmentQueryResult = getEmploymentQueryResult(employmentId, employmentDTO, unitId, unit, oldEmployment, currentEmploymentLine, existingCtaWtaAndAccumulatedTimebankWrapper, employmentType, employmentLineEmploymentTypeRelationShip, changedParams, changeResultDTO);
        } else {
            currentEmploymentLine.setEndDate(employmentDTO.getEndDate());
            if (saveAsDraft) {
                currentEmploymentLine.setStartDate(employmentDTO.getStartDate());
                oldEmployment.setStartDate(employmentDTO.getStartDate());
            }
            setEndDateToEmployment(oldEmployment, employmentDTO);
            oldEmployment.setLastWorkingDate(employmentDTO.getLastWorkingDate());
            employmentQueryResult = getBasicDetails(employmentType, employmentDTO, oldEmployment, employmentLineEmploymentTypeRelationShip, unit.getId(), null, currentEmploymentLine);
            employmentGraphRepository.save(oldEmployment);
        }
        PositionQueryResult positionQueryResult = updateEmploymentData(employmentId, employmentDTO, unitId, oldEmployment, employmentQueryResult,existingCtaWtaAndAccumulatedTimebankWrapper);
        return new PositionWrapper(employmentQueryResult, positionQueryResult);
    }

    private void setDataInExistingEmployment(EmploymentDTO employmentDTO, boolean saveAsDraft, Employment oldEmployment) {
        oldEmployment.setPublished(!saveAsDraft);
        oldEmployment.setAccumulatedTimebankMinutes(employmentDTO.getAccumulatedTimebankMinutes());
        oldEmployment.setAccumulatedTimebankDate(employmentDTO.getAccumulatedTimebankDate());
        oldEmployment.setTaxDeductionPercentage(employmentDTO.getTaxDeductionPercentage());
    }

    private PositionQueryResult updateEmploymentData(long employmentId, EmploymentDTO employmentDTO, Long unitId, Employment oldEmployment, EmploymentQueryResult employmentQueryResult,CTAWTAAndAccumulatedTimebankWrapper existingCtaWtaAndAccumulatedTimebankWrapper) throws Exception {
        Organization organization = organizationService.fetchParentOrganization(unitId);
        initialTimeBankLogService.saveInitialTimeBankLog(oldEmployment.getId(), oldEmployment.getAccumulatedTimebankMinutes());
        Position position = positionService.updatePositionEndDate(organization, employmentDTO.getStaffId(),
                employmentDTO.getEndDate() != null ? DateUtils.getDateFromEpoch(employmentDTO.getEndDate()) : null, employmentDTO.getReasonCodeId(), employmentDTO.getAccessGroupId(),false);
        PositionQueryResult positionQueryResult = new PositionQueryResult(position.getId(), position.getStartDateMillis(), position.getEndDateMillis(), position.getReasonCodeId(), position.getAccessGroupIdOnPositionEnd());
        // Deleting All shifts after position end date
        if (employmentDTO.getEndDate() != null) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRetrievalService.getStaffEmploymentDataByEmploymentId(employmentDTO.getEndDate(), employmentId, employmentDTO.getUnitId(), null,null);
            activityIntegrationService.deleteShiftsAfterEmploymentEndDate(unitId, employmentDTO.getEndDate(), employmentDTO.getStaffId(), staffAdditionalInfoDTO);
        }
        setHourlyCost(employmentQueryResult);
        return positionQueryResult;
    }

    private EmploymentQueryResult getEmploymentQueryResult(long employmentId, EmploymentDTO employmentDTO, Long unitId, Unit unit, Employment oldEmployment, EmploymentLine currentEmploymentLine, CTAWTAAndAccumulatedTimebankWrapper existingCtaWtaAndAccumulatedTimebankWrapper, EmploymentType employmentType, EmploymentLineEmploymentTypeRelationShip employmentLineEmploymentTypeRelationShip, List<NameValuePair> changedParams, EmploymentLineChangeResultDTO changeResultDTO) {
        EmploymentQueryResult employmentQueryResult;
        if (currentEmploymentLine.getStartDate().isEqual(employmentDTO.getStartDate())||!employmentDTO.isPublished()) {
            //both are of same start Date only set  data
            updateCurrentEmploymentLine(currentEmploymentLine, employmentDTO);
            if (changeResultDTO.isEmploymentTypeChanged()) {
                employmentAndEmploymentTypeRelationShipGraphRepository.updateEmploymentTypeInCurrentEmploymentLine(currentEmploymentLine.getId(), employmentDTO.getEmploymentTypeId(), employmentDTO.getEmploymentTypeCategory());
            }
            linkFunctions(changeResultDTO.getFunctions(), currentEmploymentLine, true, employmentDTO.getFunctions());
            setEndDateToEmployment(oldEmployment, employmentDTO);
            employmentGraphRepository.save(oldEmployment);
            employmentQueryResult = getBasicDetails(employmentType, employmentDTO, oldEmployment, employmentLineEmploymentTypeRelationShip, unit.getId(), null, currentEmploymentLine);
        } else {
            EmploymentLine employmentLine = createEmploymentLine(oldEmployment, currentEmploymentLine, employmentDTO);
            oldEmployment.getEmploymentLines().add(employmentLine);
            setEndDateToEmployment(oldEmployment, employmentDTO);
            employmentGraphRepository.save(oldEmployment);
            linkEmploymentLineWithEmploymentType(employmentLine, employmentDTO);
            linkFunctions(changeResultDTO.getFunctions(), employmentLine, false, employmentDTO.getFunctions());
            employmentQueryResult = getBasicDetails(employmentType, employmentDTO, oldEmployment, employmentLineEmploymentTypeRelationShip, unit.getId(), null, employmentLine);
        }
        CTAWTAAndAccumulatedTimebankWrapper newCTAWTAAndAccumulatedTimebankWrapper = null;
        if (changeResultDTO.getCtaId() != null || changeResultDTO.getWtaId() != null) {
            changedParams.add(new BasicNameValuePair("startDate", employmentDTO.getStartDate() + ""));
            newCTAWTAAndAccumulatedTimebankWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.CREATE, APPLY_CTA_WTA, changedParams,new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>>() {}, employmentId); }
        setCTAAndWTADetails(employmentId, unitId, existingCtaWtaAndAccumulatedTimebankWrapper, employmentQueryResult, changeResultDTO, newCTAWTAAndAccumulatedTimebankWrapper);
        return employmentQueryResult;
    }

    private void setEmploymentSubType(long employmentId, EmploymentDTO employmentDTO, Employment oldEmployment) {
        if (EmploymentSubType.MAIN.equals(employmentDTO.getEmploymentSubType()) && positionService.eligibleForMainEmployment(employmentDTO, employmentId)) {
            oldEmployment.setEmploymentSubType(EmploymentSubType.MAIN);
        }
        if((EmploymentSubType.SECONDARY.equals(employmentDTO.getEmploymentSubType())&& isNull(oldEmployment.getEmploymentSubType())||(EmploymentSubType.SECONDARY.equals(employmentDTO.getEmploymentSubType())&&EmploymentSubType.MAIN.equals(oldEmployment.getEmploymentSubType())))){
            oldEmployment.setEmploymentSubType(EmploymentSubType.SECONDARY);
        }
    }

    private CTAWTAAndAccumulatedTimebankWrapper getCtawtaAndAccumulatedTimebankWrapper(long employmentId, EmploymentDTO employmentDTO, Long unitId, EmploymentLine currentEmploymentLine) {
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
        return existingCtaWtaAndAccumulatedTimebankWrapper;
    }

    private void setCTAAndWTADetails(long employmentId, Long unitId, CTAWTAAndAccumulatedTimebankWrapper existingCtaWtaAndAccumulatedTimebankWrapper, EmploymentQueryResult employmentQueryResult, EmploymentLineChangeResultDTO changeResultDTO, CTAWTAAndAccumulatedTimebankWrapper newCTAWTAAndAccumulatedTimebankWrapper) {
        if (changeResultDTO.getWtaId() != null) {
            employmentQueryResult.getEmploymentLines().get(0).setWorkingTimeAgreement(newCTAWTAAndAccumulatedTimebankWrapper.getWta().get(0));
        } else {
            employmentQueryResult.getEmploymentLines().get(0).setWorkingTimeAgreement(existingCtaWtaAndAccumulatedTimebankWrapper.getWta().get(existingCtaWtaAndAccumulatedTimebankWrapper.getWta().size()-1));
        }
        if (changeResultDTO.getCtaId() != null) {
            employmentQueryResult.getEmploymentLines().get(0).setCostTimeAgreement(newCTAWTAAndAccumulatedTimebankWrapper.getCta().get(0));
        } else {
            employmentQueryResult.getEmploymentLines().get(0).setCostTimeAgreement(existingCtaWtaAndAccumulatedTimebankWrapper.getCta().get(existingCtaWtaAndAccumulatedTimebankWrapper.getCta().size()-1));
        }
        if (newCTAWTAAndAccumulatedTimebankWrapper != null && isCollectionNotEmpty(newCTAWTAAndAccumulatedTimebankWrapper.getCta())) {
            updateTimeBank(newCTAWTAAndAccumulatedTimebankWrapper.getCta().get(0).getId(), employmentId, employmentQueryResult.getEmploymentLines().get(0).getStartDate(), employmentQueryResult.getEmploymentLines().get(0).getEndDate(), unitId);
        }
    }

    private void updateTimeBank(BigInteger ctaId, long employmentId, LocalDate employmentLineStartDate, LocalDate employmentLineEndDate, Long unitId) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRetrievalService.getStaffEmploymentDataByEmploymentIdAndStaffId(employmentLineStartDate, employmentGraphRepository.getStaffIdFromEmployment(employmentId), employmentId, unitId, Collections.emptySet());
        activityIntegrationService.updateTimeBankOnEmploymentUpdation(ctaId, employmentId, employmentLineStartDate, employmentLineEndDate, staffAdditionalInfoDTO);
    }

    private void setEndDateToEmployment(Employment employment, EmploymentDTO employmentDTO) {
        if (employmentDTO.getEndDate() == null) {
            employment.setEndDate(null);
            return;
        }
        if(!employmentDTO.getEndDate().equals(employment.getEndDate())){
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
        Position position = positionGraphRepository.findPositionOfStaff(staffId);
        PositionQueryResult positionQueryResult = new PositionQueryResult(position.getId(), position.getStartDateMillis(), position.getEndDateMillis(), position.getReasonCodeId(), position.getAccessGroupIdOnPositionEnd());
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
            setEmploymentLineDetails(hourlyCostMap, ctawtaAndAccumulatedTimebankWrapper, employment);
            employment.getExpertise().setName(TranslationUtil.getName(employment.getExpertise().getTranslations(),employment.getExpertise().getName()));
            employment.getExpertise().setDescription(TranslationUtil.getDescription(employment.getExpertise().getTranslations(),employment.getExpertise().getDescription()));
        });
        return new EmploymentAndPositionDTO(positionQueryResult, employmentQueryResults);
    }

    private void setEmploymentLineDetails(Map<Long, BigDecimal> hourlyCostMap, CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper, EmploymentQueryResult employment) {
        employment.getEmploymentLines().forEach(employmentLine -> {
            BigDecimal hourlyCost = employmentLine.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
            employmentLine.setHourlyCost(hourlyCost);
            ctawtaAndAccumulatedTimebankWrapper.getCta().forEach(cta -> {
                validateAndSetCTA(employment, employmentLine, cta);
                //This is the Map of employmentLineId and accumulated timebank in minutes map
                Map<Long, Long> employmentLineAndTimebankMinutes = ctawtaAndAccumulatedTimebankWrapper.getEmploymentLineAndTimebankMinuteMap().getOrDefault(employment.getId(), new HashMap<>());
                employmentLine.setAccumulatedTimebankMinutes(employmentLineAndTimebankMinutes.getOrDefault(employmentLine.getId(), 0l));
            });
            ctawtaAndAccumulatedTimebankWrapper.getWta().forEach(wta -> {
                LocalDate wtaStartDate = wta.getStartDate();
                LocalDate wtaEndDate = wta.getEndDate();
                if (employment.getId().equals(wta.getEmploymentId()) && employmentLine.isValid(wtaStartDate,wtaEndDate)) {
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
    }

    private void validateAndSetCTA(EmploymentQueryResult employment, EmploymentLinesQueryResult employmentLine, CTAResponseDTO cta) {
        if (employment.getId().equals(cta.getEmploymentId()) && employmentLine.isValid(cta.getStartDate(),cta.getEndDate())) {
            employmentLine.setCostTimeAgreement(cta);
        }
    }

    private EmploymentQueryResult getBasicDetails(EmploymentType employmentType, EmploymentDTO employmentDTO, Employment employment, EmploymentLineEmploymentTypeRelationShip relationShip,
                                                  Long parentOrganizationId, WTAResponseDTO wtaResponseDTO, EmploymentLine employmentLine) {
        Map<String, Object> reasonCode = null;
        if (Optional.ofNullable(employment.getReasonCodeId()).isPresent()) {
            reasonCode = new HashMap();
            reasonCode.put("name", "");
            reasonCode.put("id", employment.getReasonCodeId());
        }
        Map<String, Object> employmentTypes = setEmploymentTypeDetails(employmentType, employmentDTO, relationShip);
        Map<String, Object> unitInfo = setUnitInfo(employment);
        Map<String, Object> seniorityLevel;
        ObjectMapper objectMapper = new ObjectMapper();
        seniorityLevel = objectMapper.convertValue(employmentLine.getSeniorityLevel(), Map.class);

        seniorityLevel.put("functions", employmentDTO.getFunctions());
        seniorityLevel.put("payGrade", Optional.ofNullable(employmentLine.getSeniorityLevel().getPayGrade()).isPresent() ? employmentLine.getSeniorityLevel().getPayGrade() : payGradeGraphRepository.getPayGradeBySeniorityLevelId(employmentLine.getSeniorityLevel().getId()));
        EmploymentLinesQueryResult employmentLinesQueryResult = new EmploymentLinesQueryResult(employmentLine.getId(), employmentLine.getStartDate(), employmentLine.getEndDate()
                , employmentLine.getWorkingDaysInWeek(), employmentLine.getTotalWeeklyMinutes() / 60, employmentLine.getAvgDailyWorkingHours(), employmentLine.getFullTimeWeeklyMinutes(), 0D,
                employmentLine.getTotalWeeklyMinutes() % 60, employmentLine.getHourlyCost(), employmentTypes, seniorityLevel, employment.getId(), employment.getAccumulatedTimebankMinutes());
        Expertise expertise =expertiseGraphRepository.findOne(employmentDTO.getExpertiseId());
        if(!employment.getExpertise().getId().equals(expertise.getId())) {
           expertiseGraphRepository.updateExpertiseByExpertiseIdAndEmploymentId(employmentDTO.getExpertiseId(),employment.getId());
           employment.setExpertise(expertise);
        }
        ExpertiseDTO expertiseDTO = ObjectMapperUtils.copyPropertiesByMapper(employment.getExpertise(), ExpertiseDTO.class);
        ExpertiseLine expertiseLine = expertiseGraphRepository.getCurrentlyActiveExpertiseLineByDate(expertiseDTO.getId(), employment.getStartDate().toString());
        expertiseDTO.setNumberOfWorkingDaysInWeek(expertiseLine.getNumberOfWorkingDaysInWeek());
        expertiseDTO.setFullTimeWeeklyMinutes(expertiseLine.getFullTimeWeeklyMinutes());
        return new EmploymentQueryResult(employment.getExpertise(), employment.getStartDate(),
                employment.getEndDate(), employment.getId(), employment.getUnion(), employment.getLastWorkingDate()
                , wtaResponseDTO, employment.getUnit().getId(), parentOrganizationId, employment.isPublished(), employment.getReasonCodeId(), unitInfo, employment.getEmploymentSubType(),
                Collections.singletonList(employmentLinesQueryResult), employmentDTO.getTaxDeductionPercentage(), employment.getAccumulatedTimebankMinutes(), employment.getAccumulatedTimebankDate());
    }

    private Map<String, Object> setUnitInfo(Employment employment) {
        Map<String, Object> unitInfo = new HashMap<>();
        unitInfo.put("id", employment.getUnit().getId());
        unitInfo.put("name", employment.getUnit().getName());
        return unitInfo;
    }

    private Map<String, Object> setEmploymentTypeDetails(EmploymentType employmentType, EmploymentDTO employmentDTO, EmploymentLineEmploymentTypeRelationShip relationShip) {
        Map<String, Object> employmentTypes = new HashMap();
        employmentTypes.put("name", employmentType.getName());
        employmentTypes.put("id", employmentDTO.getEmploymentTypeId());
        employmentTypes.put("employmentTypeCategory", employmentDTO.getEmploymentTypeCategory());
        employmentTypes.put("editableAtEmployment", employmentType.isEditableAtEmployment());
        employmentTypes.put("weeklyMinutes", employmentType.getWeeklyMinutes());
        return employmentTypes;
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
    public List<com.kairos.dto.activity.shift.StaffEmploymentDetails> getEmploymentDetails(Collection<Long> employmentIds,boolean includePlannedTimeDetails) {
        List<EmploymentQueryResult> employments = employmentGraphRepository.getEmploymentDetailsByIds(employmentIds);
        List<com.kairos.dto.activity.shift.StaffEmploymentDetails> employmentDetails = new ArrayList<>();
        List<EmploymentLinesQueryResult> employmentLinesQueryResults = employmentGraphRepository.findFunctionalHourlyCost(employmentIds);
        Map<Long, BigDecimal> hourlyCostMap = employmentLinesQueryResults.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getHourlyCost, (previous, current) -> current));
        for (EmploymentQueryResult employment : employments) {
            if (employment != null) {
                com.kairos.dto.activity.shift.StaffEmploymentDetails employmentDetail = convertEmploymentObject(employment);
                if(includePlannedTimeDetails){
                    ExpertisePlannedTimeQueryResult expertisePlannedTimeQueryResult = expertiseEmploymentTypeRelationshipGraphRepository.findPlannedTimeByExpertise(employmentDetail.getExpertise().getId(),
                            employmentDetail.getEmploymentType().getId());
                    if (Optional.ofNullable(expertisePlannedTimeQueryResult).isPresent()) {
                        employmentDetail.setExcludedPlannedTime(expertisePlannedTimeQueryResult.getExcludedPlannedTime());
                        employmentDetail.setIncludedPlannedTime(expertisePlannedTimeQueryResult.getIncludedPlannedTime());
                    }
                }
                employmentDetail.getEmploymentLines().forEach(employmentLinesDTO -> {
                    if (hourlyCostMap.containsKey(employmentLinesDTO.getId())) {
                        BigDecimal hourlyCost = employmentLinesDTO.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLinesDTO.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLinesDTO.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
                        employmentLinesDTO.setHourlyCost(hourlyCost);
                    }
                });
                EmploymentLinesQueryResult employmentLinesQueryResult = ObjectMapperUtils.copyPropertiesByMapper(employment.getEmploymentLines().get(0), EmploymentLinesQueryResult.class);
                BigDecimal hourlyCost = employmentLinesQueryResult.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLinesQueryResult.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLinesQueryResult.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
                employmentDetail.setHourlyCost(hourlyCost);
                employmentDetails.add(employmentDetail);
            }
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

        Expertise expertise= expertiseService.getExpertise(expertiseId, countryId);
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
            createEmployment(unitEmploymentPosition, true);
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
        List<StaffAdditionalInfoQueryResult> staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndStaffIds(unit.getId(), staffIds, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        List<com.kairos.dto.activity.shift.StaffEmploymentDetails> staffAdditionalInfoDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(staffAdditionalInfoQueryResult, com.kairos.dto.activity.shift.StaffEmploymentDetails.class);
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
        User user=userGraphRepository.findOne(UserContext.getUserDetails().getId());
        staffEmploymentUnitDataWrapper.setUnitWiseAccessRole(user.getUnitWiseAccessRole());
        staffRetrievalService.setRequiredDataForShiftCreationInWrapper(staffEmploymentUnitDataWrapper, unit, countryId, expertiseId);
        return staffEmploymentUnitDataWrapper;
    }

    public List<StaffEmploymentDetails> getStaffIdAndEmploymentId(Long unitId, Long expertiseId, List<Long> staffId) {
        return staffGraphRepository.getStaffIdAndEmploymentId(unitId, expertiseId, staffId);
    }


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
            employmentDTOList = ObjectMapperUtils.copyCollectionPropertiesByMapper(employments, EmploymentDTO.class);
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
        employments = ObjectMapperUtils.copyCollectionPropertiesByMapper(employments, EmploymentQueryResult.class);
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
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRetrievalService.getStaffEmploymentDataByEmploymentId(employment.getEndDate(), employment.getId(), employment.getUnit().getId(), null,null);
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
        List<Long> employmentLineIds = employmentsList.stream().flatMap(e->e.getEmploymentLines().stream().map(el-> el.getId())).collect(Collectors.toList());
        List<EmploymentLine> employmentLineList = employmentGraphRepository.getEmploymentLineByIds(employmentLineIds);
        Map<Long,EmploymentLine> employmentLineMap = employmentLineList.stream().collect(Collectors.toMap(k->k.getId(),v->v));
        List<Employment> employments = new CopyOnWriteArrayList<>(employmentsList);
        DateTimeInterval expertiseLineInterval = new DateTimeInterval(expertiseLine.getStartDate(), expertiseLine.getEndDate());
        List<Employment> employmentList = new ArrayList<>();
        updateAllEmployments(expertiseId, expertiseLine, employmentLineMap, employments, expertiseLineInterval, employmentList);
        employmentGraphRepository.saveAll(employmentList);
    }

    private void updateAllEmployments(Long expertiseId, ExpertiseLine expertiseLine, Map<Long, EmploymentLine> employmentLineMap, List<Employment> employments, DateTimeInterval expertiseLineInterval, List<Employment> employmentList) {
        for (Employment employment : employments) {
            List<EmploymentLine> employmentLines = new CopyOnWriteArrayList<>(employment.getEmploymentLines());
            employmentLines.sort(Comparator.comparing(EmploymentLine::getStartDate));
            ListIterator iterator=employmentLines.listIterator();
            while (iterator.hasNext()){
                updateEmploymentLines(expertiseId, expertiseLine, employmentLineMap, expertiseLineInterval, employment, iterator);
            }
            employmentList.add(employment);
        }
    }

    private void updateEmploymentLines(Long expertiseId, ExpertiseLine expertiseLine, Map<Long, EmploymentLine> employmentLineMap, DateTimeInterval expertiseLineInterval, Employment employment, ListIterator iterator) {
        EmploymentLine employmentLine=(EmploymentLine) iterator.next();
        DateTimeInterval employmentLineInterval = new DateTimeInterval(employmentLine.getStartDate(), employmentLine.getEndDate());
        if (expertiseLineInterval.overlaps(employmentLineInterval)) {
            if (employmentLine.getStartDate().isBefore(expertiseLine.getStartDate())) {
                employmentLine.setEndDate(expertiseLine.getStartDate().minusDays(1));
                LocalDate endDate=expertiseLine.getEndDate();
                if(isNull(endDate) && iterator.hasNext()){
                    endDate= ((EmploymentLine) iterator.next()).getStartDate().minusDays(1);
                    iterator.previous();
                }
                EmploymentLine employmentLineToBeCreated = getEmploymentLine(expertiseLine, employment, expertiseLine.getStartDate(), endDate, employmentLine, expertiseId);
                employment.getEmploymentLines().add(employmentLineToBeCreated);
                linkExistingRelations(employmentLineToBeCreated, employmentLine);
            } else {
                setDetailsInEmploymentLine(employment, employmentLine, expertiseLine.getFullTimeWeeklyMinutes(), expertiseLine, expertiseId, expertiseLine.getNumberOfWorkingDaysInWeek());
            }
        }else{
            employmentLine.setSeniorityLevel(employmentLineMap.get(employmentLine.getId()).getSeniorityLevel());
        }
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
        return EmploymentLine.builder()
                .seniorityLevel(expertiseLine == null ? employmentLine.getSeniorityLevel() : seniorityLevelService.getSeniorityLevelByStaffAndExpertise(employment.getStaff().getId(), expertiseLine, expertiseId))
                .startDate(startDate)
                .endDate(endDate)
                .totalWeeklyMinutes(employmentLine.getTotalWeeklyMinutes())
                .fullTimeWeeklyMinutes(expertiseLine == null ? employmentLine.getFullTimeWeeklyMinutes() : expertiseLine.getFullTimeWeeklyMinutes())
                .workingDaysInWeek(expertiseLine == null ? employmentLine.getWorkingDaysInWeek() : expertiseLine.getNumberOfWorkingDaysInWeek())
                .avgDailyWorkingHours(employmentLine.getAvgDailyWorkingHours())
                .hourlyCost(employmentLine.getHourlyCost())
                .build();
    }

    public void createEmploymentLineOnPayTableChanges(PayTable payTable) {
        List<Employment> employments = employmentGraphRepository.getAllEmploymentByLevel(payTable.getLevel().getId(), payTable.getStartDateMillis().toString(), payTable.getEndDateMillis() == null ? null : payTable.getEndDateMillis().toString());
        createEmploymentLineOnChanges(employments, payTable.getStartDateMillis(), payTable.getEndDateMillis());
    }

    public void createEmploymentLineOnFunctionTableChanges(FunctionalPayment functionalPayment) {
        List<Employment> employments = employmentGraphRepository.getAllEmploymentByFunctionId(functionalPayment.getId(), functionalPayment.getStartDate().toString(), isNull(functionalPayment.getEndDate()) ? null : functionalPayment.getEndDate().toString());
        createEmploymentLineOnChanges(employments, functionalPayment.getStartDate(), functionalPayment.getEndDate());
    }

    public void createEmploymentLineOnChanges(List<Employment> employments, LocalDate startDate, LocalDate endDate) {
        DateTimeInterval expertiseLineInterval = new DateTimeInterval(startDate, endDate);
        List<Employment> employmentList = new ArrayList<>();
        for (Employment employment : employments) {
            List<EmploymentLine> employmentLines = new CopyOnWriteArrayList<>(employment.getEmploymentLines());
            employmentLines.sort(Comparator.comparing(EmploymentLine::getStartDate));
            ListIterator iterator=employmentLines.listIterator();
            while (iterator.hasNext()){
                updateEmploymentLines(startDate, endDate, expertiseLineInterval, employment, iterator);
            }
            employmentList.add(employment);
        }
        employmentGraphRepository.saveAll(employmentList);
    }

    private void updateEmploymentLines(LocalDate startDate, LocalDate endDate, DateTimeInterval expertiseLineInterval, Employment employment, ListIterator iterator) {
        EmploymentLine employmentLine=(EmploymentLine) iterator.next();
        DateTimeInterval employmentLineInterval = new DateTimeInterval(employmentLine.getStartDate(), employmentLine.getEndDate());
        if (expertiseLineInterval.overlaps(employmentLineInterval)) {
            if (employmentLine.getStartDate().isBefore(startDate)) {
                employmentLine.setEndDate(startDate.minusDays(1));
                LocalDate localEndDate = endDate;
                if(endDate == null && iterator.hasNext()){
                    localEndDate= ((EmploymentLine) iterator.next()).getStartDate().minusDays(1);
                    iterator.previous();
                }
                EmploymentLine employmentLineToBeCreated = getEmploymentLine(null, employment, startDate, localEndDate, employmentLine, null);
                employment.getEmploymentLines().add(employmentLineToBeCreated);
                linkExistingRelations(employmentLineToBeCreated, employmentLine);
            } else {
                setDetailsInEmploymentLine(employment, employmentLine, employmentLine.getFullTimeWeeklyMinutes(), null, employment.getExpertise().getId(), employmentLine.getWorkingDaysInWeek());
            }
        }
    }

    private void setDetailsInEmploymentLine(Employment employment, EmploymentLine employmentLine, int fullTimeWeeklyMinutes, ExpertiseLine expertiseLine, Long id, int workingDaysInWeek) {
        employmentLine.setFullTimeWeeklyMinutes(fullTimeWeeklyMinutes);
        if(isNotNull(expertiseLine)) {
            employmentLine.setSeniorityLevel(seniorityLevelService.getSeniorityLevelByStaffAndExpertise(employment.getStaff().getId(), expertiseLine, id));
        }
        employmentLine.setWorkingDaysInWeek(workingDaysInWeek);
    }
}
