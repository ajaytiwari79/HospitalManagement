package com.kairos.activity.service.counter;

import com.kairos.activity.persistence.enums.counter.*;
import com.kairos.activity.persistence.model.counter.CounterAccessiblity;
import com.kairos.activity.persistence.model.counter.CounterDefinition;
import com.kairos.activity.persistence.repository.counter.CounterRepository;
import com.kairos.activity.response.dto.counter.CounterModuleLinkDTO;
import com.kairos.activity.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class CounterGenerationService extends MongoBaseService{
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private CounterManagementService counterManagementService;

    public void setRestingHoursPerPresenceDay(){
        CounterDefinition cDef = new CounterDefinition();
        cDef.setType(CounterType.RESTING_HOURS_PER_PRESENCE_DAY);
        cDef.setChartSize(CounterSize.SIZE_1X1);
        cDef.setChartsSupported(new ArrayList<ChartType>());
        cDef.setViewSupported(CounterView.CHART);
        counterManagementService.storeCounterDefinition(cDef);
    }

    public void setModuleLinks(){
        
    }

    public void generateCounterDefinitions(){
        //RESTING HOURS PER PRESENCE DAY
        setRestingHoursPerPresenceDay();
    }

    public void storeCounterRelation(){

        //configureCounterAccessiblityByUnit
        //i.e. unitId = 2597
        String moduleId = "tab_1";
        BigInteger unitId = BigInteger.valueOf(2597);
        List<CounterModuleLinkDTO> links = counterRepository.getCounterModuleLinks(moduleId);
        for(CounterModuleLinkDTO cmLink : links){
            CounterAccessiblity accessiblity = new CounterAccessiblity();
            accessiblity.setAccessLevel(CounterLevel.INDIVIDUAL);
            accessiblity.setCounterModuleLinkId(cmLink.getId());
            accessiblity.setUnitId(unitId);
        }

        //settingCounterAccessiblityByUnitByLevel
        List<CounterAccessiblity> accessiblities = new ArrayList<CounterAccessiblity>();
    }
}
