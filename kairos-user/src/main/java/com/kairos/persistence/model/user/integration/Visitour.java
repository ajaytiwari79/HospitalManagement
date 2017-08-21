package com.kairos.persistence.model.user.integration;

import org.neo4j.ogm.annotation.NodeEntity;

import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Created by oodles on 21/2/17.
 */
@NodeEntity
public class Visitour  extends UserBaseEntity implements Cloneable {
    private String serverName;
    private String username;
    private String password;
    private Long OrganizationId;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(Long organizationId) {
        OrganizationId = organizationId;
    }

    public Visitour() {
    }

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }
}
