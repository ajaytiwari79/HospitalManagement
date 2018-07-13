package com.kairos.persistence.repository.table_settings;

import com.kairos.persistence.model.table_settings.TableSetting;

public interface CustomTableSettingMongoRepository {
    TableSetting findByUserIdAndOrganizationId(long userId, long organizationId, String tableId);
}
