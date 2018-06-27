package com.kairos.user.user.password;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;
import java.util.Arrays;

/**
 * Created by prabjot on 6/10/17.
 */
public class FirstTimePasswordUpdateDTO {

    @Size(min = 6)
    private char[] password1;
    @Size(min = 6)
    private char[] password2;
    @Email
    String email;

    public char[] getPassword1() {
        return password1;
    }

    public void setPassword1(char[] password1) {
        this.password1 = password1;
    }

    public char[] getPassword2() {
        return password2;
    }

    public void setPassword2(char[] password2) {
        this.password2 = password2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @AssertTrue(message="The password fields mush match")
    private boolean isValid() {
        return Arrays.equals(this.password1,this.password2);
    }
}
