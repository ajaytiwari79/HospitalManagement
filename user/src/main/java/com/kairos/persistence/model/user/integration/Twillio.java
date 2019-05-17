package com.kairos.persistence.model.user.integration;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.beans.BeanUtils;

/**
 * Created by oodles on 21/2/17.
 */
@NodeEntity
public class Twillio  extends UserBaseEntity {
    private String accountId;
    private String authToken;
    private String number;
    private Long organizationId;

    public Twillio() {
        //Default Constructor
    }

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

    public static Twillio getInstance(){
        return new Twillio();
    }

    public static Twillio copyProperties(Twillio source, Twillio target){
        BeanUtils.copyProperties(source,target);
        return target;
    }

}
