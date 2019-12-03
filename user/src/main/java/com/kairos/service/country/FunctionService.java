package com.kairos.service.country;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.FunctionDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.employment.Employment;
import com.kairos.persistence.model.user.employment.EmploymentFunctionRelationship;
import com.kairos.persistence.model.user.employment.EmploymentFunctionRelationshipQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentFunctionRelationshipRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user_context.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.UserMessagesConstants.*;

/**
 * Created by pavan on 13/3/18.
 */
@Service
@Transactional
public class FunctionService {

    @Inject
    private CountryGraphRepository countryGraphRepository;

    @Inject
    private FunctionGraphRepository functionGraphRepository;
    @Inject
    private CountryService countryService;

    @Inject
    private UnitGraphRepository unitGraphRepository;

    @Inject
    private ExceptionService exceptionService;
    @Inject
    private EmploymentFunctionRelationshipRepository employmentFunctionRelationshipRepository;


    public com.kairos.persistence.model.country.functions.FunctionDTO createFunction(Long countryId, FunctionDTO functionDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }
        Function isAlreadyExists = functionGraphRepository.findByNameIgnoreCase(countryId, functionDTO.getName());
        if (Optional.ofNullable(isAlreadyExists).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_FUNCTION_NAME_ALREADYEXIST, functionDTO.getName());

        }
        List<Level> levels = new ArrayList<>();
        if (!functionDTO.getOrganizationLevelIds().isEmpty()) {
            levels = countryGraphRepository.getLevelsByIdsIn(countryId, functionDTO.getOrganizationLevelIds());
        }
        List<Organization> unions = new ArrayList<>();
        if (!functionDTO.getUnionIds().isEmpty()) {
            unions = unitGraphRepository.findUnionsByIdsIn(functionDTO.getUnionIds());
        }
        Function function = new Function(functionDTO.getName(), functionDTO.getDescription(), functionDTO.getStartDate(), functionDTO.getEndDate(), unions, levels, country, functionDTO.getIcon(),functionDTO.getCode());
        functionGraphRepository.save(function);
        return new com.kairos.persistence.model.country.functions.FunctionDTO(function.getId(), function.getName(), function.getDescription(),
                function.getStartDate(), function.getEndDate(), function.getUnions(), function.getOrganizationLevels(), function.getIcon(),function.getCode());

    }

    public List<com.kairos.persistence.model.country.functions.FunctionDTO> getFunctionsByCountry(long countryId) {
        return functionGraphRepository.findFunctionsByCountry(countryId);

    }

    public List<com.kairos.persistence.model.country.functions.FunctionDTO> getFunctionsIdAndNameByCountry(long countryId) {
        return functionGraphRepository.findFunctionsIdAndNameByCountry(countryId);
    }

    public com.kairos.persistence.model.country.functions.FunctionDTO updateFunction(Long countryId, FunctionDTO functionDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }
        Function function = functionGraphRepository.findOne(functionDTO.getId());
        if (!Optional.ofNullable(function).isPresent() || function.isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_FUNCTION_ID_NOTFOUND, functionDTO.getId());

        }
        Function isNameAlreadyExists = functionGraphRepository.findByNameExcludingCurrent(countryId, functionDTO.getId(), functionDTO.getName().trim());
        if (Optional.ofNullable(isNameAlreadyExists).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_FUNCTION_NAME_ALREADYEXIST, functionDTO.getName());

        }
        List<Level> levels = new ArrayList<>();
        if (!functionDTO.getOrganizationLevelIds().isEmpty()) {
            levels = countryGraphRepository.getLevelsByIdsIn(countryId, functionDTO.getOrganizationLevelIds());
        }
        List<Organization> unions = new ArrayList<>();
        if (!functionDTO.getUnionIds().isEmpty()) {
            unions = unitGraphRepository.findUnionsByIdsIn(functionDTO.getUnionIds());
        }

        function.setName(functionDTO.getName());
        function.setDescription(functionDTO.getDescription());
        function.setStartDate(functionDTO.getStartDate());
        function.setEndDate(functionDTO.getEndDate());
        function.setUnions(unions);
        function.setOrganizationLevels(levels);
        function.setIcon(functionDTO.getIcon());
        function.setCode(functionDTO.getCode());
        functionGraphRepository.save(function);

        return new com.kairos.persistence.model.country.functions.FunctionDTO(function.getId(), function.getName(), function.getDescription(),
                function.getStartDate(), function.getEndDate(), function.getUnions(), function.getOrganizationLevels(), function.getIcon(),function.getCode());
    }

    public boolean deleteFunction(long functionId) {
        Function function = functionGraphRepository.findOne(functionId);
        if (!Optional.ofNullable(function).isPresent() || function.isDeleted() == true) {
            exceptionService.dataNotFoundByIdException(MESSAGE_FUNCTION_ID_NOTFOUND, functionId);

        }
        function.setDeleted(true);
        functionGraphRepository.save(function);
        return true;
    }

    public List<com.kairos.persistence.model.country.functions.FunctionDTO> getFunctionsByExpertiseId(long expertiseId) {
        return functionGraphRepository.getFunctionsByExpertiseId(expertiseId);

    }

    public Map<Long, Map<Long, Set<LocalDate>>> getEmploymentIdWithFunctionIdShiftDateMap(Set<Long> employmentIds) {
        Map<Long, Map<Long, Set<LocalDate>>> employmentWithFunctionIdAndLocalDateMap = new HashMap<>();
        if (!employmentIds.isEmpty()) {
            List<EmploymentFunctionRelationshipQueryResult> employmentFunctionRelationships = employmentFunctionRelationshipRepository.getApplicableFunctionIdWithDatesByEmploymentIds(employmentIds);
            if (!employmentFunctionRelationships.isEmpty()) {
                for (EmploymentFunctionRelationshipQueryResult employmentFunctionRelationship : employmentFunctionRelationships) {
                    Map<Long, Set<LocalDate>> functionIdWithAppliedDates = new HashMap<>();
                    functionIdWithAppliedDates.put(employmentFunctionRelationship.getFunction().getId(), employmentFunctionRelationship.getAppliedDates());
                    employmentWithFunctionIdAndLocalDateMap.put(employmentFunctionRelationship.getEmployment().getId(), functionIdWithAppliedDates);
                }
            }
            return employmentWithFunctionIdAndLocalDateMap;
        } else {
            //TODO throw exception
        }
        return null;
    }

    /**
     * Assuming one function per Date per employment
     *
     * @param employmentIdWithShiftDateFunctionIdMap
     * @return
     */
    public boolean updateEmploymentFunctionRelationShipDates(Map<Long, Map<LocalDate, Long>> employmentIdWithShiftDateFunctionIdMap) {
        boolean result = false;
        List<EmploymentFunctionRelationshipQueryResult> employmentFunctionRelationshipQueryResults = employmentFunctionRelationshipRepository.getApplicableFunctionsWithRelationShipIByEmploymentId(employmentIdWithShiftDateFunctionIdMap.keySet());
        List<EmploymentFunctionRelationship> updatedEmploymentFunctionRelationshipList = new ArrayList<>();
        if (!employmentFunctionRelationshipQueryResults.isEmpty()) {
            for (EmploymentFunctionRelationshipQueryResult employmentFunctionRelationshipQueryResult : employmentFunctionRelationshipQueryResults) {
                EmploymentFunctionRelationship employmentFunctionRelationship = new EmploymentFunctionRelationship();
                employmentFunctionRelationship.setId(employmentFunctionRelationshipQueryResult.getId());
                Function existingFunction = employmentFunctionRelationshipQueryResult.getFunction();
                Employment existingEmployment = employmentFunctionRelationshipQueryResult.getEmployment();
                employmentFunctionRelationship.setFunction(existingFunction);
                employmentFunctionRelationship.setEmployment(existingEmployment);
                Set<LocalDate> existingAppliedDates = employmentFunctionRelationshipQueryResult.getAppliedDates();
                Map<LocalDate, Long> localDateFunctionIdMap = employmentIdWithShiftDateFunctionIdMap.get(existingEmployment.getId());
                Set<LocalDate> datesToBeAdded = localDateFunctionIdMap.keySet().stream().filter(date -> existingFunction.getId().equals(localDateFunctionIdMap.get(date))).collect(Collectors.toSet());
                Set<LocalDate> datesToBeRemoved = localDateFunctionIdMap.keySet().stream().filter(date -> ((localDateFunctionIdMap.get(date) == null) && existingAppliedDates.contains(date))).collect(Collectors.toSet());
                if (!datesToBeAdded.isEmpty() || !datesToBeRemoved.isEmpty()) {
                    existingAppliedDates.addAll(datesToBeAdded);
                    existingAppliedDates.removeAll(datesToBeRemoved);
                }
                employmentFunctionRelationship.setDate(existingAppliedDates);
                updatedEmploymentFunctionRelationshipList.add(employmentFunctionRelationship);
            }
            employmentFunctionRelationshipRepository.saveAll(updatedEmploymentFunctionRelationshipList);
            result = true;
        }
        return result;
    }

    public List<com.kairos.persistence.model.country.functions.FunctionDTO> getFunctionsAtUnit(Long unitId) {
        Long countryId = UserContext.getUserDetails().getCountryId();
        return functionGraphRepository.findFunctionsByCountry(countryId);
    }

    public Map<LocalDate, List<FunctionDTO>> findAppliedFunctionsAtEmployment(Long unitId, String startDate, String endDate) {
        List<EmploymentQueryResult> employmentQueryResults = ObjectMapperUtils.copyPropertiesOfListByMapper(functionGraphRepository.findAppliedFunctionsAtEmpployment(unitId, startDate, endDate), EmploymentQueryResult.class);
        Map<LocalDate, List<FunctionDTO>> dateWiseFunctionMap = new HashMap<>();
        for (EmploymentQueryResult employmentQueryResult : employmentQueryResults) {
            for (com.kairos.persistence.model.country.functions.FunctionDTO appliedFunctionDTO : employmentQueryResult.getAppliedFunctions()) {
                for (LocalDate localDate : appliedFunctionDTO.getAppliedDates()) {
                    FunctionDTO functionDTO = new FunctionDTO(appliedFunctionDTO.getId(), appliedFunctionDTO.getName(), appliedFunctionDTO.getIcon());
                    functionDTO.setEmploymentId(employmentQueryResult.getId());
                    functionDTO.setCode(appliedFunctionDTO.getCode());
                    List<FunctionDTO> functionDTOS = dateWiseFunctionMap.getOrDefault(localDate, new ArrayList<>());
                    functionDTOS.add(functionDTO);
                    dateWiseFunctionMap.put(localDate, functionDTOS);
                }
            }
        }
        return dateWiseFunctionMap;
    }

    public List<LocalDate> getAllDateByFunctionIds(Long unitId, List<Long> functionIds) {
        return functionGraphRepository.findAllDateByFunctionIds(unitId, functionIds);
    }
}
