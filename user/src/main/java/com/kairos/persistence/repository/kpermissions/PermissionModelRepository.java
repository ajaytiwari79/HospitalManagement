package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.kpermissions.PermissionModel;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import java.util.List;
import java.util.Map;

public interface PermissionModelRepository  extends Neo4jBaseRepository<PermissionModel,Long> {

    @Query(value = "MATCH (m:PermissionModel{isPermissionSubModel:false, deleted:false})-[:HAS_FIELD]-(f:PermissionField{deleted:false}) return  m as model, collect(f.fieldName) as fields, m.modelName as modelName")
    List<Map<String, Object>> getPermissionModelWithModelAndFields();

    @Query(value = "match (smf:PermissionField)<-[:HAS_FIELD]-(sm:PermissionModel)<-[:HAS_SUB_MODEL]-(m:PermissionModel)-[:HAS_FIELD]-(f:PermissionField) return sm as subModel,collect(distinct smf.fieldName) as submodelfields, m as model, collect(distinct f.fieldName) as modelfields")
    List<Map<String, Object>> getPermissionModelDataWithFields();

    @Query(value = "MATCH (m:PermissionModel)-[:HAS_SUB_MODEL]-(sm:PermissionModel)-[:HAS_FIELD]-(f:PermissionField) where m.id = {0} return  sm as model, collect(f.fieldName) as fields, f.modelName as modelName")
    List<Map<String, Object>> getPermissionSubModelWithFields(Long permissionModelId);
}
