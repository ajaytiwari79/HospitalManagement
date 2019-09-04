package com.kairos.persistence.repository.kpermissions;

import com.kairos.persistence.model.kpermissions.KPermissionModel;
import com.kairos.persistence.model.kpermissions.ModelPermissionQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

public interface PermissionModelRepository  extends Neo4jBaseRepository<KPermissionModel,Long> {


    @Query(value = "MATCH(ag:AccessGroup{deleted:false})<-[modelPermission:"+ HAS_PERMISSION+"]-(model:KPermissionModel)-[:"+HAS_SUB_MODEL+"]->(subModel:KPermissionModel) WHERE id(ag)={0}\n" +
            "MATCH(ag)<-[fieldPermission:"+HAS_PERMISSION+"]-(field:KPermissionField)<-[:"+HAS_FIELD+"]-(model)\n" +
            "MATCH(ag)<-[subModelPermission:"+HAS_PERMISSION+"]-(subModel)\n" +
            "MATCH(ag)<-[subModelFieldPermission:"+HAS_PERMISSION+"]-(subModelField:KPermissionField)<-[:"+HAS_FIELD+"]-(subModel)\n" +
            "with distinct\n" +
            "collect(distinct{fieldId:id(subModelField),fieldPermission:subModelFieldPermission.fieldLevelPermission}) as subModelData,model,modelPermission, field,fieldPermission,subModel,subModelPermission\n" +
            "WITH distinct subModelData,\n" +
            "collect(distinct {permissionModelId:id(subModel), \n" +
            " modelPermission:subModelPermission.fieldLevelPermission, \n" +
            " fieldPermissions:subModelData\n" +
            "}) as subModelPermissions,model,modelPermission, field,fieldPermission\n" +
            "return distinct id(model) as permissionModelId, \n" +
            "modelPermission.fieldLevelPermission as modelPermission,\n" +
            "collect( DISTINCT {fieldId:id(field),fieldPermission:fieldPermission.fieldLevelPermission}) as fieldPermissions,subModelPermissions")
    List<ModelPermissionQueryResult> getModelPermissionsByAccessGroupId(Long accessGroupId);


}
