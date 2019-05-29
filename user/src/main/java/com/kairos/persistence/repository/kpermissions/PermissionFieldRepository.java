package com.kairos.persistence.repository.kpermissions;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.kpermissions.KPermissionField;
import com.kairos.persistence.model.kpermissions.KPermissionFieldQueryResult;
import com.kairos.persistence.model.kpermissions.KPermissionSubModelFieldQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

public interface PermissionFieldRepository extends Neo4jBaseRepository<KPermissionField,Long> {

    @Query(value = "MATCH (kPermissionModel:KPermissionModel)-[:"+HAS_FIELD+"]-(kPermissionField:KPermissionField) WHERE id(kPermissionModel)={0} AND id(kPermissionField)={1} RETURN  kPermissionField,kPermissionModel")
    KPermissionFieldQueryResult getPermissionFieldByIdAndPermissionModelId(Long permissionModelId, Long permissionActionId);

    //@Query(value = "MATCH(kPermissionModel:KPermissionModel)-[mField:HAS_FIELD]-(kPermissionField:KPermissionField)-[mfPermission:HAS_PERMISSION]-(mfAccessGroup:AccessGroup) where kPermissionModel.modelClass={0} AND mfPermission.fieldLevelPermission IN{2} AND id(mfAccessGroup) IN{1} with collect(distinct kPermissionField.fieldName) as modelFields UNWIND modelFields as result RETURN result")
    //List<String> findPermissionFieldsByAccessGroupAndModelClass(String modelClass, List<Long> accessGroupIds, List<FieldLevelPermissions> permissions);

    @Query(value = "MATCH(kPermissionModel:KPermissionModel)-[mField:"+HAS_FIELD+"]-(kPermissionField:KPermissionField)-[mfPermission:"+HAS_PERMISSION+"]-(mfAccessGroup:AccessGroup) where kPermissionModel.modelClass={0} AND mfPermission.fieldLevelPermission IN{2} AND id(mfAccessGroup) IN{1} \n" +
            "MATCH(kPermissionModel)-[smr:"+HAS_SUB_MODEL+"]-(kpermissionSubModel:KPermissionModel)-[smPermission:"+HAS_PERMISSION+"]-(mfAccessGroup:AccessGroup) where smPermission.fieldLevelPermission IN{2} AND id(mfAccessGroup) IN{1}\n" +
            "with  collect(distinct kPermissionField.fieldName) as d, collect(distinct kpermissionSubModel.modelName) as modelFields  return d+modelFields")
    List<List<String>> findPermissionFieldsByAccessGroupAndModelClass(String modelClass, List<Long> accessGroupIds, List<FieldLevelPermission> permissions);

    @Query(value = "MATCH(kPermissionModel:KPermissionModel)-[smr:"+HAS_SUB_MODEL+"]-(kpermissionSubModel:KPermissionModel)-[smPermission:"+HAS_PERMISSION+"]-(mfAccessGroup:AccessGroup) where kPermissionModel.modelClass={0} AND smPermission.fieldLevelPermission IN{2} AND id(mfAccessGroup) IN{1} OPTIONAL MATCH(kpermissionSubModel)-[smField:HAS_FIELD]-(kPermissionField:KPermissionField)-[mfPermission:HAS_PERMISSION]-(mfAccessGroup) where mfPermission.fieldLevelPermission IN{2}  AND id(mfAccessGroup) IN{1}  with kpermissionSubModel, collect(distinct kPermissionField.fieldName) as modelFields  return   kpermissionSubModel.modelName as modelName,modelFields as modelFields")
    List<KPermissionSubModelFieldQueryResult> findSubModelPermissionFieldsByAccessGroupAndModelClass(String modelClass, List<Long> accessGroupIds, List<FieldLevelPermission> permissions);

    /*@Query(value = "MATCH(kPermissionModel:KPermissionModel)-[mField:HAS_FIELD]-(kPermissionField:KPermissionField)-[mfPermission:HAS_PERMISSION]-(mfAccessGroup:AccessGroup) where kPermissionModel.modelClass={0} AND mfPermission.fieldLevelPermission IN{2} AND id(mfAccessGroup) IN{1} OPTIONAL MATCH(kPermissionModel)-[:HAS_SUB_MODEL]->(kPermissionSubModel:KPermissionModel)-[smField:HAS_FIELD]-(kPermissionSubModelField:KPermissionField)-[smfPermission:HAS_PERMISSION]-(smfAccessGroup:AccessGroup) where  id(smfAccessGroup) IN{1} AND smfPermission.fieldLevelPermission IN{2}  with collect(distinct kPermissionField.fieldName) as modelFields , collect(distinct kPermissionSubModelField.fieldName) as modelFields UNWIND modelFields+modelFields as result RETURN result")*/
}