package com.kairos.service.counter;

import com.kairos.activity.counter.FilterCriteria;
import com.kairos.enums.CounterType;
import com.kairos.persistence.model.counter.Counter;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.counter.KPICategory;
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

    private void verifyForCategoryAvailability(List<String> categoryNames){
        List<KPICategory> categories = counterRepository.getCategoriesByNames(categoryNames);
        if(categories!=null && categories.size()>0) exceptionService.duplicateDataException("error.kpi_category.duplicate");
    }

    private List<String> formatCategoriesNames(List<KPICategory> categories){
        List<String> categoriesNames = new ArrayList<>();
        categories.forEach(category -> {
            category.setName(category.getName().trim().toUpperCase());
            categoriesNames.add(category.getName());
        });
        return categoriesNames;
    }

    public List<KPICategory> addCategories(List<KPICategory> categories){
        List<String > names = formatCategoriesNames(categories);
        verifyForCategoryAvailability(names);
        return save(categories);
    }

    public List<KPICategory> updateCategories(List<KPICategory> categories){
        Set<String> categoriesNames = new HashSet<>(formatCategoriesNames(categories));
        if(categoriesNames.size() != categories.size())  exceptionService.duplicateDataException("error.kpi_category.duplicate");
        return save(categories);
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
            kpis.add(new KPI(counterType.getName(), null, null, counterType, false, null));
        });
        save(kpis);
    }
}
