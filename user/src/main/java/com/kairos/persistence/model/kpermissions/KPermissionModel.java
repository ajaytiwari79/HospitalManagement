package com.kairos.persistence.model.kpermissions;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FIELD;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_SUB_MODEL;
import static org.neo4j.ogm.annotation.Relationship.OUTGOING;

@Getter
@Setter
@NoArgsConstructor
@NodeEntity
public class KPermissionModel extends UserBaseEntity {
    @NotBlank(message = ERROR_NAME_NOTNULL)
    private String modelName;


    @Relationship(type = HAS_FIELD,direction = OUTGOING)
    private List<KPermissionField> fields = new ArrayList<>();

    private boolean isPermissionSubModel;

    @Relationship(type = HAS_SUB_MODEL,direction = OUTGOING)
    private List<KPermissionModel> subModels = new ArrayList<>();


}
