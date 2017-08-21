package com.kairos.persistence.model.user.integration;

import org.neo4j.ogm.annotation.NodeEntity;

import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Created by oodles on 21/2/17.
 */
@NodeEntity
public class Twillio  extends UserBaseEntity implements Cloneable {
    private String accountId;
    private String authToken;
    private String number;
    private Long organizationId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Twillio() {
    }

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }
}
