package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.kpermissions.PermissionModel;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import java.util.List;
import java.util.Map;

public interface PermissionModelRepository  extends Neo4jBaseRepository<PermissionModel,Long> {

    @Query(value = "MATCH (m:PermissionModel{isPermissionSubModel:false, deleted:false})-[:HAS_FIELD]-(f:PermissionField{deleted:false}) RETURN  m as model, collect(f.fieldName) as fields, m.modelName as modelName")
    List<Map<String, Object>> getPermissionModelWithModelAndFields();

    @Query(value = "MATCH (smf:PermissionField)<-[:HAS_FIELD]-(sm:PermissionModel)<-[:HAS_SUB_MODEL]-(m:PermissionModel)-[:HAS_FIELD]-(f:PermissionField) RETURN sm as subModel,collect(distinct smf.fieldName) as submodelfields, m as model, collect(distinct f.fieldName) as modelfields")
    List<Map<String, Object>> getPermissionModelDataWithFields();

    @Query(value = "MATCH (m:PermissionModel)-[:HAS_SUB_MODEL]-(sm:PermissionModel)-[:HAS_FIELD]-(f:PermissionField) where m.id = {0} RETURN  sm as model, collect(f.fieldName) as fields, f.modelName as modelName")
    List<Map<String, Object>> getPermissionSubModelWithFields(Long permissionModelId);

    @Query(value = "MATCH (permissionModel:PermissionModel)-[:HAS_SUB_MODEL]-(permissionSubModel:PermissionModel) WHERE id(permissionModel)={1} AND id(permissionSubModel)={0} RETURN  permissionSubModel")
    PermissionModel getPermissionSubModelByIdAndPermissionModelId(Long subPermissionModelId, Long permissionModelId);

}
