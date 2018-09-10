package com.kairos.service.counter;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.distribution.category.KPICategoryDTO;
import com.kairos.dto.activity.counter.distribution.category.KPICategoryUpdationDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

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
        List<KPICategoryDTO> categories = counterRepository.getKPICategory(null,level, refId);
        List<KPICategoryDTO> duplicateEntries = new ArrayList<>();
        categories.forEach(category -> {
            if(formattedNames.contains(category.getName().trim().toLowerCase())){
                duplicateEntries.add(category);
            }
        });
        if(duplicateEntries.size()>0) exceptionService.duplicateDataException("error.kpi_category.duplicate");
    }

    private List<String> getTrimmedNames(List<KPICategoryDTO> categories){
        List<String> categoriesNames = new ArrayList<>();
        categories.forEach(category -> {
            if(isBlank(category.getName())){
                exceptionService.dataNotFoundException("error.name.notnull");
            }
            category.setName(category.getName().trim());
            categoriesNames.add(category.getName());
        });
        return categoriesNames;
    }

    public List<KPICategory> addCategories(List<KPICategoryDTO> categories, ConfLevel level, Long refId){
        Long countryId = ConfLevel.COUNTRY.equals(level)? refId: null;
        Long unitId=ConfLevel.UNIT.equals(level)? refId: null;
        List<String > names = getTrimmedNames(categories);
        verifyForCategoryAvailability(names, refId, level);
        List<KPICategory> kpiCategories=new ArrayList<>();
        categories.stream().forEach(kpiCategoryDTO -> {
            kpiCategories.add(new KPICategory(kpiCategoryDTO.getName(),countryId,unitId,level));
        });
        if(!kpiCategories.isEmpty()){
            save(kpiCategories);
        }
        return kpiCategories;
    }

    private List<KPICategoryDTO> getExistingCategories(List<KPICategoryDTO> categories, ConfLevel level, Long refId){
        if(categories.isEmpty()) return new ArrayList<>();
        List<BigInteger> categoryIds = categories.stream().map(KPICategoryDTO::getId).collect(Collectors.toList());
        List<KPICategoryDTO> categoryDTOs = counterRepository.getKPICategory(categoryIds, level, refId);
        if(categories.size() != categoryDTOs.size()){
            exceptionService.invalidOperationException("error.kpi.invalidData");
        }
        return categoryDTOs;
    }

         private List<KPICategory> modifyCategories(List<KPICategoryDTO> changedCategories, List<KPICategoryDTO> existingAssignmentDTOs, ConfLevel level, Long refId) {
           if(existingAssignmentDTOs.isEmpty()){
               return new ArrayList<>();
           }
        Map<BigInteger, KPICategoryDTO> categoryDTOMapById = changedCategories.parallelStream().collect(Collectors.toMap(kPICategoryDTO -> kPICategoryDTO.getId(), kPICategoryDTO -> kPICategoryDTO));
             List<BigInteger> categoriesIds = changedCategories.stream().map(kpiCategoryDTO -> kpiCategoryDTO.getId()).collect(Collectors.toList());
             List<KPICategory> kpiCategories = counterRepository.getKPICategoryByIds(categoriesIds, level, refId);
            for( KPICategory kpiCategory : kpiCategories){
                KPICategoryDTO kpiCategoryDTO = categoryDTOMapById.get(kpiCategory.getId());
            if(!kpiCategoryDTO.getName().equals(kpiCategory.getName())){
             kpiCategory.setName(kpiCategoryDTO.getName());
            }
        }
            save(kpiCategories);
             return kpiCategories;
    }

    public List<KPICategoryDTO> updateCategories(KPICategoryUpdationDTO categories, ConfLevel level, Long refId){
        Set<String> categoriesNames = categories.getUpdatedCategories().stream().map(category -> category.getName().trim().toLowerCase()).collect(Collectors.toSet());
        if(categoriesNames.size() != categories.getUpdatedCategories().size())  exceptionService.duplicateDataException("error.kpi_category.duplicate");
        List<KPICategoryDTO> deletableCategories = getExistingCategories(categories.getDeletedCategories(), level, refId);
        List<KPICategoryDTO> existingCategories = getExistingCategories(categories.getUpdatedCategories(), level, refId);
        List<KPICategory> kpiCategories=modifyCategories(categories.getUpdatedCategories(), existingCategories, level, refId);
        List<BigInteger> deletableCategoryIds = deletableCategories.stream().map(kpiCategoryDTO -> kpiCategoryDTO.getId()).collect(Collectors.toList());
        counterRepository.removeAll("categoryId", deletableCategoryIds, CategoryKPIConf.class);
        counterRepository.removeAll("id", deletableCategoryIds, KPICategory.class);
       return ObjectMapperUtils.copyPropertiesOfListByMapper(kpiCategories, KPICategoryDTO.class);
    }

    public void addEntries(Long countryId){
        List<KPI> kpis = new ArrayList<>();
        /// String title, BaseChart chart, CounterSize size, CounterType type, boolean counter, BigInteger primaryCounter
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
