package com.kairos.persistence.repository.kpermissions;

import com.kairos.enums.StaffStatusEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.enums.kpermissions.PermissionAction;
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

    @Query(value = "MATCH (kPermissionField:KPermissionField),(accessGroup:AccessGroup) where id(kPermissionField)={0} AND id(accessGroup) IN{1} CREATE UNIQUE (kPermissionField)-[r:"+HAS_PERMISSION+"]->(accessGroup) " +
            "SET r.fieldLevelPermissions={2},r.expertiseIds={3},r.unionIds={4},r.teamIds={5},r.employmentTypeIds={6},r.tagIds={7},r.staffStatuses={8},r.forOtherFieldLevelPermissions={9}")
    void createAccessGroupPermissionFieldRelationshipType(Long kpermissionModelId, List<Long> accessGroupIds, Set<FieldLevelPermission> fieldLevelPermissions,Set<Long> expertiseIds,Set<Long> unionIds,Set<Long> teamIds,Set<Long> employmentTypeIds,Set<Long> tagIds,Set<StaffStatusEnum> staffStatuses,Set<FieldLevelPermission> forOtherFieldLevelPermissions);

    @Query(value = "MATCH (kPermissionModel:KPermissionModel),(accessGroup:AccessGroup) where id(kPermissionModel)={0} AND id(accessGroup) IN{1} CREATE UNIQUE (kPermissionModel)-[r:"+HAS_PERMISSION+"]->(accessGroup) " +
            "SET r.fieldLevelPermissions={2},r.expertiseIds={3},r.unionIds={4},r.teamIds={5},r.employmentTypeIds={6},r.tagIds={7},r.staffStatuses={8},r.forOtherFieldLevelPermissions={9}")
    void createAccessGroupPermissionModelRelationship(Long kpermissionModelId, List<Long> accessGroupIds, Set<FieldLevelPermission> fieldLevelPermissions,Set<Long> expertiseIds,Set<Long> unionIds,Set<Long> teamIds,Set<Long> employmentTypeIds,Set<Long> tagIds,Set<StaffStatusEnum> staffStatuses,Set<FieldLevelPermission> forOtherFieldLevelPermissions);

    @Query("MATCH(ag:AccessGroup{deleted:false}) where id(ag) in {0} MATCH (ag)<-[permission:HAS_PERMISSION]-(field:KPermissionField) " +
            "OPTIONAL MATCH(staff:Staff)<-[:"+BELONGS_TO+"]-(position:Position)-["+HAS_UNIT_PERMISSIONS+"]->(up:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]-(unit) WHERE ID(staff)={2} AND ID(unit)={1} " +
            "OPTIONAL MATCH(up)-[customRel:"+HAS_CUSTOMIZED_PERMISSION_FOR_FIELD+"]->(field) WHERE customRel.accessGroupId=id(ag) " +
            "RETURN CASE WHEN customRel IS NULL THEN  permission.fieldLevelPermissions ELSE customRel.fieldLevelPermissions END AS permissions, \n" +
            "CASE WHEN customRel IS NULL THEN permission.expertiseIds ELSE customRel.expertiseIds END as expertiseIds,\n" +
            "CASE WHEN customRel IS NULL THEN permission.forOtherFieldLevelPermissions ELSE customRel.forOtherFieldLevelPermissions END as forOtherFieldLevelPermissions,\n" +
            "CASE WHEN customRel IS NULL THEN permission.staffStatuses ELSE customRel.staffStatuses END as staffStatuses,\n" +
            "CASE WHEN customRel IS NULL THEN permission.tagIds ELSE customRel.tagIds END as tagIds,\n" +
            "CASE WHEN customRel IS NULL THEN permission.teamIds ELSE customRel.teamIds END as teamIds,\n" +
            "CASE WHEN customRel IS NULL THEN permission.employmentTypeIds ELSE customRel.employmentTypeIds END as employmentTypeIds,\n" +
            "CASE WHEN customRel IS NULL THEN permission.unionIds ELSE customRel.unionIds END as unionIds,id(field) as id,field.fieldName as fieldName " +
            " UNION " +
            " MATCH(ag:AccessGroup{deleted:false}) where id(ag) in {3} MATCH (ag)<-[permission:HAS_PERMISSION]-(field:KPermissionField) " +
            "RETURN permission.fieldLevelPermissions AS permissions,permission.expertiseIds as expertiseIds, permission.forOtherFieldLevelPermissions AS forOtherFieldLevelPermissions, " +
            "permission.staffStatuses AS staffStatuses, permission.tagIds AS tagIds,permission.teamIds AS teamIds, permission.employmentTypeIds AS employmentTypeIds,permission.unionIds AS  unionIds, id(field) as id,field.fieldName as fieldName ")
    List<FieldPermissionQueryResult> getAllFieldPermission(Collection<Long> accessGroupIds, Long lastSelectedOrganizationId, Long staffId, Set<Long> unitAccessGroupIds);

    @Query("MATCH(model:KPermissionModel) where model.modelName={0} MATCH(model)-[unitRel:HAS_FIELD]->(field:KPermissionField) where field.fieldName IN {1}\n" +
            "MATCH (field)-[permission:HAS_PERMISSION]->(ag:AccessGroup{deleted:false}) where id(ag) in {2} \n" +
            "RETURN permission.fieldLevelPermissions AS permissions,permission.expertiseIds as expertiseIds," +
            "permission.forOtherFieldLevelPermissions as forOtherFieldLevelPermissions,permission.staffStatuses as staffStatuses,permission.tagIds as tagIds," +
            "permission.teamIds as teamIds,permission.employmentTypeIds as employmentTypeIds," +
            "permission.unionIds as unionIds,id(field) as id,field.fieldName as fieldName")
    List<FieldPermissionQueryResult> getAllFieldPermissionByFieldNames(String modelName,Collection<String> fieldNames,Collection<Long> accessGroupIds);

    @Query("MATCH(ag:AccessGroup{deleted:false}) where id(ag) in {0} MATCH (ag)<-[permission:HAS_PERMISSION]-(model:KPermissionModel) \n" +
            "OPTIONAL MATCH(staff:Staff)<-[:"+BELONGS_TO+"]-(position:Position)-["+HAS_UNIT_PERMISSIONS+"]->(up:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]-(unit) WHERE ID(staff)={2} AND ID(unit)={1} " +
            "OPTIONAL MATCH(up)-[customRel:"+HAS_CUSTOMIZED_PERMISSION_FOR_FIELD+"]->(model) WHERE customRel.accessGroupId=id(ag)" +
            "RETURN CASE WHEN customRel IS NULL THEN  permission.fieldLevelPermissions ELSE customRel.fieldLevelPermissions END AS permissions,\n" +
            "CASE WHEN customRel IS NULL THEN permission.expertiseIds ELSE customRel.expertiseIds END as expertiseIds,\n" +
            "CASE WHEN customRel IS NULL THEN permission.forOtherFieldLevelPermissions ELSE customRel.forOtherFieldLevelPermissions END as forOtherFieldLevelPermissions,\n" +
            "CASE WHEN customRel IS NULL THEN permission.staffStatuses ELSE customRel.staffStatuses END as staffStatuses,\n" +
            "CASE WHEN customRel IS NULL THEN permission.actions ELSE customRel.actions END as actions,\n" +
            "CASE WHEN customRel IS NULL THEN permission.tagIds ELSE customRel.tagIds END as tagIds,\n" +
            "CASE WHEN customRel IS NULL THEN permission.teamIds ELSE customRel.teamIds END as teamIds,\n" +
            "CASE WHEN customRel IS NULL THEN permission.employmentTypeIds ELSE customRel.employmentTypeIds END as employmentTypeIds,\n" +
            "CASE WHEN customRel IS NULL THEN permission.unionIds ELSE customRel.unionIds END as unionIds,id(model) as id,model.modelName as modelName " +
            " UNION " +
            " MATCH(ag:AccessGroup{deleted:false}) where id(ag) in {3} MATCH (ag)<-[permission:HAS_PERMISSION]-(model:KPermissionModel) " +
            "RETURN permission.fieldLevelPermissions AS permissions,permission.expertiseIds as expertiseIds, permission.forOtherFieldLevelPermissions AS forOtherFieldLevelPermissions, " +
            "permission.staffStatuses AS staffStatuses,permission.actions AS actions, permission.tagIds AS tagIds,permission.teamIds AS teamIds, permission.employmentTypeIds AS employmentTypeIds,permission.unionIds AS  unionIds, id(model) as id,model.modelName as modelName ")
    List<ModelPermissionQueryResult> getAllModelPermission(Collection<Long> accessGroupIds, Long unitId, Long staffId, Set<Long> unitAccessGroupIds);

    @Query("MATCH(model:KPermissionModel{deleted:false}) where model.modelName in {0}" +
            "WITH model " +
            "  OPTIONAL MATCH(model)-[orgRel:HAS_SUB_MODEL*]->(subModel:KPermissionModel)  OPTIONAL MATCH(model)-[unitRel:HAS_FIELD]->(field:KPermissionField)  OPTIONAL MATCH(subModel)-[orgUnitRel:HAS_FIELD]->(fieldn:KPermissionField)  \n" +
            "Return model,collect(subModel), collect(field),collect(fieldn),COLLECT(orgRel),collect(orgUnitRel),collect(unitRel)")
    List<KPermissionModel> getAllPermissionModelByName(Collection<String> modelName);

    @Query("MATCH(kPermissionModel) where id(kPermissionModel)={0}\n" +
            "OPTIONAL MATCH(kPermissionModel)-[subModelRel:"+HAS_SUB_MODEL+"*]->(subModel:KPermissionModel) \n" +
            "OPTIONAL MATCH(kPermissionModel)-[fieldRel:"+HAS_FIELD+"]->(kPermissionField:KPermissionField) \n" +
            "OPTIONAL MATCH(subModel)-[subModelFieldRel:"+HAS_FIELD+"]->(subModelFields:KPermissionField) \n" +
            "WITH collect(id(kPermissionField)) as kPermissionFieldIds,collect(id(subModelFields)) as subModelFieldsIds,collect(id(subModel)) as subModelIds, collect(id(kPermissionModel)) as kPermissionModelId\n" +
            "WITH kPermissionFieldIds+subModelFieldsIds+subModelIds+ kPermissionModelId AS ids\n" +
            "unwind ids as allIdsToSetPermission with distinct allIdsToSetPermission \n" +
            "Return allIdsToSetPermission ")
    Set<Long> kPermissionModelIds(Long kPermissionModelId);

    @Query(value = "MATCH (accessGroup:AccessGroup) WHERE id(accessGroup) IN {2} " +
            "MATCH (kPermissionAction:KPermissionAction)-[rel:"+HAS_ACTION_PERMISSION+"]->(accessGroup) where kPermissionAction.modelName={0} AND kPermissionAction.action={1}  " +
            "OPTIONAL MATCH(staff:Staff)<-[:"+BELONGS_TO+"]-(position:Position)-["+HAS_UNIT_PERMISSIONS+"]->(up:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]-(unit) WHERE ID(staff)={3} AND ID(unit)={4} " +
            "OPTIONAL MATCH(up)-[customRel:"+HAS_CUSTOMIZED_PERMISSION_FOR_ACTION+"]->(kPermissionAction) WHERE customRel.accessGroupId=id(ag) " +
            "RETURN CASE WHEN customRel IS NULL THEN  rel.hasPermission ELSE customRel.hasPermission END AS result \n" )
    boolean hasActionPermission(String modelName, PermissionAction action, Set<Long> accessGroupIds, Long loggedInStaffId, Long staffId);

    @Query(value ="MATCH (kPermissionAction:KPermissionAction),(accessGroup:AccessGroup) where id(accessGroup)={0} AND id(kPermissionAction) IN {1} " +
            "CREATE UNIQUE (kPermissionAction)-[r:"+HAS_ACTION_PERMISSION+"]->(accessGroup) " +
            "SET r.hasPermission=TRUE ")
    void setActionPermissions(Long accessGroupId, Set<Long> actions,Long id);

    @Query("MATCH(kPermissionModel:KPermissionModel)-[:"+HAS_ACTION+"]-(k:KPermissionAction)-[rel:"+HAS_ACTION_PERMISSION+"]->(accessGroup:AccessGroup) WHERE id(kPermissionModel)={0} AND id(accessGroup)={1} SET rel.hasPermission=FALSE ")
    void disableActionPermission(Long id,Long accessGroupId);


    @Query(value = "MATCH(accessGroup:AccessGroup)<-[r:"+HAS_ACTION_PERMISSION+"]-(kPermissionAction:KPermissionAction)<-[:"+HAS_ACTION+"]-(kPermissionModel:KPermissionModel) where id(accessGroup)={0} AND ID(kPermissionModel)={1} " +
            "RETURN id(kPermissionModel) as id,kPermissionModel.modelName as modelName," +
            "{id:id(kPermissionAction),action:kPermissionAction.action,hasPermission:r.hasPermission} as actions " )
    ModelPermissionQueryResult getActionPermissions(Long accessGroupId,Long modelId);

    @Query(value = "MATCH (accessGroup:AccessGroup) WHERE id(accessGroup) = {0} " +
            "MATCH(kPermissionModel:KPermissionModel)-[:"+HAS_ACTION+"]-(kPermissionAction:KPermissionAction) WHERE ID(kPermissionModel)={3} " +
            "OPTIONAL MATCH(kPermissionAction)-[rel:"+HAS_ACTION_PERMISSION+"]->(accessGroup) " +
            "OPTIONAL MATCH(staff:Staff)<-[:"+BELONGS_TO+"]-(position:Position)-["+HAS_UNIT_PERMISSIONS+"]->(up:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]-(unit) WHERE ID(staff)={1} AND ID(unit)={2} " +
            "OPTIONAL MATCH(up)-[customRel:"+HAS_CUSTOMIZED_PERMISSION_FOR_ACTION+"]->(kPermissionAction) WHERE customRel.accessGroupId=id(accessGroup) " +
            "RETURN id(kPermissionModel) as id,kPermissionModel.modelName as modelName," +
            "COLLECT({id:id(kPermissionAction),action:kPermissionAction.action,hasPermission:CASE WHEN customRel IS NULL THEN  rel.hasPermission ELSE customRel.hasPermission END}) as actions " )
    ModelPermissionQueryResult getActionPermissionsForStaff(Long accessGroupId,Long staffId,Long unitId,Long modelId);
}
