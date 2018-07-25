package com.kairos.service.expertise;

import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.user.expertise.*;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentMatrixQueryResult;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.repository.user.country.FunctionGraphRepository;
import com.kairos.persistence.repository.user.expertise.*;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.user.country.experties.FunctionalPaymentMatrixDTO;
import com.kairos.user.country.experties.FunctionalSeniorityLevelDTO;
import com.kairos.user.country.experties.FunctionsDTO;
import com.kairos.user.country.experties.SeniorityLevelFunctionDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.rule_validator.functional_paymment.IsFunctionalPaymentAvailable;
import com.kairos.rule_validator.functional_paymment.IsGreaterThanStartDate;
import com.kairos.rule_validator.functional_paymment.IsGreaterThanToday;
import com.kairos.rule_validator.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FunctionalPaymentService extends UserBaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
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
        save(functionalPayment);
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
        save(functionalPayment.get());
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
        save(functionalPayment.get());
        return functionalPaymentMatrixDTOS;
    }

    private FunctionalPaymentMatrix addMatrixInFunctionalPayment(FunctionalPaymentMatrixDTO functionalPaymentMatrixDTO, List<SeniorityLevel> seniorityLevels, List<Function> functions) {
        FunctionalPaymentMatrix functionalPaymentMatrix = new FunctionalPaymentMatrix();
        if (Optional.ofNullable(functionalPaymentMatrixDTO.getPayGroupAreasIds()).isPresent() && !functionalPaymentMatrixDTO.getPayGroupAreasIds().isEmpty()) {
            List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllById(functionalPaymentMatrixDTO.getPayGroupAreasIds());
            if (payGroupAreas.size() != functionalPaymentMatrixDTO.getPayGroupAreasIds().size())
                exceptionService.actionNotPermittedException("message.multipleDataNotFound", "payGroup-areas");
            functionalPaymentMatrix.setPayGroupAreas(new HashSet<>(payGroupAreas));
        }
        functionalPaymentMatrix.setSeniorityLevelFunction(getSeniorityLevelFunction(functionalPaymentMatrixDTO.getSeniorityLevelFunction(), seniorityLevels, functions));
        save(functionalPaymentMatrix);
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
                        filter(function1 -> function1.getId().equals(currentFunction.getFunctionId())).findAny().get();
                SeniorityLevelFunctionsRelationship seniorityLevelFunctionsRelationship = new SeniorityLevelFunctionsRelationship(function, seniorityLevelFunction, currentFunction.getAmount());
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
        List<FunctionalPaymentMatrixDTO> functionalPaymentMatrixDTOS = functionalSeniorityLevelDTO.getFunctionalPaymentMatrix();
        List<FunctionalPaymentMatrix> list = new ArrayList<FunctionalPaymentMatrix>(1);
        List<Function> functions = getFunctions(functionalPaymentMatrixDTOS);
        List<SeniorityLevel> seniorityLevels = getSeniorityLevel(functionalPaymentMatrixDTOS);

        Optional<FunctionalPayment> functionalPayment = functionalPaymentGraphRepository.findById(functionalSeniorityLevelDTO.getFunctionalPaymentId());
        if (!functionalPayment.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "functionalpayment", functionalSeniorityLevelDTO.getFunctionalPaymentId());
        }

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
            save(functionalPaymentCopy);
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
                        List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllById(functionalPaymentMatrixDTO.getPayGroupAreasIds());
                        functionalPaymentMatrix.setPayGroupAreas(new HashSet<>(payGroupAreas));
                    }

                    functionalPaymentMatrix.setSeniorityLevelFunction(getSeniorityLevelFunction(functionalPaymentMatrixDTO.getSeniorityLevelFunction(), seniorityLevels, functions));
                    // find object by db and update in that
                }
                functionalPaymentMatrixDTO.setId(functionalPaymentMatrix.getId());
                list.add(functionalPaymentMatrix);
            });
            functionalPayment.get().setFunctionalPaymentMatrices(list);
            save(functionalPayment.get());
        }
        return functionalSeniorityLevelDTO;
    }

    public FunctionalPaymentDTO publishFunctionalPayment(Long functionalPaymentId, FunctionalPaymentDTO functionalPaymentDTO) {
        Optional<FunctionalPayment> functionalPayment = functionalPaymentGraphRepository.findById(functionalPaymentId);
        if (!functionalPayment.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "functionalpayment", functionalPaymentDTO.getId());
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
        save(functionalPayment.get());
        return parentFunctionalPayment;

    }

}
