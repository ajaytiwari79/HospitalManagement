package com.kairos.persistence.model.kpermissions;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FIELD;
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

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<PermissionField> getFields() {
        return fields;
    }

    public void setFields(List<PermissionField> fields) {
        this.fields = fields;
    }


}
