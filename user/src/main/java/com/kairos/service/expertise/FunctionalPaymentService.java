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
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentMatrixQueryResult;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentQueryResult;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.expertise.*;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.functional_paymment.IsFunctionalPaymentAvailable;
import com.kairos.rule_validator.functional_paymment.IsGreaterThanStartDate;
import com.kairos.rule_validator.functional_paymment.IsGreaterThanToday;
import com.kairos.service.exception.ExceptionService;
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
public class FunctionalPaymentService{

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

    private List<SeniorityLevelFunction> getSeniorityLevelFunction(List<SeniorityLevelFunctionDTO> seniorityLevelFunctionDTOS, List<SeniorityLevel> seniorityLevels1, List<Function> functions) {
        List<SeniorityLevelFunction> seniorityLevelFunctions = new ArrayList<>();
        List<SeniorityLevel> seniorityLevels=ObjectMapperUtils.copyPropertiesOfListByMapper(seniorityLevels1,SeniorityLevel.class);
        seniorityLevelFunctionDTOS.forEach(currentSRLevelFunction -> {
            List<SeniorityLevelFunctionsRelationship> seniorityLevelFunctionsRelationships = new ArrayList<>();

            SeniorityLevel seniorityLevel = seniorityLevels.stream().
                    filter(seniorityLevel1 -> seniorityLevel1.getId().equals(currentSRLevelFunction.getSeniorityLevelId())).findAny().get();
            SeniorityLevelFunction seniorityLevelFunction = new SeniorityLevelFunction();
            seniorityLevelFunction.setSeniorityLevel(seniorityLevel);

            currentSRLevelFunction.getFunctions().forEach(currentFunction -> {
                Function function = functions.stream().
                        filter(function1 -> function1.getId().equals(currentFunction.getId())).findAny().get();
                SeniorityLevelFunctionsRelationship seniorityLevelFunctionsRelationship = new SeniorityLevelFunctionsRelationship(function, seniorityLevelFunction, currentFunction.getAmount(),currentFunction.isAmountEditableAtUnit());
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
        List<SeniorityLevel> seniorityLevels = ObjectMapperUtils.copyPropertiesOfListByMapper(getSeniorityLevel(functionalPaymentMatrixDTOS),SeniorityLevel.class);

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
        if (functionalPayment.get().getFunctionalPaymentMatrices().isEmpty()){
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


    public void updateAmountInFunctionalTable(Long payTableId, Date startDate, Date endDate, BigDecimal percentageValue){

        List<FunctionalPayment> functionalPaymentList=seniorityLevelFunctionRelationshipGraphRepository.findAllActiveByPayTableId(payTableId,startDate.getTime(),null);
        List<FunctionalPayment> toBreakInNewList=new ArrayList<>();
        List<FunctionalPayment> toUpdateInExisting=new ArrayList<>();
        for (FunctionalPayment functionalPayment:functionalPaymentList){
            if(endDate==null){
                if(functionalPayment.getStartDate().isAfter(DateUtils.asLocalDate(startDate).minusDays(1))){
                    toUpdateInExisting.add(functionalPayment);
                }
                else if(functionalPayment.getStartDate().isBefore(DateUtils.asLocalDate(startDate))){
                    toBreakInNewList.add(functionalPayment);
                }
            }
            else {
                if(functionalPayment.getEndDate()!=null || (functionalPayment.getStartDate().isAfter(DateUtils.asLocalDate(startDate).minusDays(1)) && functionalPayment.getEndDate().isBefore(DateUtils.asLocalDate(startDate).plusDays(1)))){
                    toUpdateInExisting.add(functionalPayment);
                }
                else {
                    toBreakInNewList.add(functionalPayment);
                }
            }
        }

        functionalPaymentGraphRepository.updateFunctionalAmount(toUpdateInExisting.stream().map(FunctionalPayment::getId).collect(Collectors.toList()),percentageValue);
        List<FunctionalPaymentQueryResult> functionalPaymentQueryResults=functionalPaymentGraphRepository.getFunctionalPaymentDataT(toBreakInNewList.stream().map(FunctionalPayment::getId).collect(Collectors.toList()));
        List<FunctionalPayment> functionalPayments=functionalPaymentGraphRepository.findAllById(toBreakInNewList.stream().map(FunctionalPayment::getId).collect(Collectors.toList()));
        Map<Long,FunctionalPayment> functionalPaymentMap=functionalPayments.stream().collect(Collectors.toMap(FunctionalPayment::getId,Functions.identity()));
        List<FunctionalPayment> outside=new ArrayList<>();
        List<FunctionalPayment> inside=new ArrayList<>();
        List<FunctionalPayment> allFunctionalPayments=new ArrayList<>();


        for (FunctionalPaymentQueryResult functionalPaymentQueryResult:functionalPaymentQueryResults){

            if(functionalPaymentQueryResult.getEndDate()==null && functionalPaymentQueryResult.getStartDate().isBefore(DateUtils.asLocalDate(startDate))){
                FunctionalPayment existing=functionalPaymentMap.get(functionalPaymentQueryResult.getId());
                existing.setStartDate(functionalPaymentQueryResult.getStartDate());
                existing.setEndDate(DateUtils.asLocalDate(startDate).minusDays(1));

                outside.add(existing);

                //Creating new and updating values


                FunctionalPayment createNew = new FunctionalPayment(functionalPaymentQueryResult.getExpertise(), functionalPaymentQueryResult.getStartDate(),
                        functionalPaymentQueryResult.getEndDate(), functionalPaymentQueryResult.getPaymentUnit());

                //createNewMatrixInFunctionalPayment(functionalPaymentQueryResult,createNew);
                createNew.setFunctionalPaymentMatrices(functionalPaymentQueryResult.getFunctionalPaymentMatrices());
                inside.add(createNew);
            }
        }
        allFunctionalPayments.addAll(inside);
        allFunctionalPayments.addAll(outside);
        functionalPaymentGraphRepository.saveAll(allFunctionalPayments);
}


    public List<FunctionalPaymentQueryResult> test(List<Long> ids){
        List<FunctionalPaymentQueryResult> functionalPaymentQueryResults=functionalPaymentGraphRepository.getFunctionalPaymentDataT(Arrays.asList(new Long("3362")));
        return functionalPaymentQueryResults;

    }

    private List<SeniorityLevel> getSeniorityLevelFromFunctionalPaymentMatrix(List<FunctionalPaymentMatrix> functionalPaymentMatrices){
        List<SeniorityLevel> seniorityLevels=new ArrayList<>();
        for (FunctionalPaymentMatrix functionalPaymentMatrix:functionalPaymentMatrices){
            for(SeniorityLevelFunction seniorityLevelFunction:functionalPaymentMatrix.getSeniorityLevelFunction()){
                seniorityLevels.add(seniorityLevelFunction.getSeniorityLevel());
            }
        }
        return seniorityLevels;
    }

//    private List<Function> getFunctionsFromFunctionalPaymentMatrix(List<FunctionalPaymentMatrix> functionalPaymentMatrices){
//        List<Function> functions=new ArrayList<>();
//        for (FunctionalPaymentMatrix functionalPaymentMatrix:functionalPaymentMatrices){
//            for(SeniorityLevelFunction seniorityLevelFunction:functionalPaymentMatrix.getSeniorityLevelFunction()){
//                functions.add();
//            }
//        }
//        return functions;
//    }


//    public FunctionalSeniorityLevelDTO createNewMatrixInFunctionalPayment(FunctionalPaymentQueryResult functionalSeniorityLevelDTO,FunctionalPayment functionalPayment) {
//        List<FunctionalPaymentMatrix> functionalPaymentMatrixDTOS = functionalSeniorityLevelDTO.getFunctionalPaymentMatrices();
//        List<FunctionalPaymentMatrix> list = new ArrayList<>(1);
//        List<Function> functions =getFunctionsFromFunctionalPaymentMatrix(functionalSeniorityLevelDTO.getFunctionalPaymentMatrices());
//        List<SeniorityLevel> seniorityLevels = getSeniorityLevel(functionalPaymentMatrixDTOS);
//
//            // functional payment is published so we need to create a  new copy and update in same
//            FunctionalPayment functionalPaymentCopy = new FunctionalPayment(functionalPayment.getExpertise(), functionalPayment.getStartDate(),
//                    functionalPayment.getEndDate(), functionalPayment.getPaymentUnit());
//
//            functionalPaymentMatrixDTOS.forEach(functionalPaymentMatrixDTO -> {
//                FunctionalPaymentMatrix functionalPaymentMatrix = addMatrixInFunctionalPayment(functionalPaymentMatrixDTO, seniorityLevels, functions);
//                functionalPaymentMatrixDTO.setId(functionalPaymentMatrix.getId());
//                list.add(functionalPaymentMatrix);
//            });
//            functionalPaymentCopy.setFunctionalPaymentMatrices(list);
//
//            functionalPayment.setHasDraftCopy(true);
//            functionalPaymentCopy.setParentFunctionalPayment(functionalPayment);
//            functionalPaymentGraphRepository.save(functionalPaymentCopy);
//            functionalSeniorityLevelDTO.setFunctionalPaymentId(functionalPaymentCopy.getId());
//    }



}
