package com.kairos.activity.service.counter;

import com.kairos.activity.persistence.repository.counter.CounterRepository;
import com.kairos.activity.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class CounterGenerationService extends MongoBaseService{
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private CounterManagementService counterManagementService;

//    public void setRestingHoursPerPresenceDay(){
//        Counter cDef = new Counter();
//        cDef.setCounterType(CounterType.RESTING_HOURS_PER_PRESENCE_DAY);
//        cDef.setChartSize(CounterSize.SIZE_1X1);
//        cDef.setChartsSupported(new ArrayList<ChartType>());
//        cDef.setViewSupported(CounterView.CHART);
//        counterManagementService.storeCounter(cDef);
//    }

    public void setModuleLinks(){
        
    }

    public void generateCounterDefinitions(){
        //RESTING HOURS PER PRESENCE DAY
        //setRestingHoursPerPresenceDay();
    }

//    public void storeCounterRelation(){
//
//        //configureCounterAccessiblityByUnit
//        //i.e. unitId = 2597
//        String moduleId = "tab_1";
//        BigInteger unitId = BigInteger.valueOf(2597);
//        List<CounterModuleLinkDTO> links = counterRepository.getCounterModuleLinks(moduleId);
//        for(CounterModuleLinkDTO cmLink : links){
//            UnitRoleWiseCounter accessiblity = new UnitRoleWiseCounter();
//            accessiblity.setAccessLevel(CounterLevel.INDIVIDUAL);
//            accessiblity.setRefCounterId(cmLink.getId());
//            accessiblity.setUnitId(unitId);
//        }
//
//        //settingCounterAccessiblityByUnitByLevel
//        List<UnitRoleWiseCounter> accessiblities = new ArrayList<UnitRoleWiseCounter>();
//    }
}
