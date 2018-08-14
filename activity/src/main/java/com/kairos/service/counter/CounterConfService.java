package com.kairos.service.counter;

import com.kairos.activity.counter.FilterCriteria;
import com.kairos.activity.counter.KPICategoryDTO;
import com.kairos.activity.counter.distribution.category.CategoryAssignmentDTO;
import com.kairos.activity.counter.distribution.category.KPICategoryUpdationDTO;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.activity.counter.enums.CounterType;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.response.dto.web.organization.UnitAndParentOrganizationAndCountryDTO;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Service
public class CounterConfService extends MongoBaseService {

    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CounterRepository counterRepository;

    public void updateCounterCriteria(BigInteger counterId, List<FilterCriteria> criteriaList){
        Counter counter = (Counter) counterRepository.getEntityById(counterId, Counter.class);
        counter.setCriteriaList(criteriaList);
        save(counter);
    }

    private void verifyForValidCounterType(CounterType type){
        boolean validCounterType = Arrays.stream(CounterType.values()).anyMatch(s -> s.equals(type));
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
        categoryNames.forEach(category -> formattedNames.add(category.trim().toLowerCase()));
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
        Long countryId = ConfLevel.COUNTRY.equals(level)? ownerId: null;
        Long unitId=ConfLevel.UNIT.equals(level)? ownerId: null;
        List<String > names = getTrimmedNames(categories);
        verifyForCategoryAvailability(names, ownerId, level);
        List<KPICategory> kpiCategories=save(categories);
        List<CategoryAssignment> categoryAssignment = kpiCategories.parallelStream().map(category -> new CategoryAssignment(category.getId(),countryId,unitId,level)).collect(Collectors.toList());
        save(categoryAssignment);
        return kpiCategories;
    }

    private List<CategoryAssignmentDTO> getExistingAssignments(List<KPICategoryDTO> deletedCategories, ConfLevel level, Long refId){
        if(deletedCategories.isEmpty()) return new ArrayList<>();
        List<BigInteger> deletableCategories = deletedCategories.stream().map(KPICategoryDTO::getId).collect(Collectors.toList());
        List<CategoryAssignmentDTO> assignmentDTOs = counterRepository.getCategoryAssignments(deletableCategories, level, refId);
        if(deletedCategories.size() != assignmentDTOs.size()){
            exceptionService.invalidOperationException("error.kpi.invalidData");
        }
        return assignmentDTOs;
    }

    private List<KPICategory> modifyCategories(List<KPICategoryDTO> changedCategories, List<CategoryAssignmentDTO> existingAssignmentDTOs, ConfLevel level, Long refId){
        Map<BigInteger, CategoryAssignmentDTO> categoryDTOMapById = existingAssignmentDTOs.parallelStream().collect(Collectors.toMap(assignmentDTO -> assignmentDTO.getCategory().getId(), assignmentDTO -> assignmentDTO));
        Map<String, BigInteger>  categoryNameAssignemtIdMap = new HashMap<>();
        List<KPICategory> updatableCategories = new ArrayList<>();
        for( KPICategoryDTO kpiCategoryDTO: changedCategories){
            CategoryAssignmentDTO assignmentDTO = categoryDTOMapById.get(kpiCategoryDTO.getId());
            if(!assignmentDTO.getCategory().getName().equals(kpiCategoryDTO.getName())){
                KPICategory category = new KPICategory(kpiCategoryDTO.getName(), refId,assignmentDTO.getCategory().getId());
                if(assignmentDTO.getCategory().getLevelId().equals(refId) && !ConfLevel.COUNTRY.equals(level)){
                    category.setId(assignmentDTO.getCategory().getId());
                }
                categoryNameAssignemtIdMap.put(category.getName(), assignmentDTO.getId());
                updatableCategories.add(category);
            }
        }
        List<CategoryAssignment> categoryAssignments = new ArrayList<>();
        if(!updatableCategories.isEmpty()) {
            updatableCategories = save(updatableCategories);
        }
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
        if(!categoryAssignments.isEmpty()) {
            save(categoryAssignments);
        }
        return  updatableCategories;
    }

    public List<KPICategoryDTO> updateCategories(KPICategoryUpdationDTO categories, ConfLevel level, Long refId){
        Set<String> categoriesNames = categories.getUpdatedCategories().stream().map(category -> category.getName().trim().toLowerCase()).collect(Collectors.toSet());
        if(categoriesNames.size() != categories.getUpdatedCategories().size())  exceptionService.duplicateDataException("error.kpi_category.duplicate");
        List<CategoryAssignmentDTO> deletableAssignments = getExistingAssignments(categories.getDeletedCategories(), level, refId);
        List<CategoryAssignmentDTO> existingAssignments = getExistingAssignments(categories.getUpdatedCategories(), level, refId);
        List<KPICategory> kpiCategories=modifyCategories(categories.getUpdatedCategories(), existingAssignments, level, refId);
        List<BigInteger> deletableCategoryAssignmentIds = deletableAssignments.stream().map(CategoryAssignmentDTO::getId).collect(Collectors.toList());
        List<BigInteger> ownedDeletableCategoryIds  = new ArrayList<>();
        if(ConfLevel.UNIT.equals(level)){
            ownedDeletableCategoryIds = deletableAssignments.parallelStream().filter(assignmentDTO -> assignmentDTO.getCategory().getLevelId().equals(refId)).map(categoryAssignmentDTO -> categoryAssignmentDTO.getCategory().getId()).collect(Collectors.toList());
        }
        counterRepository.removeAll("categoryAssignmentId", deletableCategoryAssignmentIds, CategoryKPIConf.class);
        counterRepository.removeAll("id", deletableCategoryAssignmentIds, CategoryAssignment.class);
        counterRepository.removeAll("id", ownedDeletableCategoryIds, KPICategory.class);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(kpiCategories, KPICategoryDTO.class);
    }

    public void addEntries(Long countryId){
        List<KPI> kpis = new ArrayList<>();
        /// String title, BaseChart chart, CounterSize size, CounterType type, boolean treatAsCounter, BigInteger primaryCounter
        //verification for availability
        List<Counter> availableCounters = counterRepository.getCounterByTypes(Arrays.asList(CounterType.values()));
        if(availableCounters.size() == CounterType.values().length) exceptionService.duplicateDataException("error.counterType.duplicate", "Duplicate Available");
        List<CounterType> availableTypes = availableCounters.stream().map(Counter::getType).collect(Collectors.toList());
        List<CounterType> addableCounters = Arrays.stream(CounterType.values()).filter(counterType -> !availableTypes.contains(counterType)).collect(Collectors.toList());
        addableCounters.forEach(counterType -> kpis.add(new KPI(counterType.getName(), null, null, counterType, false, null)));
        List<KPI> savedKPIs = save(kpis);
        List<ApplicableKPI> applicableKPIS = savedKPIs.parallelStream().map(kpi -> new ApplicableKPI(kpi.getId(),kpi.getId(), countryId, null, null, ConfLevel.COUNTRY)).collect(Collectors.toList());
        save(applicableKPIS);
    }
}
