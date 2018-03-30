package com.kairos.response.dto.web;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;
import java.util.Arrays;

/**
 * Created by prabjot on 6/10/17.
 */
public class PasswordUpdateDTO {

    @NotEmpty
    private char[] oldPassword;
    @NotEmpty
    private char[] confirmPassword;

    @Size(min = 6)
    private char[] newPassword;

    public char[] getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(char[] oldPassword) {
        this.oldPassword = oldPassword;
    }

    public char[] getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(char[] newPassword) {
        this.newPassword = newPassword;
    }

    public char[] getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(char[] confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @AssertTrue(message="The password fields mush match")
    private boolean isValid() {
        return Arrays.equals(this.newPassword,this.confirmPassword);
    }
}
