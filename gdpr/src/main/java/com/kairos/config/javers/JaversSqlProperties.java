package com.kairos.config.javers;

import org.javers.spring.JaversSpringProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "javers")
class JaversSqlProperties extends JaversSpringProperties {

    private boolean sqlSchemaManagementEnabled = true;
    private String sqlSchema;

    public boolean isSqlSchemaManagementEnabled() {
        return sqlSchemaManagementEnabled;
    }

    public void setSqlSchemaManagementEnabled(boolean sqlSchemaManagementEnabled) {
        this.sqlSchemaManagementEnabled = sqlSchemaManagementEnabled;
    }

    public String getSqlSchema() {
        return sqlSchema;
    }

    public void setSqlSchema(String sqlSchema) {
        this.sqlSchema = sqlSchema;
    }
}
