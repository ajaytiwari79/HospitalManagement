package com.kairos.persistence.repository.table_settings;

import com.kairos.dto.activity.activity.TableConfiguration;
import com.kairos.persistence.model.table_settings.TableSetting;

public interface CustomTableSettingMongoRepository {
    TableSetting findByUserIdAndOrganizationId(Long userId, Long organizationId, String tableId);
    TableConfiguration findTableConfigurationByUserIdAndOrganizationId(Long userId, Long organizationId, String tableId);
}
