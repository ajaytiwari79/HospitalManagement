package com.kairos.persistence.model.kpermissions;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;
import static org.neo4j.ogm.annotation.Relationship.OUTGOING;

@NodeEntity
public class PermissionModel extends UserBaseEntity {

    public PermissionModel() {
        //Default Constructor
    }
    @NotBlank(message = "error.name.notnull")
    private String modelName;


    @Relationship(type = HAS_FIELD,direction = OUTGOING)
    private List<PermissionField> fields = new ArrayList<>();

    private boolean isPermissionSubModel;

    @Relationship(type = HAS_SUB_MODEL,direction = OUTGOING)
    private List<PermissionModel> subModels = new ArrayList<>();


}
