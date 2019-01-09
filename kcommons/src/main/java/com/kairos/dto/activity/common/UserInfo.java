package com.kairos.dto.activity.common;

/**
 * @author pradeep
 * @date - 9/1/19
 */

public class UserInfo {
    private Long id;
    private String email;
    private String fullName;


    public UserInfo() {
    }

    public UserInfo(Long id, String email, String fullName) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
