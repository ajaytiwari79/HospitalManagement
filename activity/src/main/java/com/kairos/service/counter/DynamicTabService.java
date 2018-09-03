package com.kairos.service.counter;

import com.kairos.activity.counter.KPICategoryDTO;
import com.kairos.activity.counter.KPIDashboardDTO;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.counter.chart.KPIDashboard;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class DynamicTabService extends MongoBaseService {
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CounterRepository counterRepository;

    public List<KPIDashboardDTO> addDashboardTabToRef(Long unitId, Long countryId, List<KPIDashboardDTO> kpiDashboardDTOS, ConfLevel level) {
        Long staffId=null;
        List<String > names = getTrimmedNames(kpiDashboardDTOS);
        verifyForDashboardTabAvailability(names, unitId,staffId, countryId, level);
        List<KPIDashboard> kpiDashboards = new ArrayList<>();
        kpiDashboardDTOS.stream().forEach(kpiDashboardDTO -> {
            kpiDashboards.add(new KPIDashboard(kpiDashboardDTO.getParentModuleId(), kpiDashboardDTO.getName(), unitId, staffId, countryId, level));
        });
        save(kpiDashboards);
        kpiDashboards.stream().forEach(kpiDashboard -> {
            kpiDashboard.setModuleId(createModuleId(kpiDashboard.getId(),kpiDashboard.getParentModuleId()));
        });
        if(!kpiDashboards.isEmpty()) {
            save(kpiDashboards);
        }else{
            exceptionService.actionNotPermittedException("error.kpi.invalidData");
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(kpiDashboards,KPIDashboardDTO.class);
    }
    private String createModuleId(BigInteger id, String parentModuleId) {
    return parentModuleId+"_"+id;
    }

    private void verifyForDashboardTabAvailability(List<String> dashboardTabs,Long unitId, Long staffId, Long countryId, ConfLevel level){
        // confLevel, name
        Long refId=null;
        refId=ConfLevel.UNIT.equals(level)?unitId:(ConfLevel.STAFF.equals(staffId)?staffId:countryId);
        List<String> formattedNames = new ArrayList<>();
        dashboardTabs.forEach(category -> formattedNames.add(category.trim().toLowerCase()));
        List<KPIDashboardDTO> kpiDashboardDTOS = counterRepository.getKPIDashboard(null,level, refId);
        List<KPIDashboardDTO> duplicateEntries = new ArrayList<>();
        kpiDashboardDTOS.forEach(kpiDashboardDTO  -> {
            if(formattedNames.contains(kpiDashboardDTO.getName().trim().toLowerCase())){
                duplicateEntries.add(kpiDashboardDTO);
            }
        });
        if(duplicateEntries.size()>0) exceptionService.duplicateDataException("error.kpi_category.duplicate");
    }

    private List<String> getTrimmedNames(List<KPIDashboardDTO> dashboardTabs){
        List<String> dashboardTabsName = new ArrayList<>();
        try {
            dashboardTabs.forEach(kpiDashboardDTO -> {
                kpiDashboardDTO.setName(kpiDashboardDTO.getName().trim());
                dashboardTabsName.add(kpiDashboardDTO.getName());
            });
        }catch (NullPointerException e){
            exceptionService.dataNotFoundException("message.category.name.notnull");
        }
        return dashboardTabsName;
    }

}