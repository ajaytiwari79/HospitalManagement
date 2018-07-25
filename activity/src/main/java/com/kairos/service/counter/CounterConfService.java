package com.kairos.service.counter;

import com.kairos.activity.counter.CategoryAssignmentDTO;
import com.kairos.activity.counter.FilterCriteria;
import com.kairos.activity.counter.KPICategoryDTO;
import com.kairos.activity.counter.KPICategoryUpdationDTO;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.activity.counter.enums.CounterType;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Service
public class CounterConfService extends MongoBaseService {

    @Inject
    ExceptionService exceptionService;
    @Inject
    private CounterRepository counterRepository;

    public void updateCounterCriteria(BigInteger counterId, List<FilterCriteria> criteriaList){
        Counter counter = (Counter) counterRepository.getItemById(counterId, Counter.class);
        counter.setCriteriaList(criteriaList);
        save(counter);
    }

    private void verifyForValidCounterType(CounterType type){
        boolean validCounterType = Arrays.stream(CounterType.values()).filter(s -> s.equals(type)).findAny().isPresent();
        if(!validCounterType) exceptionService.invalidRequestException("error.counterType.invalid", type);
    }

    private void verifyForCounterDuplicacy(CounterType type){
        Optional<Counter> counterContainer = Optional.ofNullable(counterRepository.getCounterByType(type));
        if(counterContainer.isPresent()) exceptionService.duplicateDataException("error.counterType.duplicate", type);
    }

    public void addCounter(Counter counter){
        verifyForValidCounterType(counter.getType());
        verifyForCounterDuplicacy(counter.getType());
        save(counter);
    }

    private void verifyForCategoryAvailability(List<String> categoryNames, Long refId, ConfLevel level){
        // confLevel, name
        List<String> formattedNames = new ArrayList<>();
        categoryNames.forEach(category -> {
            formattedNames.add(category.trim().toLowerCase());
        });
        List<KPICategoryDTO> categories = counterRepository.getKPICategory(level, refId);
        List<KPICategoryDTO> duplicateEntries = new ArrayList<>();
        categories.forEach(category -> {
            if(formattedNames.contains(category.getName().trim().toLowerCase())){
                duplicateEntries.add(category);
            }
        });
        if(duplicateEntries.size()>0) exceptionService.duplicateDataException("error.kpi_category.duplicate");
    }

    private List<String> getTrimmedNames(List<KPICategory> categories){
        List<String> categoriesNames = new ArrayList<>();
        categories.forEach(category -> {
            category.setName(category.getName().trim());
            categoriesNames.add(category.getName());
        });
        return categoriesNames;
    }

    public List<KPICategory> addCategories(List<KPICategory> categories, ConfLevel level, Long ownerId){
        List<String > names = getTrimmedNames(categories);
        verifyForCategoryAvailability(names, ownerId, level);
        return save(categories);
    }

    private void deleteCategories(List<KPICategoryDTO> deletedCategories, ConfLevel level, Long refId){
        List<BigInteger> deletableCategories = deletedCategories.stream().map(categoryDTO -> categoryDTO.getId()).collect(Collectors.toList());
        List<CategoryAssignmentDTO> assignmentDTOs = counterRepository.getCategoryAssignments(deletableCategories, level, refId);
        List<BigInteger> deletableCategoryAssignmentIds = assignmentDTOs.stream().map(assignmentDTO -> assignmentDTO.getId()).collect(Collectors.toList());
        counterRepository.removeAll("categoryAssignmentId", deletableCategoryAssignmentIds, CategoryKPIConf.class);
        counterRepository.removeAll("id", deletableCategoryAssignmentIds, CategoryAssignment.class);
        List<BigInteger> ownedDeletableCategories = deletedCategories.parallelStream().filter(categoryDTO -> categoryDTO.getLevelId().equals(refId)).map(categoryDTO -> categoryDTO.getId()).collect(Collectors.toList());
        assignmentDTOs = counterRepository.getCategoryAssignments(ownedDeletableCategories, level, refId);
        Map<BigInteger, CategoryAssignmentDTO> assignmentDTOMap = assignmentDTOs.parallelStream().collect(Collectors.toMap(assignmentDTO -> assignmentDTO.getId(), assignmentDTO -> assignmentDTO));
        List<BigInteger> finalDeletableCategories = ownedDeletableCategories.parallelStream().filter(categoryId -> assignmentDTOMap.get(categoryId)==null).collect(Collectors.toList());
        counterRepository.removeAll("id", finalDeletableCategories, KPICategory.class);
    }

    private void modifyCategories(List<KPICategoryDTO> changedCategories, ConfLevel level, Long refId){
        List<BigInteger> categoryIds = changedCategories.stream().map(changedCategory -> changedCategory.getId()).collect(Collectors.toList());
        List<CategoryAssignmentDTO>  assignmentDTOs = counterRepository.getCategoryAssignments(categoryIds, level, refId);
        Map<BigInteger, CategoryAssignmentDTO> categoryDTOMapById = assignmentDTOs.parallelStream().collect(Collectors.toMap(assignmentDTO -> assignmentDTO.getCategory().getId(), assignmentDTO -> assignmentDTO));
        Map<String, BigInteger>  categoryNameAssignemtIdMap = new HashMap<>();
        List<KPICategory> updatableCategories = new ArrayList<>();
        for( KPICategoryDTO kpiCategoryDTO: changedCategories){
            CategoryAssignmentDTO assignmentDTO = categoryDTOMapById.get(kpiCategoryDTO.getId());
            if(!assignmentDTO.getCategory().getName().equals(kpiCategoryDTO.getName())){
                KPICategory category = new KPICategory(kpiCategoryDTO.getName(), refId);
                if(assignmentDTO.getCategory().getLevelId().equals(refId) && !ConfLevel.COUNTRY.equals(level)){
                    category.setId(assignmentDTO.getId());
                }
                categoryNameAssignemtIdMap.put(category.getName(), assignmentDTO.getId());
                updatableCategories.add(category);
            }
        }
        List<CategoryAssignment> categoryAssignments = new ArrayList<>();
        updatableCategories = save(updatableCategories);
        updatableCategories.forEach(kpiCategory -> {
            Long countryId = null;
            Long unitId = null;
            if(ConfLevel.COUNTRY.equals(level)){
                countryId = refId;
            }else{
                unitId = refId;
            }
            CategoryAssignment assignment = new CategoryAssignment(kpiCategory.getId(), countryId, unitId, level);
            assignment.setId(categoryNameAssignemtIdMap.get(kpiCategory.getName()));
            categoryAssignments.add(assignment);
        });
        save(categoryAssignments);
    }

    public void updateCategories(KPICategoryUpdationDTO categories, ConfLevel level, Long refId){
        Set<String> categoriesNames = categories.getUpdatedCategories().stream().map(category -> category.getName().trim().toLowerCase()).collect(Collectors.toSet());
        if(categoriesNames.size() != categories.getUpdatedCategories().size())  exceptionService.duplicateDataException("error.kpi_category.duplicate");
        deleteCategories(categories.getDeletedCategories(), level, refId);
        modifyCategories(categories.getUpdatedCategories(), level, refId);
    }

    public void addEntries(Long countryId){
        List<KPI> kpis = new ArrayList<>();
        /// String title, BaseChart chart, CounterSize size, CounterType type, boolean treatAsCounter, BigInteger primaryCounter
        //verification for availability
        List<Counter> availableCounters = counterRepository.getCounterByTypes(Arrays.asList(CounterType.values()));
        if(availableCounters.size() == CounterType.values().length) exceptionService.duplicateDataException("error.counterType.duplicate", "Duplicate Available");
        List<CounterType> availableTypes = availableCounters.stream().map(counter -> counter.getType()).collect(Collectors.toList());
        List<CounterType> addableCounters = Arrays.stream(CounterType.values()).filter(counterType -> !availableTypes.contains(counterType)).collect(Collectors.toList());
        addableCounters.forEach(counterType -> {
            kpis.add(new KPI(counterType.getName(), null, null, counterType, false, null));
        });
        List<KPI> savedKPIs = save(kpis);
        List<KPIAssignment> kpiAssignment = savedKPIs.parallelStream().map(kpi -> new KPIAssignment(kpi.getId(), countryId, null, null, ConfLevel.COUNTRY)).collect(Collectors.toList());
        save(kpiAssignment);
    }
}
