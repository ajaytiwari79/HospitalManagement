package com.kairos.service.unit_position;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta.CTAWTAAndAccumulatedTimebankWrapper;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.country.experties.FunctionsDTO;
import com.kairos.dto.user.staff.unit_position.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.staff.unit_position.EmploymentDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.query_results.ClientMinimumDTO;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.functions.FunctionWithAmountQueryResult;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.staff.TimeCareEmploymentDTO;
import com.kairos.persistence.model.staff.position.Position;
import com.kairos.persistence.model.staff.position.PositionQueryResult;
import com.kairos.persistence.model.staff.position.PositionReasonCodeQueryResult;
import com.kairos.persistence.model.staff.position.EmploymentAndPositionDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffAdditionalInfoQueryResult;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.ExpertisePlannedTimeQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.unit_position.*;
import com.kairos.persistence.model.user.unit_position.query_result.StaffEmploymentDetails;
import com.kairos.persistence.model.user.unit_position.query_result.EmploymentLinesQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.EmploymentQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseEmploymentTypeRelationshipGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.staff.PositionGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.unit_position.EmploymentAndEmploymentTypeRelationShipGraphRepository;
import com.kairos.persistence.repository.user.unit_position.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.unit_position.EmploymentLineFunctionRelationShipGraphRepository;
import com.kairos.rest_client.WorkingTimeAgreementRestClient;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.AsynchronousService;
import com.kairos.service.exception.ExceptionService;
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
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ApiConstants.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION;
import static com.kairos.service.unit_position.EmploymentUtility.convertStaffUnitPositionObject;
import static com.kairos.service.unit_position.EmploymentUtility.convertEmploymentObject;

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
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private StaffRetrievalService staffRetrievalService;
    @Inject
    private EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    private OrganizationService organizationService;
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



    public PositionWrapper createUnitPosition(Long id, String type, EmploymentDTO employmentDTO, Boolean createFromTimeCare, Boolean saveAsDraft) throws Exception {
        Organization organization = organizationService.getOrganizationDetail(employmentDTO.getUnitId(), type);
        Organization parentOrganization = organization.isParentOrganization() ? organization : organizationService.getParentOfOrganization(organization.getId());

        Position position = positionGraphRepository.findByStaffId(employmentDTO.getStaffId());
        if (!Optional.ofNullable(position).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.position.notFound", employmentDTO.getStaffId());
        }
        if (position.getStartDateMillis() != null) {
            if (employmentDTO.getStartDate().isBefore(DateUtils.getDateFromEpoch(position.getStartDateMillis()))) {
                exceptionService.actionNotPermittedException("message.staff.data.employmentdate.lessthan");
            }
        }

        if (!saveAsDraft) {
            List<Employment> oldEmployments = employmentGraphRepository.getStaffUnitPositionsByExpertise(organization.getId(), employmentDTO.getStaffId(), employmentDTO.getExpertiseId());
            validateUnitPositionWithExpertise(oldEmployments, employmentDTO);
        }


        EmploymentType employmentType = organizationGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(parentOrganization.getId(), employmentDTO.getEmploymentTypeId(), false);
        if (!Optional.ofNullable(employmentType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.position.employmenttype.notexist", employmentDTO.getEmploymentTypeId());
        }
        List<FunctionWithAmountQueryResult> functions = findAndValidateFunction(employmentDTO);
        Employment employment = new Employment(organization, employmentDTO.getStartDate(), employmentDTO.getTimeCareExternalId(), !saveAsDraft, employmentDTO.getTaxDeductionPercentage(), employmentDTO.getAccumulatedTimebankMinutes(), employmentDTO.getAccumulatedTimebankDate());

        preparePosition(employment, employmentDTO);
        if ((employmentDTO.isMainUnitPosition()) && positionService.eligibleForMainUnitPosition(employmentDTO, -1)) {
            employment.setMainUnitPosition(true);
        }
        employmentGraphRepository.save(employment);
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = assignCTAAndWTAToUnitPosition(employment, employmentDTO);
        Long reasonCodeId = updateEmploymentEndDate(parentOrganization, employmentDTO, position);


        EmploymentLineEmploymentTypeRelationShip relationShip = new EmploymentLineEmploymentTypeRelationShip(employment.getEmploymentLines().get(0), employmentType, employmentDTO.getEmploymentTypeCategory());
        employmentAndEmploymentTypeRelationShipGraphRepository.save(relationShip);
        linkFunctions(functions, employment.getEmploymentLines().get(0), false, employmentDTO.getFunctions());

        EmploymentQueryResult employmentQueryResult = getBasicDetails(employmentType, employmentDTO, employment, relationShip, parentOrganization.getId(), parentOrganization.getName(), ctawtaAndAccumulatedTimebankWrapper.getWta().get(0), employment.getEmploymentLines().get(0));
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
        functions.forEach(currentFunction -> {
            functionsEmploymentLines.add(new EmploymentLineFunctionRelationShip(employmentLine, currentFunction.getFunction(), functionAmountMap.get(currentFunction.getFunction().getId())));
        });
        employmentLineFunctionRelationRepository.saveAll(functionsEmploymentLines);
    }

    private CTAWTAAndAccumulatedTimebankWrapper assignCTAAndWTAToUnitPosition(Employment employment, EmploymentDTO employmentDTO) {
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = workingTimeAgreementRestClient.assignWTAToUnitPosition(employment.getId(), employmentDTO.getWtaId(), employmentDTO.getCtaId(), employmentDTO.getStartDate());
        if (ctawtaAndAccumulatedTimebankWrapper.getWta().isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.wta.id");
        }
        if (ctawtaAndAccumulatedTimebankWrapper.getCta().isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.cta.id");
        }
        return ctawtaAndAccumulatedTimebankWrapper;
    }

    private Long updateEmploymentEndDate(Organization organization, EmploymentDTO employmentDTO, Position position) throws Exception {
        Position position1 = positionService.updateEmploymentEndDate(organization, employmentDTO.getStaffId(), employmentDTO.getEndDate() != null ? DateUtils.getDateFromEpoch(employmentDTO.getEndDate()) : null, employmentDTO.getReasonCodeId(), employmentDTO.getAccessGroupId());
        return Optional.ofNullable(position.getReasonCode()).isPresent() ? position1.getReasonCode().getId() : null;

    }

    public boolean validateUnitPositionWithExpertise(List<Employment> employments, EmploymentDTO employmentDTO) {

        LocalDate unitPositionStartDate = employmentDTO.getStartDate();
        LocalDate unitPositionEndDate = employmentDTO.getEndDate();

        employments.forEach(unitPosition -> {
            // if null date is set
            if (unitPosition.getEndDate() != null) {
                if (unitPositionStartDate.isBefore(unitPosition.getEndDate()) && unitPositionStartDate.isAfter(unitPosition.getStartDate())) {
                    exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", unitPositionEndDate, unitPosition.getStartDate());
                }
                if (unitPositionEndDate != null) {
                    Interval previousInterval = new Interval(DateUtils.getDateFromEpoch(unitPosition.getStartDate()), DateUtils.getDateFromEpoch(unitPosition.getEndDate()));
                    Interval interval = new Interval(DateUtils.getDateFromEpoch(unitPositionStartDate), DateUtils.getDateFromEpoch(unitPositionEndDate));
                    LOGGER.info(" Interval of CURRENT UEP " + previousInterval + " Interval of going to create  " + interval);
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


    private List<FunctionWithAmountQueryResult> findAndValidateFunction(EmploymentDTO employmentDTO) {
        List<Long> funIds = employmentDTO.getFunctions().stream().map(FunctionsDTO::getId).collect(Collectors.toList());
        List<FunctionWithAmountQueryResult> functions = functionGraphRepository.getFunctionsByExpertiseAndSeniorityLevelAndIds
                (employmentDTO.getUnitId(), employmentDTO.getExpertiseId(), employmentDTO.getSeniorityLevelId(), employmentDTO.getStartDate().toString(),
                        funIds);

        if (functions.size() != employmentDTO.getFunctions().size()) {
            exceptionService.actionNotPermittedException("message.unitposition.functions.unable");
        }
        return functions;
    }

    private EmploymentLine createEmploymentLine(Employment oldEmployment, EmploymentLine oldEmploymentLine, EmploymentDTO employmentDTO) {
        if (Optional.ofNullable(employmentDTO.getEndDate()).isPresent() && employmentDTO.getStartDate().isAfter(employmentDTO.getEndDate())) {
            exceptionService.actionNotPermittedException("message.startdate.notlessthan.enddate");
        }
        if (Optional.ofNullable(employmentDTO.getLastWorkingDate()).isPresent() && employmentDTO.getStartDate().isAfter(employmentDTO.getLastWorkingDate())) {
            exceptionService.actionNotPermittedException("message.lastdate.notlessthan.enddate");
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
                exceptionService.actionNotPermittedException("message.region.enddate");
            }
            if (oldEmployment.getReasonCode() == null || !oldEmployment.getReasonCode().getId().equals(employmentDTO.getReasonCodeId())) {
                Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(employmentDTO.getReasonCodeId(), 0);
                if (!Optional.ofNullable(reasonCode).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound", employmentDTO.getReasonCodeId());
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
        Map<Long, BigDecimal> functionAmountMap = employmentDTO.getFunctions().stream().collect(Collectors.toMap(FunctionsDTO::getId, FunctionsDTO::getAmount));
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
            exceptionService.dataNotFoundByIdException("message.position.employmenttype.notexist", employmentDTO.getEmploymentTypeId());
        }

        EmploymentLineEmploymentTypeRelationShip relationShip = new EmploymentLineEmploymentTypeRelationShip(employmentLine, employmentType, employmentDTO.getEmploymentTypeCategory());
        employmentAndEmploymentTypeRelationShipGraphRepository.save(relationShip);
    }


    public PositionWrapper updateUnitPosition(long unitPositionId, EmploymentDTO employmentDTO, Long unitId, String type, Boolean saveAsDraft) throws Exception {

        Organization organization = organizationService.getOrganizationDetail(unitId, type);
        List<ClientMinimumDTO> clientMinimumDTO = clientGraphRepository.getCitizenListForThisContactPerson(employmentDTO.getStaffId());
        if (clientMinimumDTO.size() > 0) {
            return new PositionWrapper(clientMinimumDTO);
        }

        Employment oldEmployment = employmentGraphRepository.findOne(unitPositionId, 2);
        if (!Optional.ofNullable(oldEmployment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.positionid.notfound", unitPositionId);
        }
        EmploymentLine currentEmploymentLine = oldEmployment.getEmploymentLines().stream().filter(employmentLine -> employmentLine.getId().equals(employmentDTO.getEmploymentLineId()))
                .findFirst().orElse(null);
        if (currentEmploymentLine == null) {
            exceptionService.dataNotFoundByIdException("message.position_line.notfound", unitPositionId);
        }

        List<NameValuePair> param = Arrays.asList(new BasicNameValuePair("unitPositionId", unitPositionId + ""), new BasicNameValuePair("startDate", currentEmploymentLine.getStartDate().toString()));
        CTAWTAAndAccumulatedTimebankWrapper existingCtaWtaAndAccumulatedTimebankWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, APPLICABLE_CTA_WTA, param,
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>>() {
                });
        if (existingCtaWtaAndAccumulatedTimebankWrapper.getCta().isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.unitPosition.ctamissing", employmentDTO.getStartDate(), unitPositionId);
        }
        if(existingCtaWtaAndAccumulatedTimebankWrapper.getWta().isEmpty()){
            exceptionService.dataNotFoundByIdException("message.unitPosition.wtamissing", employmentDTO.getStartDate(), unitPositionId);
        }

        EmploymentType employmentType = employmentTypeGraphRepository.findById(employmentDTO.getEmploymentTypeId(), 0).orElse(null);
        if (employmentDTO.isMainUnitPosition() && positionService.eligibleForMainUnitPosition(employmentDTO, unitPositionId)) {
            oldEmployment.setMainUnitPosition(true);
        }

        EmploymentLineEmploymentTypeRelationShip employmentLineEmploymentTypeRelationShip = employmentGraphRepository.findEmploymentTypeByUnitPositionId(currentEmploymentLine.getId());
        PositionQueryResult positionQueryResult;
        EmploymentQueryResult employmentQueryResult;
        List<NameValuePair> changedParams = new ArrayList<>();
        oldEmployment.setPublished(!saveAsDraft);
        oldEmployment.setAccumulatedTimebankMinutes(employmentDTO.getAccumulatedTimebankMinutes());
        oldEmployment.setAccumulatedTimebankDate(employmentDTO.getAccumulatedTimebankDate());
        oldEmployment.setTaxDeductionPercentage(employmentDTO.getTaxDeductionPercentage());
        EmploymentLineChangeResultDTO changeResultDTO = calculativeValueChanged(employmentDTO, employmentLineEmploymentTypeRelationShip, currentEmploymentLine, existingCtaWtaAndAccumulatedTimebankWrapper, changedParams);
        /**
         *  Old unit position's calculative values is changed
         *  Old unit position is published so need to create a new  position line
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
                setEndDateToUnitPosition(oldEmployment, employmentDTO);
                employmentGraphRepository.save(oldEmployment);
                employmentQueryResult = getBasicDetails(employmentType, employmentDTO, oldEmployment, employmentLineEmploymentTypeRelationShip, organization.getId(), organization.getName(), null, currentEmploymentLine);
            } else {
                EmploymentLine employmentLine = createEmploymentLine(oldEmployment, currentEmploymentLine, employmentDTO);
                oldEmployment.getEmploymentLines().add(employmentLine);
                setEndDateToUnitPosition(oldEmployment, employmentDTO);
                employmentGraphRepository.save(oldEmployment);
                linkEmploymentLineWithEmploymentType(employmentLine, employmentDTO);
               // if (changeResultDTO.isFunctionsChanged()) {
                   linkFunctions(changeResultDTO.getFunctions(), employmentLine, false, employmentDTO.getFunctions());
                //}
                employmentQueryResult = getBasicDetails(employmentType, employmentDTO, oldEmployment, employmentLineEmploymentTypeRelationShip, organization.getId(), organization.getName(), null, employmentLine);
            }

            CTAWTAAndAccumulatedTimebankWrapper newCTAWTAAndAccumulatedTimebankWrapper = null;
            if (changeResultDTO.getCtaId() != null || changeResultDTO.getWtaId() != null) {
                changedParams.add(new BasicNameValuePair("startDate", employmentDTO.getStartDate() + ""));
                newCTAWTAAndAccumulatedTimebankWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.CREATE, APPLY_CTA_WTA, changedParams,
                        new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAAndAccumulatedTimebankWrapper>>() {
                        }, unitPositionId);
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
            if(newCTAWTAAndAccumulatedTimebankWrapper!=null && isCollectionNotEmpty(newCTAWTAAndAccumulatedTimebankWrapper.getCta())){
                updateTimeBank(newCTAWTAAndAccumulatedTimebankWrapper.getCta().get(0).getId(), unitPositionId, employmentQueryResult.getEmploymentLines().get(0).getStartDate(), employmentQueryResult.getEmploymentLines().get(0).getEndDate(), unitId);
            }

        }
        // calculative value is not changed it means only end date is updated.
        else {
            currentEmploymentLine.setEndDate(employmentDTO.getEndDate());
            setEndDateToUnitPosition(oldEmployment, employmentDTO);
            oldEmployment.setLastWorkingDate(employmentDTO.getLastWorkingDate());
            employmentGraphRepository.save(oldEmployment);
            employmentQueryResult = getBasicDetails(employmentType, employmentDTO, oldEmployment, employmentLineEmploymentTypeRelationShip, organization.getId(), organization.getName(), null, currentEmploymentLine);
            employmentQueryResult.getEmploymentLines().get(0).setWorkingTimeAgreement(existingCtaWtaAndAccumulatedTimebankWrapper.getWta().get(0));
            employmentQueryResult.getEmploymentLines().get(0).setCostTimeAgreement(existingCtaWtaAndAccumulatedTimebankWrapper.getCta().get(0));
        }


        Position position = positionService.updateEmploymentEndDate(oldEmployment.getUnit(), employmentDTO.getStaffId(),
                employmentDTO.getEndDate() != null ? DateUtils.getDateFromEpoch(employmentDTO.getEndDate()) : null, employmentDTO.getReasonCodeId(), employmentDTO.getAccessGroupId());
        Long reasonCodeId = Optional.ofNullable(position.getReasonCode()).isPresent() ? position.getReasonCode().getId() : null;
        positionQueryResult = new PositionQueryResult(position.getId(), position.getStartDateMillis(), position.getEndDateMillis(), reasonCodeId, position.getAccessGroupIdOnPositionEnd());
        // Deleting All shifts after position end date
        if (employmentDTO.getEndDate() != null) {
            activityIntegrationService.deleteShiftsAfterEmploymentEndDate(unitId, employmentDTO.getEndDate(), employmentDTO.getStaffId());
        }
        setHourlyCost(employmentQueryResult);
        //plannerSyncService.publishUnitPosition(unitId, oldEmployment, unitPositionEmploymentTypeRelationShip.getEmploymentType(), IntegrationOperation.UPDATE);
        return new PositionWrapper(employmentQueryResult, positionQueryResult);

    }


    /**
     * @param unitPositionId
     * @param employmentLineStartDate
     * @param employmentLineEndDate
     * @param unitId
     */
    private void updateTimeBank(BigInteger ctaId, long unitPositionId, LocalDate employmentLineStartDate, LocalDate employmentLineEndDate, Long unitId) {
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = staffRetrievalService.getStaffEmploymentDataByEmploymentIdAndStaffId(employmentLineStartDate, employmentGraphRepository.getStaffIdFromUnitPosition(unitPositionId), unitPositionId, unitId, ORGANIZATION, Collections.emptySet());
        activityIntegrationService.updateTimeBankOnUnitPositionUpdation(ctaId, unitPositionId, employmentLineStartDate, employmentLineEndDate, staffAdditionalInfoDTO);
    }

    private void setEndDateToUnitPosition(Employment employment, EmploymentDTO employmentDTO) {
        if (employmentDTO.getEndDate() == null) {
            employment.setEndDate(null);
        } else if (employmentDTO.getEndDate() != null && employment.getEndDate() == null) {
            employment.setEndDate(employmentDTO.getEndDate());
            setEndDateToCTAWTA(employment.getUnit().getId(), employment.getId(), employmentDTO.getEndDate());
        } else if (employmentDTO.getEndDate() != null && employment.getEndDate() != null && employment.getEndDate().isBefore(employmentDTO.getEndDate())) {
            employment.setEndDate(employmentDTO.getEndDate());
            setEndDateToCTAWTA(employment.getUnit().getId(), employment.getId(), employmentDTO.getEndDate());
        }


    }

    private void setEndDateToCTAWTA(Long unitId, Long unitPositionId, LocalDate endDate) {

        genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.UPDATE, APPLY_CTA_WTA_END_DATE,
                Collections.singletonList(new BasicNameValuePair("endDate", endDate + "")), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                }, unitPositionId);
    }

    private void updateCurrentEmploymentLine(EmploymentLine employmentLine, EmploymentDTO employmentDTO) {
        employmentLine.setAvgDailyWorkingHours(employmentDTO.getAvgDailyWorkingHours());
        employmentLine.setTotalWeeklyMinutes((employmentDTO.getTotalWeeklyHours() * 60) + employmentDTO.getTotalWeeklyMinutes());
        employmentLine.setHourlyCost(employmentDTO.getHourlyCost());
        employmentLine.setStartDate(employmentDTO.getStartDate());
        employmentLine.setEndDate(employmentDTO.getEndDate());
    }

    public PositionQueryResult removePosition(long positionId, Long unitId) throws Exception {
        Employment employment = employmentGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(employment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitposition.id.notexist", positionId);

        }
        employment.setDeleted(true);
        employmentGraphRepository.save(employment);

        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        Long staffId = employmentGraphRepository.getStaffIdFromUnitPosition(positionId);
        Position position = positionService.updateEmploymentEndDate(unit, staffId);
        //plannerSyncService.publishUnitPosition(unitId, employment, null, IntegrationOperation.DELETE);
        return new PositionQueryResult(position.getId(), position.getStartDateMillis(), position.getEndDateMillis());
    }


    public EmploymentQueryResult getUnitPosition(Long unitPositionId) {
        return employmentGraphRepository.findByUnitPositionId(unitPositionId);
    }

    @Async
    private CompletableFuture<Boolean> setDefaultData(EmploymentDTO employmentDTO, Employment employment) throws InterruptedException, ExecutionException {
        Callable<Expertise> expertiseCallable = () -> {
            Optional<Expertise> expertise = expertiseGraphRepository.findById(employmentDTO.getExpertiseId(), 1);
            if (!expertise.isPresent()) {
                exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", employmentDTO.getExpertiseId());
            }
            return expertise.get();
        };
        Future<Expertise> expertiseFuture = asynchronousService.executeAsynchronously(expertiseCallable);

        employment.setExpertise(expertiseFuture.get());
        if (Optional.ofNullable(employmentDTO.getUnionId()).isPresent()) {
            Callable<Organization> organizationCallable = () -> organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(employmentDTO.getUnionId());
            Future<Organization> organizationFuture = asynchronousService.executeAsynchronously(organizationCallable);
            if (!Optional.ofNullable(organizationFuture.get()).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.unitposition.union.notexist", employmentDTO.getUnionId());
            }
            employment.setUnion(organizationFuture.get());
        }

        Callable<Staff> staffCallable = () -> staffGraphRepository.findOne(employmentDTO.getStaffId());
        Future<Staff> staffFuture = asynchronousService.executeAsynchronously(staffCallable);
        if (!Optional.ofNullable(staffFuture.get()).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitposition.staff.notfound", employmentDTO.getStaffId());
        }
        employment.setExpertise(expertiseFuture.get());
        employment.setStaff(staffFuture.get());
        return CompletableFuture.completedFuture(true);
    }


    private Employment preparePosition(Employment employment, EmploymentDTO employmentDTO) throws Exception {
        CompletableFuture<Boolean> done = setDefaultData(employmentDTO, employment);
        CompletableFuture.allOf(done).join();
        // UEP can be created for past dates from time care

        employment.setStartDate(employmentDTO.getStartDate());
        if (Optional.ofNullable(employmentDTO.getEndDate()).isPresent()) {
            if (employmentDTO.getStartDate().isAfter(employmentDTO.getEndDate())) {
                exceptionService.actionNotPermittedException("message.startdate.notlessthan.enddate");
            }
            if (!Optional.ofNullable(employmentDTO.getReasonCodeId()).isPresent()) {
                exceptionService.actionNotPermittedException("message.region.enddate");
            }
            Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(employmentDTO.getReasonCodeId(), 0);
            if (!Optional.ofNullable(reasonCode).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound", employmentDTO.getReasonCodeId());
            }
            employment.setReasonCode(reasonCode.get());
            employment.setEndDate(employmentDTO.getEndDate());
        }

        if (Optional.ofNullable(employmentDTO.getLastWorkingDate()).isPresent()) {
            if (employmentDTO.getStartDate().isAfter(employmentDTO.getLastWorkingDate())) {
                exceptionService.actionNotPermittedException("message.lastdate.notlessthan.startdate");
            }
            employment.setLastWorkingDate(employmentDTO.getLastWorkingDate());
        }


        SeniorityLevel seniorityLevel = getSeniorityLevelByStaffAndExpertise(employment.getStaff().getId(), employment.getExpertise());

        if (!Optional.ofNullable(seniorityLevel).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.seniorityLevel.id.notfound", employmentDTO.getReasonCodeId());
        }

        EmploymentLine employmentLine = new EmploymentLine.EmploymentLineBuilder()
                .setSeniorityLevel(seniorityLevel)
                .setStartDate(employmentDTO.getStartDate())
                .setEndDate(employmentDTO.getEndDate())
                .setTotalWeeklyMinutes(employmentDTO.getTotalWeeklyMinutes() + (employmentDTO.getTotalWeeklyHours() * 60))
                .setFullTimeWeeklyMinutes(employment.getExpertise().getFullTimeWeeklyMinutes())
                .setWorkingDaysInWeek(employment.getExpertise().getNumberOfWorkingDaysInWeek())
                .setAvgDailyWorkingHours(employmentDTO.getAvgDailyWorkingHours())
                .setHourlyCost(employmentDTO.getHourlyCost())
                .build();
        employment.setEmploymentLines(Collections.singletonList(employmentLine));

        return employment;
    }

    /*
     * @author vipul
     * used to get all positions of organization n by organization and staff Id
     * */
    public EmploymentAndPositionDTO getEmploymentsOfStaff(long unitId, long staffId, boolean allOrganization) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitposition.staff.notfound", staffId);
        }

        User user = userGraphRepository.getUserByStaffId(staffId);

        PositionReasonCodeQueryResult employmentReasonCode = positionGraphRepository.findEmploymentreasonCodeByStaff(staffId);
        Position position = employmentReasonCode.getPosition();

        Long reasonCodeId = Optional.ofNullable(employmentReasonCode.getReasonCode()).isPresent() ? employmentReasonCode.getReasonCode().getId() : null;
        PositionQueryResult positionQueryResult = new PositionQueryResult(position.getId(), position.getStartDateMillis(), position.getEndDateMillis(), reasonCodeId, position.getAccessGroupIdOnPositionEnd());

        List<EmploymentQueryResult> employmentQueryResults = (allOrganization) ? employmentGraphRepository.getAllUnitPositionsByUser(user.getId()) : employmentGraphRepository.getAllUnitPositionsForCurrentOrganization(staffId, unitId);

        List<Long> unitPositionIds = employmentQueryResults.stream().map(EmploymentQueryResult::getId).collect(Collectors.toList());

        List<EmploymentLinesQueryResult> employmentLines = employmentGraphRepository.findAllEmploymentLines(unitPositionIds);
        List<EmploymentLinesQueryResult> hourlyCostPerLine = employmentGraphRepository.findFunctionalHourlyCost(unitPositionIds);
        Map<Long, BigDecimal> hourlyCostMap = hourlyCostPerLine.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getHourlyCost, (previous, current) -> current));
        Map<Long, List<EmploymentLinesQueryResult>> employmentLinesMap = employmentLines.stream().collect(Collectors.groupingBy(EmploymentLinesQueryResult::getUnitPositionId));
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = activityIntegrationService.getCTAWTAAndAccumulatedTimebankByUnitPosition(employmentLinesMap,unitId);
        employmentQueryResults.forEach(u -> {
            u.setEmploymentLines(employmentLinesMap.get(u.getId()));
            u.getEmploymentLines().forEach(employmentLine -> {
                BigDecimal hourlyCost = employmentLine.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLine.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
                employmentLine.setHourlyCost(hourlyCost);

                ctawtaAndAccumulatedTimebankWrapper.getCta().forEach(cta -> {
                    if ((employmentLine.getEndDate() == null && (cta.getEndDate() == null || cta.getEndDate().plusDays(1).isAfter(employmentLine.getStartDate())) ||
                            employmentLine.getEndDate() != null && (cta.getStartDate().isBefore(employmentLine.getEndDate().plusDays(1))) && (cta.getEndDate() == null || cta.getEndDate().isAfter(employmentLine.getStartDate()) || cta.getEndDate().equals(employmentLine.getStartDate())))) {
                        employmentLine.setCostTimeAgreement(cta);
                    }
                    //This is the Map of employmentLineId and accumulated timebank in minutes map
                    Map<Long,Long> employmentLineAndTimebankMinutes = ctawtaAndAccumulatedTimebankWrapper.getEmploymentLineAndTimebankMinuteMap().getOrDefault(u.getId(),new HashMap<>());
                    employmentLine.setAccumulatedTimebankMinutes(employmentLineAndTimebankMinutes.getOrDefault(employmentLine.getId(),0l));
                });

                ctawtaAndAccumulatedTimebankWrapper.getWta().forEach(wta -> {
                    LocalDate wtaStartDate = wta.getStartDate();
                    LocalDate wtaEndDate = wta.getEndDate();
                    if ((employmentLine.getEndDate() == null && (wtaEndDate == null || wtaEndDate.plusDays(1).isAfter(employmentLine.getStartDate())) ||
                            employmentLine.getEndDate() != null && (wtaStartDate.isBefore(employmentLine.getEndDate().plusDays(1))) && (wtaEndDate == null || wtaEndDate.isAfter(employmentLine.getStartDate()) || wtaEndDate.equals(employmentLine.getStartDate())))) {
                        employmentLine.setWorkingTimeAgreement(wta);
                    }
                });
                if (u.getEndDate() != null && employmentLine.getEndDate() != null) {
                    u.setEndDate(employmentLine.getEndDate());
                    u.setEditable(!employmentLine.getEndDate().isBefore(DateUtils.getCurrentLocalDate()));
                } else {
                    u.setEditable(true);
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
        employmentTypes.put("editableAtUnitPosition", employmentType.isEditableAtUnitPosition());
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

        return new EmploymentQueryResult(employment.getExpertise().retrieveBasicDetails(), employment.getStartDate(),
                employment.getEndDate(), employment.getId(), employment.getUnion(), employment.getLastWorkingDate()
                , wtaResponseDTO, employment.getUnit().getId(), parentOrganizationId, employment.isPublished(), reasonCode, unitInfo, employment.isMainUnitPosition(),
                Collections.singletonList(employmentLinesQueryResult), employmentDTO.getTaxDeductionPercentage(), employment.getAccumulatedTimebankMinutes(), employment.getAccumulatedTimebankDate());

    }

    protected EmploymentQueryResult getBasicDetails(Employment employment, WTAResponseDTO wtaResponseDTO, EmploymentLine employmentLine) {
        EmploymentQueryResult employmentQueryResult = employmentGraphRepository.getUnitIdAndParentUnitIdByUnitPositionId(employment.getId());
        return new EmploymentQueryResult(employment.getExpertise().retrieveBasicDetails(), employment.getStartDate(), employment.getEndDate(), employment.getId(), employment.getUnion(),
                employment.getLastWorkingDate(), wtaResponseDTO, employmentQueryResult.getUnitId(), employment.isPublished(), employmentQueryResult.getParentUnitId());

    }

    public List<com.kairos.dto.activity.shift.StaffEmploymentDetails> getEmploymentDetails(List<Long> unitPositionIds, Organization organization, Long countryId) {
        List<EmploymentQueryResult> unitPositions = employmentGraphRepository.getUnitPositionByIds(unitPositionIds);
        List<com.kairos.dto.activity.shift.StaffEmploymentDetails> unitPositionDetailsList = new ArrayList<>();
        unitPositions.forEach(unitPosition -> {
            com.kairos.dto.activity.shift.StaffEmploymentDetails unitPositionDetail = convertEmploymentObject(unitPosition);
            List<EmploymentLinesQueryResult> employmentLinesQueryResults = employmentGraphRepository.findFunctionalHourlyCost(Arrays.asList(unitPosition.getId()));
            Map<Long, BigDecimal> hourlyCostMap = employmentLinesQueryResults.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getHourlyCost, (previous, current) -> current));
            unitPositionDetail.setStaffId(unitPosition.getStaffId());
            unitPositionDetail.setCountryId(countryId);
            unitPositionDetail.setUnitTimeZone(organization.getTimeZone());
            EmploymentLinesQueryResult employmentLinesQueryResult = ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getEmploymentLines().get(0), EmploymentLinesQueryResult.class);
            BigDecimal hourlyCost = employmentLinesQueryResult.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLinesQueryResult.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLinesQueryResult.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
            unitPositionDetail.setHourlyCost(hourlyCost);
            unitPositionDetailsList.add(unitPositionDetail);
        });

        return unitPositionDetailsList;
    }

    // since we have employmentLine are on date so we are matching and might we wont have any active position line on date.
    public com.kairos.dto.activity.shift.StaffEmploymentDetails getEmploymentDetails(Long unitPositionId) {
        EmploymentQueryResult unitPosition = employmentGraphRepository.getUnitPositionById(unitPositionId);
        com.kairos.dto.activity.shift.StaffEmploymentDetails unitPositionDetails = null;
        if (unitPosition != null) {
            unitPositionDetails = convertEmploymentObject(unitPosition);
            List<EmploymentLinesQueryResult> employmentLinesQueryResults = employmentGraphRepository.findFunctionalHourlyCost(Arrays.asList(unitPositionId));
            Map<Long, BigDecimal> hourlyCostMap = employmentLinesQueryResults.stream().collect(Collectors.toMap(EmploymentLinesQueryResult::getId, EmploymentLinesQueryResult::getHourlyCost, (previous, current) -> current));
            ExpertisePlannedTimeQueryResult expertisePlannedTimeQueryResult = expertiseEmploymentTypeRelationshipGraphRepository.findPlannedTimeByExpertise(unitPositionDetails.getExpertise().getId(),
                    unitPositionDetails.getEmploymentType().getId());
            if (Optional.ofNullable(expertisePlannedTimeQueryResult).isPresent()) {
                unitPositionDetails.setExcludedPlannedTime(expertisePlannedTimeQueryResult.getExcludedPlannedTime());
                unitPositionDetails.setIncludedPlannedTime(expertisePlannedTimeQueryResult.getIncludedPlannedTime());
            }
            unitPositionDetails.getEmploymentLines().forEach(employmentLinesDTO -> {
                BigDecimal hourlyCost = employmentLinesDTO.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLinesDTO.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLinesDTO.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
                employmentLinesDTO.setHourlyCost(hourlyCost);
                });
            EmploymentLinesQueryResult employmentLinesQueryResult = ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getEmploymentLines().get(0), EmploymentLinesQueryResult.class);
            BigDecimal hourlyCost = employmentLinesQueryResult.getStartDate().isLeapYear() ? hourlyCostMap.get(employmentLinesQueryResult.getId()).divide(new BigDecimal(LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING) : hourlyCostMap.get(employmentLinesQueryResult.getId()).divide(new BigDecimal(NON_LEAP_YEAR).multiply(PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE), 2, BigDecimal.ROUND_CEILING);
            unitPositionDetails.setHourlyCost(hourlyCost);
        }
        return unitPositionDetails;
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
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = workingTimeAgreementRestClient.getWTAByExpertise(expertise.getId());
        if (!CollectionUtils.isNotEmpty(ctawtaAndAccumulatedTimebankWrapper.getCta())) {
            exceptionService.dataNotFoundByIdException("message.organization.cta.notfound", organization.getId());
        }
        if (!CollectionUtils.isNotEmpty(ctawtaAndAccumulatedTimebankWrapper.getWta())) {
            exceptionService.dataNotFoundByIdException("message.wta.notFound", organization.getId());
        }
        for (TimeCareEmploymentDTO timeCareEmploymentDTO : timeCareEmploymentDTOs) {
            Staff staff = staffGraphRepository.findByExternalId(timeCareEmploymentDTO.getPersonID());
            if (staff == null) {
                exceptionService.dataNotFoundByIdException("message.staff.externalid.notexist", timeCareEmploymentDTO.getPersonID());
            }
            EmploymentDTO unitEmploymentPosition = convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(timeCareEmploymentDTO, expertise.getId(), staff.getId(), employmentType.getId(), ctawtaAndAccumulatedTimebankWrapper.getWta().get(0).getId(), ctawtaAndAccumulatedTimebankWrapper.getCta().get(0).getId(), organization.getId());
            createUnitPosition(organization.getId(), "Organization", unitEmploymentPosition, true, true);
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


    public SeniorityLevel getSeniorityLevelByStaffAndExpertise(Long staffId, Expertise currentExpertise) {
        StaffExperienceInExpertiseDTO staffSelectedExpertise = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndExpertiseId(staffId, currentExpertise.getId());
        if (!Optional.ofNullable(staffSelectedExpertise).isPresent() || !Optional.ofNullable(currentExpertise).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.expertise.notassigned");
        }
        Integer experienceInMonth = (int) ChronoUnit.MONTHS.between(DateUtils.asLocalDate(staffSelectedExpertise.getExpertiseStartDate()), LocalDate.now());
        LOGGER.info("user has current experience in months :{}", experienceInMonth);
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
                LOGGER.info("user has current experience in months :{} ,{},{},{}", seniorityLevel.getFrom(), experienceInMonth, seniorityLevel.getTo(), experienceInMonth);

                if (seniorityLevel.getFrom() * 12 <= experienceInMonth && seniorityLevel.getTo() * 12 >= experienceInMonth) {
                    appliedSeniorityLevel = seniorityLevel;
                    break;
                }
            }
        }

        return appliedSeniorityLevel;
    }

    /**
     * @param unitId
     * @param staffId
     * @param expertiseId
     * @return
     * @Desc This method is used to veify the unit position of staff while copy shift
     */
    public Long getUnitPositionIdByStaffAndExpertise(Long unitId, Long staffId, Long expertiseId) {
        return employmentGraphRepository.getUnitPositionIdByStaffAndExpertise(unitId, staffId, expertiseId);
    }

    public Map<Long, Long> getUnitPositionExpertiseMap(Long unitId) {
        List<Map<Long, Long>> listOfMap = employmentGraphRepository.getMapOfUnitPositionAndExpertiseId(unitId);
        Map<Long, Long> mapOfUnitPositionAndExpertise = new HashMap<>(listOfMap.size());
        listOfMap.forEach(mapOfUnitPositionAndExpertise::putAll);
        return mapOfUnitPositionAndExpertise;
    }

    public StaffEmploymentUnitDataWrapper getStaffsUnitPosition(Long unitId, Long expertiseId, List<Long> staffIds) {
        Organization organization = organizationService.getOrganizationDetail(unitId, ORGANIZATION);
        Long countryId = organization.isParentOrganization() ? organization.getCountry().getId() : organizationGraphRepository.getCountryByParentOrganization(organization.getId()).getId();
        // TODO MIght We dont need these details I(vipul) will verify and remove
        List<StaffAdditionalInfoQueryResult> staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndStaffIds(organization.getId(), staffIds);
        List<com.kairos.dto.activity.shift.StaffEmploymentDetails> staffAdditionalInfoDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(staffAdditionalInfoQueryResult, com.kairos.dto.activity.shift.StaffEmploymentDetails.class);
        List<StaffEmploymentDetails> staffData = employmentGraphRepository.getStaffInfoByUnitIdAndStaffId(unitId, expertiseId, staffIds);
        Map<Long, StaffEmploymentDetails> staffUnitPositionDetailsMap = staffData.stream().collect(Collectors.toMap(StaffEmploymentDetails::getStaffId, Function.identity()));
        List<String> invalidStaffs = staffAdditionalInfoQueryResult.stream().filter(staffAdditionalInfoQueryResult1 -> !staffUnitPositionDetailsMap.containsKey(staffAdditionalInfoQueryResult1.getId())).map(StaffAdditionalInfoQueryResult::getName).collect(Collectors.toList());
        if (isCollectionNotEmpty(invalidStaffs)) {
            exceptionService.dataNotMatchedException("unit_position.absent", invalidStaffs);
        }
        Map<Long, StaffEmploymentDetails> unitPositionDetailsMap = staffData.stream().collect(Collectors.toMap(o -> o.getStaffId(), v -> v));
        List<ExpertisePlannedTimeQueryResult> expertisePlannedTimes = expertiseEmploymentTypeRelationshipGraphRepository.findPlannedTimeByExpertise(expertiseId);
        staffAdditionalInfoDTOS.forEach(currentData -> convertStaffUnitPositionObject(unitPositionDetailsMap.get(currentData.getId()), currentData, expertisePlannedTimes));
        StaffEmploymentUnitDataWrapper staffEmploymentUnitDataWrapper = new StaffEmploymentUnitDataWrapper(staffAdditionalInfoDTOS);
        staffRetrievalService.setRequiredDataForShiftCreationInWrapper(staffEmploymentUnitDataWrapper, organization, countryId, expertiseId);
        return staffEmploymentUnitDataWrapper;
    }

    public List<StaffEmploymentDetails> getStaffIdAndUnitPositionId(Long unitId, Long expertiseId, List<Long> staffId) {
        return staffGraphRepository.getStaffIdAndUnitPositionId(unitId, expertiseId, staffId, System.currentTimeMillis());
    }

    /**
     * @param unitId
     * @param staffId
     * @return
     */
    public List<EmploymentDTO> getUnitPositionsByStaffId(Long unitId, Long staffId) {
        Object object = employmentGraphRepository.getUnitPositionsByUnitIdAndStaffId(unitId, staffId);
        List<EmploymentDTO> employmentDTOList = new ArrayList<>();
        if (object instanceof String) {
            if (ORGANIZATION.equals(object)) {
                exceptionService.unitNotFoundException("message.organization.id.notFound", unitId);
            } else if (STAFF.equals(object)) {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "Staff", staffId);
            }
        } else {
            List<Map<Object, Object>> unitPositions = (List<Map<Object, Object>>) object;
            employmentDTOList = ObjectMapperUtils.copyPropertiesOfListByMapper(unitPositions, EmploymentDTO.class);
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

    public Long getUnitByUnitPositionId(Long unitPositionId){
        Employment employment = employmentGraphRepository.findOne(unitPositionId);
        return isNotNull(employment) ? isNotNull(employment.getUnit()) ? employment.getUnit().getId(): null:null;
    }
}


