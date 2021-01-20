package com.kairos.persistence.model.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/**
 * Created by prerna on 6/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class PlanningPeriod extends MongoBaseEntity {

    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
    private String dateRange;
    @Indexed
    private Long unitId = -1L;
    private BigInteger currentPhaseId;
    private BigInteger nextPhaseId;
    private List<PeriodPhaseFlippingDate> phaseFlippingDate = new ArrayList<>();
    private Type type;
    private int duration;
    private DurationType durationType;
    private boolean active=true;
    private Set<Long> publishEmploymentIds=new HashSet<>();



    public PlanningPeriod(String name, String dateRange, LocalDate startDate, LocalDate endDate, Long unitId) {
        this.name = name;
        this.dateRange=dateRange;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unitId = unitId;

    }

    public PlanningPeriod(String name,String dateRange, LocalDate startDate, LocalDate endDate, Long unitId,DurationType durationType,int duration) {
        this.name = name;
        this.dateRange=dateRange;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unitId = unitId;
        this.durationType=durationType;
        this.duration=duration;
    }


     public DateTimeInterval takeInterval(){
        return new DateTimeInterval(asDate(startDate),asDate(endDate));
    }

    public boolean contains(LocalDate localDate){
        return takeInterval().contains(asDate(localDate)) || endDate.equals(localDate);
    }

    public Map<LocalDate,BigInteger> getLocalDatePhaseIdMap(){
        AtomicReference<LocalDate> start = new AtomicReference<>(startDate);
        Map<LocalDate,BigInteger> localDateMap = new HashMap<LocalDate, BigInteger>(){{
            while (!start.get().isAfter(endDate)){
                put(start.get(),currentPhaseId);
                start.set(start.get().plusDays(1));
            }
        }};
        return localDateMap;
    }

    public Map<LocalDate,Boolean> getLocalDatePublishPlanningMap(Long employmentTypeId){
        AtomicReference<LocalDate> start = new AtomicReference<>(startDate);
        Map<LocalDate,Boolean> localDateMap = new HashMap<LocalDate, Boolean>(){{
            while (!start.get().isAfter(endDate)){
                put(start.get(),publishEmploymentIds.contains(employmentTypeId));
                start.set(start.get().plusDays(1));
            }
        }};
        return localDateMap;
    }

    public Set<LocalDate> getLocalDates(){
        LocalDate start = startDate;
        Set<LocalDate> localDates = new HashSet<>();
        while (!start.isAfter(endDate)){
            localDates.add(start);
            start = start.plusDays(1);
        }
        return localDates;
    }

    public enum Type {

        WEEKLY, MONTHLY;

        public String value;

        public static Type getByValue(String value) {
            for (Type status : Type.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            return null;
        }

        public static List<Type> getListByValue(List<String> values) {
            if (Optional.ofNullable(values).isPresent()) {
                return values.stream().map(Type::valueOf)
                        .collect(Collectors.toList());
            }
            return null;

        }
    }

    public Set<Long> getPublishEmploymentIds() {
        return isNullOrElse(publishEmploymentIds,new HashSet<>());
    }
}
