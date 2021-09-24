package com.kairos.persistence.model.table_settings;

import com.kairos.dto.activity.activity.TableConfiguration;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by prabjot on 28/4/17.
 */
@Document
public class TableSetting extends MongoBaseEntity {


    private Long userId;
    private Long organizationId;
    private List<TableConfiguration> tableConfigurations;

    public Long getOrganizationId() {
        return organizationId;
    }


    public List<TableConfiguration> getTableConfigurations() {
        return Optional.ofNullable(tableConfigurations).orElse(new ArrayList<>());
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {

        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public void setTableConfigurations(List<TableConfiguration> tableConfigurations) {
        this.tableConfigurations = tableConfigurations;
    }
}
