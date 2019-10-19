package com.kairos.dto.user.staff.staff;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
public class StaffChatDetails {
    private Map<String,String> auth;
    private String user_id;
    private String password;
    private String access_token;
    private String home_server;
    private String device_id;
    private String session;

    public StaffChatDetails(Map<String, String> auth, String user_id, String password) {
        this.auth = auth;
        this.user_id = user_id;
        this.password = password;
    }

}
