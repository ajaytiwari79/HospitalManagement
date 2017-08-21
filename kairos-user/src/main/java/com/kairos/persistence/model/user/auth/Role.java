package com.kairos.persistence.model.user.auth;


import org.neo4j.ogm.annotation.NodeEntity;

import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Role Domain & it's properties
 */

@NodeEntity
public class Role extends UserBaseEntity {

    private String authority;
    private String accessLevel;
    private Long countryId;


    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }




    /**
     * getAuthority
     * @return authority
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * setAuthority
     * @param authority
     */
    public void setAuthority(String authority) {
        this.authority = authority;
    }

    /**
     * getAccessLevel
     * @return
     */
    public String getAccessLevel() {
        return accessLevel;
    }

    /**
     * setAccessLevel
     * @param accessLevel
     */
    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }


    /**
     * For Jackson parsing
     */
    public Role() {
    }

    /**
     * Role Constructor
     * @param authority
     * @param accessLevel
     */
    public Role(String authority, String accessLevel) {
        this.authority = authority;
        this.accessLevel = accessLevel;
    }


    public Role(String authority, String accessLevel, Long countryId) {
        this.authority = authority;
        this.accessLevel = accessLevel;
        this.countryId = countryId;
    }
}
