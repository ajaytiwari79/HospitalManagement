package com.kairos.dto.user.user.password;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;
import java.util.Arrays;

/**
 * Created by prabjot on 6/10/17.
 */
public class FirstTimePasswordUpdateDTO {

    @Size(min = 6)
    private char[] password;
    @Size(min = 6)
    private char[] repeatPassword;
    @Email
    String email;

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public char[] getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(char[] repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @AssertTrue(message="The password fields mush match")
    private boolean isValid() {
        return Arrays.equals(this.password,this.repeatPassword);
    }
}
