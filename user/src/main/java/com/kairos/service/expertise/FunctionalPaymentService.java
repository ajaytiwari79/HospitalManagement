package com.kairos.service.expertise;

import com.google.common.base.Functions;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.experties.FunctionalPaymentMatrixDTO;
import com.kairos.dto.user.country.experties.FunctionalSeniorityLevelDTO;
import com.kairos.dto.user.country.experties.FunctionsDTO;
import com.kairos.dto.user.country.experties.SeniorityLevelFunctionDTO;
import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.user.expertise.*;
import com.kairos.persistence.model.user.expertise.response.*;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.expertise.*;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.functional_paymment.IsFunctionalPaymentAvailable;
import com.kairos.rule_validator.functional_paymment.IsGreaterThanStartDate;
import com.kairos.rule_validator.functional_paymment.IsGreaterThanToday;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.UserMessagesConstants.*;

@Service
@Transactional
public class FunctionalPaymentService {

    private static Logger LOGGER = LoggerFactory.getLogger(FunctionalPaymentService.class);
    private ExpertiseGraphRepository expertiseGraphRepository;
    private ExceptionService exceptionService;
    private FunctionalPaymentGraphRepository functionalPaymentGraphRepository;
    private PayGroupAreaGraphRepository payGroupAreaGraphRepository;
    private SeniorityLevelGraphRepository seniorityLevelGraphRepository;
    private FunctionGraphRepository functionGraphRepository;
    private SeniorityLevelFunctionRelationshipGraphRepository seniorityLevelFunctionRelationshipGraphRepository;
    private FunctionalPaymentMatrixRepository functionalPaymentMatrixRepository;


    public FunctionalPaymentService(ExpertiseGraphRepository expertiseGraphRepository, ExceptionService exceptionService, FunctionalPaymentGraphRepository functionalPaymentGraphRepository
            , PayGroupAreaGraphRepository payGroupAreaGraphRepository, SeniorityLevelGraphRepository seniorityLevelGraphRepository, FunctionGraphRepository functionGraphRepository
            , SeniorityLevelFunctionRelationshipGraphRepository seniorityLevelFunctionRelationshipGraphRepository, FunctionalPaymentMatrixRepository functionalPaymentMatrixRepository) {
        this.expertiseGraphRepository = expertiseGraphRepository;
        this.exceptionService = exceptionService;
        this.functionalPaymentGraphRepository = functionalPaymentGraphRepository;
        this.payGroupAreaGraphRepository = payGroupAreaGraphRepository;
        this.seniorityLevelGraphRepository = seniorityLevelGraphRepository;
        this.functionGraphRepository = functionGraphRepository;
        this.seniorityLevelFunctionRelationshipGraphRepository = seniorityLevelFunctionRelationshipGraphRepository;
        this.functionalPaymentMatrixRepository = functionalPaymentMatrixRepository;
    }

    public FunctionalPaymentDTO saveFunctionalPayment(Long expertiseId, FunctionalPaymentDTO functionalPaymentDTO) {
        Optional<Expertise> expertise = expertiseGraphRepository.findById(expertiseId);
        if (!expertise.isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, EXPERTISE, expertiseId);
        }
        FunctionalPayment functionalPayment = validateAndGetDomainObject(functionalPaymentDTO, expertise.get());
        functionalPaymentGraphRepository.save(functionalPayment);
        functionalPaymentDTO.setId(functionalPayment.getId());
        return functionalPaymentDTO;
    }

    public List<FunctionalPaymentDTO> getFunctionalPayment(Long expertiseId) {
        return functionalPaymentGraphRepository.getFunctionalPaymentOfExpertise(expertiseId);
    }

    private FunctionalPayment validateAndGetDomainObject(FunctionalPaymentDTO functionalPaymentDTO, Expertise expertise) {
        FunctionalPayment functionalPaymentFromDb = functionalPaymentGraphRepository.getLastFunctionalPaymentOfExpertise(expertise.getId());
        Specification<FunctionalPaymentDTO> isGreaterThanStartDateAndToday = new IsGreaterThanStartDate(expertise, exceptionService)
                .and(new IsGreaterThanToday(exceptionService))
                .and(new IsFunctionalPaymentAvailable(functionalPaymentFromDb, exceptionService));

        isGreaterThanStartDateAndToday.isSatisfied(functionalPaymentDTO);
        return new FunctionalPayment(expertise, functionalPaymentDTO.getStartDate(), functionalPaymentDTO.getEndDate(), functionalPaymentDTO.getPaymentUnit());
    }

    public FunctionalPaymentDTO updateFunctionalPayment(Long expertiseId, FunctionalPaymentDTO functionalPaymentDTO) {
        Optional<FunctionalPayment> functionalPayment = functionalPaymentGraphRepository.findById(functionalPaymentDTO.getId());
        if (!functionalPayment.isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, "functionalpayment", functionalPaymentDTO.getId());
        }
        if (!functionalPayment.get().getStartDate().equals(functionalPaymentDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_FUNCTIONALPAYMENT_UNEDITABLE, "startdate");
        }
        functionalPayment.get().setPaymentUnit(functionalPaymentDTO.getPaymentUnit());
        functionalPayment.get().setEndDate(functionalPaymentDTO.getEndDate());
        functionalPaymentGraphRepository.save(functionalPayment.get());
        functionalPaymentDTO.setId(functionalPayment.get().getId());
        return functionalPaymentDTO;
    }

    public List<FunctionalPaymentMatrixDTO> addMatrixInFunctionalPayment(FunctionalSeniorityLevelDTO functionalSeniorityLevelDTO) {
        List<FunctionalPaymentMatrixDTO> functionalPaymentMatrixDTOS = functionalSeniorityLevelDTO.getFunctionalPaymentMatrix();

        Optional<FunctionalPayment> functionalPayment = functionalPaymentGraphRepository.findById(functionalSeniorityLevelDTO.getFunctionalPaymentId());
        if (!functionalPayment.isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, "functionalpayment", functionalSeniorityLevelDTO.getFunctionalPaymentId());
        }
        List<FunctionalPaymentMatrix> list = new ArrayList<FunctionalPaymentMatrix>(1);
        List<Function> functions = getFunctions(functionalPaymentMatrixDTOS);
        List<SeniorityLevel> seniorityLevels = getSeniorityLevel(functionalPaymentMatrixDTOS);

        functionalPaymentMatrixDTOS.forEach(functionalPaymentMatrixDTO -> {
            FunctionalPaymentMatrix functionalPaymentMatrix = addMatrixInFunctionalPayment(functionalPaymentMatrixDTO, seniorityLevels, functions);
            functionalPaymentMatrixDTO.setId(functionalPaymentMatrix.getId());
            list.add(functionalPaymentMatrix);
        });
        functionalPayment.get().setFunctionalPaymentMatrices(list);
        functionalPaymentGraphRepository.save(functionalPayment.get());
        return functionalPaymentMatrixDTOS;
    }

    private FunctionalPaymentMatrix addMatrixInFunctionalPayment(FunctionalPaymentMatrixDTO functionalPaymentMatrixDTO, List<SeniorityLevel> seniorityLevels, List<Function> functions) {
        FunctionalPaymentMatrix functionalPaymentMatrix = new FunctionalPaymentMatrix();
        if (Optional.ofNullable(functionalPaymentMatrixDTO.getPayGroupAreasIds()).isPresent() && !functionalPaymentMatrixDTO.getPayGroupAreasIds().isEmpty()) {
            List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllByIds(functionalPaymentMatrixDTO.getPayGroupAreasIds());
            if (payGroupAreas.size() != functionalPaymentMatrixDTO.getPayGroupAreasIds().size())
                exceptionService.actionNotPermittedException(MESSAGE_MULTIPLEDATANOTFOUND, PAYGROUP_AREAS);
            functionalPaymentMatrix.setPayGroupAreas(new HashSet<>(payGroupAreas));
        }
        functionalPaymentMatrix.setSeniorityLevelFunction(getSeniorityLevelFunction(functionalPaymentMatrixDTO.getSeniorityLevelFunction(), seniorityLevels, functions));
        functionalPaymentMatrixRepository.save(functionalPaymentMatrix);
        return functionalPaymentMatrix;
    }

    private List<SeniorityLevelFunction> getSeniorityLevelFunction(List<SeniorityLevelFunctionDTO> seniorityLevelFunctionDTOS, List<SeniorityLevel> seniorityLevels, List<Function> functions) {
        List<SeniorityLevelFunction> seniorityLevelFunctions = new ArrayList<>();
        seniorityLevelFunctionDTOS.forEach(currentSRLevelFunction -> {
            List<SeniorityLevelFunctionsRelationship> seniorityLevelFunctionsRelationships = new ArrayList<>();
            SeniorityLevel seniorityLevel = seniorityLevels.stream().
                    filter(seniorityLevel1 -> seniorityLevel1.getId().equals(currentSRLevelFunction.getSeniorityLevelId())).findAny().get();
            SeniorityLevelFunction seniorityLevelFunction = new SeniorityLevelFunction();
            seniorityLevelFunction.setSeniorityLevel(seniorityLevel);

            currentSRLevelFunction.getFunctions().forEach(currentFunction -> {
                Function function = functions.stream().
                        filter(function1 -> function1.getId().equals(currentFunction.getId())).findAny().get();
                SeniorityLevelFunctionsRelationship seniorityLevelFunctionsRelationship = new SeniorityLevelFunctionsRelationship(function, seniorityLevelFunction, currentFunction.getAmount(), currentFunction.isAmountEditableAtUnit());
                seniorityLevelFunctionsRelationships.add(seniorityLevelFunctionsRelationship);
            });
            seniorityLevelFunctionRelationshipGraphRepository.saveAll(seniorityLevelFunctionsRelationships);
            seniorityLevelFunctions.add(seniorityLevelFunction);
        });
        return seniorityLevelFunctions;
    }

    public List<FunctionalPaymentMatrixQueryResult> getMatrixOfFunctionalPayment(Long functionalPaymentId) {
        return functionalPaymentGraphRepository.getFunctionalPaymentMatrix(functionalPaymentId);
    }

    private List<Function> getFunctions(List<FunctionalPaymentMatrixDTO> functionalPaymentMatrixDTOS) {
        Set<Long> functionIds =
                functionalPaymentMatrixDTOS.stream()
                        .flatMap(seniorityLevelFunction -> seniorityLevelFunction.getSeniorityLevelFunction().stream()
                                .flatMap(functionsDTO -> functionsDTO.getFunctions().stream())
                                .map(FunctionsDTO::getFunctionId))
                        .collect(Collectors.toSet());
        List<Function> functions = functionGraphRepository.findAllFunctionsById(functionIds);
        if (functionIds.size() != functions.size()) {
            exceptionService.actionNotPermittedException(MESSAGE_MULTIPLEDATANOTFOUND, "functions");
        }
        return functions;
    }

    private List<SeniorityLevel> getSeniorityLevel(List<FunctionalPaymentMatrixDTO> functionalPaymentMatrixDTOS) {
        Set<Long> seniorityLevelIds = functionalPaymentMatrixDTOS.stream()
                .flatMap(seniorityLevelFunction -> seniorityLevelFunction.getSeniorityLevelFunction()
                        .stream()
                        .map(SeniorityLevelFunctionDTO::getSeniorityLevelId))
                .collect(Collectors.toSet());
        List<SeniorityLevel> seniorityLevels = seniorityLevelGraphRepository.findAll(seniorityLevelIds);
        if (seniorityLevelIds.size() != seniorityLevels.size()) {
            exceptionService.actionNotPermittedException(MESSAGE_MULTIPLEDATANOTFOUND, "seniority-level");
        }
        return seniorityLevels;
    }

    public FunctionalSeniorityLevelDTO updateMatrixInFunctionalPayment(FunctionalSeniorityLevelDTO functionalSeniorityLevelDTO) {
        Optional<FunctionalPayment> functionalPayment = functionalPaymentGraphRepository.findById(functionalSeniorityLevelDTO.getFunctionalPaymentId());
        if (!functionalPayment.isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, "functionalpayment", functionalSeniorityLevelDTO.getFunctionalPaymentId());
        }
        if (functionalPayment.get().isOneTimeUpdatedAfterPublish()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DRAFT_COPY_CREATED);
        }
        List<FunctionalPaymentMatrixDTO> functionalPaymentMatrixDTOS = functionalSeniorityLevelDTO.getFunctionalPaymentMatrix();
        List<FunctionalPaymentMatrix> list = new ArrayList<>(1);
        List<Function> functions = getFunctions(functionalPaymentMatrixDTOS);
        List<SeniorityLevel> seniorityLevels = getSeniorityLevel(functionalPaymentMatrixDTOS);

        if (functionalPayment.get().isPublished()) {
            // functional payment is published so we need to create a  new copy and update in same
            FunctionalPayment functionalPaymentCopy = new FunctionalPayment(functionalPayment.get().getExpertise(), functionalPayment.get().getStartDate(),
                    functionalPayment.get().getEndDate(), functionalPayment.get().getPaymentUnit());

            functionalPaymentMatrixDTOS.forEach(functionalPaymentMatrixDTO -> {
                FunctionalPaymentMatrix functionalPaymentMatrix = addMatrixInFunctionalPayment(functionalPaymentMatrixDTO, seniorityLevels, functions);
                functionalPaymentMatrixDTO.setId(functionalPaymentMatrix.getId());
                list.add(functionalPaymentMatrix);
            });
            functionalPaymentCopy.setFunctionalPaymentMatrices(list);

            functionalPayment.get().setHasDraftCopy(true);
            functionalPayment.get().setOneTimeUpdatedAfterPublish(true);
            functionalPaymentCopy.setParentFunctionalPayment(functionalPayment.get());
            functionalPaymentGraphRepository.save(functionalPaymentCopy);
            functionalSeniorityLevelDTO.setFunctionalPaymentId(functionalPaymentCopy.getId());

        } else {
            // update in current copy
            functionalPaymentMatrixDTOS.forEach(functionalPaymentMatrixDTO -> {
                FunctionalPaymentMatrix functionalPaymentMatrix;
                if (functionalPaymentMatrixDTO.getId() == null) {
                    functionalPaymentMatrix = addMatrixInFunctionalPayment(functionalPaymentMatrixDTO, seniorityLevels, functions);
                } else {
                    functionalPaymentMatrix
                            = functionalPaymentMatrixRepository.findOne(functionalPaymentMatrixDTO.getId());
                    Set<Long> payGroupAreaIds = functionalPaymentMatrix.getPayGroupAreas().stream().map
                            (pay -> pay.getId()).collect(Collectors.toSet());

                    if (!payGroupAreaIds.equals(functionalPaymentMatrixDTO.getPayGroupAreasIds())) {
                        // user has updated the payGroupAreas   // remove all payGroup areas and add the new one
                        functionalPaymentGraphRepository.removeAllPayGroupAreas(functionalPaymentMatrix.getId());
                        List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllByIds(functionalPaymentMatrixDTO.getPayGroupAreasIds());
                        functionalPaymentMatrix.setPayGroupAreas(new HashSet<>(payGroupAreas));
                    }

                    functionalPaymentMatrix.setSeniorityLevelFunction(getSeniorityLevelFunction(functionalPaymentMatrixDTO.getSeniorityLevelFunction(), seniorityLevels, functions));
                    // find object by db and update in that
                }
                functionalPaymentMatrixDTO.setId(functionalPaymentMatrix.getId());
                list.add(functionalPaymentMatrix);
            });
            functionalPayment.get().setFunctionalPaymentMatrices(list);
            functionalPaymentGraphRepository.save(functionalPayment.get());
        }
        return functionalSeniorityLevelDTO;
    }

    public FunctionalPaymentDTO publishFunctionalPayment(Long functionalPaymentId, FunctionalPaymentDTO functionalPaymentDTO) {
        Optional<FunctionalPayment> functionalPayment = functionalPaymentGraphRepository.findById(functionalPaymentId);
        if (!functionalPayment.isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, "functionalpayment", functionalPaymentDTO.getId());
        }
        if (functionalPayment.get().getFunctionalPaymentMatrices().isEmpty()) {
            exceptionService.actionNotPermittedException(MESSAGE_FUNCTIONAL_PAYMENT_EMPTY_MATRIX);
        }
        if (functionalPayment.get().isPublished()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_FUNCTIONALPAYMENT_ALREADYPUBLISHED);
        }
        if (functionalPayment.get().getStartDate().isAfter(functionalPaymentDTO.getStartDate())) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PUBLISHDATE_NOTLESSTHAN_STARTDATE);
        }
        functionalPayment.get().setPublished(true);
        functionalPayment.get().setStartDate(functionalPaymentDTO.getStartDate()); // changing
        FunctionalPaymentDTO parentFunctionalPayment = functionalPaymentGraphRepository.getParentFunctionalPayment(functionalPaymentId);
        FunctionalPayment lastFunctionPayment = functionalPaymentGraphRepository.findByExpertiseId(functionalPayment.get().getExpertise().getId());
        boolean onGoingUpdated = false;
        if (lastFunctionPayment != null && functionalPaymentDTO.getStartDate().isAfter(lastFunctionPayment.getStartDate()) && lastFunctionPayment.getEndDate() == null) {
            lastFunctionPayment.setEndDate(functionalPaymentDTO.getStartDate().minusDays(1));
            functionalPaymentGraphRepository.save(lastFunctionPayment);
            functionalPaymentGraphRepository.detachFunctionalPayment(functionalPaymentId, parentFunctionalPayment.getId());
            functionalPayment.get().setEndDate(null);
            onGoingUpdated = true;
        }
        if (!onGoingUpdated && Optional.ofNullable(parentFunctionalPayment).isPresent()) {
            if (parentFunctionalPayment.getStartDate().isEqual(functionalPaymentDTO.getStartDate()) || parentFunctionalPayment.getStartDate().isAfter(functionalPaymentDTO.getStartDate())) {
                exceptionService.dataNotFoundByIdException(MESSAGE_PUBLISHDATE_NOTLESSTHAN_OR_EQUALS_PARENT_STARTDATE);
            }
            functionalPaymentGraphRepository.setEndDateToFunctionalPayment(functionalPaymentId, parentFunctionalPayment.getId(),
                    functionalPaymentDTO.getStartDate().minusDays(1L).toString());
            parentFunctionalPayment.setEndDate(functionalPaymentDTO.getStartDate().minusDays(1L));
            if (lastFunctionPayment == null && functionalPayment.get().getEndDate() != null && functionalPayment.get().getEndDate().isBefore(functionalPaymentDTO.getStartDate())) {
                functionalPayment.get().setEndDate(null);
            }
        }
        functionalPaymentGraphRepository.save(functionalPayment.get());
        return parentFunctionalPayment;

    }


    public void updateAmountInFunctionalTable(Long payTableId, LocalDate startDate, LocalDate endDate, BigDecimal percentageValue) {
        List<FunctionalPayment> functionalPaymentList = functionalPaymentGraphRepository.findAllActiveByPayTableId(payTableId, startDate.toString(), null);
        List<FunctionalPayment> toBreakInNewList = new ArrayList<>();
        List<FunctionalPayment> toUpdateInExisting = new ArrayList<>();
        for (FunctionalPayment functionalPayment : functionalPaymentList) {
            if (endDate == null) {
                if (functionalPayment.getStartDate().isAfter(startDate.minusDays(1))) {
                    functionalPayment.setPercentageValue(percentageValue);
                    toUpdateInExisting.add(functionalPayment);
                } else if (functionalPayment.getStartDate().isBefore(startDate)) {
                    toBreakInNewList.add(functionalPayment);
                }
            } else {
                if (functionalPayment.getEndDate() != null || (functionalPayment.getStartDate().isAfter(startDate.minusDays(1)) && functionalPayment.getEndDate().isBefore(startDate.plusDays(1)))) {
                    toUpdateInExisting.add(functionalPayment);
                } else {
                    toBreakInNewList.add(functionalPayment);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(toUpdateInExisting)) {
            functionalPaymentGraphRepository.updateFunctionalAmount(toUpdateInExisting.stream().map(FunctionalPayment::getId).collect(Collectors.toList()), percentageValue.toString());
        }

        if (CollectionUtils.isNotEmpty(toBreakInNewList)) {
            List<FunctionalPaymentQueryResult> functionalPaymentQueryResultList = functionalPaymentGraphRepository.getFunctionalPaymentData(toBreakInNewList.stream().map(FunctionalPayment::getId).collect(Collectors.toList()));
            List<FunctionalPaymentQueryResult> functionalPaymentQueryResults = ObjectMapperUtils.copyPropertiesOfListByMapper(functionalPaymentQueryResultList, FunctionalPaymentQueryResult.class);
            Map<Set<Long>, List<SeniorityLevelFunctionQR>> payGroupAreaWiseMap = constructMapOfFunctionalPaymentMatrixQueryResult(functionalPaymentQueryResults);
            functionalPaymentQueryResults.forEach(functionalPaymentQueryResult -> functionalPaymentQueryResult.setFunctionalPaymentMatrices(getMatrixFromPayGroupAreaWiseMap(payGroupAreaWiseMap)));
            List<FunctionalPayment> functionalPayments = functionalPaymentGraphRepository.findAllById(toBreakInNewList.stream().map(FunctionalPayment::getId).collect(Collectors.toList()));
            Map<Long, FunctionalPayment> functionalPaymentMap = functionalPayments.stream().collect(Collectors.toMap(FunctionalPayment::getId, Functions.identity()));
            List<FunctionalPayment> functionalPaymentListBeforeDate = new ArrayList<>();
            List<FunctionalPayment> functionalPaymentListAfterDate = new ArrayList<>();
            List<FunctionalPayment> allFunctionalPayments = new ArrayList<>();

            for (FunctionalPaymentQueryResult functionalPaymentQueryResult : functionalPaymentQueryResults) {

                if (functionalPaymentQueryResult.getEndDate() == null && functionalPaymentQueryResult.getStartDate().isBefore(startDate)) {
                    FunctionalPayment existing = functionalPaymentMap.get(functionalPaymentQueryResult.getId());
                    existing.setStartDate(functionalPaymentQueryResult.getStartDate());
                    existing.setEndDate(startDate.minusDays(1));

                    functionalPaymentListBeforeDate.add(existing);

                    //Creating new and updating values

                    FunctionalPayment functionalPayment = new FunctionalPayment(functionalPaymentQueryResult.getExpertise(), startDate,
                            functionalPaymentQueryResult.getEndDate(), functionalPaymentQueryResult.getPaymentUnit());
                    functionalPayment.setParentFunctionalPayment(existing);
                    functionalPayment.setPublished(functionalPaymentQueryResult.isPublished());
                    functionalPayment.setPercentageValue(percentageValue);
                    updateMatrixInFunctionalPayment(functionalPaymentQueryResult.getFunctionalPaymentMatrices(), functionalPayment, percentageValue);
                    functionalPaymentListAfterDate.add(functionalPayment);
                }
            }
            allFunctionalPayments.addAll(functionalPaymentListBeforeDate);
            allFunctionalPayments.addAll(functionalPaymentListAfterDate);
            functionalPaymentGraphRepository.saveAll(allFunctionalPayments);
        }


    }

    private void updateMatrixInFunctionalPayment(List<FunctionalPaymentMatrixQueryResult> functionalPaymentMatrixQueryResults, FunctionalPayment functionalPayment, BigDecimal percentageValue) {
        List<FunctionalPaymentMatrix> functionalPaymentMatrices = new ArrayList<>();
        List<Function> functions = getFunctionList(functionalPaymentMatrixQueryResults);
        List<SeniorityLevel> seniorityLevels = getSeniorityLevelList(functionalPaymentMatrixQueryResults);
        for (FunctionalPaymentMatrixQueryResult functionalPaymentMatrixQueryResult : functionalPaymentMatrixQueryResults) {
            FunctionalPaymentMatrix functionalPaymentMatrix = new FunctionalPaymentMatrix();
            functionalPaymentMatrix = addMatrixInFunctionalPayment(functionalPaymentMatrix, functionalPaymentMatrixQueryResult, seniorityLevels, functions, percentageValue);
            functionalPaymentMatrixQueryResult.setId(functionalPaymentMatrix.getId());
            functionalPaymentMatrices.add(functionalPaymentMatrix);
        }
        functionalPaymentMatrixRepository.saveAll(functionalPaymentMatrices);
        functionalPayment.setFunctionalPaymentMatrices(functionalPaymentMatrices);
        functionalPaymentGraphRepository.save(functionalPayment);
    }

    private List<Function> getFunctionList(List<FunctionalPaymentMatrixQueryResult> functionalPaymentMatrixQueryResults) {
        Set<Long> functionIds =
                functionalPaymentMatrixQueryResults.stream()
                        .flatMap(seniorityLevelFunction -> seniorityLevelFunction.getSeniorityLevelFunction().stream()
                                .flatMap(functionsDTO -> functionsDTO.getFunctions().stream())
                                .map(FunctionQR::getFunctionId))
                        .collect(Collectors.toSet());
        List<Function> functions = functionGraphRepository.findAllFunctionsById(functionIds);
        if (functionIds.size() != functions.size()) {
            exceptionService.actionNotPermittedException(MESSAGE_MULTIPLEDATANOTFOUND, "functions");
        }
        return functions;
    }

    private List<SeniorityLevel> getSeniorityLevelList(List<FunctionalPaymentMatrixQueryResult> functionalPaymentMatrixQueryResults) {
        Set<Long> seniorityLevelIds = functionalPaymentMatrixQueryResults.stream()
                .flatMap(seniorityLevelFunction -> seniorityLevelFunction.getSeniorityLevelFunction()
                        .stream()
                        .map(SeniorityLevelFunctionQR::getSeniorityLevelId))
                .collect(Collectors.toSet());
        return seniorityLevelGraphRepository.findAll(seniorityLevelIds);
    }

    private FunctionalPaymentMatrix addMatrixInFunctionalPayment(FunctionalPaymentMatrix functionalPaymentMatrix, FunctionalPaymentMatrixQueryResult functionalPaymentMatrixQueryResult, List<SeniorityLevel> seniorityLevels, List<Function> functions, BigDecimal percentageValue) {
        if (Optional.ofNullable(functionalPaymentMatrixQueryResult.getPayGroupAreasIds()).isPresent() && !functionalPaymentMatrixQueryResult.getPayGroupAreasIds().isEmpty()) {
            List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllByIds(functionalPaymentMatrixQueryResult.getPayGroupAreasIds());
            if (payGroupAreas.size() != functionalPaymentMatrixQueryResult.getPayGroupAreasIds().size())
                exceptionService.actionNotPermittedException(MESSAGE_MULTIPLEDATANOTFOUND, PAYGROUP_AREAS);
            functionalPaymentMatrix.setPayGroupAreas(new HashSet<>(payGroupAreas));
        }
        functionalPaymentMatrix.setSeniorityLevelFunction(getSeniorityLevelFunctionList(functionalPaymentMatrixQueryResult.getSeniorityLevelFunction(), seniorityLevels, functions, percentageValue));
        return functionalPaymentMatrix;
    }

    private List<SeniorityLevelFunction> getSeniorityLevelFunctionList(List<SeniorityLevelFunctionQR> seniorityLevelFunctionQRS, List<SeniorityLevel> seniorityLevels, List<Function> functions, BigDecimal percentageValue) {
        List<SeniorityLevelFunction> seniorityLevelFunctions = new ArrayList<>();

        seniorityLevelFunctionQRS.forEach(currentSRLevelFunction -> {
            SeniorityLevelFunction seniorityLevelFunction = new SeniorityLevelFunction();
            List<SeniorityLevelFunctionsRelationship> seniorityLevelFunctionsRelationships = new ArrayList<>();
            SeniorityLevel seniorityLevel = seniorityLevels.stream().
                    filter(seniorityLevel1 -> seniorityLevel1.getId().equals(currentSRLevelFunction.getSeniorityLevelId())).findAny().get();
            seniorityLevelFunction.setSeniorityLevel(seniorityLevel);
            currentSRLevelFunction.getFunctions().forEach(currentFunction -> {
                Function function = functions.stream().
                        filter(function1 -> function1.getId().equals(currentFunction.getFunctionId())).findAny().get();
                BigDecimal updatedAmount = currentFunction.getAmount().add(currentFunction.getAmount().multiply(percentageValue).divide(new BigDecimal(100), RoundingMode.CEILING));
                SeniorityLevelFunctionsRelationship seniorityLevelFunctionsRelationship = new SeniorityLevelFunctionsRelationship(function, seniorityLevelFunction, updatedAmount, currentFunction.isAmountEditableAtUnit());
                seniorityLevelFunctionsRelationships.add(seniorityLevelFunctionsRelationship);
            });
            seniorityLevelFunctionRelationshipGraphRepository.saveAll(seniorityLevelFunctionsRelationships);
            seniorityLevelFunctions.add(seniorityLevelFunction);
        });
        return seniorityLevelFunctions;
    }

    private Map<Set<Long>, List<SeniorityLevelFunctionQR>> constructMapOfFunctionalPaymentMatrixQueryResult(List<FunctionalPaymentQueryResult> functionalPaymentQueryResults) {
        Map<Set<Long>, List<SeniorityLevelFunctionQR>> payGroupAreaWiseMap = new HashMap<>();
        functionalPaymentQueryResults.forEach(functionalPaymentQueryResult -> {
            functionalPaymentQueryResult.getFunctionalPaymentMatrices().forEach(functionalPaymentMatrixQueryResult -> {
                if (payGroupAreaWiseMap.containsKey(functionalPaymentMatrixQueryResult.getPayGroupAreasIds())) {
                    payGroupAreaWiseMap.get(functionalPaymentMatrixQueryResult.getPayGroupAreasIds()).addAll(functionalPaymentMatrixQueryResult.getSeniorityLevelFunction());
                } else {
                    payGroupAreaWiseMap.put(functionalPaymentMatrixQueryResult.getPayGroupAreasIds(), functionalPaymentMatrixQueryResult.getSeniorityLevelFunction());
                }
            });
        });
        return payGroupAreaWiseMap;
    }

    private List<FunctionalPaymentMatrixQueryResult> getMatrixFromPayGroupAreaWiseMap(Map<Set<Long>, List<SeniorityLevelFunctionQR>> payGroupAreaWiseMap) {
        return payGroupAreaWiseMap.entrySet().stream().map(setListEntry -> new FunctionalPaymentMatrixQueryResult(setListEntry.getKey(), setListEntry.getValue())).collect(Collectors.toList());
    }

    public boolean deleteFunctionalPayment(Long functionalPaymentId) {
        return functionalPaymentGraphRepository.deleteFunctionalPayment(functionalPaymentId);
    }

}