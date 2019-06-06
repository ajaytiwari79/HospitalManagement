package com.kairos.persistence.model.client;
import com.kairos.annotations.KPermissionRelatedModel;
import com.kairos.annotations.KPermissionRelationshipFrom;
import com.kairos.annotations.KPermissionRelationshipTo;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.staff.personal_details.Staff;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.SERVED_BY_STAFF;


/**
 * Created by oodles on 3/10/16.
 */
@KPermissionRelatedModel
@RelationshipEntity(type = SERVED_BY_STAFF)
public class ClientStaffRelation extends UserBaseEntity {
    public enum StaffType{
        PREFERRED,FORBIDDEN,NONE
    }

    @KPermissionRelationshipTo
    @StartNode
    private Client client;

    @KPermissionRelationshipFrom
    @EndNode
    private Staff staff;

    private StaffType type = StaffType.NONE;

    public ClientStaffRelation(Client client, Staff staff, StaffType type) {
        this.client = client;
        this.staff = staff;
        this.type = type;

    }

    public ClientStaffRelation() {
    }

    public StaffType getType() {
        return type;
    }

    public void setType(StaffType type) {
        this.type = type;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }


}
