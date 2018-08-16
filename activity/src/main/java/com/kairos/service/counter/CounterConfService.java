package com.kairos.service.counter;

import com.kairos.activity.enums.counter.ModuleType;
import com.kairos.activity.counter.FilterCriteria;
import com.kairos.activity.counter.KPICategoryUpdationDTO;
import com.kairos.enums.CounterType;
import com.kairos.persistence.model.counter.Counter;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.counter.KPICategory;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ObjectMapperUtils;
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

    private void verifyForCategoryAvailability(List<String> categoryNames){
        List<String> formattedNames = new ArrayList<>();
        categoryNames.forEach(category -> {
            formattedNames.add(category.trim().toLowerCase());
        });
        List<KPICategory> categories = counterRepository.getEntityItemList(KPICategory.class);
        List<KPICategory> duplicateEntries = new ArrayList<>();
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

    public List<KPICategory> addCategories(List<KPICategory> categories){
        List<String > names = getTrimmedNames(categories);
        verifyForCategoryAvailability(names);
        return save(categories);
    }

    public List<KPICategory> updateCategories(KPICategoryUpdationDTO categories){
        Set<String> categoriesNames = categories.getUpdatedCategories().stream().map(category -> category.getName().trim().toLowerCase()).collect(Collectors.toSet());
        if(categoriesNames.size() != categories.getUpdatedCategories().size())  exceptionService.duplicateDataException("error.kpi_category.duplicate");
        List<KPICategory> updatableCategories = ObjectMapperUtils.copyPropertiesOfListByMapper(categories.getUpdatedCategories(), KPICategory.class);
        List<KPICategory> deletableCategories = ObjectMapperUtils.copyPropertiesOfListByMapper(categories.getDeletedCategories(), KPICategory.class);
        if(deletableCategories!=null && deletableCategories.size()>0) {
            List<BigInteger> categoryIds = deletableCategories.stream().map(category -> category.getId()).collect(Collectors.toList());
            List<KPI> removedCategoriesKPIs = counterRepository.getKPIsByCategory(categoryIds);
            removedCategoriesKPIs.parallelStream().forEach(kpi -> {
                kpi.setCategoryId(null);
            });
            save(removedCategoriesKPIs);
            counterRepository.removeAll("id", categoryIds, KPICategory.class);
        }
        return save(updatableCategories);
    }

    public void addEntries(){
        List<KPI> kpis = new ArrayList<>();
        /// String title, BaseChart chart, CounterSize size, CounterType type, boolean treatAsCounter, BigInteger primaryCounter
        //verification for availability
        List<Counter> availableCounters = counterRepository.getCounterByTypes(Arrays.asList(CounterType.values()));
        if(availableCounters.size() == CounterType.values().length) exceptionService.duplicateDataException("error.counterType.duplicate", "Duplicate Available");
        List<CounterType> availableTypes = availableCounters.stream().map(counter -> counter.getType()).collect(Collectors.toList());
        List<CounterType> addableCounters = Arrays.stream(CounterType.values()).filter(counterType -> !availableTypes.contains(counterType)).collect(Collectors.toList());
        addableCounters.forEach(counterType -> {
            kpis.add(new KPI(counterType.getName(), null, null, counterType, false, null,Collections.singleton(ModuleType.OPEN_SHIFT)));
        });
        save(kpis);
    }
}
