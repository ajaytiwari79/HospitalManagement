package com.kairos.persistence.repository.user.control_panel;

import com.kairos.persistence.model.user.control_panel.ControlPanel;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 29/12/16.
 */
@Repository
public interface ControlPanelGraphRepository extends GraphRepository<ControlPanel> {

       // @Query("MATCH (cp:ControlPanel)-[:UNITS]->(o:Organization) where id(o)={0} return cp")
    @Query ("MATCH (cp:ControlPanel)-[:UNITS]->(o:Organization) where id(o)={0} AND cp.active=true \n" +
            "with cp as cp\n" +
            "OPTIONAL MATCH (cp)-[:INTEGRATION_CONFIGURATION]-(ic:IntegrationConfiguration)\n" +
            "with cp as cp , ic as ic\n" +
            "OPTIONAL MATCH (cp)-[:UNITS]->(o:Organization)\n" +
            "return {\n" +
            "id:id(cp),\n" +
            "processType:cp.processType,\n" +
            "integrationConfigurationId:id(ic),\n" +
            "organizationId:id(o),\n" +
            "lastRunTime:cp.lastRunTime,\n" +
            "nextRunTime:cp.nextRunTime,\n" +
            "interval:cp.interval,\n" +
            "selectedHours:cp.selectedHours,\n" +
            "days:cp.days,\n" +
            "runOnce:cp.runOnce,\n" +
            "startMinute:cp.startMinute,\n" +
            "repeat:cp.repeat,\n" +
            "name:cp.name,\n" +
            "unitId:cp.unitId,\n" +
            "startDate:cp.startDate,\n" +
            "endDate:cp.endDate\n" +
            "} as result")
    List<Map<String, Object>> getControlPanelByUnitId(long unitId);

    @Query("MATCH (controlPanel:ControlPanel) where id(controlPanel)={0} return controlPanel")
    ControlPanel getControlPanel(long controlPanel);

    List<ControlPanel> findByActive(boolean active);
}
