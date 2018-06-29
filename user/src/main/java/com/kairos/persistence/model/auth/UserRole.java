package com.kairos.persistence.model.auth;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ROLE_OF;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_USERROLE;

/**
 * UserRole
 * It holds reference to User ,Role & Permission of User
 */
@NodeEntity
public class UserRole extends UserBaseEntity {

    private String[] permissions;


    /**
     * getPermissions
     * @return
     */
    public String[] getPermissions() {
        return permissions;
    }


    /**
     * setPermissions
     * @param permissions
     */
    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }




    @Relationship(type = HAS_USERROLE,direction = "OUTGOING")
    User user;

    /**
     * getStaff
     * @return user
     */
    public User getUser() {
        return user;
    }

    @Relationship (type = HAS_ROLE_OF,direction = "OUTGOING")
    Role role;

    /**
     * getRole
     * @return role
     */
    public Role getRole() {
        return role;
    }



    /**
     * For Jackson parsing
     */
    public UserRole() {
    }




    /**
     * UserRole Constructor
     * @param user
     * @param role
     */
    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }




    /**
     * UserRole Constructor
     * @param role
     * @param permissions
     * @param user

     */
    public UserRole(Role role ,String[] permissions, User user) {
        this.role = role;
        this.permissions = permissions;
        this.user = user;

    }


}
