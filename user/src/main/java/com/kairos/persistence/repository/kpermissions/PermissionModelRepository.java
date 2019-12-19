package com.kairos.persistence.repository.kpermissions;

import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.kpermissions.FieldPermissionQueryResult;
import com.kairos.persistence.model.kpermissions.KPermissionModel;
import com.kairos.persistence.model.kpermissions.ModelPermissionQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

public interface PermissionModelRepository  extends Neo4jBaseRepository<KPermissionModel,Long> {


    @Query("MATCH(ag:AccessGroup{deleted:false})<-[modelPermission:"+HAS_PERMISSION+"]-(model:KPermissionModel)-[:HAS_SUB_MODEL]->(subModel:KPermissionModel) WHERE id(ag)={0} \n" +
            "OPTIONAL MATCH(ag)<-[fieldPermission:"+HAS_PERMISSION+"]-(field:KPermissionField)<-[:HAS_FIELD]-(model) \n" +
            "OPTIONAL MATCH(ag)<-[subModelPermission:"+HAS_PERMISSION+"]-(subModel) \n" +
            "OPTIONAL MATCH(ag)<-[subModelFieldPermission:"+HAS_PERMISSION+"]-(subModelField:KPermissionField)<-[:"+HAS_FIELD+"]-(subModel) \n" +
            "with distinct CASE WHEN subModelField IS NULL THEN [] else collect(distinct{id:id(subModelField),fieldPermission:subModelFieldPermission.fieldLevelPermissions}) END as subModelData,model,permissions, field,fieldPermission,subModel,subModelPermission WITH distinct subModelData,collect(distinct {id:id(subModel),  modelPermission:subModelPermission.fieldLevelPermissions,  permissions:subModelData}) as subModelPermissions,model,modelPermission, field,fieldPermission return distinct id(model) as id, modelPermission.fieldLevelPermissions as modelPermission,CASE WHEN field IS NULL THEN [] else collect( DISTINCT {id:id(field),fieldPermission:fieldPermission.fieldLevelPermissions}) END as permissions,subModelPermissions")
    List<ModelPermissionQueryResult> getModelPermissionsByAccessGroupId(Long accessGroupId);

    @Query(value = "MATCH (kPermissionField:KPermissionField),(accessGroup:AccessGroup) where id(kPermissionField)={0} AND id(accessGroup) IN{1} CREATE UNIQUE (kPermissionField)-[r:"+HAS_PERMISSION+"]->(accessGroup) " +
            "SET r.fieldLevelPermissions={2},r.expertiseIds={3},r.unionIds={4},r.teamIds={5},r.employmentTypeIds={6},r.tagIds={7},r.staffStatuses={8},r.forOtherFieldLevelPermissions={9}")
    void createAccessGroupPermissionFieldRelationshipType(Long kpermissionModelId, List<Long> accessGroupIds, Set<FieldLevelPermission> fieldLevelPermissions,Set<Long> expertiseIds,Set<Long> unionIds,Set<Long> teamIds,Set<Long> employmentTypeIds,Set<Long> tagIds,Set<StaffStatusEnum> staffStatuses,Set<FieldLevelPermission> forOtherFieldLevelPermissions);

    @Query(value = "MATCH (kPermissionModel:KPermissionModel),(accessGroup:AccessGroup) where id(kPermissionModel)={0} AND id(accessGroup) IN{1} CREATE UNIQUE (kPermissionModel)-[r:"+HAS_PERMISSION+"]->(accessGroup) " +
            "SET r.fieldLevelPermissions={2},r.expertiseIds={3},r.unionIds={4},r.teamIds={5},r.employmentTypeIds={6},r.tagIds={7},r.staffStatuses={8},r.forOtherFieldLevelPermissions={9}")
    void createAccessGroupPermissionModelRelationship(Long kpermissionModelId, List<Long> accessGroupIds, Set<FieldLevelPermission> fieldLevelPermissions,Set<Long> expertiseIds,Set<Long> unionIds,Set<Long> teamIds,Set<Long> employmentTypeIds,Set<Long> tagIds,Set<StaffStatusEnum> staffStatuses,Set<FieldLevelPermission> forOtherFieldLevelPermissions);

    @Query("MATCH(ag:AccessGroup{deleted:false}) where id(ag) in {0} MATCH (ag)<-[permission:HAS_PERMISSION]-(field:KPermissionField) RETURN permission.fieldLevelPermissions AS permissions,\n" +
            "permission.expertiseIds as expertiseIds,\n" +
            "permission.forOtherFieldLevelPermissions as forOtherFieldLevelPermissions,\n" +
            "permission.staffStatuses as staffStatuses,\n" +
            "permission.tagIds as tagIds,\n" +
            "permission.teamIds as teamIds,\n" +
            "permission.unionIds as unionIds,id(field) as id,field.fieldName as fieldName")
    List<FieldPermissionQueryResult> getAllFieldPermission(Collection<Long> accessGroupIds);

    @Query("MATCH(ag:AccessGroup{deleted:false}) where id(ag) in {0} MATCH (ag)<-[permission:HAS_PERMISSION]-(model:KPermissionModel) \n" +
            "RETURN permission.fieldLevelPermissions AS permissions,\n" +
            "permission.expertiseIds as expertiseIds,\n" +
            "permission.forOtherFieldLevelPermissions as forOtherFieldLevelPermissions,\n" +
            "permission.staffStatuses as staffStatuses,\n" +
            "permission.tagIds as tagIds,\n" +
            "permission.teamIds as teamIds,\n" +
            "permission.unionIds as unionIds,id(model) as id,model.modelName as modelName")
    List<ModelPermissionQueryResult> getAllModelPermission(Collection<Long> accessGroupIds);

    @Query("MATCH(model:KPermissionModel{deleted:false})  OPTIONAL MATCH(model)-[orgRel:HAS_SUB_MODEL*]->(subModel:KPermissionModel)  OPTIONAL MATCH(model)-[unitRel:HAS_FIELD]->(field:KPermissionField)  OPTIONAL MATCH(subModel)-[orgUnitRel:HAS_FIELD]->(fieldn:KPermissionField) where model.modelName in {0} \n" +
            "Return model,collect(subModel), collect(field),collect(fieldn),COLLECT(orgRel),collect(orgUnitRel),collect(unitRel)")
    List<KPermissionModel> getAllPermissionModelByName(Collection<String> modelName);

}
