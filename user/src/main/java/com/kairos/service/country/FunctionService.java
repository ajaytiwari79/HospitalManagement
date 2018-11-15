package com.kairos.service.country;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.FunctionDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.UnitPositionFunctionRelationship;
import com.kairos.persistence.model.user.unit_position.UnitPositionFunctionRelationshipQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionFunctionRelationshipRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UnitPositionFunctionRelationshipRepository unitPositionFunctionRelationshipRepository;


    public com.kairos.persistence.model.country.functions.FunctionDTO createFunction(Long countryId, FunctionDTO functionDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);

        }
        Function isAlreadyExists = functionGraphRepository.findByNameIgnoreCase(countryId, functionDTO.getName());
        if (Optional.ofNullable(isAlreadyExists).isPresent()) {
            exceptionService.duplicateDataException("message.function.name.alreadyExist", functionDTO.getName());

        }
        List<Level> levels = new ArrayList<>();
        if (!functionDTO.getOrganizationLevelIds().isEmpty()) {
            levels = countryGraphRepository.getLevelsByIdsIn(countryId, functionDTO.getOrganizationLevelIds());
        }
        List<Organization> unions = new ArrayList<>();
        if (!functionDTO.getUnionIds().isEmpty()) {
            unions = organizationGraphRepository.findUnionsByIdsIn(functionDTO.getUnionIds());
        }
        Function function = new Function(functionDTO.getName(), functionDTO.getDescription(), functionDTO.getStartDate(), functionDTO.getEndDate(), unions, levels, country, functionDTO.getIcon());
        functionGraphRepository.save(function);
        com.kairos.persistence.model.country.functions.FunctionDTO functionResponseDTO = new com.kairos.persistence.model.country.functions.FunctionDTO(function.getId(), function.getName(), function.getDescription(),
                function.getStartDate(), function.getEndDate(), function.getUnions(), function.getOrganizationLevels(), function.getIcon());

        return functionResponseDTO;
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
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);

        }
        Function function = functionGraphRepository.findOne(functionDTO.getId());
        if (!Optional.ofNullable(function).isPresent() || function.isDeleted() == true) {
            exceptionService.dataNotFoundByIdException("message.function.id.notFound", functionDTO.getId());

        }
        Function isNameAlreadyExists = functionGraphRepository.findByNameExcludingCurrent(countryId, functionDTO.getId(), functionDTO.getName().trim());
        if (Optional.ofNullable(isNameAlreadyExists).isPresent()) {
            exceptionService.duplicateDataException("message.function.name.alreadyExist", functionDTO.getName());

        }
        List<Level> levels = new ArrayList<>();
        if (!functionDTO.getOrganizationLevelIds().isEmpty()) {
            levels = countryGraphRepository.getLevelsByIdsIn(countryId, functionDTO.getOrganizationLevelIds());
        }
        List<Organization> unions = new ArrayList<>();
        if (!functionDTO.getUnionIds().isEmpty()) {
            unions = organizationGraphRepository.findUnionsByIdsIn(functionDTO.getUnionIds());
        }

        function.setName(functionDTO.getName());
        function.setDescription(functionDTO.getDescription());
        function.setStartDate(functionDTO.getStartDate());
        function.setEndDate(functionDTO.getEndDate());
        function.setUnions(unions);
        function.setOrganizationLevels(levels);
        function.setIcon(functionDTO.getIcon());
        functionGraphRepository.save(function);
        com.kairos.persistence.model.country.functions.FunctionDTO functionResponseDTO = new com.kairos.persistence.model.country.functions.FunctionDTO(function.getId(), function.getName(), function.getDescription(),
                function.getStartDate(), function.getEndDate(), function.getUnions(), function.getOrganizationLevels(), function.getIcon());

        return functionResponseDTO;
    }

    public boolean deleteFunction(long functionId) {
        Function function = functionGraphRepository.findOne(functionId);
        if (!Optional.ofNullable(function).isPresent() || function.isDeleted() == true) {
            exceptionService.dataNotFoundByIdException("message.function.id.notFound", functionId);

        }
        function.setDeleted(true);
        functionGraphRepository.save(function);
        return true;
    }

    public List<com.kairos.persistence.model.country.functions.FunctionDTO> getFunctionsByExpertiseId(long expertiseId) {
        return functionGraphRepository.getFunctionsByExpertiseId(expertiseId);

    }

    //====================================================================================
    public Map<Long, Map<Long, Set<LocalDate>>> getUnitPositionIdWithFunctionIdShiftDateMap(Set<Long> unitPositionIds) {
        Map<Long, Map<Long, Set<LocalDate>>> unitPositionWithFunctionIdAndLocalDateMap = new HashMap<>();
        if (!unitPositionIds.isEmpty()) {
            List<UnitPositionFunctionRelationshipQueryResult> unitPositionFunctionRelationships = unitPositionFunctionRelationshipRepository.getApplicableFunctionIdWithDatesByUnitPositionId(unitPositionIds);
            if (!unitPositionFunctionRelationships.isEmpty()) {
                for (UnitPositionFunctionRelationshipQueryResult unitPositionFunctionRelationship : unitPositionFunctionRelationships) {
                    Map<Long, Set<LocalDate>> functionIdWithAppliedDates = new HashMap<>();
                    functionIdWithAppliedDates.put(unitPositionFunctionRelationship.getFunction().getId(), unitPositionFunctionRelationship.getAppliedDates());
                    unitPositionWithFunctionIdAndLocalDateMap.put(unitPositionFunctionRelationship.getUnitPosition().getId(), functionIdWithAppliedDates);
                }
            }
            return unitPositionWithFunctionIdAndLocalDateMap;
        } else {
            //TODO throw exception
        }
        return null;
    }

    //=================================================================================

    /**
     * Assuming one function per Date per unitPosition
     * @param unitPositionIdWithShiftDateFunctionIdMap
     * @return
     */
    public boolean updateUnitPositionFunctionRelationShipDates(Map<Long, Map<LocalDate, Long>> unitPositionIdWithShiftDateFunctionIdMap) {
        boolean result = false;
        List<UnitPositionFunctionRelationshipQueryResult> unitPositionFunctionRelationshipQueryResults = unitPositionFunctionRelationshipRepository.getApplicableFunctionsWithRelationShipIByUnitPositionId(unitPositionIdWithShiftDateFunctionIdMap.keySet());
        List<UnitPositionFunctionRelationship> updatedUnitPositionFunctionRelationshipList = new ArrayList<>();
        if (!unitPositionFunctionRelationshipQueryResults.isEmpty()) {
            for (UnitPositionFunctionRelationshipQueryResult unitPositionFunctionRelationshipQueryResult : unitPositionFunctionRelationshipQueryResults) {
                UnitPositionFunctionRelationship unitPositionFunctionRelationship = new UnitPositionFunctionRelationship();
                unitPositionFunctionRelationship.setId(unitPositionFunctionRelationshipQueryResult.getId());
                Function existingFunction = unitPositionFunctionRelationshipQueryResult.getFunction();
                UnitPosition existingUnitPosition = unitPositionFunctionRelationshipQueryResult.getUnitPosition();
                unitPositionFunctionRelationship.setFunction(existingFunction);
                unitPositionFunctionRelationship.setUnitPosition(existingUnitPosition);
                Set<LocalDate> existingAppliedDates = unitPositionFunctionRelationshipQueryResult.getAppliedDates();
                Map<LocalDate, Long> localDateFunctionIdMap = unitPositionIdWithShiftDateFunctionIdMap.get(existingUnitPosition.getId());
                Set<LocalDate> datesToBeAdded = localDateFunctionIdMap.keySet().stream().filter(date -> existingFunction.getId().equals(localDateFunctionIdMap.get(date))).collect(Collectors.toSet());
                Set<LocalDate> datesToBeRemoved = localDateFunctionIdMap.keySet().stream().filter(date -> ((localDateFunctionIdMap.get(date)==null) && existingAppliedDates.contains(date))).collect(Collectors.toSet());
                if (!datesToBeAdded.isEmpty() || !datesToBeRemoved.isEmpty()) {
                    existingAppliedDates.addAll(datesToBeAdded);
                    existingAppliedDates.removeAll(datesToBeRemoved);
                }
                unitPositionFunctionRelationship.setDate(existingAppliedDates);
                updatedUnitPositionFunctionRelationshipList.add(unitPositionFunctionRelationship);
            }
            unitPositionFunctionRelationshipRepository.saveAll(updatedUnitPositionFunctionRelationshipList);
            result = true;
        }
        return result;
    }
}
