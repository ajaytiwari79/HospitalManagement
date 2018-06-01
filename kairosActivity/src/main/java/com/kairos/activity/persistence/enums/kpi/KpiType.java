package com.kairos.activity.persistence.enums.kpi;

import com.kairos.activity.persistence.enums.counter.CounterType;
import com.kairos.activity.response.dto.counter.CounterTypeDefDTO;
import com.kairos.activity.response.dto.kpi.KpiTypeDefDTO;

import java.util.ArrayList;
import java.util.List;

public enum KpiType {
    RESTING_HOURS_PER_PRESENCE_DAY("Resting Hours Per Presence Day");
    private String name;

    private KpiType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public static List<KpiTypeDefDTO> getKpiTypes(){
        List<KpiTypeDefDTO> counterTypes = new ArrayList<>();
        for(KpiType type : KpiType.values()){
            KpiTypeDefDTO typeDef = new KpiTypeDefDTO();
            typeDef.setName(type.getName());
            typeDef.setTypeName(type.toString());
            counterTypes.add(typeDef);
        }
        return counterTypes;
    }

}
