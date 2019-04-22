package com.kairos.persistence.model.user.integration;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.beans.BeanUtils;

/**
 * Created by oodles on 21/2/17.
 */
@NodeEntity
public class Visitour  extends UserBaseEntity{
    private String serverName;
    private String username;
    private String password;
    private Long organizationId;

    public Visitour() {
        //Default Constructor
    }

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
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public static Visitour getInstance(){
        return new Visitour();
    }

    public static Visitour copyProperties(Visitour source, Visitour target){
        BeanUtils.copyProperties(source,target);
        return target;
    }
}
