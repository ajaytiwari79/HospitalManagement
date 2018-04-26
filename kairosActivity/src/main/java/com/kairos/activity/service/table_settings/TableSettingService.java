package com.kairos.activity.service.table_settings;
import com.kairos.activity.persistence.model.table_settings.TableConfiguration;
import com.kairos.activity.persistence.model.table_settings.TableSetting;
import com.kairos.activity.persistence.repository.table_settings.TableSettingMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.kairos.activity.persistence.model.constants.TableSettingConstants.ORGANIZATION_CITIZEN_TABLE_ID;

/**
 * Created by prabjot on 28/4/17.
 */
@Transactional
@Service
public class TableSettingService extends MongoBaseService {

    @Inject
    private TableSettingMongoRepository tableSettingMongoRepository;

    public TableConfiguration saveTableSettings(long userId, long organizationId, String tableId,
                                                Map<String,Object> tableConf){

        TableSetting tableSetting = tableSettingMongoRepository.findByUserIdAndOrganizationId(userId,organizationId);;
        TableConfiguration tableConfiguration;

        if(tableSetting == null){
            tableSetting = new TableSetting();
            tableSetting.setUserId(userId);
            tableSetting.setOrganizationId(organizationId);
            tableConfiguration =  new TableConfiguration();
            tableConfiguration.setSettings(tableConf);
            tableConfiguration.setTableId(tableId);
            List<TableConfiguration> configurationList = tableSetting.getTableConfigurations();
            configurationList.add(tableConfiguration);
            tableSetting.setTableConfigurations(configurationList);
        } else {
            tableConfiguration = getTableConfiguration(tableSetting, tableId);
            if(tableConfiguration == null){
                tableConfiguration =  new TableConfiguration();
                tableConfiguration.setSettings(tableConf);
                tableConfiguration.setTableId(tableId);
                List<TableConfiguration> configurationList = tableSetting.getTableConfigurations();
                configurationList.add(tableConfiguration);

                tableSetting.setTableConfigurations(configurationList);
            } else {
                tableConfiguration.setSettings(tableConf);
            }
        }
        save(tableSetting);
        return tableConfiguration;

    }

    public static List<TableConfiguration> getTableConfiguration(TableSetting tableSetting,List<String> tableIds){

        if(tableIds == null || tableIds.isEmpty()){
            return null;
        }

        List<TableConfiguration> tableSettings = new ArrayList<>();

        for(TableConfiguration tableConfiguration : tableSetting.getTableConfigurations()){
            if(tableIds.contains(tableConfiguration.getTableId())){
                tableSettings.add(tableConfiguration);
            }
        }

        return tableSettings;
    }

    public static TableConfiguration getTableConfiguration(TableSetting tableSetting,String tableId){

        if(tableId == null || tableId.isEmpty() || tableSetting == null){
            return null;
        }
        for(TableConfiguration tableConfiguration : tableSetting.getTableConfigurations()){
            if(tableConfiguration.getTableId().equals(tableId)){
                return tableConfiguration;
            }
        }

        return null;
    }

    /**
     * @auther anil maurya
     * @param staffId
     * @return
     */
    public TableConfiguration getTableConfiguration(long staffId, long unitId){

        TableSetting tableSetting = tableSettingMongoRepository.findByUserIdAndOrganizationId(staffId, unitId);

        TableConfiguration tableConfiguration = TableSettingService.getTableConfiguration(tableSetting, ORGANIZATION_CITIZEN_TABLE_ID);
        return Optional.ofNullable(tableConfiguration).orElse(new TableConfiguration());

    }
}
