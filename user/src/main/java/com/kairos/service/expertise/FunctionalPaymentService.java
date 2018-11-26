package com.kairos.service.expertise;

import com.google.common.base.Functions;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.experties.FunctionalPaymentMatrixDTO;
import com.kairos.dto.user.country.experties.FunctionalSeniorityLevelDTO;
import com.kairos.dto.user.country.experties.FunctionsDTO;
import com.kairos.dto.user.country.experties.SeniorityLevelFunctionDTO;
import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.user.expertise.*;
import com.kairos.persistence.model.user.expertise.Response.*;
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
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FunctionalPaymentService {

    private Logger logger = LoggerFactory.getLogger(FunctionalPaymentService.class);
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
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "expertise", expertiseId);
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
        FunctionalPayment functionalPayment = new FunctionalPayment(expertise, functionalPaymentDTO.getStartDate(), functionalPaymentDTO.getEndDate(), functionalPaymentDTO.getPaymentUnit());
        return functionalPayment;
    }

    public FunctionalPaymentDTO updateFunctionalPayment(Long expertiseId, FunctionalPaymentDTO functionalPaymentDTO) {
        Optional<FunctionalPayment> functionalPayment = functionalPaymentGraphRepository.findById(functionalPaymentDTO.getId());
        if (!functionalPayment.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "functionalpayment", functionalPaymentDTO.getId());
        }
        if (!functionalPayment.get().getStartDate().equals(functionalPaymentDTO.getStartDate())) {
            exceptionService.actionNotPermittedException("message.functionalPayment.uneditable", "startdate");
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
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "functionalpayment", functionalSeniorityLevelDTO.getFunctionalPaymentId());
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
                exceptionService.actionNotPermittedException("message.multipleDataNotFound", "payGroup-areas");
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
            exceptionService.actionNotPermittedException("message.multipleDataNotFound", "functions");
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
            exceptionService.actionNotPermittedException("message.multipleDataNotFound", "seniority-level");
        }
        return seniorityLevels;
    }

    public FunctionalSeniorityLevelDTO updateMatrixInFunctionalPayment(FunctionalSeniorityLevelDTO functionalSeniorityLevelDTO) {
        Optional<FunctionalPayment> functionalPayment = functionalPaymentGraphRepository.findById(functionalSeniorityLevelDTO.getFunctionalPaymentId());
        if (!functionalPayment.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "functionalpayment", functionalSeniorityLevelDTO.getFunctionalPaymentId());
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
            functionalPaymentCopy.setParentFunctionalPayment(functionalPayment.get());
            functionalPaymentGraphRepository.save(functionalPaymentCopy);
            functionalSeniorityLevelDTO.setFunctionalPaymentId(functionalPaymentCopy.getId());

        } else {
            // update in current copy
            functionalPaymentMatrixDTOS.forEach(functionalPaymentMatrixDTO -> {
                FunctionalPaymentMatrix functionalPaymentMatrix = null;
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
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "functionalpayment", functionalPaymentDTO.getId());
        }
        if (functionalPayment.get().getFunctionalPaymentMatrices().isEmpty()) {
            exceptionService.actionNotPermittedException("message_functional_Payment_empty_matrix");
        }
        if (functionalPayment.get().isPublished()) {
            exceptionService.dataNotFoundByIdException("message.functionalPayment.alreadyPublished");
        }
        if (functionalPayment.get().getStartDate().isAfter(functionalPaymentDTO.getStartDate())) {
            exceptionService.dataNotFoundByIdException("message.publishDate.notlessthan.startDate");
        }
        functionalPayment.get().setPublished(true);
        functionalPayment.get().setStartDate(functionalPaymentDTO.getStartDate()); // changing

        FunctionalPaymentDTO parentFunctionalPayment = functionalPaymentGraphRepository.getParentFunctionalPayment(functionalPaymentId);
        if (Optional.ofNullable(parentFunctionalPayment).isPresent()) {
            functionalPaymentGraphRepository.setEndDateToFunctionalPayment(functionalPaymentId, parentFunctionalPayment.getId(),
                    functionalPaymentDTO.getStartDate().minusDays(1L).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
            parentFunctionalPayment.setEndDate(functionalPaymentDTO.getStartDate().minusDays(1L));
            if (functionalPayment.get().getEndDate() != null && functionalPayment.get().getEndDate().isBefore(functionalPaymentDTO.getStartDate())) {
                functionalPayment.get().setEndDate(null);
            }
        }
        functionalPaymentGraphRepository.save(functionalPayment.get());
        return parentFunctionalPayment;

    }


    public void updateAmountInFunctionalTable(Long payTableId, Date startDate, Date endDate, BigDecimal percentageValue) {

        List<FunctionalPayment> functionalPaymentList = seniorityLevelFunctionRelationshipGraphRepository.findAllActiveByPayTableId(payTableId, startDate.getTime(), null);
        List<FunctionalPayment> toBreakInNewList = new ArrayList<>();
        List<FunctionalPayment> toUpdateInExisting = new ArrayList<>();
        for (FunctionalPayment functionalPayment : functionalPaymentList) {
            if (endDate == null) {
                if (functionalPayment.getStartDate().isAfter(DateUtils.asLocalDate(startDate).minusDays(1))) {
                    toUpdateInExisting.add(functionalPayment);
                } else if (functionalPayment.getStartDate().isBefore(DateUtils.asLocalDate(startDate))) {
                    toBreakInNewList.add(functionalPayment);
                }
            } else {
                if (functionalPayment.getEndDate() != null || (functionalPayment.getStartDate().isAfter(DateUtils.asLocalDate(startDate).minusDays(1)) && functionalPayment.getEndDate().isBefore(DateUtils.asLocalDate(startDate).plusDays(1)))) {
                    toUpdateInExisting.add(functionalPayment);
                } else {
                    toBreakInNewList.add(functionalPayment);
                }
            }
        }

        functionalPaymentGraphRepository.updateFunctionalAmount(toUpdateInExisting.stream().map(FunctionalPayment::getId).collect(Collectors.toList()), percentageValue.toString());
        if (CollectionUtils.isNotEmpty(toBreakInNewList)) {
            List<FunctionalPaymentQueryResult> functionalPaymentQueryResults1 = functionalPaymentGraphRepository.getFunctionalPaymentDataT(toBreakInNewList.stream().map(FunctionalPayment::getId).collect(Collectors.toList()));
            List<FunctionalPaymentQueryResult> functionalPaymentQueryResults= convertToFlat(functionalPaymentQueryResults1);
            List<FunctionalPayment> functionalPayments = functionalPaymentGraphRepository.findAllById(toBreakInNewList.stream().map(FunctionalPayment::getId).collect(Collectors.toList()));
            Map<Long, FunctionalPayment> functionalPaymentMap = functionalPayments.stream().collect(Collectors.toMap(FunctionalPayment::getId, Functions.identity()));
            List<FunctionalPayment> outside = new ArrayList<>();
            List<FunctionalPayment> inside = new ArrayList<>();
            List<FunctionalPayment> allFunctionalPayments = new ArrayList<>();

            for (FunctionalPaymentQueryResult functionalPaymentQueryResult : functionalPaymentQueryResults) {

                if (functionalPaymentQueryResult.getEndDate() == null && functionalPaymentQueryResult.getStartDate().isBefore(DateUtils.asLocalDate(startDate))) {
                    FunctionalPayment existing = functionalPaymentMap.get(functionalPaymentQueryResult.getId());
                    existing.setStartDate(functionalPaymentQueryResult.getStartDate());
                    existing.setEndDate(DateUtils.asLocalDate(startDate).minusDays(1));

                    outside.add(existing);

                    //Creating new and updating values


                    FunctionalPayment createNew = new FunctionalPayment(functionalPaymentQueryResult.getExpertise(), functionalPaymentQueryResult.getStartDate(),
                            functionalPaymentQueryResult.getEndDate(), functionalPaymentQueryResult.getPaymentUnit());
                    createNew.setParentFunctionalPayment(existing);
                    createNew.setHasDraftCopy(true);
                    updateMatrixInFunctionalPayment1(functionalPaymentQueryResult.getFunctionalPaymentMatrices(), createNew);
                    //createNew.setFunctionalPaymentMatrices();

                    inside.add(createNew);
                }
            }
            allFunctionalPayments.addAll(inside);
            allFunctionalPayments.addAll(outside);
            functionalPaymentGraphRepository.saveAll(allFunctionalPayments);
        }


    }


    public List<FunctionalPaymentQueryResult> test(List<Long> ids) {
        List<FunctionalPaymentQueryResult> functionalPaymentQueryResults = functionalPaymentGraphRepository.getFunctionalPaymentDataT(Arrays.asList(new Long("39856")));
        return functionalPaymentQueryResults;

    }






    public void updateMatrixInFunctionalPayment1(List<FunctionalPaymentMatrixQueryResult> functionalPaymentMatrixQueryResults, FunctionalPayment createNew) {

        ;
        List<FunctionalPaymentMatrix> list = new ArrayList<>(1);
        List<Function> functions = getFunctions1(functionalPaymentMatrixQueryResults);
        List<SeniorityLevel> seniorityLevels = getSeniorityLevel1(functionalPaymentMatrixQueryResults);

        // functional payment is published so we need to create a  new copy and update in same

        functionalPaymentMatrixQueryResults.forEach(functionalPaymentMatrixQueryResult -> {
            FunctionalPaymentMatrix functionalPaymentMatrix = addMatrixInFunctionalPayment1(functionalPaymentMatrixQueryResult, seniorityLevels, functions);
            functionalPaymentMatrixQueryResult.setId(functionalPaymentMatrix.getId());
            list.add(functionalPaymentMatrix);
        });
        createNew.setFunctionalPaymentMatrices(list);


        functionalPaymentGraphRepository.save(createNew);
    }

    private List<Function> getFunctions1(List<FunctionalPaymentMatrixQueryResult> functionalPaymentMatrixQueryResults) {
        Set<Long> functionIds =
                functionalPaymentMatrixQueryResults.stream()
                        .flatMap(seniorityLevelFunction -> seniorityLevelFunction.getSeniorityLevelFunction().stream()
                                .flatMap(functionsDTO -> functionsDTO.getFunctions().stream())
                                .map(FunctionQR::getFunctionId))
                        .collect(Collectors.toSet());
        List<Function> functions = functionGraphRepository.findAllFunctionsById(functionIds);
        if (functionIds.size() != functions.size()) {
            exceptionService.actionNotPermittedException("message.multipleDataNotFound", "functions");
        }
        return functions;
    }

    private List<SeniorityLevel> getSeniorityLevel1(List<FunctionalPaymentMatrixQueryResult> functionalPaymentMatrixQueryResults) {
        Set<Long> seniorityLevelIds = functionalPaymentMatrixQueryResults.stream()
                .flatMap(seniorityLevelFunction -> seniorityLevelFunction.getSeniorityLevelFunction()
                        .stream()
                        .map(SeniorityLevelFunctionQR::getSeniorityLevelId))
                .collect(Collectors.toSet());
        List<SeniorityLevel> seniorityLevels = seniorityLevelGraphRepository.findAll(seniorityLevelIds);
        if (seniorityLevelIds.size() != seniorityLevels.size()) {
            exceptionService.actionNotPermittedException("message.multipleDataNotFound", "seniority-level");
        }
        return seniorityLevels;
    }

    private FunctionalPaymentMatrix addMatrixInFunctionalPayment1(FunctionalPaymentMatrixQueryResult functionalPaymentMatrixQueryResult, List<SeniorityLevel> seniorityLevels, List<Function> functions) {
        FunctionalPaymentMatrix functionalPaymentMatrix = new FunctionalPaymentMatrix();
        if (Optional.ofNullable(functionalPaymentMatrixQueryResult.getPayGroupAreasIds()).isPresent() && !functionalPaymentMatrixQueryResult.getPayGroupAreasIds().isEmpty()) {
            List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllByIds(functionalPaymentMatrixQueryResult.getPayGroupAreasIds());
            if (payGroupAreas.size() != functionalPaymentMatrixQueryResult.getPayGroupAreasIds().size())
                exceptionService.actionNotPermittedException("message.multipleDataNotFound", "payGroup-areas");
            functionalPaymentMatrix.setPayGroupAreas(new HashSet<>(payGroupAreas));
        }
        functionalPaymentMatrix.setSeniorityLevelFunction(getSeniorityLevelFunction1(functionalPaymentMatrixQueryResult.getSeniorityLevelFunction(), seniorityLevels, functions));
        functionalPaymentMatrixRepository.save(functionalPaymentMatrix);
        return functionalPaymentMatrix;
    }

    private List<SeniorityLevelFunction> getSeniorityLevelFunction1(List<SeniorityLevelFunctionQR> seniorityLevelFunctionQRS, List<SeniorityLevel> seniorityLevels, List<Function> functions) {
        List<SeniorityLevelFunction> seniorityLevelFunctions = new ArrayList<>();
        seniorityLevelFunctionQRS.forEach(currentSRLevelFunction -> {
            List<SeniorityLevelFunctionsRelationship> seniorityLevelFunctionsRelationships = new ArrayList<>();
            SeniorityLevel seniorityLevel = seniorityLevels.stream().
                    filter(seniorityLevel1 -> seniorityLevel1.getId().equals(currentSRLevelFunction.getSeniorityLevelId())).findAny().get();
            SeniorityLevelFunction seniorityLevelFunction = new SeniorityLevelFunction();
            seniorityLevelFunction.setSeniorityLevel(seniorityLevel);

            currentSRLevelFunction.getFunctions().forEach(currentFunction -> {
                Function function = functions.stream().
                        filter(function1 -> function1.getId().equals(currentFunction.getFunctionId())).findAny().get();
                SeniorityLevelFunctionsRelationship seniorityLevelFunctionsRelationship = new SeniorityLevelFunctionsRelationship(function, seniorityLevelFunction, currentFunction.getAmount(), currentFunction.isAmountEditableAtUnit());
                seniorityLevelFunctionsRelationships.add(seniorityLevelFunctionsRelationship);
            });
            seniorityLevelFunctionRelationshipGraphRepository.saveAll(seniorityLevelFunctionsRelationships);
            seniorityLevelFunctions.add(seniorityLevelFunction);
        });
        return seniorityLevelFunctions;
    }

    private List<FunctionalPaymentQueryResult> convertToFlat( List<FunctionalPaymentQueryResult> functionalPaymentQueryResults1){
        List<FunctionalPaymentQueryResult> list = new ArrayList<>();
        for (FunctionalPaymentQueryResult current:functionalPaymentQueryResults1){
            FunctionalPaymentQueryResult functionalPaymentQueryResult=new FunctionalPaymentQueryResult(current.getId(),current.getStartDate(),current.getEndDate(),current.getExpertise(),current.getPaymentUnit());
            List<FunctionalPaymentMatrixQueryResult> functionalPaymentMatrixQueryResults=new ArrayList<>();
            List<FunctionalPaymentMatrixQueryResult> p=new ArrayList<>(current.getFunctionalPaymentMatrices());
            List<FunctionalPaymentMatrixDTO> functionalPaymentMatrixDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(p,FunctionalPaymentMatrixDTO.class);
            for (FunctionalPaymentMatrixQueryResult functionalPaymentMatrixQueryResult:p){
                FunctionalPaymentMatrixQueryResult functionalPaymentMatrixQueryResultNew=new FunctionalPaymentMatrixQueryResult();
                Set<Long> pgIds=new HashSet<>();
                for(Long pgId:functionalPaymentMatrixQueryResult.getPayGroupAreasIds()){
                    pgIds.add(pgId);
                }
                List<SeniorityLevelFunctionQR> seniorityLevelFunction=new ArrayList<>();
                for (SeniorityLevelFunctionQR seniorityLevelFunctionQR:functionalPaymentMatrixQueryResult.getSeniorityLevelFunction()){
                    List<FunctionQR> functionQRS=new ArrayList<>();
                    for(FunctionQR functionQR: seniorityLevelFunctionQR.getFunctions()){
                        FunctionQR functionQRNew =new FunctionQR(functionQR.getFunctionId(),functionQR.getFunctionName(),functionQR.getAmount(),functionQR.isAmountEditableAtUnit());
                        functionQRS.add(functionQRNew);
                    }
                    SeniorityLevelFunctionQR seniorityLevelFunctionQRNew=new SeniorityLevelFunctionQR(seniorityLevelFunctionQR.getSeniorityLevelId(),seniorityLevelFunctionQR.getFrom(),seniorityLevelFunctionQR.getTo(),functionQRS);
                    seniorityLevelFunction.add(seniorityLevelFunctionQRNew);
                }
                functionalPaymentMatrixQueryResultNew.setPayGroupAreasIds(pgIds);
                functionalPaymentMatrixQueryResultNew.setSeniorityLevelFunction(seniorityLevelFunction);
                functionalPaymentMatrixQueryResults.add(functionalPaymentMatrixQueryResult);
            }

            functionalPaymentQueryResult.setFunctionalPaymentMatrices(functionalPaymentMatrixQueryResults);
            list.add(functionalPaymentQueryResult);
        }
        return list;
    }


}





