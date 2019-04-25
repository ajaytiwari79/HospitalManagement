package com.kairos.persistence.model.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;

/**
 * Created by prerna on 6/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningPeriod extends MongoBaseEntity {

    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
    @Indexed
    private Long unitId = -1L;
    private BigInteger currentPhaseId;
    private BigInteger nextPhaseId;
    private List<PeriodPhaseFlippingDate> phaseFlippingDate = new ArrayList<>();
    private Type type;
    private int duration;
    private DurationType durationType;
    private boolean active=true;


    public PlanningPeriod(){
        // default constructor
    }

    public PlanningPeriod(String name, LocalDate startDate, LocalDate endDate, Long unitId) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unitId = unitId;

    }

    public PlanningPeriod(String name, LocalDate startDate, LocalDate endDate, Long unitId,DurationType durationType,int duration) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unitId = unitId;
        this.durationType=durationType;
        this.duration=duration;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<PeriodPhaseFlippingDate> getPhaseFlippingDate() {
        return phaseFlippingDate;
    }

    public void setPhaseFlippingDate(List<PeriodPhaseFlippingDate> phaseFlippingDate) {
        this.phaseFlippingDate = phaseFlippingDate;
    }

    public BigInteger getCurrentPhaseId() {
        return currentPhaseId;
    }

    public void setCurrentPhaseId(BigInteger currentPhaseId) {
        this.currentPhaseId = currentPhaseId;
    }

    public BigInteger getNextPhaseId() {
        return nextPhaseId;
    }

    public void setNextPhaseId(BigInteger nextPhaseId) {
        this.nextPhaseId = nextPhaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public DateTimeInterval getInterval(){
        return new DateTimeInterval(asDate(startDate),asDate(endDate));
    }

    public boolean contains(LocalDate localDate){
        return getInterval().contains(asDate(localDate)) || endDate.equals(localDate);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
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
