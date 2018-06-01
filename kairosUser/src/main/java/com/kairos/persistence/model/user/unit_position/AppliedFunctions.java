package com.kairos.persistence.model.user.unit_position;


import com.kairos.config.neo4j.converter.LocalDateConverter;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.Function;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.LocalDate;

@NodeEntity
public class AppliedFunctions extends UserBaseEntity {

    @Convert(LocalDateConverter.class)
    private LocalDate date;
    private Function function;

    public AppliedFunctions(LocalDate date, Function function){
        this.date=date;
        this.function=function;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
