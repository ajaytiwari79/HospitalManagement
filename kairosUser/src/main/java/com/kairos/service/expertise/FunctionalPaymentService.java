package com.kairos.service.expertise;

import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.FunctionalPayment;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.FunctionalPaymentGraphRepository;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.specification.IsFunctionalPaymentAvailable;
import com.kairos.specification.IsGreaterThanStartDate;
import com.kairos.specification.IsGreaterThanToday;
import com.kairos.specification.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FunctionalPaymentService extends UserBaseService {

    private ExpertiseGraphRepository expertiseGraphRepository;
    private ExceptionService exceptionService;
    private FunctionalPaymentGraphRepository functionalPaymentGraphRepository;


    public FunctionalPaymentService(ExpertiseGraphRepository expertiseGraphRepository, ExceptionService exceptionService, FunctionalPaymentGraphRepository functionalPaymentGraphRepository) {
        this.expertiseGraphRepository = expertiseGraphRepository;
        this.exceptionService = exceptionService;
        this.functionalPaymentGraphRepository = functionalPaymentGraphRepository;

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

        Specification<FunctionalPaymentDTO> isGreaterThanStartDateAndToday = new IsGreaterThanStartDate(expertise, exceptionService)
                .and(new IsGreaterThanToday(exceptionService))
                .and(new IsFunctionalPaymentAvailable(functionalPaymentDTO.getExpertiseId(), functionalPaymentGraphRepository, exceptionService));

        isGreaterThanStartDateAndToday.isSatisfied(functionalPaymentDTO);
        FunctionalPayment functionalPayment = new FunctionalPayment(expertise, functionalPaymentDTO.getStartDate(), functionalPaymentDTO.getEndDate(), functionalPaymentDTO.getPaidOutFrequency());
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
        functionalPayment.get().setPaidOutFrequency(functionalPaymentDTO.getPaidOutFrequency());
        functionalPayment.get().setEndDate(functionalPaymentDTO.getEndDate());
        save(functionalPayment.get());
        functionalPaymentDTO.setId(functionalPayment.get().getId());
        return functionalPaymentDTO;
    }
    /* if (Optional.ofNullable(seniorityLevelDTO.getPayGroupAreasIds()).isPresent() && !seniorityLevelDTO.getPayGroupAreasIds().isEmpty()) {
            List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllById(seniorityLevelDTO.getPayGroupAreasIds());
            if (payGroupAreas.size() != seniorityLevelDTO.getPayGroupAreasIds().size())
                exceptionService.actionNotPermittedException("message.multipleDataNotFound", "payGroup-areas");
            seniorityLevel.setPayGroupAreas(payGroupAreas);
        }


        List<SeniorityLevelFunctionsRelationship> seniorityLevelFunctionsRelationships = new ArrayList<>();
        if (Optional.ofNullable(seniorityLevelDTO.getFunctions()).isPresent() && !seniorityLevelDTO.getFunctions().isEmpty()) {
            Set<Long> functionIds = seniorityLevelDTO.getFunctions().stream().map(FunctionsDTO::getFunctionId).collect(Collectors.toSet());
            List<Function> functions = functionGraphRepository.findAllFunctionsById(functionIds);
            if (functions.size() != functionIds.size()) {
                exceptionService.actionNotPermittedException("message.multipleDataNotFound", "functions");
            }
            for (FunctionsDTO functionDTO : seniorityLevelDTO.getFunctions()) {
                Function currentFunction = functions.stream().filter(f -> f.getId().equals(functionDTO.getFunctionId())).findFirst().get();
                SeniorityLevelFunctionsRelationship functionsRelationship = new SeniorityLevelFunctionsRelationship(seniorityLevel, currentFunction, functionDTO.getAmount());
                seniorityLevelFunctionsRelationships.add(functionsRelationship);
            }
        }
seniorityLevelFunctionRelationshipGraphRepository.saveAll(seniorityLevelFunctionsRelationships);


if (Optional.ofNullable(functionAndSeniorityLevel.getPayGroupAreas()).isPresent() && !functionAndSeniorityLevel.getPayGroupAreas().isEmpty()) {
            Set<Long> payGroupAreasId = functionAndSeniorityLevel.getPayGroupAreas().stream().map(PayGroupArea::getId).collect(Collectors.toSet());
            seniorityLevelDTO.setPayGroupAreasIds(payGroupAreasId);
        }

        if (Optional.ofNullable(functionAndSeniorityLevel.getFunctions()).isPresent() && !functionAndSeniorityLevel.getFunctions().isEmpty()) {
            List<FunctionsDTO> allFunctions = new ArrayList<>();
            for (Map<String, Object> currentFunction : functionAndSeniorityLevel.getFunctions()) {
                BigDecimal functionAmount = new BigDecimal(currentFunction.get("amount").toString());
                Long currentFunctionId = (Long) currentFunction.get("functionId");
                FunctionsDTO function = new FunctionsDTO(functionAmount, currentFunctionId);
                allFunctions.add(function);
            }
            seniorityLevelDTO.setFunctions(allFunctions);
        }




if (Optional.ofNullable(seniorityLevelDTO.getFunctions()).isPresent()) {
            List<SeniorityLevelFunctionsRelationship> seniorityLevelFunctionsRelationships = new ArrayList();
            seniorityLevelGraphRepository.removeAllPreviousFunctionsFromSeniorityLevel(seniorityLevelDTO.getId());
            Set<Long> functionIds = seniorityLevelDTO.getFunctions().stream().map(FunctionsDTO::getFunctionId).collect(Collectors.toSet());
            List<Function> functions = functionGraphRepository.findAllFunctionsById(functionIds);
            for (FunctionsDTO functionDTO : seniorityLevelDTO.getFunctions()) {
                Function currentFunction = functions.stream().filter(f -> f.getId().equals(functionDTO.getFunctionId())).findFirst().get();
                SeniorityLevelFunctionsRelationship functionsRelationship = new SeniorityLevelFunctionsRelationship(seniorityLevel, currentFunction, functionDTO.getAmount());
                seniorityLevelFunctionsRelationships.add(functionsRelationship);
            }
            seniorityLevelFunctionRelationshipGraphRepository.saveAll(seniorityLevelFunctionsRelationships);
        }
if (Optional.ofNullable(functionAndSeniorityLevel.getFunctions()).isPresent() && !functionAndSeniorityLevel.getFunctions().isEmpty()) {
                    for (Map<String, Object> currentObject : functionAndSeniorityLevel.getFunctions()) {
                        BigDecimal functionAmount = new BigDecimal(currentObject.get("amount").toString());
                        Function currentFunction = new Function();
                        convertToFunctionObjectFromMap(currentFunction, currentObject);
                        SeniorityLevelFunctionsRelationship functionsRelationship = new SeniorityLevelFunctionsRelationship(seniorityLevel, currentFunction, functionAmount);
                        seniorityLevelFunctionsRelationships.add(functionsRelationship);
                    }
                }

        */

}
