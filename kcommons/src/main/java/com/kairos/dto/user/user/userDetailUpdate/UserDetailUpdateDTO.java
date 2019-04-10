package com.kairos.dto.user.user.userDetailUpdate;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

public class UserDetailUpdateDTO {

    //@NotNull(message = "email can't be null")

    private  String email;

    private boolean userNameUpdated;

   // @NotNull(message = "userName can't be null")
    private String userName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public boolean isUserNameUpdated() {
        return userNameUpdated;
    }

    public void setUserNameUpdated(boolean userNameUpdated) {
        this.userNameUpdated = userNameUpdated;
    }
}
