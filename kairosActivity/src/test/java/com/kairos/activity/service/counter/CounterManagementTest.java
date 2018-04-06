package com.kairos.activity.service.counter;
import com.kairos.activity.KairosActivityApplication;
import com.kairos.activity.persistence.enums.counter.*;
import com.kairos.activity.persistence.model.counter.CounterDefinition;
import com.kairos.activity.persistence.model.counter.CounterModuleLink;
import com.kairos.activity.persistence.repository.counter.CounterRepository;
import com.kairos.activity.response.dto.counter.CounterAccessiblityDTO;
import com.kairos.activity.response.dto.counter.CounterAccessiblityUpdatorDTO;
import com.kairos.activity.response.dto.counter.CounterModuleLinkDTO;
import com.kairos.activity.response.dto.counter.CustomCounterSettingDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KairosActivityApplication.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CounterManagementTest {
    @Inject
    private CounterGenerationService counterGenerationService;
    @Inject
    private CounterManagementService counterManagementService;
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private MongoTemplate mongoTemplate;

    @Test
    public void counterDefinitionSavingAndUniquenessTest(){
        CounterDefinition cDef = new CounterDefinition();
        cDef.setType(CounterType.RESTING_HOURS_PER_PRESENCE_DAY);
        cDef.setChartSize(CounterSize.SIZE_1X1);
        cDef.setChartsSupported(new ArrayList<ChartType>());
        cDef.setViewSupported(CounterView.CHART);
        counterManagementService.storeCounterDefinition(cDef);
        counterManagementService.storeCounterDefinition(cDef);
        Query query = new Query(Criteria.where("type").is(CounterType.RESTING_HOURS_PER_PRESENCE_DAY));
        List<CounterDefinition> cDefs = mongoTemplate.find(query, CounterDefinition.class);
        Assert.assertEquals(1, cDefs.size());
    }

    @Test
    public void counterModuleLinkUniquenessTest(){
        String moduleId = "tab_1";
        CounterType type = CounterType.RESTING_HOURS_PER_PRESENCE_DAY;
        //calling double storage of counterModuleLink
        counterManagementService.storeModuleCounterLink(moduleId, type);
        counterManagementService.storeModuleCounterLink(moduleId, type);
        //checking for unique entry
        CounterDefinition cDef = counterRepository.getCounterByType(type);
        Query query = new Query(Criteria.where("moduleId").is(moduleId).and("counterDefinitionId").is(cDef.getId()));
        List<CounterModuleLink> links = mongoTemplate.find(query, CounterModuleLink.class);
        Assert.assertEquals(1, links.size());
    }

    @Test
    public void counterAccessiblityTest(){
        String moduleId = "tab_1";
        BigInteger unitId = BigInteger.valueOf(1234);
        List<CounterModuleLinkDTO> counterModuleLinks = counterManagementService.getCounterModuleLinks(moduleId);
        CounterAccessiblityUpdatorDTO accessiblityUpdatorDTO = new CounterAccessiblityUpdatorDTO();
        accessiblityUpdatorDTO.setCounterLevel(CounterLevel.INDIVIDUAL);
        accessiblityUpdatorDTO.setUnitId(unitId);
        accessiblityUpdatorDTO.setCounterModuleLinkDTOs(counterModuleLinks);
        counterManagementService.setCounterAccessLevelForUnit(accessiblityUpdatorDTO);
        List<CounterAccessiblityDTO> list = counterRepository.getCounterAccessiblityList(unitId, CounterLevel.INDIVIDUAL);
        Assert.assertEquals(counterModuleLinks.size(), list.size());
    }

    @Test
    public void customCounterConfiguration(){
        String moduleId = "tab_1";
        BigInteger unitId = BigInteger.valueOf(1234);
        BigInteger staffId = BigInteger.valueOf(3213);
        List<CounterAccessiblityDTO> list = counterRepository.getCounterAccessiblityList(unitId, CounterLevel.INDIVIDUAL);
        CustomCounterSettingDTO customCounterSettingDTO;
        for(CounterAccessiblityDTO dto : list){
            customCounterSettingDTO = new CustomCounterSettingDTO();
            customCounterSettingDTO.setCounterAccessiblity(dto);
            customCounterSettingDTO.setLevel(CounterLevel.INDIVIDUAL);
            customCounterSettingDTO.setViewDefault(CounterView.CELL);
            customCounterSettingDTO.setOrder(1);
            counterManagementService.setCounterConfiguration(customCounterSettingDTO, staffId);
        }
        List<CustomCounterSettingDTO> counterSettingDTOS = counterRepository.getConfiguredCounters(staffId);
        Assert.assertEquals(list.size(), counterSettingDTOS.size());

    }
}
