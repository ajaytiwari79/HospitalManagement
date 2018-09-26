package com.kairos.scheduler.persistence.model.user_auth;

import com.kairos.scheduler.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UserAuthCredentials extends MongoBaseEntity {

    private String username;
    private String password;
    private String authToken;

    public UserAuthCredentials() {

    }
    public UserAuthCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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
}
