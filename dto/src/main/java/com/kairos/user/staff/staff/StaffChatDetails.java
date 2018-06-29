package com.kairos.user.staff.staff;


import java.util.Map;

public class StaffChatDetails {
    private Map<String,String> auth;
    private String user_id;
    private String password;
    private String access_token;
    private String home_server;
    private String device_id;
    private String session;

    public StaffChatDetails() {
        //Default Constructor
    }

    public StaffChatDetails(Map<String, String> auth, String user_id, String password) {
        this.auth = auth;
        this.user_id = user_id;
        this.password = password;
    }

    public StaffChatDetails(String user_id, String password) {
        this.user_id = user_id;
        this.password = password;
    }

    public StaffChatDetails(String user_id, String access_token, String home_server, String device_id) {
        this.user_id = user_id;
        this.access_token = access_token;
        this.home_server = home_server;
        this.device_id = device_id;
    }

    public Map<String, String> getAuth() {
        return auth;
    }

    public void setAuth(Map<String, String> auth) {
        this.auth = auth;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getHome_server() {
        return home_server;
    }

    public void setHome_server(String home_server) {
        this.home_server = home_server;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
