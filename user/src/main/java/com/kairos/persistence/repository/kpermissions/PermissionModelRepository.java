package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.kpermissions.PermissionModel;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import java.util.List;
import java.util.Map;

public interface PermissionModelRepository  extends Neo4jBaseRepository<PermissionModel,Long> {

    @Query(value = "MATCH (p:PermissionModel)-[:HAS_FIELD]-(t:PermissionField) return  p as model, collect(t.fieldName) as fields, p.modelName as modelName")
    List<Map<String, Object>> getPermissionModelWithFields();
}
