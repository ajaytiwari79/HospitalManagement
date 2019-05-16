package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.kpermissions.KPermissionModel;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import java.util.List;
import java.util.Map;

public interface PermissionModelRepository  extends Neo4jBaseRepository<KPermissionModel,Long> {

    @Query(value = "MATCH (m:KPermissionModel{isPermissionSubModel:false, deleted:false})-[:HAS_FIELD]-(f:KPermissionField{deleted:false}) RETURN  m as model, collect(f.fieldName) as fields, m.modelName as modelName")
    List<Map<String, Object>> getPermissionModelWithModelAndFields();

    @Query(value = "MATCH (smf:KPermissionField)<-[:HAS_FIELD]-(sm:KPermissionModel)<-[:HAS_SUB_MODEL]-(m:KPermissionModel)-[:HAS_FIELD]-(f:KPermissionField) RETURN sm as subModel,collect(distinct smf.fieldName) as submodelfields, m as model, collect(distinct f.fieldName) as modelfields")
    List<Map<String, Object>> getPermissionModelDataWithFields();

    @Query(value = "MATCH (m:KPermissionModel)-[:HAS_SUB_MODEL]-(sm:KPermissionModel)-[:HAS_FIELD]-(f:KPermissionField) where m.id = {0} RETURN  sm as model, collect(f.fieldName) as fields, f.modelName as modelName")
    List<Map<String, Object>> getPermissionSubModelWithFields(Long permissionModelId);

    @Query(value = "MATCH (permissionModel:KPermissionModel)-[:HAS_SUB_MODEL]-(permissionSubModel:KPermissionModel) WHERE id(permissionModel)={1} AND id(permissionSubModel)={0} RETURN  permissionSubModel")
    KPermissionModel getPermissionSubModelByIdAndPermissionModelId(Long subPermissionModelId, Long permissionModelId);

}
