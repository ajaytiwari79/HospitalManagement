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
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;

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
    
}
